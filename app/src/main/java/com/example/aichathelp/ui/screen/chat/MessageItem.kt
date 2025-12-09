package com.example.aichathelp.ui.screen.chat

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aichathelp.R
import com.example.aichathelp.domain.model.MessageType
import com.example.aichathelp.ui.screen.chat.model.MessageUi

@Composable
fun MessageItem(
    message: MessageUi,
    isNew: Boolean = false,
    onRetry: ((MessageUi) -> Unit)? = null,
) {
    var hasAnimated by remember(message.id) { mutableStateOf(false) }

    LaunchedEffect(message.id) {
        if (isNew && !hasAnimated) {
            hasAnimated = true
        }
    }

    val targetScale = if (hasAnimated || message.isSending) 1f else 0.5f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    val alpha by animateFloatAsState(
        targetValue = if (hasAnimated || message.isSending) 1f else 0f,
        animationSpec = tween()
    )

    val initialOffsetX = if (message.isUser) 40.dp else (-40).dp
    val initialOffsetY = 20.dp

    val offsetX by animateDpAsState(
        targetValue = if (hasAnimated || message.isSending) 0.dp else initialOffsetX,
        animationSpec = tween(easing = FastOutSlowInEasing)
    )

    val offsetY by animateDpAsState(
        targetValue = if (hasAnimated || message.isSending) 0.dp else initialOffsetY,
        animationSpec = tween(easing = FastOutSlowInEasing)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .alpha(alpha)
            .offset(x = offsetX, y = offsetY),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            Avatar(isUser = false)
            Spacer(modifier = Modifier.width(4.dp))
        }

        Row(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    transformOrigin = TransformOrigin(0f, 1f)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MessageContent(message)
            if (message.type is MessageType.Error && onRetry != null) {
                RetryButton(message, onRetry)
            }
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(4.dp))
            Avatar(isUser = true)
        }
    }
}

@Composable
private fun MessageContent(message: MessageUi) {
    Box(
        modifier = Modifier
            .background(color = getBackgroundColor(message), shape = getMessageShape(message))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .widthIn(max = 275.dp)
    ) {
        when (message.type) {
            is MessageType.Answer,
            is MessageType.Question,
            is MessageType.Error -> MessageText(message)
        }
    }
}

private fun getMessageShape(message: MessageUi) = if (message.isUser)
    RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
else
    RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 4.dp)

@Composable
private fun getBackgroundColor(message: MessageUi): Color = when {
    message.isUser -> colorScheme.surface.copy(alpha = 0.8f)
    message.type is MessageType.Error -> colorScheme.errorContainer
    message.type is MessageType.Question -> colorScheme.primaryContainer
    else -> colorScheme.primaryContainer
}

@Composable
private fun getTextColor(message: MessageUi): Color = when {
    message.isUser -> colorScheme.onSurface
    message.type is MessageType.Error -> colorScheme.error
    else -> colorScheme.onPrimaryContainer
}

@Composable
private fun Avatar(isUser: Boolean) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(if (isUser) colorScheme.surface else colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isUser) {
            Icon(
                painter = painterResource(R.drawable.ic_person),
                contentDescription = null,
                tint = colorScheme.surfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.ic_ai_chat),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun MessageText(message: MessageUi) {
    Column {
        Text(
            text = message.text,
            color = getTextColor(message),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = message.time,
            fontSize = 10.sp,
            color = getTextColor(message).copy(alpha = 0.5f),
            modifier = Modifier.align(if (message.isUser) Alignment.Start else Alignment.End)
        )
    }
}

@Composable
private fun RetryButton(message: MessageUi, onRetry: (MessageUi) -> Unit) {
    Spacer(modifier = Modifier.width(4.dp))
    IconButton(onClick = { onRetry(message) }, modifier = Modifier.size(28.dp)) {
        Icon(
            painter = painterResource(R.drawable.ic_refresh),
            contentDescription = null,
            tint = colorScheme.error
        )
    }
}