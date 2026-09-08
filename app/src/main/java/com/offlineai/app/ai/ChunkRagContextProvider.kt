package com.offlineai.app.ai

import com.offlineai.app.data.repository.ChunkKnowledgeContextBuilder
import com.offlineai.app.data.repository.ChunkKnowledgeSearch
import com.offlineai.app.data.repository.StudyRepository

class ChunkRagContextProvider(
    private val chunkKnowledgeSearch: ChunkKnowledgeSearch,
    private val studyRepository: StudyRepository,
    private val contextBuilder: ChunkKnowledgeContextBuilder
) {
    suspend fun build(question: String, limit: Int = 5): String {
        val chunks = chunkKnowledgeSearch.search(
            question = question,
            limit = limit.coerceIn(1, 5)
        )

        if (chunks.isEmpty()) {
            return contextBuilder.build(
                chunks = emptyList(),
                contents = emptyList()
            )
        }

        val contents = chunks
            .mapNotNull { chunk ->
                studyRepository.getStudyContent(
                    chunk.studyContentId
                )
            }
            .distinctBy { it.id }

        return contextBuilder.build(
            chunks = chunks,
            contents = contents
        )
    }
}
