package com.offlineai.app.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "extracted_text",
    foreignKeys = [
        ForeignKey(
            entity = SourceEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("sourceId")
    ]
)
data class ExtractedTextEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceId: Long,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
