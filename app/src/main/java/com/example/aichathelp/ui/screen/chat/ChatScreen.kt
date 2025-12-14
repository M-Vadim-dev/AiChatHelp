package com.example.aichathelp.ui.screen.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aichathelp.R
import com.example.aichathelp.domain.model.ModelVendor
import com.example.aichathelp.ui.screen.chat.components.ChatHeader
import com.example.aichathelp.ui.screen.chat.components.HistoryPanel
import com.example.aichathelp.ui.screen.chat.components.MessageItem
import com.example.aichathelp.ui.screen.chat.components.SettingsDialog
import com.example.aichathelp.ui.screen.chat.components.SettingsPanel
import com.example.aichathelp.ui.screen.chat.model.ChatIntent
import com.example.aichathelp.ui.screen.chat.model.MessageUi
import com.example.aichathelp.ui.screen.chat.viewmodel.ChatViewModel
import com.example.aichathelp.ui.theme.AiChatHelpTheme
import com.example.aichathelp.ui.theme.RoyalBlue
import com.example.aichathelp.ui.util.toUiTime
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import java.time.LocalDateTime

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    var showSettingsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.messages.size, state.isLoading) {
        if (state.messages.isNotEmpty()) listState.animateScrollToItem(0)
    }

    ChatScreenContent(
        messages = state.messages,
        loading = state.isLoading,
        input = state.input,
        provider = state.settings.provider,
        useHistory = state.settings.useHistory,
        onUseHistoryChange = { viewModel.onIntent(ChatIntent.UseHistoryChanged(it)) },
        useSummaryCompression = state.settings.useSummaryCompression,
        onUseSummaryCompressionChange = {
            viewModel.onIntent(
                ChatIntent.UseSummaryCompressionChanged(
                    it
                )
            )
        },
        onInputChange = { viewModel.onIntent(ChatIntent.InputChanged(it)) },
        onSendClick = { viewModel.onIntent(ChatIntent.SendClicked) },
        onRetryClick = { viewModel.onIntent(ChatIntent.RetryClicked) },
        onClearChatClick = { viewModel.onIntent(ChatIntent.ClearChat) },
        onSettingsClick = { showSettingsDialog = true },
        onProviderChange = { viewModel.onIntent(ChatIntent.ProviderChanged(it)) },
        onMaxTokensChanged = { viewModel.onIntent(ChatIntent.MaxTokensChanged(it)) },
        listState = listState,
        modifier = modifier,
    )

    if (showSettingsDialog) {
        SettingsDialog(
            selectedMode = state.settings.currentPromptType,
            temperature = state.settings.temperature,
            topP = state.settings.topP,
            onModeSelected = { viewModel.onIntent(ChatIntent.PromptTypeChanged(it)) },
            onTemperatureChanged = { viewModel.onIntent(ChatIntent.TemperatureChanged(it)) },
            onTopPChanged = { viewModel.onIntent(ChatIntent.TopPChanged(it)) },
            onResetConfig = { viewModel.onIntent(ChatIntent.ResetConfigClicked) },
            onDismiss = { showSettingsDialog = false }
        )
    }
}

@Composable
private fun ChatScreenContent(
    modifier: Modifier = Modifier,
    messages: List<MessageUi>,
    loading: Boolean,
    input: String,
    provider: ModelVendor,
    useHistory: Boolean,
    onUseHistoryChange: (Boolean) -> Unit,
    useSummaryCompression: Boolean,
    onUseSummaryCompressionChange: (Boolean) -> Unit,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onRetryClick: (MessageUi) -> Unit,
    onClearChatClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProviderChange: (ModelVendor) -> Unit,
    onMaxTokensChanged: (Int) -> Unit,
    listState: LazyListState,
) {
    val hazeState = rememberHazeState(true)

    val focusManager = LocalFocusManager.current

    var showTokenPanel by rememberSaveable { mutableStateOf(false) }
    var showHistoryPanel by rememberSaveable { mutableStateOf(false) }
    var maxTokens by rememberSaveable { mutableFloatStateOf(provider.maxTokens.toFloat()) }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {

            val statusBarPadding = WindowInsets.statusBars
                .asPaddingValues()
                .calculateTopPadding()

            val headerHeight = 64.dp
            val fullHeaderHeight = statusBarPadding + headerHeight

            Box {
                Spacer(modifier = Modifier.height(fullHeaderHeight))

                val settingsHeight = 90.dp
                val settingsOffset by animateDpAsState(
                    if (showTokenPanel) fullHeaderHeight
                    else fullHeaderHeight - settingsHeight
                )

                SettingsPanel(
                    height = settingsHeight,
                    visible = showTokenPanel,
                    hazeState = hazeState,
                    maxTokens = maxTokens,
                    maxAllowed = provider.maxAllowedTokens,
                    onValueChange = {
                        maxTokens = it
                        onMaxTokensChanged(it.toInt())
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(y = settingsOffset)

                )

                val historyHeight = 240.dp
                val historyOffset by animateDpAsState(
                    if (showHistoryPanel) fullHeaderHeight
                    else fullHeaderHeight - historyHeight
                )

                HistoryPanel(
                    height = historyHeight,
                    visible = showHistoryPanel,
                    hazeState = hazeState,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(y = historyOffset),
                    onClearChat = onClearChatClick,
                    useHistory = useHistory,
                    onUseHistoryChange = onUseHistoryChange,
                    useSummaryCompression = useSummaryCompression,
                    onUseSummaryChange = onUseSummaryCompressionChange,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .hazeEffect(
                        hazeState,
                        HazeStyle(
                            blurRadius = 8.dp,
                            backgroundColor = colorScheme.background,
                            tint = HazeTint(colorScheme.background.copy(alpha = 0.8f))
                        )
                    )
            ) {
                ChatHeader(
                    currentProvider = provider,
                    onSettings = onSettingsClick,
                    onProviderSelected = onProviderChange,
                    onToggleTokenPanel = {
                        focusManager.clearFocus(force = true)

                        showHistoryPanel = false
                        showTokenPanel = !showTokenPanel
                    },
                    onToggleHistoryPanel = {
                        focusManager.clearFocus(force = true)

                        showTokenPanel = false
                        showHistoryPanel = !showHistoryPanel
                    },
                    modifier = Modifier
                        .zIndex(1f)
                        .align(Alignment.TopStart)
                        .windowInsetsPadding(WindowInsets.statusBars)
                )
            }

        },
        bottomBar = {
            MessageInput(
                input = input,
                onInputChange = onInputChange,
                onSendClick = onSendClick,
                sendingDisabled = loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .hazeEffect(
                        hazeState,
                        HazeStyle(
                            blurRadius = 8.dp,
                            backgroundColor = colorScheme.background,
                            tint = HazeTint(colorScheme.background.copy(alpha = 0.8f))
                        )
                    )
                    .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 4.dp),
                onFocusChanged = { focused ->
                    if (focused) {
                        showTokenPanel = false
                        showHistoryPanel = false
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to colorScheme.tertiaryContainer,
                            0.4f to colorScheme.background,
                            1.0f to colorScheme.background
                        )
                    )
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState)
            ) {
                if (messages.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier.size(64.dp),
                            painter = painterResource(R.drawable.ic_ai_chat),
                            contentDescription = null,
                            tint = RoyalBlue
                        )
                        Text(
                            text = stringResource(R.string.chat_title),
                            fontSize = 22.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        state = listState,
                        verticalArrangement = Arrangement.Bottom,
                        contentPadding = innerPadding,
                        reverseLayout = true
                    ) {
                        if (loading) item { IndicatorBubble(visible = loading) }

                        items(items = messages.reversed(), key = { it.id }) { message ->
                            MessageItem(
                                message = message,
                                onRetry = onRetryClick,
                                isNew = true,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageInput(
    modifier: Modifier = Modifier,
    input: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    sendingDisabled: Boolean,
    onFocusChanged: (Boolean) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = input,
            onValueChange = onInputChange,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged {
                    isFocused = it.isFocused
                    onFocusChanged(it.isFocused)
                },
            placeholder = { if (!isFocused) Text(stringResource(R.string.message_placeholder)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    onSendClick()
                    focusManager.clearFocus()
                }
            ),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surface.copy(alpha = 0.8f),
                unfocusedContainerColor = colorScheme.surface.copy(alpha = 0.8f),
                focusedPlaceholderColor = colorScheme.onSurface,
                unfocusedPlaceholderColor = colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface,

                ),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_add_circle),
                    contentDescription = null,
                    tint = colorScheme.surfaceVariant,
                    modifier = Modifier.size(34.dp)
                )
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isFocused && input.isEmpty()) {
                        IconButton(onClick = { onInputChange("") }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_paperclip),
                                contentDescription = null,
                                tint = colorScheme.onSurface,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    val iconPainter = when {
                        !isFocused && input.isEmpty() -> painterResource(R.drawable.ic_sound_wave_circle_filled)
                        isFocused && input.isEmpty() -> painterResource(R.drawable.ic_send_circle)
                        isFocused && input.isNotEmpty() -> painterResource(R.drawable.ic_send_circle)
                        else -> painterResource(R.drawable.ic_sound_wave_circle_filled)
                    }

                    val iconTint = when {
                        !isFocused && input.isEmpty() -> colorScheme.primary
                        isFocused && input.isNotEmpty() -> colorScheme.primary
                        else -> colorScheme.surfaceVariant
                    }

                    IconButton(
                        enabled = input.isNotBlank() && !sendingDisabled,
                        onClick = onSendClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = iconPainter,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun IndicatorBubble(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .background(colorScheme.surface, RoundedCornerShape(18.dp))
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
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenContentPreview() {
    val mockMessages = listOf(
        MessageUi(
            id = "1",
            text = "Привет! Чем могу помочь?",
            isUser = false,
            isSending = true,
            time = LocalDateTime.now().minusMinutes(5).toUiTime(),
        ),
        MessageUi(
            id = "2",
            text = "Объясни, как работает ViewModel.",
            isUser = true,
            isSending = true,
            time = LocalDateTime.now().minusMinutes(4).toUiTime(),
        ),
        MessageUi(
            id = "3",
            text = "ViewModel хранит состояние UI и переживает пересоздание экранов.",
            isUser = false,
            isSending = true,
            time = LocalDateTime.now().minusMinutes(3).toUiTime(),
            isTokenLimitExceeded = true,
        ),
        MessageUi(
            id = "4",
            text = "Хочешь пример с LiveData или StateFlow?",
            isUser = false,
            isSending = true,
            time = LocalDateTime.now().minusMinutes(2).toUiTime(),
        ),
        MessageUi(
            id = "5",
            text = "StateFlow",
            isUser = true,
            isSending = true,
            time = LocalDateTime.now().minusMinutes(1).toUiTime(),
        ),
        MessageUi(
            id = "6",
            text = "Ошибка",
            isUser = false,
            time = LocalDateTime.now().minusMinutes(1).toUiTime(),
            error = ""
        ),
    )

    AiChatHelpTheme {
        ChatScreenContent(
            messages = mockMessages,
            loading = true,
            input = "",
            provider = ModelVendor.PERPLEXITY,
            onInputChange = {},
            onSendClick = {},
            onRetryClick = {},
            onClearChatClick = {},
            onSettingsClick = {},
            onProviderChange = {},
            onMaxTokensChanged = {},
            listState = rememberLazyListState(),
            useHistory = true,
            onUseHistoryChange = {},
            useSummaryCompression = true,
            onUseSummaryCompressionChange = {},
        )
    }
}