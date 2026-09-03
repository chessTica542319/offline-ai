package com.offlineai.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExtractedTextDao {

    @Insert
    suspend fun insert(text: ExtractedTextEntity): Long

    @Query("""
        SELECT * FROM extracted_text
        WHERE sourceId = :sourceId
        ORDER BY createdAt ASC
    """)
    suspend fun getBySource(sourceId: Long): List<ExtractedTextEntity>

    @Query("""
        SELECT * FROM extracted_text
        WHERE sourceId = :sourceId
        LIMIT 1
    """)
    suspend fun getFirstBySource(sourceId: Long): ExtractedTextEntity?
}
