package com.offlineai.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StudyContentChunkDao {

    @Insert
    suspend fun insertAll(
        chunks: List<StudyContentChunkEntity>
    ): List<Long>

    @Insert
    suspend fun insertFtsRows(
        rows: List<StudyContentChunkFtsEntity>
    )

    @Query("""
        SELECT * FROM study_content_chunks
        WHERE studyContentId = :studyContentId
        ORDER BY chunkIndex ASC
    """)
    suspend fun getByStudyContent(
        studyContentId: Long
    ): List<StudyContentChunkEntity>

    @Query("""
        SELECT study_content_chunks.*
        FROM study_content_chunks
        INNER JOIN study_content_chunk_fts
            ON study_content_chunks.id = study_content_chunk_fts.rowid
        WHERE study_content_chunk_fts MATCH :query
        ORDER BY study_content_chunks.chunkIndex ASC
        LIMIT :limit
    """)
    suspend fun searchRelevantChunks(
        query: String,
        limit: Int
    ): List<StudyContentChunkEntity>

    @Query("""
        DELETE FROM study_content_chunk_fts
    """)
    suspend fun deleteAllFts()

    @Query("""
        DELETE FROM study_content_chunk_fts
            WHERE rowid IN (:rowIds)
         """)
    suspend fun deleteFtsRows(
        rowIds: List<Long>
        )

    @Query("""
        DELETE FROM study_content_chunks
        WHERE studyContentId = :studyContentId
    """)
    suspend fun deleteByStudyContent(
        studyContentId: Long
    )

    @Query("""
        DELETE FROM study_content_chunks
    """)
    suspend fun deleteAll()
}
