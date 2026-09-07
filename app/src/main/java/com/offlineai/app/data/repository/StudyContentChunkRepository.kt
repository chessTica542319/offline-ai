package com.offlineai.app.data.repository

import com.offlineai.app.data.database.StudyContentChunkDao
import com.offlineai.app.data.database.StudyContentChunkEntity
import com.offlineai.app.data.database.StudyContentChunkFtsEntity
import com.offlineai.app.data.database.StudyContentEntity

class StudyContentChunkRepository(
    private val chunkDao: StudyContentChunkDao
) {

    suspend fun indexStudyContent(
        content: StudyContentEntity
    ) {
        deleteStudyContentChunks(content.id)

        val chunkTexts = StudyContentChunker.split(content.text)

        if (chunkTexts.isEmpty()) {
            return
        }

        val chunks = chunkTexts.mapIndexed { index, text ->
            StudyContentChunkEntity(
                studyContentId = content.id,
                chunkIndex = index,
                text = text
            )
        }

        val chunkIds = chunkDao.insertAll(chunks)

        val ftsRows = chunkIds.mapIndexed { index, rowId ->
            StudyContentChunkFtsEntity(
                rowId = rowId,
                text = chunkTexts[index]
            )
        }

        chunkDao.insertFtsRows(ftsRows)
    }

    suspend fun deleteStudyContentChunks(
        studyContentId: Long
    ) {
        val chunks = chunkDao.getByStudyContent(
            studyContentId
        )

        if (chunks.isEmpty()) {
            return
        }

        val rowIds = chunks.map {
            it.id
        }

        chunkDao.deleteFtsRows(rowIds)

        chunkDao.deleteByStudyContent(
            studyContentId
        )
    }

    suspend fun searchRelevantChunks(
        query: String,
        limit: Int = 5
    ): List<StudyContentChunkEntity> {
        val cleanQuery = query.trim()
            .split(Regex("\\s+"))
            .map {
                it.replace(
                    Regex("[^A-Za-z0-9_]+"),
                    ""
                )
            }
            .filter {
                it.length >= 2
            }
            .distinct()
            .joinToString(" OR ")

        if (cleanQuery.isBlank()) {
            return emptyList()
        }

        return chunkDao.searchRelevantChunks(
            query = cleanQuery,
            limit = limit.coerceIn(1, 20)
        )
    }

    suspend fun getChunks(
        studyContentId: Long
    ): List<StudyContentChunkEntity> {
        return chunkDao.getByStudyContent(
            studyContentId
        )
    }
}
