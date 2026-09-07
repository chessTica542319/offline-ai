package com.offlineai.app.data.repository

import com.offlineai.app.data.database.StudyContentEntity

class KnowledgeSearch(
    private val repository: StudyRepository
) {

    suspend fun search(
        question: String,
        limit: Int = 5
    ): List<StudyContentEntity> {
        val cleanQuestion = question.trim()

        if (cleanQuestion.isBlank()) {
            return emptyList()
        }

        return repository.searchRelevantStudyContents(
            query = cleanQuestion,
            limit = limit
        )
    }
}
