package com.offlineai.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SourceDao {

    @Insert
    suspend fun insert(source: SourceEntity): Long

    @Query("""
        SELECT * FROM sources
        WHERE lessonId = :lessonId
        ORDER BY createdAt ASC
    """)
    suspend fun getByLesson(lessonId: Long): List<SourceEntity>

    @Query("""
        SELECT * FROM sources
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun getById(id: Long): SourceEntity?
}
