package com.offlineai.app.ui.chat

import android.graphics.Color as AndroidColor
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ChatMessageRenderer(
    text: String,
    modifier: Modifier = Modifier
) {
    var fullScreenCode by remember {
        mutableStateOf<ChatCodeBlock?>(null)
    }

    val rendererHolder =
        remember {
            arrayOfNulls<ChatWebRenderer>(1)
        }

    AndroidView(
        modifier =
            modifier.fillMaxWidth(),
        factory = { context ->
            WebView(context).apply {

                setBackgroundColor(
                    AndroidColor.TRANSPARENT
                )

                rendererHolder[0] =
                    ChatWebRenderer(
                        webView = this,
                        onFullScreenCode = { language, code ->
                            fullScreenCode =
                                ChatCodeBlock(
                                    language = language,
                                    code = code
                                )
                        }
                    )
            }
        },
        update = { webView ->
            rendererHolder[0]?.render(text)
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            rendererHolder[0] = null
        }
    }

    fullScreenCode?.let { codeBlock ->

        ChatCodeFullScreen(
            language = codeBlock.language,
            code = codeBlock.code,
            onBack = {
                fullScreenCode = null
            }
        )
    }
}
