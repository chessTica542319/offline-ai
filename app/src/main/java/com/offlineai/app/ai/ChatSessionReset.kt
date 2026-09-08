package com.offlineai.app.ai

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.offlineai.app.ui.chat.ChatMessage


fun resetChatSession(
    messages: SnapshotStateList<ChatMessage>,
    sessionMemory: SessionMemory,
    clearDraft: () -> Unit,
    resetResponseCount: () -> Unit,
    resetScrollDistance: () -> Unit,
    stopGeneration: () -> Unit
) {
    stopGeneration()
    messages.clear()
    sessionMemory.clear()
    clearDraft()
    resetResponseCount()
    resetScrollDistance()
}
