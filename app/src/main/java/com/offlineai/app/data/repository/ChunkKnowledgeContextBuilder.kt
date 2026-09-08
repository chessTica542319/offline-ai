package com.offlineai.app.data.repository

import com.offlineai.app.data.database.StudyContentChunkEntity
import com.offlineai.app.data.database.StudyContentEntity

class ChunkKnowledgeContextBuilder {

    companion object {
        private const val MAX_TOTAL_CHARS = 6000
        private const val MAX_CHUNK_CHARS = 1800
    }

    fun build(
        chunks: List<StudyContentChunkEntity>,
        contents: List<StudyContentEntity>
    ): String {

        if (chunks.isEmpty()) {
            return "No relevant study materials were found."
        }

        val contentMap = contents.associateBy { it.id }
        val builder = StringBuilder()

        chunks.forEachIndexed { index, chunk ->

            if (builder.length >= MAX_TOTAL_CHARS) {
                return@forEachIndexed
            }

            val content = contentMap[chunk.studyContentId]

            val sourceName =
                content?.originalFileName
                    ?.takeIf { it.isNotBlank() }
                    ?: "Unknown source"

            val chunkText =
                chunk.text.take(MAX_CHUNK_CHARS)

            val remaining =
                MAX_TOTAL_CHARS - builder.length

            val text =
                if (chunkText.length > remaining) {
                    chunkText.take(remaining)
                } else {
                    chunkText
                }

            if (text.isBlank()) {
                return@forEachIndexed
            }

            builder.append("[Source ")
                .append(index + 1)
                .append(": ")
                .append(sourceName)
                .append("]\n")

            builder.append(text.trim())
                .append("\n\n")
        }

        return builder.toString().trim()
            .ifBlank {
                "No relevant study materials were found."
            }
    }
}
