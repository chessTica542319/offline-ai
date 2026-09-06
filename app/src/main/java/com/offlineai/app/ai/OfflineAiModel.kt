package com.offlineai.app.ai

import android.content.Context
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object OfflineAiModel {

    private const val assetPath =
        "models/qwen2.5-1.5b-instruct-q4_k_m.gguf"

    private const val modelFileName =
        "qwen2.5-1.5b-instruct-q4_k_m.gguf"

    suspend fun ensureAvailable(context: Context): String =
        withContext(Dispatchers.IO) {
            val modelDirectory = File(context.filesDir, "models")

            if (!modelDirectory.exists()) {
                modelDirectory.mkdirs()
            }

            val modelFile = File(modelDirectory, modelFileName)

            val expectedSize = runCatching {
                context.assets.openFd(assetPath).length
            }.getOrDefault(-1L)

            if (modelFile.exists() &&
                modelFile.length() > 0 &&
                (expectedSize <= 0 || modelFile.length() == expectedSize)
            ) {
                return@withContext modelFile.absolutePath
            }

            if (modelFile.exists()) {
                modelFile.delete()
            }

            context.assets.open(assetPath).use { input ->
                modelFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            modelFile.absolutePath
        }
}
