package com.offlineai.app.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4
@Entity(tableName = "study_content_chunk_fts")
data class StudyContentChunkFtsEntity(

    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowId: Long,

    val text: String
)
