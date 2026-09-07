package com.offlineai.app.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_content_chunks",
    foreignKeys = [
        ForeignKey(
            entity = StudyContentEntity::class,
            parentColumns = ["id"],
            childColumns = ["studyContentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("studyContentId")
    ]
)
data class StudyContentChunkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val studyContentId: Long,

    val chunkIndex: Int,

    val text: String
)
