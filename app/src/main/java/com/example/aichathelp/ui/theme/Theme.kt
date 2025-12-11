package com.example.aichathelp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DodgerBlue,
    onPrimary = White,
    primaryContainer = RoyalBlue,
    onPrimaryContainer = White,

    secondary = SkyBlue,
    onSecondary = White,
    secondaryContainer = DodgerBlue,

    tertiaryContainer = DarkSlateGray400,

    background = DarkSlateGray300,
    surface = SlateGray,
    surfaceVariant = Slate,

    onSurface = Lavender,
    onBackground = Lavender,

    error = Pink,
    errorContainer = Tomato,

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

    tertiaryContainer = Lavender,

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