package com.example.aichathelp

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aichathelp.ui.screen.chat.ChatScreen
import com.example.aichathelp.ui.theme.AiChatHelpTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AiChatHelpTheme {
                ChatScreen()
            }
        }
    }
}
