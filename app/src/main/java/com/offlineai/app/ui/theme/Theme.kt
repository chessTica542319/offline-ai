package com.offlineai.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val OfflineAIColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = White,

    primaryContainer = LightGreenSurface,
    onPrimaryContainer = GreenDeep,

    secondary = GrayMuted,
    onSecondary = White,

    tertiary = GreenDeep,
    onTertiary = White,

    background = AppBackground,
    onBackground = Black,

    surface = White,
    onSurface = Black,

    surfaceVariant = LightGreenSurface,
    onSurfaceVariant = GrayMuted,

    outline = BorderGreen
)

@Composable
fun OfflineAITheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OfflineAIColors,
        typography = Typography(),
        content = content
    )
}
