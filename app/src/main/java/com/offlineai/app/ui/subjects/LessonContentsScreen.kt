package com.offlineai.app.ui.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.offlineai.app.data.database.LessonEntity
import com.offlineai.app.data.database.StudyContentEntity
import com.offlineai.app.data.database.SubjectEntity
import com.offlineai.app.data.repository.StudyRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LessonContentsScreen(
    subject: SubjectEntity,
    lesson: LessonEntity,
    repository: StudyRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    onOpenContent: (StudyContentEntity) -> Unit
) {
    val scope = rememberCoroutineScope()

    var contents by remember {
        mutableStateOf<List<StudyContentEntity>>(emptyList())
    }

    var selectedContent by remember {
        mutableStateOf<StudyContentEntity?>(null)
    }

    var showRenameDialog by remember {
        mutableStateOf(false)
    }

    var showDetailsDialog by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    fun refresh() {
        scope.launch {
            contents = repository.getStudyContents(lesson.id)
        }
    }

    LaunchedEffect(lesson.id) {
        refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = lesson.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF101110)
                )

                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF68736D)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${contents.size} study file" +
                    if (contents.size == 1) "" else "s",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF68736D)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        if (contents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "No study files yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Imported study material will appear here.",
                        color = Color(0xFF68736D)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = contents,
                    key = { it.id }
                ) { content ->

                    StudyContentCard(
                        content = content,
                        onOpen = {
                            onOpenContent(content)
                        },
                        onRename = {
                            selectedContent = content
                            errorMessage = null
                            showRenameDialog = true
                        },
                        onDetails = {
                            selectedContent = content
                            showDetailsDialog = true
                        },
                        onDelete = {
                            selectedContent = content
                            showDeleteDialog = true
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    selectedContent?.let { content ->

        if (showRenameDialog) {
            RenameContentDialog(
                content = content,
                errorMessage = errorMessage,
                onDismiss = {
                    showRenameDialog = false
                    selectedContent = null
                    errorMessage = null
                },
                onRename = { newTitle ->
                    scope.launch {
                        try {
                            repository.updateStudyContent(
                                content.copy(
                                    title = newTitle.trim()
                                )
                            )

                            showRenameDialog = false
                            selectedContent = null
                            errorMessage = null
                            refresh()
                        } catch (exception: Exception) {
                            errorMessage =
                                exception.message
                                    ?: "Unable to rename study file."
                        }
                    }
                }
            )
        }

        if (showDetailsDialog) {
            ContentDetailsDialog(
                content = content,
                subject = subject,
                lesson = lesson,
                onDismiss = {
                    showDetailsDialog = false
                    selectedContent = null
                }
            )
        }

        if (showDeleteDialog) {
            DeleteContentDialog(
                content = content,
                onDismiss = {
                    showDeleteDialog = false
                    selectedContent = null
                },
                onConfirm = {
                    scope.launch {
                        try {
                            repository.deleteStudyContent(content.id)

                            showDeleteDialog = false
                            selectedContent = null
                            refresh()
                        } catch (exception: Exception) {
                            showDeleteDialog = false
                            selectedContent = null
                            errorMessage =
                                exception.message
                                    ?: "Unable to delete study file."
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun StudyContentCard(
    content: StudyContentEntity,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDetails: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F9F7)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF101110)
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = content.originalFileName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF68736D)
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${content.sourceType} • ${formatDate(content.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF68736D)
                )
            }

            Box {
                IconButton(
                    onClick = {
                        menuExpanded = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = {
                        menuExpanded = false
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Open")
                        },
                        onClick = {
                            menuExpanded = false
                            onOpen()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Rename")
                        },
                        onClick = {
                            menuExpanded = false
                            onRename()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Details")
                        },
                        onClick = {
                            menuExpanded = false
                            onDetails()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                "Delete",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RenameContentDialog(
    content: StudyContentEntity,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    var title by remember {
        mutableStateOf(content.title)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Rename Study File")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = {
                        Text("Study title")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.trim().isNotEmpty(),
                onClick = {
                    onRename(title)
                }
            ) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ContentDetailsDialog(
    content: StudyContentEntity,
    subject: SubjectEntity,
    lesson: LessonEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Study File Details")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailRow("Title", content.title)
                DetailRow("Original file", content.originalFileName)
                DetailRow("Type", content.sourceType)
                DetailRow("Subject", subject.name)
                DetailRow("Lesson", lesson.name)
                DetailRow("Imported", formatDate(content.createdAt))
                DetailRow(
                    "Characters",
                    content.text.length.toString()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF68736D)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF101110)
        )
    }
}

@Composable
private fun DeleteContentDialog(
    content: StudyContentEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Study File?")
        },
        text = {
            Text(
                "Are you sure you want to delete \"${content.title}\"?\n\n" +
                    "The extracted study content will be permanently removed. " +
                    "This action cannot be undone."
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat(
        "MMM d, yyyy • h:mm a",
        Locale.getDefault()
    ).format(Date(timestamp))
}
