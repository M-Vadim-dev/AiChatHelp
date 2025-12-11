package com.example.aichathelp.ui.screen.chat.components

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
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
import com.example.aichathelp.ui.theme.MediumSeaGreen

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
                        style = typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "System prompt:",
                        style = typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
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
                        text = "\uD83C\uDF21\uFE0FТемпература (creativity): ${"%.1f".format(temperature)}",
                        style = typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    CustomGradientSlider(
                        value = temperature.toFloat(),
                        onValueChange = { onTemperatureChanged(it.toDouble()) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "\uD83C\uDFB2 Top P (diversity): ${"%.1f".format(topP)}",
                        style = typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Slider(
                        value = topP.toFloat(),
                        onValueChange = { onTopPChanged(it.toDouble()) },
                        valueRange = 0.2f..1.0f,
                        steps = 0,
                        colors = SliderDefaults.colors(
                            thumbColor = colorScheme.secondary,
                            activeTickColor = colorScheme.onPrimary.copy(alpha = 0.2f),
                            activeTrackColor = colorScheme.secondary,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomGradientSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0.2f..1.4f,
    gradientColors: List<Color> = listOf(colorScheme.secondary, MediumSeaGreen, colorScheme.error),
) {
    val min = valueRange.start
    val max = valueRange.endInclusive
    val t = ((value - min) / (max - min)).coerceIn(0f, 1f)

    val trackHeight = 10.dp
    val thumbWidth = 6.dp

    val density = LocalDensity.current
    val trackPx = with(density) { trackHeight.toPx() }
    val halfThumbPx = with(density) { (thumbWidth / 2).toPx() }

    val gradient = Brush.horizontalGradient(gradientColors)

    val thumbColor = when {
        t < 1f / 3 -> lerp(gradientColors[0], gradientColors[1], t * 3)
        t < 2f / 3 -> lerp(gradientColors[1], gradientColors[2], (t - 1f/3) * 3)
        else -> gradientColors.last()
    }

    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = min..max,
        modifier = Modifier
            .height(50.dp)
            .drawBehind {

                val centerY = size.height / 2f

                val trackEnd = size.width - halfThumbPx
                val trackWidth = trackEnd - halfThumbPx

                val activeWidth = trackWidth * t

                drawRoundRect(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    topLeft = Offset(halfThumbPx, centerY - trackPx / 2),
                    size = Size(trackWidth, trackPx),
                    cornerRadius = CornerRadius(trackPx / 2)
                )

                drawRoundRect(
                    brush = gradient,
                    topLeft = Offset(halfThumbPx, centerY - trackPx / 2),
                    size = Size(activeWidth, trackPx),
                    cornerRadius = CornerRadius(trackPx / 2)
                )
            },
        colors = SliderDefaults.colors(
            thumbColor = Color.Transparent,
            activeTrackColor = Color.Transparent,
            inactiveTrackColor = Color.Transparent
        ),
        thumb = {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(thumbColor, CircleShape)
            )
        }
    )
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