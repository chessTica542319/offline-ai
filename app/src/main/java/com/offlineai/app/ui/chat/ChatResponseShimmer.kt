package com.offlineai.app.ui.chat

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatResponseShimmer() {

    val transition =
        rememberInfiniteTransition(
            label = "responseShimmer"
        )

    val shimmerPosition by
        transition.animateFloat(
            initialValue = -1f,
            targetValue = 2f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = 1200,
                            easing =
                                FastOutSlowInEasing
                        ),
                    repeatMode =
                        RepeatMode.Restart
                ),
            label = "shimmerPosition"
        )

    val shimmerBrush =
        Brush.linearGradient(
            colors =
                listOf(
                    Color(0xFFE1E8E3),
                    Color(0xFFF4F7F5),
                    Color(0xFFC9D8CE),
                    Color(0xFFF4F7F5),
                    Color(0xFFE1E8E3)
                ),
            start =
                Offset(
                    shimmerPosition * 700f,
                    0f
                ),
            end =
                Offset(
                    shimmerPosition * 700f + 350f,
                    0f
                )
        )

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        color =
            Color.Transparent
    ) {

        Column(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            ShimmerLine(
                modifier =
                    Modifier
                        .fillMaxWidth(0.92f)
                        .height(12.dp),
                brush = shimmerBrush
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            ShimmerLine(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                brush = shimmerBrush
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            ShimmerLine(
                modifier =
                    Modifier
                        .fillMaxWidth(0.76f)
                        .height(12.dp),
                brush = shimmerBrush
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            ShimmerLine(
                modifier =
                    Modifier
                        .fillMaxWidth(0.58f)
                        .height(12.dp),
                brush = shimmerBrush
            )
        }
    }
}

@Composable
private fun ShimmerLine(
    modifier: Modifier,
    brush: Brush
) {

    Spacer(
        modifier =
            modifier
                .clip(
                    RoundedCornerShape(
                        6.dp
                    )
                )
                .background(
                    brush
                )
    )
}
