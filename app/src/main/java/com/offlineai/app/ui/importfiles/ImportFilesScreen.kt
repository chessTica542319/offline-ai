package com.offlineai.app.ui.importfiles

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.offlineai.app.ui.components.AppTopBar

private val AllowedMimeTypes = arrayOf(
    "application/pdf",

    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",

    "text/plain",
    "application/rtf",

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

@Composable
fun ImportFilesScreen(
    onOpenDrawer: () -> Unit,
    onContinue: (List<Uri>) -> Unit
) {

    var selectedFiles by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->

        if (uris.isNotEmpty()) {
            selectedFiles = selectedFiles + uris
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        AppTopBar(
            title = "Import Files",
            onOpenDrawer = onOpenDrawer
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Import your study materials",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF101110)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Select lessons, notes, presentations, spreadsheets, or images to add to your private knowledge base.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF68736D)
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            SelectFilesCard(
                onSelectFiles = {
                    filePicker.launch(AllowedMimeTypes)
                }
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            if (selectedFiles.isEmpty()) {

                EmptyFilesCard()

            } else {

                Text(
                    text = "Selected files",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF101110)
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(
                        items = selectedFiles,
                        key = { it.toString() }
                    ) { uri ->

                        SelectedFileItem(
                            uri = uri,
                            onRemove = {
                                selectedFiles =
                                    selectedFiles.filterNot { it == uri }
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = {
                        onContinue(selectedFiles)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "Continue"
                    )

                    Spacer(
                        modifier = Modifier.height(0.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SelectFilesCard(
    onSelectFiles: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F7F4)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = "Select files",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Choose your study files",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF101110)
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "You can select one or multiple supported files.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF68736D)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = onSelectFiles,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    text = "Select Files"
                )
            }
        }
    }
}

@Composable
private fun EmptyFilesCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = Color(0xFF68736D)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "No files selected",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF101110)
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Your selected study materials will appear here.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF68736D)
            )
        }
    }
}

@Composable
private fun SelectedFileItem(
    uri: Uri,
    onRemove: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAF8)
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
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            Text(
                text = uri.lastPathSegment ?: "Selected file",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF101110)
            )

            IconButton(
                onClick = onRemove
            ) {

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove file",
                    tint = Color(0xFF68736D)
                )
            }
        }
    }
}
