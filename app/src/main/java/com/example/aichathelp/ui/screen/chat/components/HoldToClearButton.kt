package com.example.aichathelp.ui.screen.chat.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.aichathelp.R
import com.example.aichathelp.ui.theme.Pink
import com.example.aichathelp.ui.theme.Tomato
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun HoldToClearButton(
    modifier: Modifier = Modifier,
    onClear: () -> Unit,
    duration: Long = 3000L
) {
    val progress = remember { Animatable(0f) }

    var boxWidthPx by remember { mutableStateOf(0f) }
    var iconWidthPx by remember { mutableStateOf(0f) }
    var textWidthPx by remember { mutableStateOf(0f) }

    val density = LocalDensity.current
    val spacerWidthPx = with(density) { 8.dp.toPx() }

    Box(
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray.copy(alpha = 0.2f))
            .onGloballyPositioned {
                boxWidthPx = it.size.width.toFloat()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        coroutineScope {
                            val job = launch {
                                progress.animateTo(
                                    1f,
                                    animationSpec = tween(duration.toInt())
                                )
                                if (progress.value >= 1f) onClear()
                            }

                            try {
                                awaitRelease()
                                job.cancel()
                                progress.animateTo(0f, tween(300))
                            } catch (_: Exception) {
                                job.cancel()
                                progress.snapTo(0f)
                            }
                        }
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(colorScheme.surface)
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(with(density) { (boxWidthPx * progress.value).toDp() })
                .background(Pink)
        )

        val filledPx = boxWidthPx * progress.value

        val contentWidthPx =
            iconWidthPx + spacerWidthPx + textWidthPx

        val contentStartPx =
            ((boxWidthPx - contentWidthPx) / 2f).coerceAtLeast(0f)

        val contentFilledPx =
            (filledPx - contentStartPx).coerceAtLeast(0f)

        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier.onGloballyPositioned {
                    iconWidthPx = it.size.width.toFloat()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = null,
                    tint = colorScheme.onSurface
                )

                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = null,
                    tint = Tomato,
                    modifier = Modifier.drawWithContent {
                        val iconFillPx =
                            contentFilledPx.coerceIn(0f, iconWidthPx)

                        clipRect(right = iconFillPx) {
                            this@drawWithContent.drawContent()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier.onGloballyPositioned {
                    textWidthPx = it.size.width.toFloat()
                }
            ) {
                Text(
                    text = "Hold to Clear",
                    style = typography.titleMedium,
                    color = colorScheme.onSurface
                )

                Text(
                    text = "Hold to Clear",
                    style = typography.titleMedium,
                    color = Tomato,
                    modifier = Modifier.drawWithContent {
                        val textFillPx =
                            (contentFilledPx - iconWidthPx - spacerWidthPx)
                                .coerceIn(0f, textWidthPx)

                        clipRect(right = textFillPx) {
                            this@drawWithContent.drawContent()
                        }
                    }
                )
            }
        }
    }
}
