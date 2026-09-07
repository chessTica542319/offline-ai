package com.offlineai.app.ai

import android.content.Context

import com.offlineai.app.data.repository.KnowledgeContextBuilder
import com.offlineai.app.data.repository.KnowledgeSearch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatGenerationService(
    private val context: Context,
    private val knowledgeSearch: KnowledgeSearch,
    private val knowledgeContextBuilder: KnowledgeContextBuilder,
    private val config: ChatGenerationConfig
) {

    suspend fun generate(
        question: String
    ): String {

        val cleanQuestion = question.trim()

        if (cleanQuestion.isBlank()) {
            return "Error: question is empty."
        }

        return runCatching {

            val relevantContents =
                withContext(Dispatchers.IO) {
                    knowledgeSearch.search(
                        question = cleanQuestion,
                        limit = config.maxRetrievedContents
                    )
                }

            val knowledgeContext =
                knowledgeContextBuilder.build(
                    relevantContents
                )

            val modelPath =
                OfflineAiModel.ensureAvailable(
                    context
                )

            val aiPrompt = """
                <|im_start|>system
                You are Offline AI, a helpful study assistant. Answer clearly and accurately.

                Use the user's local study materials below as the primary source when they are relevant.

                $knowledgeContext

                If the study materials do not contain enough information to answer the question,
                say that the available study materials do not contain enough information.
                Do not invent facts and do not pretend that unsupported information came from the study materials.

                <|im_end|>
                <|im_start|>user
                $cleanQuestion
                <|im_end|>
                <|im_start|>assistant
            """.trimIndent()

            withContext(Dispatchers.IO) {

                OfflineAiNative.loadModel(
                    modelPath
                )

                OfflineAiNative.generate(
                    prompt = aiPrompt,
                    contextSize = config.contextSize,
                    maxTokens = config.maxTokens,
                    threads = config.threads
                )
            }

        }.getOrElse {

            "Error: ${it.message ?: "Unable to generate a response."}"
        }
    }

    fun limitResponse(
        text: String
    ): String {

        val cleaned = text.trim()

        if (cleaned.isBlank()) {
            return cleaned
        }

        val sentences =
            Regex(
                """[^.!?]*[.!?]+(?:\s+|$)|[^.!?]+$"""
            )
                .findAll(cleaned)
                .map {
                    it.value.trim()
                }
                .filter {
                    it.isNotBlank()
                }
                .toList()

        if (sentences.size <= config.maxResponseSentences) {
            return cleaned
        }

        return sentences
            .take(config.maxResponseSentences)
            .joinToString(" ")
            .trim()
    }
}
