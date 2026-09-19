package com.offlineai.app.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private data class AttachmentOption(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun AttachmentOptionsDialog(
    onDismiss: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    val options =
        listOf(
            AttachmentOption(
                title = "Camera",
                icon = Icons.Default.CameraAlt
            ),
            AttachmentOption(
                title = "Photos",
                icon = Icons.Default.PhotoLibrary
            ),
            AttachmentOption(
                title = "Files",
                icon = Icons.Default.Description
            )
        )

    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            )
    ) {
        Surface(
            modifier =
                Modifier.fillMaxWidth(),
            shape =
                MaterialTheme.shapes.extraLarge,
            color =
                Color.White,
            tonalElevation =
                6.dp,
            shadowElevation =
                8.dp
        ) {
            Column(
                modifier =
                    Modifier.padding(
                        vertical = 16.dp
                    )
            ) {
                Text(
                    text = "Add to chat",
                    style =
                        MaterialTheme.typography.titleLarge,
                    color =
                        Color(0xFF101110),
                    modifier =
                        Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 8.dp
                        )
                )

                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(
                                max = 272.dp
                            ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            4.dp
                        )
                ) {
                    items(
                        items = options,
                        key = {
                            it.title
                        }
                    ) { option ->

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOptionSelected(
                                            option.title
                                        )
                                    }
                                    .padding(
                                        horizontal = 20.dp,
                                        vertical = 12.dp
                                    ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector =
                                    option.icon,
                                contentDescription =
                                    option.title,
                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .primary,
                                modifier =
                                    Modifier.size(
                                        24.dp
                                    )
                            )

                            Text(
                                text = option.title,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyLarge,
                                color =
                                    Color(0xFF101110),
                                modifier =
                                    Modifier.padding(
                                        start = 16.dp
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
