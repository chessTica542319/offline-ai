package com.offlineai.app.ui.chat

data class ChatMessage(
    val id: Long,
    val isUser: Boolean,
    val text: String,
    val prompt: String? = null
)
