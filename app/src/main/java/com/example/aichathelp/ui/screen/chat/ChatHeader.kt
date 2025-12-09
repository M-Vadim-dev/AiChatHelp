package com.example.aichathelp.ui.screen.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.aichathelp.R

@Composable
fun ChatHeader(
    onClearChat: () -> Unit,
    isChatEmpty: Boolean,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_ai_chat),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.chat_title),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "Online",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        IconButton(onClick = onClearChat, enabled = !isChatEmpty) {
            Icon(
                painter = painterResource(R.drawable.ic_trash_outline),
                contentDescription = null,
                tint = if (isChatEmpty) colorScheme.surfaceVariant else colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(onClick = onSettings) {
            Icon(
                painter = painterResource(R.drawable.ic_more),
                contentDescription = null
            )
        }
    }
}