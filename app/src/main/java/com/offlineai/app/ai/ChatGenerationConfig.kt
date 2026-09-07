package com.offlineai.app.ai

data class ChatGenerationConfig(
    val contextSize: Int = 2048,
    val maxTokens: Int = 1024,
    val threads: Int = 4,
    val maxRetrievedContents: Int = 5,
    val maxResponseSentences: Int = 60
)
