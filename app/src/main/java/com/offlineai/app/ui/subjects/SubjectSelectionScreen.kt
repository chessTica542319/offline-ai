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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.offlineai.app.data.database.SubjectEntity
import com.offlineai.app.data.repository.StudyRepository
import kotlinx.coroutines.launch

@Composable
fun SubjectSelectionScreen(
    selectedFilesCount: Int,
    repository: StudyRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    onSubjectSelected: (SubjectEntity) -> Unit
) {
    val scope = rememberCoroutineScope()

    var subjects by remember {
        mutableStateOf<List<SubjectEntity>>(emptyList())
    }

    var showCreateDialog by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    suspend fun loadSubjects() {
        subjects = repository.getSubjects()
    }

    LaunchedEffect(Unit) {
        loadSubjects()
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
                text = "Subjects",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        if (subjects.isEmpty()) {

            EmptySubjectsState(
                selectedFilesCount = selectedFilesCount,
                onCreateSubject = {
                    errorMessage = null
                    showCreateDialog = true
                }
            )

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Choose a Subject",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${subjects.size} subject" +
                                    if (subjects.size == 1) "" else "s",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            errorMessage = null
                            showCreateDialog = true
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text("Create")
                    }
                }

                if (selectedFilesCount > 0) {

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "$selectedFilesCount file" +
                                if (selectedFilesCount == 1) "" else "s" +
                                " selected for import",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    items(
                        items = subjects,
                        key = { it.id }
                    ) { subject ->

                        SubjectCard(
                            subject = subject,
                            onClick = {
                                onSubjectSelected(subject)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {

        CreateSubjectDialog(
            errorMessage = errorMessage,

            onDismiss = {
                showCreateDialog = false
                errorMessage = null
            },

            onCreate = { name, description ->

                scope.launch {

                    try {

                        val subjectId = repository.createSubject(
                            name = name,
                            description = description
                        )

                        val subject = repository.getSubject(subjectId)

                        if (subject != null) {
                            showCreateDialog = false
                            errorMessage = null
                            onSubjectSelected(subject)
                        } else {
                            errorMessage = "Unable to load the new subject."
                        }

                    } catch (exception: IllegalArgumentException) {

                        errorMessage =
                            exception.message
                                ?: "Unable to create subject."
                    }
                }
            }
        )
    }
}

@Composable
private fun EmptySubjectsState(
    selectedFilesCount: Int,
    onCreateSubject: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = null,
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "No subjects yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Create your first subject to organize your study materials.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (selectedFilesCount > 0) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "$selectedFilesCount file" +
                            if (selectedFilesCount == 1) "" else "s" +
                            " selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = onCreateSubject,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Create Your First Subject"
                )
            }
        }
    }
}

@Composable
private fun SubjectCard(
    subject: SubjectEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
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
                imageVector = Icons.Default.Book,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (subject.description.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = subject.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "0 files",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CreateSubjectDialog(
    errorMessage: String?,
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
            Text("Create Subject")
        },

        text = {

            Column {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Subject name")
                    },
                    singleLine = true,
                    trailingIcon = {

                        if (name.isNotEmpty()) {

                            IconButton(
                                onClick = {
                                    name = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear"
                                )
                            }
                        }
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Description (optional)")
                    }
                )

                if (errorMessage != null) {

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },

        confirmButton = {

            TextButton(
                onClick = {
                    onCreate(
                        name.trim(),
                        description.trim()
                    )
                },
                enabled = name.trim().isNotEmpty()
            ) {
                Text("Create")
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
