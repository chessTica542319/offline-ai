package com.offlineai.app.debug

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CrashLogger {

    fun install(context: Context) {

        val previousHandler =
            Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->

            try {
                val writer = StringWriter()

                throwable.printStackTrace(
                    PrintWriter(writer)
                )

                val crashText = buildString {
                    append("Offline AI Crash Report\n")
                    append("=======================\n")
                    append("Time: ")
                    append(
                        SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.US
                        ).format(Date())
                    )
                    append("\n")
                    append("Thread: ")
                    append(thread.name)
                    append("\n")
                    append("Android: ")
                    append(Build.VERSION.RELEASE)
                    append(" (API ")
                    append(Build.VERSION.SDK_INT)
                    append(")\n")
                    append("Device: ")
                    append(Build.MANUFACTURER)
                    append(" ")
                    append(Build.MODEL)
                    append("\n\n")
                    append(writer.toString())
                }

                saveCrash(context, crashText)

            } catch (_: Exception) {
            }

            previousHandler?.uncaughtException(
                thread,
                throwable
            )
        }
    }

    private fun saveCrash(
        context: Context,
        crashText: String
    ) {

        val resolver =
            context.contentResolver

        val values =
            ContentValues().apply {
                put(
                    MediaStore.Downloads.DISPLAY_NAME,
                    "crash.txt"
                )
                put(
                    MediaStore.Downloads.MIME_TYPE,
                    "text/plain"
                )
                put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS +
                        "/Offline AI"
                )
                put(
                    MediaStore.Downloads.IS_PENDING,
                    1
                )
            }

        val uri =
            resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            ) ?: return

        try {

            resolver.openOutputStream(uri)?.use { output ->
                output.write(
                    crashText.toByteArray(
                        Charsets.UTF_8
                    )
                )
            }

            val completedValues =
                ContentValues().apply {
                    put(
                        MediaStore.Downloads.IS_PENDING,
                        0
                    )
                }

            resolver.update(
                uri,
                completedValues,
                null,
                null
            )

        } catch (_: Exception) {

            resolver.delete(
                uri,
                null,
                null
            )
        }
    }
}
