package com.example.aichathelp.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichathelp.domain.model.ChatConfig
import com.example.aichathelp.domain.model.ChatContext
import com.example.aichathelp.domain.model.ChatStep
import com.example.aichathelp.domain.model.Message
import com.example.aichathelp.domain.model.MessageType
import com.example.aichathelp.domain.model.PromptType
import com.example.aichathelp.domain.repository.PromptRepository
import com.example.aichathelp.domain.usecase.SendQuestionUseCase
import com.example.aichathelp.ui.screen.chat.model.MessageUi
import com.example.aichathelp.ui.util.toUiTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendQuestionUseCase: SendQuestionUseCase,
    private val promptRepository: PromptRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state

    private var chatContext = ChatContext()

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
        }
    }

    private fun updatePromptType(type: PromptType) {
        _state.value = _state.value.copy(currentPromptType = type)
    }

    private fun clearChatHistory() {
        chatContext = ChatContext()
        _state.value = _state.value.copy(
            messages = emptyList(),
            input = "",
            error = null,
        )
    }

    private fun resetConfig() {
        val defaultConfig = ChatConfig.default()
        _state.value = _state.value.copy(
            currentPromptType = ChatState().currentPromptType,
            temperature = defaultConfig.temperature,
            topP = defaultConfig.topP,
        )
    }

    private fun updateInput(text: String) {
        _state.value = _state.value.copy(input = text)
    }

    private fun updateTemperature(value: Double) {
        _state.value = _state.value.copy(temperature = value)
    }

    private fun updateTopP(value: Double) {
        _state.value = _state.value.copy(topP = value)
    }

    private fun sendQuestion() {
        val text = _state.value.input
        if (text.isBlank()) return

        appendUserMessage(text)

        _state.value = _state.value.copy(isLoading = true, error = null)

        val baseConfig = ChatConfig.creative()
        val config = baseConfig.copy(
            temperature = _state.value.temperature,
            topP = _state.value.topP
        )

        val promptText = getPrompt(_state.value.currentPromptType)

        viewModelScope.launch {
            try {
                val result = sendQuestionUseCase(
                    userMessage = text,
                    chatContext = chatContext,
                    systemPrompt = promptText,
                    config = config
                )

                result
                    .onSuccess { response ->
                        val newAnswers = if (response.question.isNotBlank()) {
                            chatContext.answers + mapOf(response.question to text)
                        } else chatContext.answers

                        chatContext = chatContext.copy(
                            state = response.state,
                            answers = newAnswers
                        )

                        appendChatStep(response)
                    }
                    .onFailure { error ->
                        appendMessages(
                            listOf(
                                buildErrorMessage(error.message ?: "Unknown error")
                            )
                        )
                    }
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun retryLastQuestion() {
        val lastUserMessage = _state.value.messages.lastOrNull { it.isUser }?.text
        lastUserMessage?.let {
            _state.value = _state.value.copy(input = it)
            sendQuestion()
        }
    }

    private fun getPrompt(type: PromptType): String = when (type) {
        PromptType.PROFESSIONAL -> promptRepository.getFsmPrompt()
        PromptType.CREATIVE -> promptRepository.getCreativePrompt()
    }

    private fun appendUserMessage(text: String) {
        val message = Message(
            text = text,
            time = LocalDateTime.now().toUiTime(),
            type = MessageType.Answer
        )
        val messageUi = MessageUi.fromDomain(
            message,
            isUser = true,
            isSending = true
        ).copy(hasAnimated = false)

        _state.value = _state.value.copy(
            messages = _state.value.messages + messageUi,
            input = ""
        )
    }

    private fun appendChatStep(step: ChatStep) {
        val updatedMessages = _state.value.messages.mapIndexed { _, msg ->
            if (msg.isUser && msg.isSending) {
                msg.copy(isSending = false, hasAnimated = false)
            } else msg
        }

        _state.value = _state.value.copy(messages = updatedMessages)

        val time = LocalDateTime.now().toUiTime()
        val messages = mutableListOf<MessageUi>()

        step.answer.takeIf { it.isNotBlank() }?.let {
            val msg = Message(text = it, time = time, type = MessageType.Answer)
            messages += MessageUi.fromDomain(msg, isUser = false).copy(hasAnimated = false)
        }

        step.question.takeIf { it.isNotBlank() }?.let {
            val msg = Message(text = it, time = time, type = MessageType.Question)
            messages += MessageUi.fromDomain(msg, isUser = false).copy(hasAnimated = false)
        }

        appendMessages(messages)
    }

    private fun appendMessages(messages: List<MessageUi>) {
        _state.value = _state.value.copy(
            messages = _state.value.messages + messages
        )
    }

    private fun buildErrorMessage(message: String): MessageUi {
        val msg = Message(
            text = message,
            time = LocalDateTime.now().toUiTime(),
            type = MessageType.Error(message)
        )
        return MessageUi.fromDomain(message = msg, isUser = false)
    }

}
