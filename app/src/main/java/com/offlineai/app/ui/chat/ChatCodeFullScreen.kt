package com.offlineai.app.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ChatCodeFullScreen(
    language: String,
    code: String,
    onBack: () -> Unit
) {
    val clipboardManager =
        LocalClipboardManager.current

    val cleanLanguage =
        remember(language) {
            if (language.isBlank()) {
                "Code"
            } else {
                language
                    .trim()
                    .replaceFirstChar {
                        it.uppercase()
                    }
            }
        }

    Dialog(
        onDismissRequest = onBack,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
    ) {
        Surface(
            modifier =
                Modifier.fillMaxSize(),
            color =
                Color(0xFFF4F8F5)
        ) {
            Column(
                modifier =
                    Modifier.fillMaxSize()
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFE7ECE9)
                            )
                            .padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                    verticalAlignment =
                        Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription =
                                "Back"
                        )
                    }

                    Text(
                        text = cleanLanguage,
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    IconButton(
                        onClick = {
                            clipboardManager
                                .setText(
                                    AnnotatedString(
                                        code
                                    )
                                )
                        }
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.ContentCopy,
                            contentDescription =
                                "Copy code"
                        )
                    }
                }

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                Color(0xFFEAF4ED)
                            )
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .padding(20.dp)
                ) {
                    Text(
                        text = code,
                        color =
                            Color(0xFF172019),
                        fontFamily =
                            FontFamily.Monospace,
                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge
                    )
                }
            }
        }
    }
}
