package com.offlineai.app.data.extraction

import android.net.Uri

data class PendingStudyContent(
    val fileUri: Uri,
    val title: String,
    val text: String,
    val sourceType: String,
    val originalFileName: String
)
