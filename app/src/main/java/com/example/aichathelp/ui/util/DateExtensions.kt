package com.example.aichathelp.ui.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun LocalDateTime.toUiTime(): String = this.format(timeFormatter)
