package com.offlineai.app.ui.chat

data class ChatRenderPart(
    val type: Type,
    val language: String = "",
    val content: String
) {
    enum class Type {
        TEXT,
        CODE
    }
}

object ChatCodeParser {

    fun parse(text: String): List<ChatRenderPart> {

        val normalized =
            text.replace(
                "\r\n",
                "\n"
            )

        val pattern =
            Regex(
                """(^|\n)[ \t]*(```|''')[ \t]*([^\n]*?)\n([\s\S]*?)(?:\n[ \t]*\2[ \t]*|[ \t]*\2)"""
            )

        val parts =
            mutableListOf<ChatRenderPart>()

        var lastIndex = 0

        pattern.findAll(
            normalized
        ).forEach { match ->

            val textStart =
                lastIndex

            val textEnd =
                match.range.first +
                    if (
                        match.groupValues[1]
                            .isNotEmpty()
                    ) {
                        1
                    } else {
                        0
                    }

            if (
                textEnd > textStart
            ) {

                val normalText =
                    normalizeText(
                        normalized.substring(
                            textStart,
                            textEnd
                        )
                    )

                if (
                    normalText.trim()
                        .isNotEmpty()
                ) {
                    parts.add(
                        ChatRenderPart(
                            type =
                                ChatRenderPart
                                    .Type
                                    .TEXT,
                            content =
                                normalText
                        )
                    )
                }
            }

            val language =
                match.groupValues[3]
                    .trim()

            val code =
                normalizeCode(
                    match.groupValues[4]
                )

            parts.add(
                ChatRenderPart(
                    type =
                        ChatRenderPart
                            .Type
                            .CODE,
                    language =
                        language,
                    content =
                        code
                )
            )

            lastIndex =
                match.range.last + 1
        }

        if (
            lastIndex <
                normalized.length
        ) {

            val remaining =
                normalizeText(
                    normalized.substring(
                        lastIndex
                    )
                )

            if (
                remaining.trim()
                    .isNotEmpty()
            ) {

                parts.add(
                    ChatRenderPart(
                        type =
                            ChatRenderPart
                                .Type
                                .TEXT,
                        content =
                            remaining
                    )
                )
            }
        }

        if (
            parts.isEmpty()
        ) {

            parts.add(
                ChatRenderPart(
                    type =
                        ChatRenderPart
                            .Type
                            .TEXT,
                    content =
                        normalized
                )
            )
        }

        return parts
    }

    private fun normalizeCode(
        code: String
    ): String {

        var value =
            code.replace(
                "\r\n",
                "\n"
            )

        value =
            value.replace(
                Regex(
                    "^\\n+"
                ),
                ""
            )

        value =
            value.replace(
                Regex(
                    "\\n+$"
                ),
                ""
            )

        val lines =
            value.split("\n")

        var minimumIndent:
            Int? = null

        lines.forEach { line ->

            if (
                line.trim().isEmpty()
            ) {
                return@forEach
            }

            val match =
                Regex(
                    "^[ \\t]+"
                ).find(
                    line
                )

            if (
                match == null
            ) {
                minimumIndent = 0
                return@forEach
            }

            val indent =
                match.value
                    .replace(
                        "\t",
                        "    "
                    )
                    .length

            if (
                minimumIndent == null ||
                indent < minimumIndent!!
            ) {
                minimumIndent =
                    indent
            }
        }

        if (
            minimumIndent != null &&
            minimumIndent!! > 0
        ) {

            value =
                lines
                    .map { line ->
                        line.replace(
                            Regex(
                                "^ {0," +
                                    minimumIndent!! +
                                    "}"
                            ),
                            ""
                        )
                    }
                    .joinToString(
                        "\n"
                    )
        }

        return value
    }

    private fun normalizeText(
        text: String
    ): String {

        var value =
            text.replace(
                "\r\n",
                "\n"
            )

        val lines =
            value.split("\n")

        var minimumIndent:
            Int? = null

        lines.forEach { line ->

            if (
                line.trim().isEmpty()
            ) {
                return@forEach
            }

            val match =
                Regex(
                    "^[ \\t]+"
                ).find(
                    line
                )

            if (
                match == null
            ) {
                minimumIndent = 0
                return@forEach
            }

            val indent =
                match.value
                    .replace(
                        "\t",
                        "    "
                    )
                    .length

            if (
                minimumIndent == null ||
                indent < minimumIndent!!
            ) {
                minimumIndent =
                    indent
            }
        }

        if (
            minimumIndent != null &&
            minimumIndent!! > 0
        ) {

            value =
                lines
                    .map { line ->
                        line.replace(
                            Regex(
                                "^ {0," +
                                    minimumIndent!! +
                                    "}"
                            ),
                            ""
                        )
                    }
                    .joinToString(
                        "\n"
                    )
        }

        return value
    }
}
