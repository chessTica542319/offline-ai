package com.offlineai.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "subjects"
)
data class SubjectEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val description: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    // null = active subject
    // non-null = subject is in Recently Deleted
    val deletedAt: Long? = null
)
