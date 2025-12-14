package com.example.aichathelp.ui.screen.chat.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.aichathelp.R
import com.example.aichathelp.ui.theme.AiChatHelpTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect


@Composable
fun SettingsPanel(
    visible: Boolean,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    height: Dp,
    maxTokens: Float,
    maxAllowed: Int,
    onValueChange: (Float) -> Unit,
) {
    CustomTopPanel(
        visible = visible,
        height = height,
        hazeState = hazeState,
        modifier = modifier,
        backgroundBrush = Brush.verticalGradient(
            colorStops = arrayOf(
                0.0f to colorScheme.tertiaryContainer,
                0.1f to colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                0.3f to Color.Transparent,
                1.0f to Color.Transparent
            )
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Max tokens: ${maxTokens.toInt()}",
                color = colorScheme.onBackground
            )
            Spacer(Modifier.width(16.dp))
            Slider(
                value = maxTokens,
                onValueChange = onValueChange,
                valueRange = 50f..maxAllowed.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = colorScheme.secondary,
                    activeTickColor = colorScheme.secondary,
                    activeTrackColor = colorScheme.secondary,
                    inactiveTrackColor = colorScheme.surface,
                    inactiveTickColor = colorScheme.surface,
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun HistoryPanel(
    modifier: Modifier = Modifier,
    height: Dp,
    visible: Boolean,
    hazeState: HazeState,
    useHistory: Boolean,
    onUseHistoryChange: (Boolean) -> Unit,
    useSummaryCompression: Boolean,
    onUseSummaryChange: (Boolean) -> Unit,
    onClearChat: () -> Unit,
) {
    CustomTopPanel(
        visible = visible,
        height = height,
        hazeState = hazeState,
        modifier = modifier,
        backgroundBrush = Brush.verticalGradient(
            colorStops = arrayOf(
                0.0f to colorScheme.tertiaryContainer,
                0.05f to colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                0.09f to Color.Transparent,
                1.0f to Color.Transparent
            )
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            SettingHistoryRow(
                icon = painterResource(R.drawable.ic_conversation),
                title = "Use chat history",
                description = "Keep conversation history to provide context for AI responses.",
                action = {
                    Switch(
                        checked = useHistory,
                        onCheckedChange = onUseHistoryChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorScheme.onPrimary,
                            checkedTrackColor = colorScheme.primary,
                            uncheckedThumbColor = colorScheme.onSurface.copy(alpha = 0.2f),
                            uncheckedTrackColor = colorScheme.surface,
                            uncheckedBorderColor = colorScheme.onSurface.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.scale(0.8f)
                    )
                }
            )

            SettingHistoryRow(
                icon = painterResource(R.drawable.ic_compress),
                title = "Compress history",
                description = "Summarize previous messages to reduce context size while keeping important information.",
                action = {
                    Switch(
                        checked = useSummaryCompression,
                        onCheckedChange = onUseSummaryChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorScheme.onPrimary,
                            checkedTrackColor = colorScheme.primary,
                            uncheckedThumbColor = colorScheme.onSurface.copy(alpha = 0.2f),
                            uncheckedTrackColor = colorScheme.surface,
                            uncheckedBorderColor = colorScheme.onSurface.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.scale(0.8f)
                    )
                }
            )

            SettingHistoryRow(
                icon = painterResource(R.drawable.ic_auto_delete_outline),
                title = "Clear chat",
                description = "Delete all previous messages from history.",
                action = {
                    HoldToClearButton(
                        onClear = onClearChat,
                        modifier = Modifier.size(48.dp).weight(1f)
                    )
                }
            )
        }
    }
}

@Composable
private fun SettingHistoryRow(
    icon: Painter,
    title: String,
    description: String,
    action: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = typography.titleMedium,
                color = colorScheme.onBackground
            )

            Text(
                text = description,
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        action()
    }
}

@Composable
private fun CustomTopPanel(
    modifier: Modifier = Modifier,
    visible: Boolean,
    height: Dp,
    hazeState: HazeState,
    backgroundBrush: Brush,
    content: @Composable ColumnScope.() -> Unit,
) {
    val offsetY by animateDpAsState(targetValue = if (visible) 0.dp else -height)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .offset(y = offsetY)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                clip = false
            )
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .hazeEffect(
                hazeState,
                HazeStyle(
                    blurRadius = 8.dp,
                    backgroundColor = colorScheme.background,
                    tint = HazeTint(colorScheme.background.copy(alpha = 0.8f))
                )
            )
            .background(backgroundBrush)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSettingsPanel() {
    val haze = remember { HazeState() }

    AiChatHelpTheme {
        Column(modifier = Modifier.background(colorScheme.background)) {
            SettingsPanel(
                visible = true,
                hazeState = haze,
                height = 90.dp,
                maxTokens = 600f,
                maxAllowed = 1000,
                onValueChange = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHistoryPanel() {
    val haze = remember { HazeState() }

    AiChatHelpTheme {
        Column(modifier = Modifier.background(colorScheme.background)) {
            HistoryPanel(
                visible = true,
                hazeState = haze,
                height = 230.dp,
                useHistory = true,
                onUseHistoryChange = {},
                useSummaryCompression = false,
                onUseSummaryChange = {},
                onClearChat = {},
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewHistoryPanelDark() {
    val haze = remember { HazeState() }

    AiChatHelpTheme {
        Column(modifier = Modifier.background(colorScheme.background)) {
            HistoryPanel(
                visible = true,
                hazeState = haze,
                height = 240.dp,
                useHistory = true,
                onUseHistoryChange = {},
                useSummaryCompression = false,
                onUseSummaryChange = {},
                onClearChat = {},
            )
        }
    }
}
