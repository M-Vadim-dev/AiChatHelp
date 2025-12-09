package com.example.aichathelp.ui.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.aichathelp.R
import com.example.aichathelp.domain.model.PromptType
import com.example.aichathelp.ui.theme.AiChatHelpTheme

@Composable
fun SettingsDialog(
    selectedMode: PromptType,
    temperature: Double,
    topP: Double,
    onModeSelected: (PromptType) -> Unit,
    onTemperatureChanged: (Double) -> Unit,
    onTopPChanged: (Double) -> Unit,
    onResetConfig: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true,
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.TopCenter
        ) {

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = colorScheme.background,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = 32.dp,
                        bottom = 24.dp,
                        start = 24.dp,
                        end = 24.dp
                    ),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Настройки AI",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "System prompt:",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ModeButton(
                            mode = PromptType.PROFESSIONAL,
                            selectedMode = selectedMode,
                            onModeSelected = onModeSelected,
                            text = "Профессиональный",
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        ModeButton(
                            mode = PromptType.CREATIVE,
                            selectedMode = selectedMode,
                            onModeSelected = onModeSelected,
                            text = "Зумерский 🔥",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Температура: ${"%.1f".format(temperature)}",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Slider(
                        value = temperature.toFloat(),
                        onValueChange = { onTemperatureChanged(it.toDouble()) },
                        valueRange = 0.2f..1.0f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = colorScheme.primary,
                            activeTickColor = colorScheme.onPrimary.copy(alpha = 0.2f),
                            activeTrackColor = colorScheme.primary,
                            inactiveTrackColor = colorScheme.surface,
                            inactiveTickColor = colorScheme.onPrimary,
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Top P: ${"%.1f".format(topP)}",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Slider(
                        value = topP.toFloat(),
                        onValueChange = { onTopPChanged(it.toDouble()) },
                        valueRange = 0.2f..1.0f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = colorScheme.primary,
                            activeTickColor = colorScheme.onPrimary.copy(alpha = 0.2f),
                            activeTrackColor = colorScheme.primary,
                            inactiveTrackColor = colorScheme.surface,
                            inactiveTickColor = colorScheme.onPrimary,
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onResetConfig,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Сбросить настройки")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = onDismiss) { Text("Отмена") }
                        TextButton(onClick = onDismiss) { Text("Сохранить") }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .offset(y = (-24).dp)
                    .zIndex(1f)
                    .clip(CircleShape)
                    .background(colorScheme.background)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun ModeButton(
    mode: PromptType,
    selectedMode: PromptType,
    onModeSelected: (PromptType) -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { onModeSelected(mode) },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (mode == selectedMode) colorScheme.primaryContainer else colorScheme.surface.copy(
                alpha = 0.8f
            ),
            contentColor = if (mode == selectedMode) colorScheme.onPrimaryContainer else colorScheme.onSurface,
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
    ) {
        Text(text = text, fontSize = 12.sp)
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsDialogPreview() {
    AiChatHelpTheme {
        SettingsDialog(
            selectedMode = PromptType.PROFESSIONAL,
            temperature = 0.6,
            topP = 0.9,
            onModeSelected = {},
            onTemperatureChanged = {},
            onTopPChanged = {},
            onResetConfig = {},
            onDismiss = {}
        )
    }
}