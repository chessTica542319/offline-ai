package com.offlineai.app.ai

data class SessionMessage(
    val isUser: Boolean,
    val text: String
)

class SessionMemory {

    private val messages =
        mutableListOf<SessionMessage>()

    fun addUserMessage(
        text: String
    ) {
        val cleanText = text.trim()

        if (cleanText.isBlank()) {
            return
        }

        messages.add(
            SessionMessage(
                isUser = true,
                text = cleanText
            )
        )
    }

    fun addAssistantMessage(
        text: String
    ) {
        val cleanText = text.trim()

        if (cleanText.isBlank()) {
            return
        }

        messages.add(
            SessionMessage(
                isUser = false,
                text = cleanText
            )
        )
    }

    fun replaceLastAssistantMessage(
        text: String
    ) {
        val cleanText = text.trim()

        if (cleanText.isBlank()) {
            return
        }

        val index =
            messages.indexOfLast {
                !it.isUser
            }

        if (index >= 0) {
            messages[index] =
                SessionMessage(
                    isUser = false,
                    text = cleanText
                )
        } else {
            addAssistantMessage(cleanText)
        }
    }

    fun buildContext(
        maxCharacters: Int = 5000
    ): String {

        if (messages.isEmpty()) {
            return ""
        }

        val builder = StringBuilder()

        messages.asReversed().forEach { message ->

            val role =
                if (message.isUser) {
                    "User"
                } else {
                    "Assistant"
                }

            val entry =
                "$role: ${message.text}\n\n"

            if (
                builder.length +
                entry.length >
                maxCharacters
            ) {
                return@forEach
            }

            builder.insert(
                0,
                entry
            )
        }

        return builder
            .toString()
            .trim()
    }

    fun clear() {
        messages.clear()
    }

    fun isEmpty(): Boolean {
        return messages.isEmpty()
    }
}
