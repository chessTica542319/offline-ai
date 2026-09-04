package com.offlineai.app.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_content",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("lessonId")
    ]
)
data class StudyContentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val lessonId: Long,

    val title: String,

    val text: String,

    val sourceType: String,

    val originalFileName: String = "",

    val fileSize: Long = 0L,

    val createdAt: Long = System.currentTimeMillis()
)
