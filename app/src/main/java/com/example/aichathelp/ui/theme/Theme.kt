package com.example.aichathelp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DodgerBlue,
    onPrimary = Color.White,
    primaryContainer = RoyalBlue,
    onPrimaryContainer = Color.White,

    secondary = SkyBlue,
    onSecondary = Color.White,
    secondaryContainer = DodgerBlue,

    background = DarkBlue,
    surface = MidnightBlue,
    surfaceVariant = Slate,

    onSurface = Lavender,
    onBackground = Lavender,

    error = Pink,
    errorContainer = Crimson,

    outline = Slate,
    outlineVariant = Lavender,
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = White,
    primaryContainer = RoyalBlue,
    onPrimaryContainer = White,

    secondary = DodgerBlue,
    onSecondary = White,
    secondaryContainer = Lavender,
    onSecondaryContainer = MidnightBlue,

    background = White,
    onBackground = DarkBlue,

    surface = Lavender,
    onSurface = MidnightBlue,
    surfaceVariant = Slate,

    error = Crimson,
    errorContainer = Pink,

    outline = Lavender,
    outlineVariant = Slate
)

@Composable
fun AiChatHelpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) DarkColorScheme
        else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}