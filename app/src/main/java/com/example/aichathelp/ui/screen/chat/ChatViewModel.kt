package com.example.aichathelp.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ModelVendor
import com.example.aichathelp.domain.model.PromptType
import com.example.aichathelp.domain.repository.PromptRepository
import com.example.aichathelp.domain.usecase.ClearChatHistoryUseCase
import com.example.aichathelp.domain.usecase.GetChatHistoryUseCase
import com.example.aichathelp.domain.usecase.SendQuestionUseCase
import com.example.aichathelp.ui.mapper.ChatMessageUiMapper
import com.example.aichathelp.ui.screen.chat.mapper.MessageUiMapper
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
    private val promptRepository: PromptRepository,
    private val chatMessageUiMapper: ChatMessageUiMapper,
    private val messageUiMapper: MessageUiMapper,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state

    init {
        loadChatHistory()
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            val history = getChatHistoryUseCase()
            _state.update {
                it.copy(messages = chatMessageUiMapper.toUi(history))
            }
            if (history.isEmpty()) {
                _state.update { it.copy(messages = it.messages + messageUiMapper.createWelcomeMessage()) }
            }
        }
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
            is ChatIntent.UseHistoryChanged -> updateUseHistory(intent.value)
        }
    }

    private fun updatePromptType(type: PromptType) {
        _state.update { it.copy(currentPromptType = type) }
    }

    private fun updateProvider(provider: ModelVendor) {
        _state.update { it.copy(provider = provider) }
        clearChatHistory()
    }

    private fun clearChatHistory() {
        viewModelScope.launch {
            clearChatHistoryUseCase()
            _state.update {
                it.copy(
                    messages = listOf(messageUiMapper.createWelcomeMessage()),
                    input = "",
                    error = null,
                    isLoading = false
                )
            }
        }
    }

    private fun resetConfig() {
        val defaultConfig = ChatConfig.default()
        _state.update {
            it.copy(
                currentPromptType = ChatState().currentPromptType,
                temperature = defaultConfig.temperature,
                topP = defaultConfig.topP,
            )
        }
    }

    private fun updateUseHistory(value: Boolean) {
        _state.update { it.copy(useHistory = value) }
    }

    private fun updateInput(text: String) {
        _state.update { it.copy(input = text) }
    }

    private fun updateTemperature(value: Double) {
        _state.update { it.copy(temperature = value) }
    }

    private fun updateTopP(value: Double) {
        _state.update { it.copy(topP = value) }
    }

    private fun sendQuestion() {
        val text = _state.value.input.trim()
        if (text.isBlank()) return

        val userMessage = messageUiMapper.createUserMessage(text)
        _state.update {
            it.copy(
                messages = it.messages + userMessage,
                input = "",
                isLoading = true,
                error = null
            )
        }

        val config = ChatConfig(
            provider = _state.value.provider,
            model = _state.value.provider.defaultModel,
            temperature = _state.value.temperature,
            topP = _state.value.topP,
            useHistory = _state.value.useHistory,
        )

        val promptText = getPrompt(_state.value.currentPromptType)

        viewModelScope.launch {
            val result = sendQuestionUseCase(
                userMessage = text,
                systemPrompt = promptText,
                config = config
            )

            result.onSuccess { response ->
                val botMessage = messageUiMapper.createBotMessage(response)

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
                val errorMessage = messageUiMapper.createErrorMessage(error)
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

    private fun getPrompt(type: PromptType): String = when (type) {
        PromptType.PROFESSIONAL -> promptRepository.getFsmPrompt()
        PromptType.CREATIVE -> promptRepository.getCreativePrompt()
    }
}
