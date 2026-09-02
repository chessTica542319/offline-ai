package com.offlineai.app.ui.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offlineai.app.ui.theme.GreenPrimary

@Composable
fun SplashScreen() {

    val infiniteTransition = rememberInfiniteTransition(
        label = "splashDots"
    )

    val dot1 by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dot2 by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600,
                delayMillis = 180
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dot3 by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600,
                delayMillis = 360
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Offline AI",
            style = MaterialTheme.typography.displaySmall,
            color = Color(0xFF101110)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = "Your Private Study Assistant",
            fontSize = 18.sp,
            color = Color(0xFF68736D)
        )

        Spacer(
            modifier = Modifier.height(44.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            SplashDot(
                scale = dot1
            )

            SplashDot(
                scale = dot2
            )

            SplashDot(
                scale = dot3
            )
        }
    }
}

@Composable
private fun SplashDot(
    scale: Float
) {

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(10.dp)
            .scale(scale)
            .background(
                color = GreenPrimary,
                shape = CircleShape
            )
    )
}
