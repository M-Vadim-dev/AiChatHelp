package com.example.aichathelp.ui.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aichathelp.R
import com.example.aichathelp.domain.model.Message
import com.example.aichathelp.ui.theme.Lavender
import com.example.aichathelp.ui.theme.RoyalBlue

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
        verticalArrangement = Arrangement.Bottom

    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            reverseLayout = true
        ) {

            if (loading) item { IndicatorBubble() }

            items(messages.reversed()) { message -> MessageBubble(message) }
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
private fun MessageBubble(message: Message) {

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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .background(if (message.isUser) Lavender else RoyalBlue, shape)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .widthIn(max = 300.dp)
        ) {
            Text(
                text = message.text,
                fontSize = 16.sp,
                color = if (message.isUser) Color.Black else Color.White
            )
        }
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
fun ChatScreenContentPreview() {
    val mockMessages = listOf(
        Message(text = "Привет! Чем могу помочь?", isUser = false),
        Message(text = "Объясни, как работает ViewModel.", isUser = true),
        Message(
            text = "ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.ViewModel хранит состояние UI и переживает пересоздание экранов.",
            isUser = false
        ),
        Message(text = "Спасибо!", isUser = true),
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
