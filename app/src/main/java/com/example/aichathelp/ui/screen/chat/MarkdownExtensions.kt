package com.example.aichathelp.ui.screen.chat

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import java.util.regex.Pattern

@Composable
fun MarkdownText(text: String, color: Color, fontSize: Float = 16f) {
    Text(
        text = text.toMarkdownAnnotatedString(),
        color = color,
        fontSize = fontSize.sp
    )
}

private fun String.toMarkdownAnnotatedString(): AnnotatedString {
    val annotated = buildAnnotatedString {
        var remaining = this@toMarkdownAnnotatedString

        val thinkPattern = Pattern.compile("<think>(.*?)</think>", Pattern.DOTALL)
        val thinkMatcher = thinkPattern.matcher(remaining)
        var lastIndex = 0
        while (thinkMatcher.find()) {
            append(remaining.substring(lastIndex, thinkMatcher.start()))
            val thinkText = thinkMatcher.group(1)
            pushStyle(SpanStyle(fontStyle = FontStyle.Italic, color = Color.Gray))
            if (thinkText != null) append(thinkText)

            pop()
            lastIndex = thinkMatcher.end()
        }
        remaining = if (lastIndex < remaining.length) remaining.substring(lastIndex) else ""

        val pattern = Pattern.compile("(\\*\\*.*?\\*\\*|\\*.*?\\*|__.*?__)")
        val matcher = pattern.matcher(remaining)
        lastIndex = 0
        while (matcher.find()) {
            append(remaining.substring(lastIndex, matcher.start()))

            val match = matcher.group()
            val cleanText = match.substring(
                when {
                    match.startsWith("**") -> 2
                    match.startsWith("*") -> 1
                    match.startsWith("__") -> 2
                    else -> 0
                },
                match.length - when {
                    match.endsWith("**") -> 2
                    match.endsWith("*") -> 1
                    match.endsWith("__") -> 2
                    else -> 0
                }
            )

            val style = when {
                match.startsWith("**") -> SpanStyle(fontWeight = FontWeight.Bold)
                match.startsWith("*") -> SpanStyle(fontStyle = FontStyle.Italic)
                match.startsWith("__") -> SpanStyle(textDecoration = TextDecoration.Underline)
                else -> SpanStyle()
            }

            pushStyle(style)
            append(cleanText)
            pop()
            lastIndex = matcher.end()
        }

        if (lastIndex < remaining.length) {
            append(remaining.substring(lastIndex))
        }
    }
    return annotated
}
