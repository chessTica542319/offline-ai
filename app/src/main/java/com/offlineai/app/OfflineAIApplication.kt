package com.offlineai.app

import android.app.Application

import com.offlineai.app.data.database.AppDatabase

class OfflineAIApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
}
