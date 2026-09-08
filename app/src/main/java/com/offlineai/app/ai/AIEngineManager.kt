package com.offlineai.app.ai

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AIEngineManager(
    private val context: Context
) {

    private var modelPath: String? = null
    private var isModelLoaded = false

    private val _status =
        MutableStateFlow(
            AIEngineStatus.IDLE
        )

    val status: StateFlow<AIEngineStatus> =
        _status.asStateFlow()

    suspend fun generate(
        prompt: String,
        contextSize: Int,
        maxTokens: Int,
        threads: Int
    ): String {

        return withContext(Dispatchers.IO) {

            _status.value =
                AIEngineStatus.LOADING

            val path =
                ensureModelLoaded()

            if (path == null) {

                _status.value =
                    AIEngineStatus.ERROR

                return@withContext(
                    "Error: unable to load AI model."
                )
            }

            _status.value =
                AIEngineStatus.GENERATING

            val result =
                runCatching {
                    OfflineAiNative.generate(
                        prompt = prompt,
                        contextSize = contextSize,
                        maxTokens = maxTokens,
                        threads = threads
                    )
                }.getOrElse {
                    _status.value =
                        AIEngineStatus.ERROR

                    return@withContext(
                        "Error: ${
                            it.message
                                ?: "Unable to generate a response."
                        }"
                    )
                }

            _status.value =
                if (result == "__STOPPED__") {
                    AIEngineStatus.READY
                } else if (
                    result.startsWith(
                        "Error:",
                        ignoreCase = true
                    )
                ) {
                    AIEngineStatus.ERROR
                } else {
                    AIEngineStatus.READY
                }

            result
        }
    }

    fun stopGeneration() {

        if (
            _status.value ==
            AIEngineStatus.GENERATING
        ) {

            _status.value =
                AIEngineStatus.STOPPING

            OfflineAiNative.stopGeneration()
        }
    }

    suspend fun unload() {

        withContext(Dispatchers.IO) {

            if (isModelLoaded) {

                OfflineAiNative.unloadModel()

                isModelLoaded = false
                modelPath = null
            }

            _status.value =
                AIEngineStatus.IDLE
        }
    }

    private suspend fun ensureModelLoaded(): String? {

        if (
            isModelLoaded &&
            modelPath != null
        ) {
            return modelPath
        }

        val path =
            OfflineAiModel.ensureAvailable(
                context
            )

        val result =
            OfflineAiNative.loadModel(
                path
            )

        if (
            result.startsWith(
                "Error:",
                ignoreCase = true
            )
        ) {

            modelPath = null
            isModelLoaded = false

            return null
        }

        modelPath = path
        isModelLoaded = true

        return path
    }
}
