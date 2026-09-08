package com.offlineai.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        SubjectEntity::class,
        LessonEntity::class,
        SourceEntity::class,
        ExtractedTextEntity::class,
        StudyContentEntity::class,
        StudyContentFtsEntity::class,
        StudyContentChunkEntity::class,
        StudyContentChunkFtsEntity::class
    ],
    version = 9,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao

    abstract fun lessonDao(): LessonDao

    abstract fun sourceDao(): SourceDao

    abstract fun extractedTextDao(): ExtractedTextDao

    abstract fun studyContentDao(): StudyContentDao

    abstract fun studyContentChunkDao(): StudyContentChunkDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        ALTER TABLE subjects
                        ADD COLUMN deletedAt INTEGER
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_2_3 =
            object : Migration(2, 3) {
                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS lessons (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            subjectId INTEGER NOT NULL,
                            name TEXT NOT NULL,
                            description TEXT NOT NULL,
                            createdAt INTEGER NOT NULL,
                            FOREIGN KEY(subjectId)
                                REFERENCES subjects(id)
                                ON DELETE CASCADE
                        )
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS index_lessons_subjectId
                        ON lessons(subjectId)
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS sources (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            lessonId INTEGER NOT NULL,
                            name TEXT NOT NULL,
                            mimeType TEXT NOT NULL,
                            filePath TEXT NOT NULL,
                            sourceType TEXT NOT NULL,
                            createdAt INTEGER NOT NULL,
                            FOREIGN KEY(lessonId)
                                REFERENCES lessons(id)
                                ON DELETE CASCADE
                        )
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS index_sources_lessonId
                        ON sources(lessonId)
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS extracted_text (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            sourceId INTEGER NOT NULL,
                            text TEXT NOT NULL,
                            createdAt INTEGER NOT NULL,
                            FOREIGN KEY(sourceId)
                                REFERENCES sources(id)
                                ON DELETE CASCADE
                        )
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS index_extracted_text_sourceId
                        ON extracted_text(sourceId)
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_3_4 =
            object : Migration(3, 4) {
                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS study_content (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            lessonId INTEGER NOT NULL,
                            title TEXT NOT NULL,
                            text TEXT NOT NULL,
                            sourceType TEXT NOT NULL,
                            originalFileName TEXT NOT NULL,
                            createdAt INTEGER NOT NULL,
                            FOREIGN KEY(lessonId)
                                REFERENCES lessons(id)
                                ON DELETE CASCADE
                        )
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS index_study_content_lessonId
                        ON study_content(lessonId)
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_4_5 =
            object : Migration(4, 5) {
                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        ALTER TABLE study_content
                        ADD COLUMN fileSize INTEGER NOT NULL DEFAULT 0
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_5_6 =
            object : Migration(5, 6) {
                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        CREATE VIRTUAL TABLE IF NOT EXISTS study_content_fts
                        USING fts4(
                            title,
                            text,
                            originalFileName,
                            content='study_content'
                        )
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_6_7 =
            object : Migration(6, 7) {
                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS study_content_chunks (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            studyContentId INTEGER NOT NULL,
                            chunkIndex INTEGER NOT NULL,
                            text TEXT NOT NULL,
                            FOREIGN KEY(studyContentId)
                                REFERENCES study_content(id)
                                ON DELETE CASCADE
                        )
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS index_study_content_chunks_studyContentId
                        ON study_content_chunks(studyContentId)
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_7_8 =
            object : Migration(7, 8) {
                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        CREATE VIRTUAL TABLE IF NOT EXISTS study_content_chunk_fts
                        USING fts4(
                            text,
                            content='study_content_chunks'
                        )
                        """.trimIndent()
                    )
                }
            }

        fun getInstance(
            context: Context
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "offline_ai.db"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9
                    )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
