package com.offlineai.app.data.repository

object StudyContentChunker {

    private const val MAX_CHUNK_LENGTH = 2400
    private const val MIN_CHUNK_LENGTH = 300

    fun split(text: String): List<String> {
        val normalized = text
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .trim()

        if (normalized.isBlank()) {
            return emptyList()
        }

        val paragraphs = normalized
            .split(Regex("\\n\\s*\\n"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val chunks = mutableListOf<String>()
        var current = StringBuilder()

        paragraphs.forEach { paragraph ->

            if (paragraph.length > MAX_CHUNK_LENGTH) {

                if (current.isNotEmpty()) {
                    chunks.add(current.toString().trim())
                    current = StringBuilder()
                }

                splitLargeParagraph(paragraph)
                    .forEach { part ->
                        chunks.add(part)
                    }

                return@forEach
            }

            val combinedLength =
                if (current.isEmpty()) {
                    paragraph.length
                } else {
                    current.length + 2 + paragraph.length
                }

            if (
                current.isNotEmpty() &&
                combinedLength > MAX_CHUNK_LENGTH
            ) {
                chunks.add(current.toString().trim())
                current = StringBuilder()
            }

            if (current.isNotEmpty()) {
                current.append("\n\n")
            }

            current.append(paragraph)
        }

        if (current.isNotEmpty()) {
            chunks.add(current.toString().trim())
        }

        return mergeSmallChunks(chunks)
    }

    private fun splitLargeParagraph(
        paragraph: String
    ): List<String> {
        val words = paragraph.split(Regex("\\s+"))
        val parts = mutableListOf<String>()
        var current = StringBuilder()

        words.forEach { word ->

            val combinedLength =
                if (current.isEmpty()) {
                    word.length
                } else {
                    current.length + 1 + word.length
                }

            if (
                current.isNotEmpty() &&
                combinedLength > MAX_CHUNK_LENGTH
            ) {
                parts.add(current.toString().trim())
                current = StringBuilder()
            }

            if (current.isNotEmpty()) {
                current.append(" ")
            }

            current.append(word)
        }

        if (current.isNotEmpty()) {
            parts.add(current.toString().trim())
        }

        return parts
    }

    private fun mergeSmallChunks(
        chunks: List<String>
    ): List<String> {
        if (chunks.size <= 1) {
            return chunks
        }

        val result = mutableListOf<String>()

        chunks.forEach { chunk ->

            if (
                result.isNotEmpty() &&
                chunk.length < MIN_CHUNK_LENGTH
            ) {
                val previous = result.removeAt(
                    result.lastIndex
                )

                if (
                    previous.length +
                    2 +
                    chunk.length <= MAX_CHUNK_LENGTH
                ) {
                    result.add(
                        "$previous\n\n$chunk"
                    )
                } else {
                    result.add(previous)
                    result.add(chunk)
                }
            } else {
                result.add(chunk)
            }
        }

        return result
    }
}
