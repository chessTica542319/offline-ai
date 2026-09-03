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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.offlineai.app.data.database.SubjectEntity
import com.offlineai.app.data.extraction.PendingStudyContent

@Composable
fun ImportReviewScreen(
    reviewedContents: List<PendingStudyContent>,
    subject: SubjectEntity,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
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
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onOpenDrawer
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open menu"
                )
            }

            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Review Import",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Import destination",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF7F9F7)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.width(14.dp)
                    )

                    Column {
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (subject.description.isNotBlank()) {
                            Spacer(
                                modifier = Modifier.height(3.dp)
                            )

                            Text(
                                text = subject.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Content to save",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "${reviewedContents.size} content" +
                        if (reviewedContents.size == 1) "" else "s",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = 12.dp,
                bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = reviewedContents,
                key = {
                    it.fileUri.toString()
                }
            ) { content ->
                ReviewedContentCard(
                    content = content
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
        ) {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = reviewedContents.isNotEmpty()
            ) {
                Text("Confirm Import")
            }
        }
    }
}

@Composable
private fun ReviewedContentCard(
    content: PendingStudyContent
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F9F7)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = content.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF101110)
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "Source: ${content.originalFileName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF68736D)
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Type: ${content.sourceType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF68736D)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Extracted text",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF101110)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = content.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF101110)
            )
        }
    }
}
