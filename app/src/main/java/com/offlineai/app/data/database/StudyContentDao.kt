package com.offlineai.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import kotlinx.coroutines.flow.Flow

@Dao
interface StudyContentDao {

    @Insert
    suspend fun insert(content: StudyContentEntity): Long

    @Update
    suspend fun update(content: StudyContentEntity)

    @Query("""
        SELECT * FROM study_content
        WHERE lessonId = :lessonId
        ORDER BY createdAt ASC
    """)
    suspend fun getByLesson(
        lessonId: Long
    ): List<StudyContentEntity>

    @Query("""
        SELECT * FROM study_content
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun getById(
        id: Long
    ): StudyContentEntity?

    @Query("""
        DELETE FROM study_content
        WHERE id = :id
    """)
    suspend fun delete(
        id: Long
    )

    @Query("""
        SELECT COUNT(*) FROM study_content
    """)
    suspend fun count(): Int

    @Query("""
        SELECT COUNT(*)
        FROM study_content
        INNER JOIN lessons
            ON study_content.lessonId = lessons.id
        INNER JOIN subjects
            ON lessons.subjectId = subjects.id
        WHERE subjects.deletedAt IS NULL
    """)
    fun observeActiveCount(): Flow<Int>
}
