package com.example.aichathelp.ui.screen.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.aichathelp.R
import com.example.aichathelp.domain.model.ModelVendor

@Composable
fun ChatHeader(
    currentProvider: ModelVendor,
    onSettings: () -> Unit,
    onProviderSelected: (ModelVendor) -> Unit,
    onToggleTokenPanel: () -> Unit,
    onToggleHistoryPanel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))

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

        Spacer(modifier = Modifier.width(4.dp))

        ProviderSelector(
            currentProvider = currentProvider,
            onProviderSelected = onProviderSelected,
            modifier = Modifier.widthIn(min = 122.dp, max = 144.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onToggleHistoryPanel) {
            Icon(
                painter = painterResource(R.drawable.ic_settings_chat),
                contentDescription = null,
                tint = colorScheme.onSurface,
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(onClick = onToggleTokenPanel) {
            Icon(
                painter = painterResource(R.drawable.ic_settings_horizontal),
                contentDescription = null,
                tint = colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(onClick = onSettings) {
            Icon(
                painter = painterResource(R.drawable.ic_settings),
                contentDescription = null,
                tint = colorScheme.onSurface,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun ProviderSelector(
    currentProvider: ModelVendor,
    onProviderSelected: (ModelVendor) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = currentProvider.displayName,
            enabled = false,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.clickable { expanded = true },
            textStyle = typography.titleMedium.copy(color = colorScheme.primary),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = colorScheme.background,
                focusedContainerColor = colorScheme.background,
                disabledContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
                Icon(
                    painter = if (expanded) painterResource(R.drawable.ic_arrow_up)
                    else painterResource(R.drawable.ic_arrow_down),
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .clickable { expanded = !expanded }
                )
            },
            singleLine = true
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.background(colorScheme.background.copy(alpha = 0.8f))
        ) {
            ModelVendor.entries.forEachIndexed { index, provider ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = provider.displayName,
                            color = if (currentProvider == provider) colorScheme.primary
                            else colorScheme.onSurface,
                            style = typography.bodyLarge,
                        )
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = colorScheme.onSurface,
                        disabledTextColor = colorScheme.surface
                    ),
                    onClick = {
                        onProviderSelected(provider)
                        expanded = false
                    }
                )
                if (index < ModelVendor.entries.lastIndex) {
                    HorizontalDivider(
                        color = colorScheme.onSurface.copy(alpha = 0.1f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}