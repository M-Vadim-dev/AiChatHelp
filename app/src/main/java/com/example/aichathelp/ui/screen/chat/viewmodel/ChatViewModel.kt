package com.example.aichathelp.ui.screen.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ModelVendor
import com.example.aichathelp.domain.model.PromptType
import com.example.aichathelp.domain.usecase.ClearChatHistoryUseCase
import com.example.aichathelp.domain.usecase.GetChatHistoryUseCase
import com.example.aichathelp.domain.usecase.GetDefaultChatConfigUseCase
import com.example.aichathelp.domain.usecase.GetPromptUseCase
import com.example.aichathelp.domain.usecase.SendQuestionUseCase
import com.example.aichathelp.ui.mapper.ChatMessageUiMapper
import com.example.aichathelp.ui.screen.chat.model.ChatIntent
import com.example.aichathelp.ui.screen.chat.model.ChatSettingsUiState
import com.example.aichathelp.ui.screen.chat.model.ChatState
import com.example.aichathelp.ui.screen.chat.mapper.MessageUiFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendQuestionUseCase: SendQuestionUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val clearChatHistoryUseCase: ClearChatHistoryUseCase,
    private val getPromptUseCase: GetPromptUseCase,
    private val chatMessageUiMapper: ChatMessageUiMapper,
    private val messageUiFactory: MessageUiFactory,
    private val getDefaultChatConfigUseCase: GetDefaultChatConfigUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        ChatState(settings = createUiSettings(getDefaultChatConfigUseCase()))
    )
    val state: StateFlow<ChatState> = _state

    init {
        loadChatHistory()
    }

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.InputChanged -> updateInput(intent.text)
            ChatIntent.SendClicked -> sendQuestion()
            ChatIntent.ErrorShown -> clearError()
            ChatIntent.RetryClicked -> retryLastQuestion()
            is ChatIntent.PromptTypeChanged -> updatePromptType(intent.promptType)
            ChatIntent.ClearChat -> clearChatHistory()
            is ChatIntent.TemperatureChanged -> updateTemperature(intent.value)
            is ChatIntent.TopPChanged -> updateTopP(intent.value)
            ChatIntent.ResetConfigClicked -> resetConfig()
            is ChatIntent.ProviderChanged -> updateProvider(intent.provider)
            is ChatIntent.MaxTokensChanged -> updateMaxTokens(intent.value)
            is ChatIntent.UseHistoryChanged -> updateUseHistory(intent.value)
        }
    }

    private fun createUiSettings(config: ChatConfig): ChatSettingsUiState =
        ChatSettingsUiState(
            provider = config.provider,
            availableProviders = ModelVendor.entries,
            currentPromptType = PromptType.PROFESSIONAL,
            temperature = config.temperature,
            topP = config.topP,
            maxTokens = config.maxTokens,
            useHistory = config.useHistory
        )

    private fun loadChatHistory() {
        viewModelScope.launch {
            val history = getChatHistoryUseCase()
            val uiMessages = chatMessageUiMapper.toUi(history)
            val finalMessages = if (history.isEmpty()) {
                listOf(messageUiFactory.createWelcomeMessage())
            } else {
                uiMessages
            }
            _state.update { it.copy(messages = finalMessages) }
        }
    }

    private fun updatePromptType(type: PromptType) {
        _state.update { it.copy(settings = it.settings.copy(currentPromptType = type)) }
    }

    private fun updateProvider(provider: ModelVendor) {
        viewModelScope.launch {
            _state.update { it.copy(settings = it.settings.copy(provider = provider)) }
//            clearChatHistoryUseCase()
//            _state.update { it.copy(messages = listOf(messageUiFactory.createWelcomeMessage()), input = "", error = null, isLoading = false) }
        }
    }

    private fun clearChatHistory() {
        viewModelScope.launch {
            clearChatHistoryUseCase()
            _state.update {
                it.copy(
                    messages = listOf(messageUiFactory.createWelcomeMessage()),
                    input = "",
                    error = null,
                    isLoading = false
                )
            }
        }
    }

    private fun resetConfig() {
        _state.update { it.copy(settings = createUiSettings(getDefaultChatConfigUseCase())) }
    }

    private fun updateInput(text: String) {
        _state.update { it.copy(input = text) }
    }

    private fun updateTemperature(value: Double) {
        _state.update { it.copy(settings = it.settings.copy(temperature = value)) }
    }

    private fun updateTopP(value: Double) {
        _state.update { it.copy(settings = it.settings.copy(topP = value)) }
    }

    private fun updateMaxTokens(value: Int) {
        _state.update { it.copy(settings = it.settings.copy(maxTokens = value)) }
    }

    private fun updateUseHistory(value: Boolean) {
        _state.update { it.copy(settings = it.settings.copy(useHistory = value)) }
    }

    private fun sendQuestion() {
        val text = _state.value.input.trim()
        if (text.isBlank()) return

        val userMessage = messageUiFactory.createUserMessage(text)
        _state.update {
            it.copy(
                messages = it.messages + userMessage,
                input = "",
                isLoading = true,
                error = null
            )
        }

        val settings = state.value.settings
        val config = ChatConfig(
            provider = settings.provider,
            temperature = settings.temperature,
            topP = settings.topP,
            maxTokens = settings.maxTokens,
            useHistory = settings.useHistory
        )

        val promptType = settings.currentPromptType

        viewModelScope.launch {
            val result = sendQuestionUseCase(
                userMessage = text,
                systemPrompt = getPromptUseCase(promptType),
                config = config
            )

            result.onSuccess { response ->
                val botMessage = messageUiFactory.createBotMessage(response)

                _state.update { currentState ->
                    val updatedMessages = currentState.messages.map { msg ->
                        if (msg.id == userMessage.id) msg.copy(isSending = false) else msg
                    }
                    currentState.copy(
                        messages = updatedMessages + botMessage,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                val errorMessage = messageUiFactory.createErrorMessage(error)
                _state.update {
                    it.copy(
                        messages = it.messages + errorMessage,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun retryLastQuestion() {
        val lastUserMessage = _state.value.messages.lastOrNull { it.isUser }?.text
        lastUserMessage?.let {
            _state.update { state -> state.copy(input = it) }
            sendQuestion()
        }
    }
}