package com.offlineai.app

import android.app.Application

import com.offlineai.app.data.database.AppDatabase
import com.offlineai.app.debug.CrashLogger

class OfflineAIApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        CrashLogger.install(this)
    }

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
}
