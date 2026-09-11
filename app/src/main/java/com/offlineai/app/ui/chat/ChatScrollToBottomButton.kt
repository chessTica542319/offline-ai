package com.offlineai.app.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatScrollToBottomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        onClick = onClick,
        modifier =
            modifier
                .size(46.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape
                ),
        shape = CircleShape,
        color =
            Color.White.copy(
                alpha = 0.92f
            ),
        tonalElevation = 4.dp,
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    Color.White.copy(
                        alpha = 0.75f
                    )
            )
    ) {

        Box(
            modifier =
                Modifier.fillMaxSize(),
            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Default.ArrowDownward,
                contentDescription =
                    "Go to latest AI response",
                tint =
                    MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier.size(22.dp)
            )
        }
    }
}
