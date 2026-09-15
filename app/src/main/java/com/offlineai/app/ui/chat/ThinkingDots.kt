package com.offlineai.app.ui.chat

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ThinkingDots(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition =
        rememberInfiniteTransition(label = "thinkingDots")

    val dotOneAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, 0, FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotOne"
    )

    val dotTwoAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, 150, FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotTwo"
    )

    val dotThreeAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, 300, FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotThree"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .size(46.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape
            ),
        shape = CircleShape,
        color = Color(0xFF5F6964).copy(alpha = 0.62f),
        tonalElevation = 2.dp,
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.32f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThinkingDot(dotOneAlpha)
            ThinkingDotSpacer()
            ThinkingDot(dotTwoAlpha)
            ThinkingDotSpacer()
            ThinkingDot(dotThreeAlpha)
        }
    }
}

@Composable
private fun ThinkingDotSpacer() {
    androidx.compose.foundation.layout.Spacer(
        modifier = Modifier.size(4.dp)
    )
}

@Composable
private fun ThinkingDot(alpha: Float) {
    Surface(
        modifier = Modifier.size(5.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = alpha)
    ) {}
}
