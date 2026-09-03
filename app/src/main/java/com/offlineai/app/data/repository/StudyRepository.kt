package com.offlineai.app.data.repository

import com.offlineai.app.data.database.LessonDao
import com.offlineai.app.data.database.LessonEntity
import com.offlineai.app.data.database.SubjectDao
import com.offlineai.app.data.database.SubjectEntity

class StudyRepository(
    private val subjectDao: SubjectDao,
    private val lessonDao: LessonDao
) {

    companion object {
        const val DELETION_RETENTION_MILLIS =
            14L * 24L * 60L * 60L * 1000L
    }

    suspend fun getSubjects(): List<SubjectEntity> {
        cleanupExpiredDeletedSubjects()
        return subjectDao.getActiveSubjects()
    }

    suspend fun getRecentlyDeletedSubjects(): List<SubjectEntity> {
        cleanupExpiredDeletedSubjects()
        return subjectDao.getRecentlyDeleted()
    }

    suspend fun getSubject(id: Long): SubjectEntity? =
        subjectDao.getById(id)

    suspend fun getSubjectByName(name: String): SubjectEntity? =
        subjectDao.getByName(name.trim())

    suspend fun createSubject(
        name: String,
        description: String = ""
    ): Long {
        val cleanName = name.trim()

        require(cleanName.isNotEmpty()) {
            "Subject name cannot be empty."
        }

        require(subjectDao.getByName(cleanName) == null) {
            "A subject with this name already exists."
        }

        return subjectDao.insert(
            SubjectEntity(
                name = cleanName,
                description = description.trim()
            )
        )
    }

    suspend fun renameSubject(
        subject: SubjectEntity,
        newName: String
    ) {
        val cleanName = newName.trim()

        require(cleanName.isNotEmpty()) {
            "Subject name cannot be empty."
        }

        val existing = subjectDao.getByName(cleanName)

        require(
            existing == null ||
                existing.id == subject.id
        ) {
            "A subject with this name already exists."
        }

        subjectDao.update(
            subject.copy(
                name = cleanName
            )
        )
    }

    suspend fun updateSubject(
        subject: SubjectEntity
    ) {
        val cleanName = subject.name.trim()

        require(cleanName.isNotEmpty()) {
            "Subject name cannot be empty."
        }

        subjectDao.update(
            subject.copy(
                name = cleanName,
                description = subject.description.trim()
            )
        )
    }

    suspend fun deleteSubject(
        subject: SubjectEntity
    ) {
        subjectDao.softDelete(
            id = subject.id,
            deletedAt = System.currentTimeMillis()
        )
    }

    suspend fun restoreSubject(
        subject: SubjectEntity
    ) {
        val existing = subjectDao.getByName(subject.name)

        require(
            existing == null ||
                existing.id == subject.id
        ) {
            "A subject with this name already exists."
        }

        subjectDao.restore(subject.id)
    }

    suspend fun permanentlyDeleteSubject(
        subject: SubjectEntity
    ) {
        subjectDao.permanentlyDelete(subject.id)
    }

    suspend fun permanentlyDeleteAllDeletedSubjects() {
        subjectDao.permanentlyDeleteAllDeleted()
    }

    suspend fun cleanupExpiredDeletedSubjects() {
        val cutoff =
            System.currentTimeMillis() -
                DELETION_RETENTION_MILLIS

        subjectDao.permanentlyDeleteExpired(cutoff)
    }

    suspend fun getSubjectCount(): Int =
        subjectDao.countActive()

    suspend fun getLessons(
        subjectId: Long
    ): List<LessonEntity> =
        lessonDao.getBySubject(subjectId)

    suspend fun getLesson(
        id: Long
    ): LessonEntity? =
        lessonDao.getById(id)

    suspend fun createLesson(
        subjectId: Long,
        name: String,
        description: String = ""
    ): Long {
        val cleanName = name.trim()

        require(cleanName.isNotEmpty()) {
            "Lesson name cannot be empty."
        }

        val existing = lessonDao
            .getBySubject(subjectId)
            .any {
                it.name.equals(
                    cleanName,
                    ignoreCase = true
                )
            }

        require(!existing) {
            "A lesson with this name already exists in this subject."
        }

        return lessonDao.insert(
            LessonEntity(
                subjectId = subjectId,
                name = cleanName,
                description = description.trim()
            )
        )
    }
}
