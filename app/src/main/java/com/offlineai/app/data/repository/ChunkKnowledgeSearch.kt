package com.offlineai.app.data.repository

import com.offlineai.app.data.database.StudyContentChunkEntity

class ChunkKnowledgeSearch(
    private val chunkRepository: StudyContentChunkRepository
) {

    suspend fun search(
        question: String,
        limit: Int = 5
    ): List<StudyContentChunkEntity> {

        val cleanQuestion = question.trim()

        if (cleanQuestion.isBlank()) {
            return emptyList()
        }

        return chunkRepository.searchRelevantChunks(
            query = cleanQuestion,
            limit = limit.coerceIn(1, 5)
        )
    }
}
