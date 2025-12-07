package com.example.aichathelp.ui.screen.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aichathelp.domain.model.PromptType
import com.example.aichathelp.ui.theme.BlueViolet
import com.example.aichathelp.ui.theme.Lavender

@Composable
fun ModeSelection(
    selectedMode: PromptType,
    onModeSelected: (PromptType) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Cтиль общения",
            style = MaterialTheme.typography.titleMedium,
        )
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
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (mode == selectedMode) BlueViolet else Lavender,
            contentColor = if (mode == selectedMode) Color.White else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text)
    }
}
