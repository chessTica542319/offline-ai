package com.offlineai.app.data.repository

import com.offlineai.app.data.database.StudyContentEntity

object KnowledgeContextBuilder {

    private const val MAX_CONTENT_PER_FILE = 1800
    private const val MAX_TOTAL_CONTEXT = 7000

    fun build(
        contents: List<StudyContentEntity>
    ): String {
        if (contents.isEmpty()) {
            return """
                No relevant study material was found in the user's local knowledge database.

                Answer normally, but do not claim that information came from the user's study materials.
            """.trimIndent()
        }

        var totalLength = 0

        return buildString {
            appendLine("Relevant study materials from the user's local database:")
            appendLine()

            contents.forEachIndexed { index, content ->
                if (totalLength >= MAX_TOTAL_CONTEXT) {
                    return@forEachIndexed
                }

                val remaining = MAX_TOTAL_CONTEXT - totalLength
                val allowedLength =
                    minOf(
                        MAX_CONTENT_PER_FILE,
                        remaining
                    )

                val studyText = content.text
                    .trim()
                    .take(allowedLength)

                appendLine("[${index + 1}] ${content.title}")

                if (content.originalFileName.isNotBlank()) {
                    appendLine("Source: ${content.originalFileName}")
                }

                appendLine("Study material:")
                appendLine(studyText)

                if (content.text.length > allowedLength) {
                    appendLine("[Study material truncated]")
                }

                appendLine()

                totalLength += studyText.length
            }

            appendLine(
                "Use the relevant study materials above as the primary source for your answer."
            )
            appendLine(
                "If the materials do not contain enough information, say so instead of inventing details."
            )
        }
    }
}
