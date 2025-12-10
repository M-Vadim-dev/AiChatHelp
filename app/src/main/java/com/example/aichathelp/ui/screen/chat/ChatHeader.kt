package com.example.aichathelp.ui.screen.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.aichathelp.R
import com.example.aichathelp.domain.model.ModelVendor

@Composable
fun ChatHeader(
    currentProvider: ModelVendor,
    onClearChat: () -> Unit,
    isChatEmpty: Boolean,
    onSettings: () -> Unit,
    onProviderSelected: (ModelVendor) -> Unit,
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

        ProviderSelector(
            currentProvider = currentProvider,
            onProviderSelected = onProviderSelected,
            modifier = Modifier.weight(1f)
        )

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

@Composable
fun ProviderSelector(
    currentProvider: ModelVendor,
    onProviderSelected: (ModelVendor) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "AI:",
            style = MaterialTheme.typography.titleMedium
        )

        Box(
            modifier = Modifier.clip(RoundedCornerShape(4.dp))
        ) {
            TextButton(
                onClick = { expanded = true },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = currentProvider.displayName,
                    color = colorScheme.primary
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(colorScheme.surface)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                ModelVendor.entries.forEach { provider ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = provider.displayName,
                                color = colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onProviderSelected(provider)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}