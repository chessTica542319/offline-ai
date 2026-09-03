package com.offlineai.app.ui.importfiles

import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.offlineai.app.ui.components.AppTopBar

@Composable
fun ImportFilesScreen(
    selectedFiles: List<Uri>,
    onFilesChanged: (List<Uri>) -> Unit,
    onOpenDrawer: () -> Unit,
    onContinue: (List<Uri>) -> Unit,
    onTakePhoto: () -> Unit
) {
    val filePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->

            if (uris.isNotEmpty()) {
                val combinedFiles =
                    (selectedFiles + uris).distinctBy {
                        it.toString()
                    }

                onFilesChanged(combinedFiles)
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppTopBar(
            title = "Import Files",
            onOpenDrawer = onOpenDrawer
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Add Study Material",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Import documents or take a photo of a book page.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF7F9F7)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Import Files",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "PDF, DOC, DOCX, TXT, RTF, images, PowerPoint, Excel, and CSV files.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Button(
                            onClick = {
                                filePicker.launch(
                                    arrayOf(
                                        "application/pdf",
                                        "text/plain",
                                        "text/rtf",
                                        "application/rtf",
                                        "application/msword",
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                        "image/jpeg",
                                        "image/png",
                                        "image/webp",
                                        "image/heic",
                                        "application/vnd.ms-powerpoint",
                                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                                        "application/vnd.ms-excel",
                                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                        "text/csv"
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text("Choose Files")
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF7F9F7)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Take a Photo",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "Capture a book page and add it to the same study import.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        OutlinedButton(
                            onClick = onTakePhoto,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text("Take Photo")
                        }
                    }
                }
            }

            if (selectedFiles.isNotEmpty()) {
                item {
                    Text(
                        text = "${selectedFiles.size} file" +
                                if (selectedFiles.size == 1) "" else "s" +
                                " selected",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(
                    items = selectedFiles,
                    key = {
                        it.toString()
                    }
                ) { uri ->

                    SelectedFileRow(
                        uri = uri,
                        onRemove = {
                            onFilesChanged(
                                selectedFiles.filterNot {
                                    it == uri
                                }
                            )
                        }
                    )
                }

                item {
                    Button(
                        onClick = {
                            onContinue(selectedFiles)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue")
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )
                }
            } else {
                item {
                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedFileRow(
    uri: Uri,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F9F7)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 14.dp,
                    top = 10.dp,
                    bottom = 10.dp,
                    end = 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = uri.lastPathSegment
                    ?: "Selected file",
                modifier = Modifier
                    .weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF101110)
            )

            IconButton(
                onClick = onRemove
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove file"
                )
            }
        }
    }
}
