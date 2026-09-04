package com.offlineai.app.ui.importfiles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Review Import",
                    color = Color(0xFF101110)
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "${reviewedContents.size} file" +
                        if (reviewedContents.size == 1) {
                            " ready"
                        } else {
                            "s ready"
                        },
                    color = Color(0xFF68736D)
                )
            }

            IconButton(
                onClick = onOpenDrawer
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Destination",
                    color = Color(0xFF68736D)
                )

                Text(
                    text = subject.name,
                    color = Color(0xFF101110)
                )

                if (subject.description.isNotBlank()) {
                    Text(
                        text = subject.description,
                        color = Color(0xFF68736D)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Content",
            color = Color(0xFF101110)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = reviewedContents,
                key = { it.fileUri.toString() }
            ) { content ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = content.title,
                            color = Color(0xFF101110)
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = content.originalFileName,
                            color = Color(0xFF68736D)
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = content.sourceType,
                            color = Color(0xFF68736D)
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm Import")
        }
    }
}
