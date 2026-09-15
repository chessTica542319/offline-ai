package com.offlineai.app.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp

@Composable
fun FloatingChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onPlus: () -> Unit,
    onLimitReached: () -> Unit,
    onScrollToBottom: () -> Unit,
    isGenerating: Boolean,
    enabled: Boolean,
    showScrollToBottom: Boolean,
    onHeightChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var componentHeight by remember {
        mutableIntStateOf(0)
    }

    val keyboardOffset = rememberChatKeyboardOffset()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .chatKeyboardOffset(keyboardOffset)
            .onSizeChanged { size ->
                if (componentHeight != size.height) {
                    componentHeight = size.height
                    onHeightChanged(size.height)
                }
            }
    ) {
        ChatInput(
            value = value,
            onValueChange = onValueChange,
            onSend = onSend,
            onStop = onStop,
            onPlus = onPlus,
            onLimitReached = onLimitReached,
            isGenerating = isGenerating,
            enabled = enabled
        )

      if (showScrollToBottom) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(
                        y = -(46.dp + 12.dp)
                    )
            ) {
                if (isGenerating) {
                    ThinkingDots(
                        onClick = onScrollToBottom
                    )
                } else {
                    ChatScrollToBottomButton(
                        onClick = onScrollToBottom
                    )
                }
            }
        } 
    }
}
