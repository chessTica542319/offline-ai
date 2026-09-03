package com.offlineai.app.ui.importfiles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ContentReviewScreen(
    fileName: String,
    sourceType: String,
    extractedText: String,
    currentIndex: Int,
    totalFiles: Int,
    initialTitle: String? = null,
    onBack: () -> Unit,
    onContinue: (String, String) -> Unit
) {
    var title by remember(
        fileName,
        initialTitle
    ) {
        mutableStateOf(
            initialTitle
                ?.ifBlank { null }
                ?: fileName
                    .substringBeforeLast(".")
                    .ifBlank {
                        "Study Content"
                    }
        )
    }

    var text by remember(extractedText) {
        mutableStateOf(extractedText)
    }

    val isLastFile =
        currentIndex == totalFiles

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Review Content",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF101110),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "File $currentIndex of $totalFiles",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF2DB55D)
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = fileName,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF68736D)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Content title",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF101110)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Title")
                },
                placeholder = {
                    Text("Enter a title for this content")
                },
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Extracted content",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF101110)
            )
        }

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    horizontal = 20.dp
                ),
            label = {
                Text("Text")
            },
            placeholder = {
                Text("Extracted text")
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
        ) {
            Text(
                text = "Source: $sourceType",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF68736D)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Button(
                onClick = {
                    onContinue(
                        title.trim(),
                        text.trim()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = title.trim().isNotEmpty() &&
                        text.trim().isNotEmpty()
            ) {
                Text(
                    text = if (isLastFile) {
                        "Finish Review"
                    } else {
                        "Save & Next"
                    }
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}
