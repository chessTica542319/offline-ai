package com.offlineai.app.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("subjectId")
    ]
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
