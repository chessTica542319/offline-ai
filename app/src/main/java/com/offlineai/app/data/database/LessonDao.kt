package com.offlineai.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LessonDao {

    @Insert
    suspend fun insert(lesson: LessonEntity): Long

    @Query("""
        SELECT * FROM lessons
        WHERE subjectId = :subjectId
        ORDER BY name COLLATE NOCASE ASC
    """)
    suspend fun getBySubject(subjectId: Long): List<LessonEntity>

    @Query("""
        SELECT * FROM lessons
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun getById(id: Long): LessonEntity?
}
