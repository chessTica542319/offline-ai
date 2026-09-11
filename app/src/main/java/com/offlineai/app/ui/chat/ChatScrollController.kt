package com.offlineai.app.ui.chat

import androidx.compose.foundation.lazy.LazyListState
import kotlinx.coroutines.delay

suspend fun scrollToLatestAiResponse(
    listState: LazyListState,
    messages: List<ChatMessage>
) {

    val latestAiIndex =
        messages
            .indexOfLast {
                !it.isUser
            }

    if (latestAiIndex < 0) {
        return
    }

    listState.animateScrollToItem(
        latestAiIndex
    )

    delay(100)

    listState.animateScrollToItem(
        latestAiIndex,
        Int.MAX_VALUE
    )
}
