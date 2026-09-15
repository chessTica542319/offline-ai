package com.offlineai.app.ui.chat

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.offset

@Composable
fun rememberChatKeyboardOffset(): IntOffset {
    val density = LocalDensity.current

    val imeBottom =
        WindowInsets.ime.getBottom(density)

    val navigationBottom =
        WindowInsets.navigationBars.getBottom(density)

    val keyboardOffset =
        (imeBottom - navigationBottom)
            .coerceAtLeast(0)

    return remember(
        imeBottom,
        navigationBottom
    ) {
        IntOffset(
            x = 0,
            y = -keyboardOffset
        )
    }
}

fun Modifier.chatKeyboardOffset(
    offset: IntOffset
): Modifier {
    return offset {
        offset
    }
}
