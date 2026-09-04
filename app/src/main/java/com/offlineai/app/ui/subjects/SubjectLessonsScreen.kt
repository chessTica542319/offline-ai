package com.offlineai.app.ui.subjects

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.offlineai.app.data.database.StudyContentEntity
import com.offlineai.app.data.database.SubjectEntity
import com.offlineai.app.data.repository.StudyRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class ContentSort {
    LATEST,
    OLDEST,
    NAME_ASC,
    NAME_DESC,
    CHARACTERS_LARGEST,
    CHARACTERS_SMALLEST
}

@Composable
fun SubjectLessonsScreen(
    subject: SubjectEntity,
    repository: StudyRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    onOpenLesson: (com.offlineai.app.data.database.LessonEntity) -> Unit,
    onEditContent: (StudyContentEntity) -> Unit,
    onCreateTextFile: () -> Unit
) {
    var contents by remember {
        mutableStateOf<List<StudyContentEntity>>(emptyList())
    }

    var sortMenuExpanded by remember {
        mutableStateOf(false)
    }

    var addMenuExpanded by remember {
        mutableStateOf(false)
    }

    var sortOption by remember {
        mutableStateOf(ContentSort.LATEST)
    }

    var selectedContent by remember {
        mutableStateOf<StudyContentEntity?>(null)
    }

    var deleteContent by remember {
        mutableStateOf<StudyContentEntity?>(null)
    }

    var detailsContent by remember {
        mutableStateOf<StudyContentEntity?>(null)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(subject.id) {
        contents = repository.getStudyContentsForSubject(subject.id)
    }

    val sortedContents = remember(contents, sortOption) {
        when (sortOption) {
            ContentSort.LATEST ->
                contents.sortedByDescending { it.createdAt }

            ContentSort.OLDEST ->
                contents.sortedBy { it.createdAt }

            ContentSort.NAME_ASC ->
                contents.sortedBy {
                    it.title.lowercase(Locale.getDefault())
                }

            ContentSort.NAME_DESC ->
                contents.sortedByDescending {
                    it.title.lowercase(Locale.getDefault())
                }

            ContentSort.CHARACTERS_LARGEST ->
                contents.sortedByDescending {
                    it.text.length
                }

            ContentSort.CHARACTERS_SMALLEST ->
                contents.sortedBy {
                    it.text.length
                }
        }
    }

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
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF101110)
                )

                Text(
                    text = "${contents.size} file" +
                        if (contents.size == 1) "" else "s",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF68736D)
                )
            }

            BoxWithMenu(
                expanded = addMenuExpanded,
                onExpandedChange = {
                    addMenuExpanded = it
                },
                onCreateTextFile = {
                    addMenuExpanded = false
                    onCreateTextFile()
                }
            )

            IconButton(
                onClick = {
                    sortMenuExpanded = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Sort files"
                )
            }

            DropdownMenu(
                expanded = sortMenuExpanded,
                onDismissRequest = {
                    sortMenuExpanded = false
                }
            ) {
                SortItem(
                    text = "Latest first",
                    selected = sortOption == ContentSort.LATEST,
                    onClick = {
                        sortOption = ContentSort.LATEST
                        sortMenuExpanded = false
                    }
                )

                SortItem(
                    text = "Oldest first",
                    selected = sortOption == ContentSort.OLDEST,
                    onClick = {
                        sortOption = ContentSort.OLDEST
                        sortMenuExpanded = false
                    }
                )

                SortItem(
                    text = "Name A–Z",
                    selected = sortOption == ContentSort.NAME_ASC,
                    onClick = {
                        sortOption = ContentSort.NAME_ASC
                        sortMenuExpanded = false
                    }
                )

                SortItem(
                    text = "Name Z–A",
                    selected = sortOption == ContentSort.NAME_DESC,
                    onClick = {
                        sortOption = ContentSort.NAME_DESC
                        sortMenuExpanded = false
                    }
                )

                SortItem(
                    text = "Most characters",
                    selected = sortOption == ContentSort.CHARACTERS_LARGEST,
                    onClick = {
                        sortOption = ContentSort.CHARACTERS_LARGEST
                        sortMenuExpanded = false
                    }
                )

                SortItem(
                    text = "Fewest characters",
                    selected = sortOption == ContentSort.CHARACTERS_SMALLEST,
                    onClick = {
                        sortOption = ContentSort.CHARACTERS_SMALLEST
                        sortMenuExpanded = false
                    }
                )
            }
        }

        if (sortedContents.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(100.dp))

                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "No files yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF101110)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Import study material into this subject to see it here.",
                    color = Color(0xFF68736D)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(
                    items = sortedContents,
                    key = { it.id }
                ) { content ->
                    StudyContentCard(
                        content = content,
                        onOpen = {
                            selectedContent = content
                        },
                        onEdit = {
                            onEditContent(content)
                        },
                        onDetails = {
                            detailsContent = content
                        },
                        onDelete = {
                            deleteContent = content
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
        AlertDialog(
            onDismissRequest = {
                selectedContent = null
            },
            title = {
                Text(content.title)
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "The study content is stored locally and ready to be used by the offline assistant.",
                        color = Color(0xFF68736D)
                    )

                    HorizontalDivider()

                    Text(
                        text = "Source: ${content.originalFileName}",
                        color = Color(0xFF101110)
                    )

                    Text(
                        text = "Type: ${content.sourceType}",
                        color = Color(0xFF68736D)
                    )

                    Text(
                        text = "Characters: ${content.text.length}",
                        color = Color(0xFF68736D)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedContent = null
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }

    detailsContent?.let { content ->
        StudyContentDetailsDialog(
            content = content,
            repository = repository,
            onDismiss = {
                detailsContent = null
            }
        )
    }

    deleteContent?.let { content ->
        AlertDialog(
            onDismissRequest = {
                deleteContent = null
            },
            title = {
                Text("Delete study content?")
            },
            text = {
                Text(
                    "This will permanently remove \"${content.title}\" from this subject. The original file is not deleted from your device."
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deleteContent = null
                    }
                ) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = content.id

                        deleteContent = null

                        scope.launch {
                            repository.deleteStudyContent(id)

                            contents =
                                repository.getStudyContentsForSubject(
                                    subject.id
                                )
                        }
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = Color(0xFFB3261E)
                    )
                }
            }
        )
    }
}

@Composable
private fun BoxWithMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCreateTextFile: () -> Unit
) {
    androidx.compose.foundation.layout.Box {
        IconButton(
            onClick = {
                onExpandedChange(true)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add file"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onExpandedChange(false)
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text("New Text File")
                },
                onClick = onCreateTextFile
            )
        }
    }
}

@Composable
private fun SortItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = if (selected) {
                    "✓  $text"
                } else {
                    text
                }
            )
        },
        onClick = onClick
    )
}

@Composable
private fun StudyContentCard(
    content: StudyContentEntity,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF101110)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = content.originalFileName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF68736D)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${content.sourceType} • ${content.text.length} characters",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF68736D)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatDate(content.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF68736D)
                )
            }

            IconButton(
                onClick = {
                    menuExpanded = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "File options"
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
                        Text("Edit")
                    },
                    onClick = {
                        menuExpanded = false
                        onEdit()
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
                            color = Color(0xFFB3261E)
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

@Composable
private fun StudyContentDetailsDialog(
    content: StudyContentEntity,
    repository: StudyRepository,
    onDismiss: () -> Unit
) {
    var lessonName by remember {
        mutableStateOf("Loading...")
    }

    LaunchedEffect(content.lessonId) {
        lessonName =
            repository.getLesson(content.lessonId)?.name
                ?: "Unknown lesson"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("File details")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailRow("Title", content.title)
                DetailRow("Original file", content.originalFileName)
                DetailRow("Type", content.sourceType)
                DetailRow("Characters", content.text.length.toString())
                DetailRow("Lesson", lessonName)
                DetailRow("Created", formatDate(content.createdAt))
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

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat(
        "MMM d, yyyy • h:mm a",
        Locale.getDefault()
    ).format(Date(timestamp))
}
