package com.offlineai.app.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_8_9 =
    object : Migration(8, 9) {

        override fun migrate(
            database: SupportSQLiteDatabase
        ) {

            database.execSQL(
                """
                DROP TRIGGER IF EXISTS room_fts_content_sync_study_content_fts_BEFORE_UPDATE
                """.trimIndent()
            )

            database.execSQL(
                """
                DROP TRIGGER IF EXISTS room_fts_content_sync_study_content_fts_BEFORE_DELETE
                """.trimIndent()
            )

            database.execSQL(
                """
                DROP TRIGGER IF EXISTS room_fts_content_sync_study_content_fts_AFTER_UPDATE
                """.trimIndent()
            )

            database.execSQL(
                """
                DROP TRIGGER IF EXISTS room_fts_content_sync_study_content_fts_AFTER_INSERT
                """.trimIndent()
            )

            database.execSQL(
                """
                DROP TABLE IF EXISTS study_content_fts
                """.trimIndent()
            )

            database.execSQL(
                """
                CREATE VIRTUAL TABLE IF NOT EXISTS `study_content_fts`
                USING FTS4(
                    `title` TEXT NOT NULL,
                    `text` TEXT NOT NULL,
                    `originalFileName` TEXT NOT NULL,
                    content=`study_content`
                )
                """.trimIndent()
            )

            database.execSQL(
                """
                INSERT INTO `study_content_fts`(
                    `docid`,
                    `title`,
                    `text`,
                    `originalFileName`
                )
                SELECT
                    `id`,
                    `title`,
                    `text`,
                    `originalFileName`
                FROM `study_content`
                """.trimIndent()
            )

            database.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS
                room_fts_content_sync_study_content_fts_BEFORE_UPDATE
                BEFORE UPDATE ON `study_content`
                BEGIN
                    DELETE FROM `study_content_fts`
                    WHERE `docid` = OLD.`rowid`;
                END
                """.trimIndent()
            )

            database.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS
                room_fts_content_sync_study_content_fts_BEFORE_DELETE
                BEFORE DELETE ON `study_content`
                BEGIN
                    DELETE FROM `study_content_fts`
                    WHERE `docid` = OLD.`rowid`;
                END
                """.trimIndent()
            )

            database.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS
                room_fts_content_sync_study_content_fts_AFTER_UPDATE
                AFTER UPDATE ON `study_content`
                BEGIN
                    INSERT INTO `study_content_fts`(
                        `docid`,
                        `title`,
                        `text`,
                        `originalFileName`
                    )
                    VALUES(
                        NEW.`rowid`,
                        NEW.`title`,
                        NEW.`text`,
                        NEW.`originalFileName`
                    );
                END
                """.trimIndent()
            )

            database.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS
                room_fts_content_sync_study_content_fts_AFTER_INSERT
                AFTER INSERT ON `study_content`
                BEGIN
                    INSERT INTO `study_content_fts`(
                        `docid`,
                        `title`,
                        `text`,
                        `originalFileName`
                    )
                    VALUES(
                        NEW.`rowid`,
                        NEW.`title`,
                        NEW.`text`,
                        NEW.`originalFileName`
                    );
                END
                """.trimIndent()
            )

            database.execSQL(
                """
                DROP TABLE IF EXISTS `study_content_chunk_fts`
                """.trimIndent()
            )

            database.execSQL(
                """
                CREATE VIRTUAL TABLE IF NOT EXISTS `study_content_chunk_fts`
                USING FTS4(
                    `text` TEXT NOT NULL
                )
                """.trimIndent()
            )

            database.execSQL(
                """
                INSERT INTO `study_content_chunk_fts`(
                    `rowid`,
                    `text`
                )
                SELECT
                    `id`,
                    `text`
                FROM `study_content_chunks`
                """.trimIndent()
            )
        }
    }
