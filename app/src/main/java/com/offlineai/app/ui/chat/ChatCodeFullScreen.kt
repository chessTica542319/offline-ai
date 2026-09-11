package com.offlineai.app.ui.chat

import android.graphics.Color as AndroidColor
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

import androidx.compose.ui.unit.dp

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

    val rendererHolder =
        remember {
            arrayOfNulls<ChatFullScreenWebRenderer>(1)
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
                Modifier.fillMaxSize()
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
                                androidx.compose.ui.graphics.Color(
                                    AndroidColor.rgb(
                                        231,
                                        236,
                                        233
                                    )
                                )
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
                            clipboardManager.setText(
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
                                androidx.compose.ui.graphics.Color(
                                    AndroidColor.TRANSPARENT
                                )
                            )
                ) {

                    AndroidView(
                        modifier =
                            Modifier.fillMaxSize(),

                        factory = { context ->

                            WebView(context).apply {

                                setBackgroundColor(
                                    AndroidColor.TRANSPARENT
                                )

                                rendererHolder[0] =
                                    ChatFullScreenWebRenderer(
                                        webView = this
                                    )
                            }
                        },

                        update = {

                            rendererHolder[0]?.render(
                                language = language,
                                code = code
                            )
                        }
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {

        onDispose {

            rendererHolder[0] = null
        }
    }
}
