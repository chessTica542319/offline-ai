package com.offlineai.app.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4(
    contentEntity = StudyContentEntity::class
)
@Entity(tableName = "study_content_fts")
data class StudyContentFtsEntity(

    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowId: Long,

    val title: String,

    val text: String,

    val originalFileName: String
)
