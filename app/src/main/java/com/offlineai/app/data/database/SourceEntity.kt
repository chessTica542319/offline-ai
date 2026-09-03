package com.offlineai.app.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sources",
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
data class SourceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lessonId: Long,
    val name: String,
    val mimeType: String = "",
    val filePath: String = "",
    val sourceType: String = "FILE",
    val createdAt: Long = System.currentTimeMillis()
)
