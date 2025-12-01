package com.example.aichathelp.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichathelp.domain.model.Message
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

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.InputChanged -> {
                _state.value = _state.value.copy(input = intent.text)
            }

            ChatIntent.SendClicked -> {
                val text = _state.value.input
                if (text.isBlank()) return
                _state.value = _state.value.copy(
                    messages = _state.value.messages + Message(
                        text = text,
                        time = LocalDateTime.now().toUiTime(),
                        isUser = true
                    ),
                    input = "",
                    isLoading = true
                )

                viewModelScope.launch {
                    try {
                        val fullResponse = sendQuestionUseCase(text)
                        val (_, answer) = parseThinkAndAnswer(fullResponse.text)
                        answer?.let {
                            _state.value = _state.value.copy(
                                messages = _state.value.messages + Message(
                                    it,
                                    time = LocalDateTime.now().toUiTime(),
                                    isUser = false
                                )
                            )
                        }
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(
                            messages = _state.value.messages + Message(
                                "Error: ${e.message}",
                                time = LocalDateTime.now().toUiTime(),
                                isUser = false
                            )
                        )
                    } finally {
                        _state.value = _state.value.copy(isLoading = false)
                    }
                }
            }

            ChatIntent.ErrorShown -> {
                _state.value = _state.value.copy(error = null)
            }
        }
    }

    private fun parseThinkAndAnswer(text: String): Pair<String?, String?> {
        val regex = "<think>([\\s\\S]*?)</think>".toRegex()
        val match = regex.find(text)
        return if (match != null) {
            val thinkPart = match.groupValues[1].trim()
            val answerPart = text.replace(regex, "").trim()
            Pair(thinkPart, answerPart)
        } else {
            Pair(null, text.trim())
        }
    }
}
