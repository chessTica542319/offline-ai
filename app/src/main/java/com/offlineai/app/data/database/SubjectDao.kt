package com.offlineai.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Insert
    suspend fun insert(subject: SubjectEntity): Long

    @Update
    suspend fun update(subject: SubjectEntity)

    @Query("""
        SELECT * FROM subjects
        WHERE deletedAt IS NULL
        ORDER BY name COLLATE NOCASE ASC
    """)
    suspend fun getActiveSubjects(): List<SubjectEntity>

    @Query("""
        SELECT * FROM subjects
        WHERE deletedAt IS NOT NULL
        ORDER BY deletedAt DESC
    """)
    suspend fun getRecentlyDeleted(): List<SubjectEntity>

    @Query("""
        SELECT * FROM subjects
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun getById(id: Long): SubjectEntity?

    @Query("""
        SELECT * FROM subjects
        WHERE name = :name
        LIMIT 1
    """)
    suspend fun getByName(name: String): SubjectEntity?

    @Query("""
        SELECT COUNT(*) FROM subjects
        WHERE deletedAt IS NULL
    """)
    suspend fun countActive(): Int

    @Query("""
         SELECT COUNT(*)
         FROM subjects
         WHERE deletedAt IS NULL
    """)
    fun observeActiveCount(): Flow<Int>

    @Query("""
        UPDATE subjects
        SET deletedAt = :deletedAt
        WHERE id = :id
    """)
    suspend fun softDelete(
        id: Long,
        deletedAt: Long
    )

    @Query("""
        UPDATE subjects
        SET deletedAt = NULL
        WHERE id = :id
    """)
    suspend fun restore(id: Long)

    @Query("""
        DELETE FROM subjects
        WHERE id = :id
    """)
    suspend fun permanentlyDelete(id: Long)

    @Query("""
        DELETE FROM subjects
        WHERE deletedAt IS NOT NULL
    """)
    suspend fun permanentlyDeleteAllDeleted()

    @Query("""
        DELETE FROM subjects
        WHERE deletedAt IS NOT NULL
        AND deletedAt <= :cutoff
    """)
    suspend fun permanentlyDeleteExpired(cutoff: Long)
}
