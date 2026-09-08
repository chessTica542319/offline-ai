package com.offlineai.app.ui.chat

data class ChatCodeBlock(
    val language: String,
    val code: String
)

fun parseChatCodeBlocks(text: String): List<ChatCodeBlock> {
    val pattern =
        Regex(
            """```([^\r\n`]*)\r?\n([\s\S]*?)```"""
        )

    return pattern
        .findAll(text)
        .map { match ->

            val language =
                match
                    .groupValues[1]
                    .trim()

            val code =
                match
                    .groupValues[2]
                    .trimEnd()

            ChatCodeBlock(
                language =
                    if (language.isBlank()) {
                        "Code"
                    } else {
                        language
                    },
                code = code
            )
        }
        .toList()
}
