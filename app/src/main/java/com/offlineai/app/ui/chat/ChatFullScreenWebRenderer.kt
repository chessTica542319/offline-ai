package com.offlineai.app.ui.chat

import android.util.Base64
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class ChatFullScreenWebRenderer(
    private val webView: WebView
) {

    private var pendingCode: Pair<String, String>? = null
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

                    pendingCode?.let {
                        sendCode(
                            language = it.first,
                            code = it.second
                        )

                        pendingCode = null
                    }
                }
            }

        webView.loadUrl(
            "file:///android_asset/chat_fullscreen_renderer.html"
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
    }

    fun render(
        language: String,
        code: String
    ) {

        pendingCode =
            Pair(
                language,
                code
            )

        if (pageReady) {
            sendCode(
                language,
                code
            )

            pendingCode = null
        }
    }

    private fun sendCode(
        language: String,
        code: String
    ) {

        val languageEncoded =
            Base64.encodeToString(
                language.toByteArray(
                    Charsets.UTF_8
                ),
                Base64.NO_WRAP
            )

        val codeEncoded =
            Base64.encodeToString(
                code.toByteArray(
                    Charsets.UTF_8
                ),
                Base64.NO_WRAP
            )

        webView.evaluateJavascript(
            "setCode('$languageEncoded','$codeEncoded')",
            null
        )
    }
}
