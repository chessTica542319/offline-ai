package com.offlineai.app

import android.app.Application

import com.offlineai.app.data.database.AppDatabase

class OfflineAIApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
}
