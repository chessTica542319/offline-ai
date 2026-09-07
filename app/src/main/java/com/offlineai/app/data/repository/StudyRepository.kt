package com.offlineai.app.data.repository

import com.offlineai.app.data.database.LessonDao
import com.offlineai.app.data.database.LessonEntity
import com.offlineai.app.data.database.StudyContentDao
import com.offlineai.app.data.database.StudyContentEntity
import com.offlineai.app.data.database.SubjectDao
import com.offlineai.app.data.database.SubjectEntity
import com.offlineai.app.data.extraction.PendingStudyContent

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class StudyRepository(
    private val subjectDao: SubjectDao,
    private val lessonDao: LessonDao,
    private val studyContentDao: StudyContentDao,
    private val studyContentChunkRepository: StudyContentChunkRepository
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
            existing == null || existing.id == subject.id
        ) {
            "A subject with this name already exists."
        }

        subjectDao.update(
            subject.copy(name = cleanName)
        )
    }

    suspend fun updateSubject(
        subject: SubjectEntity
    ) {
        subjectDao.update(subject)
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
            System.currentTimeMillis() - DELETION_RETENTION_MILLIS

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

        val existing =
            lessonDao.getBySubject(subjectId)
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

    suspend fun getOrCreateGeneralLesson(
        subjectId: Long
    ): LessonEntity {
        val lessons = getLessons(subjectId)

        val existing = lessons.firstOrNull {
            it.name.equals("General", ignoreCase = true)
        }

        if (existing != null) {
            return existing
        }

        val lessonId = createLesson(
            subjectId = subjectId,
            name = "General"
        )

        return getLesson(lessonId)
            ?: error("Failed to create General lesson.")
    }

   suspend fun saveStudyContent(
        lessonId: Long,
        title: String,
        text: String,
        sourceType: String,
        originalFileName: String,
        fileSize: Long
    ): Long {
        val cleanTitle = title.trim()

        require(cleanTitle.isNotEmpty()) {
            "Content title cannot be empty."
        }

        val content = StudyContentEntity(
            lessonId = lessonId,
            title = cleanTitle,
            text = text,
            sourceType = sourceType,
            originalFileName = originalFileName,
            fileSize = fileSize
        )

        val contentId = studyContentDao.insert(content)

        studyContentChunkRepository.indexStudyContent(
            content.copy(id = contentId)
        )

        return contentId
    }

    suspend fun saveStudyContents(
        lessonId: Long,
        contents: List<PendingStudyContent>
    ) {
        contents.forEach { content ->
            saveStudyContent(
                lessonId = lessonId,
                title = content.title,
                text = content.text,
                sourceType = content.sourceType,
                originalFileName = content.originalFileName,
                fileSize = content.fileSize
            )
        }
    }

    suspend fun getStudyContents(
        lessonId: Long
    ): List<StudyContentEntity> =
        studyContentDao.getByLesson(lessonId)

    suspend fun getStudyContentsForSubject(
        subjectId: Long
    ): List<StudyContentEntity> =
        getLessons(subjectId)
            .flatMap { lesson ->
                getStudyContents(lesson.id)
            }

    suspend fun getStudyContent(
        id: Long
    ): StudyContentEntity? =
        studyContentDao.getById(id)

    suspend fun updateStudyContent(
        content: StudyContentEntity
    ) {
        studyContentDao.update(content)

        studyContentChunkRepository.indexStudyContent(
            content
        )
    }

    suspend fun deleteStudyContent(
        id: Long
    ) {
        studyContentChunkRepository.deleteStudyContentChunks(
            id
        )
        studyContentDao.delete(id)
    }

   suspend fun searchRelevantStudyContents(
        query: String,
        limit: Int = 5
    ): List<StudyContentEntity> {
        val cleanQuery = query
            .trim()
            .split(Regex("\\s+"))
            .map { it.replace(Regex("[^A-Za-z0-9_]+"), "") }
            .filter { it.length >= 2 }
            .distinct()
            .joinToString(" OR ")

        if (cleanQuery.isBlank()) {
            return emptyList()
        }

        return studyContentDao.searchRelevantContent(
            query = cleanQuery,
            limit = limit.coerceIn(1, 10)
        )
    } 

    suspend fun getStudyContentCount(): Int =
        studyContentDao.count()


    fun observeKnowledgeStats(): Flow<KnowledgeStats> {
        return combine(
            studyContentDao.observeActiveCount(),
            subjectDao.observeActiveCount()
        ) { files, subjects ->
            KnowledgeStats(
                files = files,
                subjects = subjects
            )   
        }
    }
}
