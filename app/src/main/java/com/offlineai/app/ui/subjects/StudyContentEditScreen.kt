package com.offlineai.app.ui.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.offlineai.app.data.database.StudyContentEntity

@Composable
fun StudyContentEditScreen(
    content: StudyContentEntity,
    onBack: () -> Unit,
    onSave: (StudyContentEntity) -> Unit
) {
    var title by remember {
        mutableStateOf(content.title)
    }

    var text by remember {
        mutableStateOf(content.text)
    }

    val canSave = title.trim().isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp
                )
        ) {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = 8.dp,
                        vertical = 8.dp
                    )
            ) {
                Text(
                    text = "Edit study content",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF101110)
                )

                Text(
                    text = content.originalFileName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF68736D)
                )
            }

            TextButton(
                onClick = {
                    if (canSave) {
                        onSave(
                            content.copy(
                                title = title.trim(),
                                text = text
                            )
                        )
                    }
                },
                enabled = canSave
            ) {
                Text("Save")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Study title")
                },
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = {
                    Text("Study content")
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Original file: ${content.originalFileName}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF68736D)
            )

            Text(
                text = "Source type: ${content.sourceType}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF68736D)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = {
                    if (canSave) {
                        onSave(
                            content.copy(
                                title = title.trim(),
                                text = text
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canSave
            ) {
                Text("Save changes")
            }
        }
    }
}
