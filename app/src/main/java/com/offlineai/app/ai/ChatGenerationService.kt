package com.offlineai.app.ai

import com.offlineai.app.data.repository.ChunkKnowledgeContextBuilder
import com.offlineai.app.data.repository.ChunkKnowledgeSearch
import com.offlineai.app.data.repository.StudyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatGenerationService(
    private val chunkKnowledgeSearch: ChunkKnowledgeSearch,
    private val studyRepository: StudyRepository,
    private val chunkKnowledgeContextBuilder: ChunkKnowledgeContextBuilder,
    private val aiEngineManager: AIEngineManager,
    private val sessionMemory: SessionMemory,
    private val config: ChatGenerationConfig,
    private val stopController: ChatGenerationStopController
) {

    suspend fun generate(
        question: String
    ): String {

        val cleanQuestion = question.trim()

        if (cleanQuestion.isBlank()) {
            return "Error: question is empty."
        }

        if (stopController.isStopRequested()) {
            return "__STOPPED__"
        }

        return runCatching {

            val chunks =
                withContext(Dispatchers.IO) {
                    chunkKnowledgeSearch.search(
                        question = cleanQuestion,
                        limit = config.maxRetrievedContents
                    )
                }

            if (stopController.isStopRequested()) {
                return@runCatching "__STOPPED__"
            }

            val contents =
                withContext(Dispatchers.IO) {

                    chunks
                        .mapNotNull { chunk ->
                            studyRepository.getStudyContent(
                                chunk.studyContentId
                            )
                        }
                        .distinctBy {
                            it.id
                        }
                }

            if (stopController.isStopRequested()) {
                return@runCatching "__STOPPED__"
            }

            val knowledgeContext =
                chunkKnowledgeContextBuilder.build(
                    chunks = chunks,
                    contents = contents
                )

            if (stopController.isStopRequested()) {
                return@runCatching "__STOPPED__"
            }

            val sessionContext =
                sessionMemory.buildContext()

            val sessionSection =
                if (sessionContext.isBlank()) {
                    "No previous conversation messages are available."
                } else {
                    """
                    Previous conversation in this session:
                    $sessionContext
                    """.trimIndent()
                }

            val aiPrompt = """
                <|im_start|>system
                You are Offline AI, a helpful study assistant. Answer clearly and accurately.

                Use the user's local study materials below as the primary source when they are relevant.

                $knowledgeContext

                $sessionSection

                Use the previous conversation to understand follow-up questions and references.
                Do not treat previous assistant answers as authoritative facts when the study
                materials provide better information.

                If the study materials do not contain enough information to answer the question,
                say that the available study materials do not contain enough information.
                Do not invent facts and do not pretend that unsupported information came from
                the study materials.

                <|im_end|>
                <|im_start|>user
                $cleanQuestion
                <|im_end|>
                <|im_start|>assistant
            """.trimIndent()


            if (stopController.isStopRequested()) {
                return@runCatching "__STOPPED__"
            }

            aiEngineManager.generate(
                prompt = aiPrompt,
                contextSize = config.contextSize,
                maxTokens = config.maxTokens,
                threads = config.threads
            )

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

        if (
            sentences.size <=
            config.maxResponseSentences
        ) {
            return cleaned
        }

        return sentences
            .take(config.maxResponseSentences)
            .joinToString(" ")
            .trim()
    }
}
