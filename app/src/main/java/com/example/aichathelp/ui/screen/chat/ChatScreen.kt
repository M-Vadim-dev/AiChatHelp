package com.example.aichathelp.ui.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aichathelp.R
import com.example.aichathelp.domain.model.Message
import com.example.aichathelp.ui.theme.Lavender
import com.example.aichathelp.ui.theme.RoyalBlue
import com.example.aichathelp.ui.util.toUiTime
import java.time.LocalDateTime

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size, state.isLoading) {
        if (state.messages.isNotEmpty()) listState.animateScrollToItem(0)

    }

    ChatScreenContent(
        messages = state.messages,
        loading = state.isLoading,
        input = state.input,
        onInputChange = { viewModel.onIntent(ChatIntent.InputChanged(it)) },
        onSendClick = { viewModel.onIntent(ChatIntent.SendClicked) },
        listState = listState,
        modifier = modifier,
    )
}

@Composable
private fun ChatScreenContent(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    loading: Boolean,
    input: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    listState: LazyListState,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Outlined.Face,
                        contentDescription = null,
                        tint = RoyalBlue
                    )
                    Text(
                        text = stringResource(R.string.empty_chat_text),
                        fontSize = 22.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    reverseLayout = true
                ) {
                    if (loading) item { IndicatorBubble() }

                    items(messages.reversed()) { message -> MessageRow(message) }

                    item {
                        Spacer(
                            modifier = Modifier
                                .fillParentMaxHeight()

                        )
                    }
                }
            }
        }

        MessageInput(
            input = input,
            onInputChange = onInputChange,
            onSendClick = onSendClick,
            sendingDisabled = loading,
        )
    }
}

@Composable
private fun MessageInput(
    input: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    sendingDisabled: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = input,
            onValueChange = onInputChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(R.string.message_placeholder)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Lavender,
                unfocusedContainerColor = Lavender,
                unfocusedPlaceholderColor = Color(0xFF666666),
                focusedPlaceholderColor = Color(0xFF444444),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        IconButton(
            enabled = input.isNotBlank() && !sendingDisabled,
            onClick = onSendClick,
            modifier = Modifier
                .padding(start = 4.dp)
                .background(
                    color = if (input.isNotBlank() && !sendingDisabled) RoyalBlue else Lavender,
                    shape = CircleShape
                )
                .size(54.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                tint = if (input.isNotBlank()) Color.White else Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun MessageRow(message: Message) {

    val shape = if (message.isUser)
        RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = 18.dp,
            bottomEnd = 4.dp
        )
    else
        RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomEnd = 18.dp,
            bottomStart = 4.dp
        )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {

        if (!message.isUser) {
            Avatar(isUser = false)
            Spacer(modifier = Modifier.width(6.dp))
        }

        Box(
            modifier = Modifier
                .background(if (message.isUser) Lavender else RoyalBlue, shape)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .widthIn(max = 275.dp)
        ) {
            Column {
                MarkdownText(
                    text = message.text,
                    color = if (message.isUser) Color.Black else Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message.time,
                        fontSize = 12.sp,
                        color = if (message.isUser) Color.DarkGray else Color.LightGray
                    )
                }
            }
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(6.dp))
            Avatar(isUser = true)
        }
    }
}

@Composable
private fun Avatar(isUser: Boolean) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(if (isUser) Lavender else RoyalBlue, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isUser) Icons.Default.Person else Icons.Outlined.Face,
            contentDescription = null,
            tint = if (isUser) Color.DarkGray else Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}


@Composable
private fun IndicatorBubble() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .background(Lavender, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenContentPreview() {
    val mockMessages = listOf(
        Message(
            text = "Привет! Чем могу помочь?",
            isUser = false,
            time = LocalDateTime.now().minusMinutes(5).toUiTime()
        ),
        Message(
            text = "Объясни, как работает ViewModel.",
            isUser = true,
            time = LocalDateTime.now().minusMinutes(4).toUiTime(),
        ),
        Message(
            text = "ViewModel хранит состояние UI и переживает пересоздание экранов.",
            isUser = false,
            time = LocalDateTime.now().minusMinutes(3).toUiTime()
        ),
        Message(
            text = "Спасибо!",
            isUser = true,
            time = LocalDateTime.now().minusMinutes(2).toUiTime(),
        )
    )

    ChatScreenContent(
        messages = mockMessages,
        loading = true,
        input = "",
        onInputChange = {},
        onSendClick = {},
        listState = rememberLazyListState()
    )
}
