package com.offlineai.app.ui.chat

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

import com.offlineai.app.ai.AIEngineStatus

@Composable
fun AIStatusText(
    status: AIEngineStatus
) {
    val text =
        when (status) {
            AIEngineStatus.IDLE ->
                "AI idle"

            AIEngineStatus.LOADING ->
                "Loading AI..."

            AIEngineStatus.READY ->
                "AI ready"

            AIEngineStatus.GENERATING ->
                "AI thinking..."

            AIEngineStatus.STOPPING ->
                "Stopping..."

            AIEngineStatus.ERROR ->
                "AI error"
        }

    val color =
        when (status) {
            AIEngineStatus.ERROR ->
                MaterialTheme.colorScheme.error

            AIEngineStatus.GENERATING,
            AIEngineStatus.LOADING,
            AIEngineStatus.STOPPING ->
                MaterialTheme.colorScheme.primary

            else ->
                Color(0xFF68736D)
        }

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color
    )
}
