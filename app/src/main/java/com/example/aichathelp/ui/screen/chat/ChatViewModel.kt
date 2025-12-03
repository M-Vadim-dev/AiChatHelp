package com.example.aichathelp.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichathelp.domain.model.ChatContext
import com.example.aichathelp.domain.model.ChatStep
import com.example.aichathelp.domain.model.Message
import com.example.aichathelp.domain.model.MessageType
import com.example.aichathelp.domain.usecase.SendQuestionUseCase
import com.example.aichathelp.ui.util.toUiTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendQuestionUseCase: SendQuestionUseCase
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
        }
    }

    private fun updateInput(text: String) {
        _state.value = _state.value.copy(input = text)
    }

    private fun sendQuestion() {
        val text = _state.value.input
        if (text.isBlank()) return

        appendUserMessage(text)

        _state.value = _state.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val response: ChatStep = sendQuestionUseCase(
                    userMessage = text,
                    chatContext = chatContext
                )

                val newAnswers = if (response.question.isNotBlank()) {
                    chatContext.answers + mapOf(response.question to text)
                } else chatContext.answers

                chatContext = chatContext.copy(state = response.state, answers = newAnswers)

                println("Accumulated: ${chatContext.answers.size} answers")
                appendChatStep(response)
            } catch (e: Exception) {
                appendMessages(listOf(buildErrorMessage(e.message ?: "Unknown error")))
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

    private fun appendUserMessage(text: String) {
        val message = Message(
            text = text,
            time = LocalDateTime.now().toUiTime(),
            isUser = true,
            type = MessageType.Answer
        )
        _state.value = _state.value.copy(
            messages = _state.value.messages + message,
            input = ""
        )
    }

    private fun appendChatStep(step: ChatStep) {
        val time = LocalDateTime.now().toUiTime()
        val messages = mutableListOf<Message>()

        step.answer.takeIf { it.isNotBlank() }?.let {
            messages += Message(
                text = it,
                time = time,
                isUser = false,
                type = MessageType.Answer
            )
        }

        step.question.takeIf { it.isNotBlank() }?.let {
            messages += Message(
                text = it,
                time = time,
                isUser = false,
                type = MessageType.Question
            )
        }

        appendMessages(messages)
    }

    private fun appendMessages(messages: List<Message>) {
        _state.value = _state.value.copy(
            messages = _state.value.messages + messages
        )
    }

    private fun buildErrorMessage(message: String): Message {
        return Message(
            text = message,
            time = LocalDateTime.now().toUiTime(),
            isUser = false,
            type = MessageType.Error(message),
            isError = true
        )
    }

}
