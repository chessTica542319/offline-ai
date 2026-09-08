package com.offlineai.app.ui.chat

import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class ChatWebRenderer(
    private val webView: WebView,
    private val onFullScreenCode: (String, String) -> Unit
) {

    private var pendingResponse: String? = null
    private var pageReady = false

    init {
        configureWebView()

        webView.webViewClient =
            object : WebViewClient() {

                override fun onPageFinished(
                    view: WebView,
                    url: String
                ) {
                    pageReady = true

                    pendingResponse?.let {
                        sendResponse(it)
                        pendingResponse = null
                    }
                }
            }

        webView.loadUrl(
            "file:///android_asset/chat_renderer.html"
        )
    }

    private fun configureWebView() {

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = false
            allowFileAccess = true
            allowContentAccess = false
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = false
            cacheMode =
                WebSettings.LOAD_NO_CACHE
        }

        webView.addJavascriptInterface(
            ClipboardBridge(),
            "AndroidClipboard"
        )

        webView.addJavascriptInterface(
            FullScreenBridge(),
            "AndroidCode"
        )
    }

    fun render(text: String) {

        pendingResponse = text

        if (pageReady) {
            sendResponse(text)
            pendingResponse = null
        }
    }

    private fun sendResponse(
        text: String
    ) {

        val encoded =
            android.util.Base64.encodeToString(
                text.toByteArray(
                    Charsets.UTF_8
                ),
                android.util.Base64.NO_WRAP
            )

        webView.evaluateJavascript(
            "setResponse('$encoded')",
            null
        )
    }

    private inner class ClipboardBridge {

        @JavascriptInterface
        fun copyCode(code: String) {

            val clipboard =
                webView.context.getSystemService(
                    android.content.Context
                        .CLIPBOARD_SERVICE
                ) as android.content.ClipboardManager

            clipboard.setPrimaryClip(
                android.content.ClipData.newPlainText(
                    "Code",
                    code
                )
            )
        }
    }

    private inner class FullScreenBridge {

        @JavascriptInterface
        fun openFullScreen(
            language: String,
            code: String
        ) {

            onFullScreenCode(
                language,
                code
            )
        }
    }
}
