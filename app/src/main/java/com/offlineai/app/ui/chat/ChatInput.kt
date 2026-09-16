package com.offlineai.app.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onPlus: () -> Unit,
    onLimitReached: () -> Unit,
    isGenerating: Boolean,
    enabled: Boolean
) {
    val canSend =
        enabled &&
            !isGenerating &&
            value.isNotBlank()

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 12.dp
                ),
        shape =
            RoundedCornerShape(
                24.dp
            ),
        color =
            Color(0xFFF3F7F4),
        tonalElevation =
            3.dp,
        shadowElevation =
            6.dp
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 6.dp,
                        vertical = 6.dp
                    ),
            verticalAlignment =
                Alignment.Bottom
        ) {

            IconButton(
                onClick = onPlus,
                modifier =
                    Modifier
                        .size(44.dp)
                        .align(
                            Alignment.Bottom
                        )
            ) {
                Icon(
                    imageVector =
                        Icons.Default.Add,
                    contentDescription =
                        "Add attachment",
                    tint =
                        MaterialTheme
                            .colorScheme
                            .primary,
                    modifier =
                        Modifier.size(24.dp)
                )
            }

            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .heightIn(
                            min = 44.dp,
                            max = 132.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                18.dp
                            )
                        )
                        .background(
                            Color.White
                        )
                        .padding(
                            horizontal = 14.dp,
                            vertical = 10.dp
                        )
            ) {

                if (value.isBlank()) {
                    Text(
                        text =
                            when {
                                isGenerating ->
                                    "Ask anything..."

                                !enabled ->
                                    "Session response limit reached"

                                else ->
                                    "Ask anything..."
                            },
                        color =
                            Color(0xFF8A938E),
                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    enabled = true,
                    maxLines = 5,
                    textStyle =
                        TextStyle(
                            color =
                                Color(0xFF101110),
                            fontSize =
                                MaterialTheme
                                    .typography
                                    .bodyLarge
                                    .fontSize
                        ),
                    keyboardOptions =
                        KeyboardOptions(
                            imeAction =
                                ImeAction.Default
                        ),
                    keyboardActions =
                        KeyboardActions(
                            onDone = {
                                if (canSend) {
                                    onSend()
                                }
                            }
                        )
                )

                if (!enabled && !isGenerating) {
                    Box(
                        modifier =
                            Modifier
                                .matchParentSize()
                                .clip(
                                    RoundedCornerShape(
                                        18.dp
                                    )
                                )
                               .background(Color.White.copy(alpha = 0.72f))
                           )
                }
            }

            IconButton(
                onClick = {
                    if (isGenerating) {
                        onStop()
                    } else if (canSend) {
                        onSend()
                    } else if (!enabled) {
                        onLimitReached()
                    }
                },
                enabled =
                    isGenerating ||
                        canSend ||
                        !enabled,
                modifier =
                    Modifier
                        .size(44.dp)
                        .align(
                            Alignment.Bottom
                        )
            ) {

                if (isGenerating) {

                    Surface(
                        modifier =
                            Modifier.size(36.dp),
                        shape =
                            CircleShape,
                        color =
                            Color(0xFFE8F1EB)
                    ) {
                        Box(
                            modifier =
                                Modifier.fillMaxWidth(),
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.Stop,
                                contentDescription =
                                    "Stop response",
                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .error,
                                modifier =
                                    Modifier.size(20.dp)
                                )
                            }
                        }

                } else {

                    Surface(
                        modifier =
                            Modifier.size(36.dp),
                        shape =
                            CircleShape,
                        color =
                            if (canSend) {
                                MaterialTheme
                                    .colorScheme
                                    .primary
                            } else {
                                Color(0xFFDDE5DF)
                            }
                    ) {
                        Box(
                            modifier =
                                Modifier.fillMaxWidth(),
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.Send,
                                contentDescription =
                                    "Send",
                                tint =
                                    if (canSend) {
                                        Color.White
                                    } else {
                                        Color(0xFF89938D)
                                    },
                                modifier =
                                    Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
