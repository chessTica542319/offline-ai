package com.offlineai.app.ai

object OfflineAiNative {

    init {
        System.loadLibrary("offlineai_jni")
    }

    external fun hello(): String

    external fun loadModel(
        modelPath: String
    ): String

    external fun generate(
        prompt: String,
        contextSize: Int,
        maxTokens: Int,
        threads: Int
    ): String

    external fun stopGeneration()

    external fun unloadModel()
}
