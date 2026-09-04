package com.offlineai.app.ui.subjects

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.offlineai.app.data.database.LessonEntity
import com.offlineai.app.data.database.SubjectEntity
import com.offlineai.app.data.repository.StudyRepository
import kotlinx.coroutines.launch

@Composable
fun LessonSelectionScreen(
    subject: SubjectEntity,
    selectedFilesCount: Int,
    repository: StudyRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    onLessonSelected: (LessonEntity) -> Unit
) {
    var lessons by remember {
        mutableStateOf<List<LessonEntity>>(emptyList())
    }

    var showCreateDialog by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(subject.id) {
        lessons = repository.getLessons(subject.id)
    }

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
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Select Lesson",
                    color = Color(0xFF101110)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subject.name,
                    color = Color(0xFF68736D)
                )
            }

            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "$selectedFilesCount file" +
                if (selectedFilesCount == 1) " ready to import"
                else "s ready to import",
            color = Color(0xFF68736D)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMessage = null
                showCreateDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Text("Create New Lesson")
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let { message ->
            Text(
                text = message,
                color = Color(0xFFB3261E),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }

        if (lessons.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No lessons yet",
                    color = Color(0xFF101110)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Create a lesson to organize this material.",
                    color = Color(0xFF68736D)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = lessons,
                    key = { it.id }
                ) { lesson ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onLessonSelected(lesson)
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = lesson.name,
                                color = Color(0xFF101110)
                            )

                            if (lesson.description.isNotBlank()) {
                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = lesson.description,
                                    color = Color(0xFF68736D)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateLessonDialog(
            onDismiss = {
                showCreateDialog = false
            },
            onCreate = { name, description ->
                scope.launch {
                    try {
                        val lessonId = repository.createLesson(
                            subjectId = subject.id,
                            name = name,
                            description = description
                        )

                        val createdLesson =
                            repository.getLesson(lessonId)

                        lessons = repository.getLessons(subject.id)

                        showCreateDialog = false
                        errorMessage = null

                        createdLesson?.let {
                            onLessonSelected(it)
                        }
                    } catch (exception: IllegalArgumentException) {
                        errorMessage =
                            exception.message
                                ?: "Unable to create lesson."
                    }
                }
            }
        )
    }
}

@Composable
private fun CreateLessonDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create New Lesson")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Lesson name")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Description (optional)")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.trim().isNotEmpty()) {
                        onCreate(
                            name.trim(),
                            description.trim()
                        )
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}
