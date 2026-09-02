
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.offlineai.app.ui.components.AppTopBar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SubjectsScreen(
    repository: StudyRepository,
    onOpenDrawer: () -> Unit
) {

    val scope = rememberCoroutineScope()

    var subjects by remember {
        mutableStateOf<List<SubjectEntity>>(emptyList())
    }

    var deletedSubjects by remember {
        mutableStateOf<List<SubjectEntity>>(emptyList())
    }

    var showCreate by remember {
        mutableStateOf(false)
    }

    var renameSubject by remember {
        mutableStateOf<SubjectEntity?>(null)
    }

    var deleteSubject by remember {
        mutableStateOf<SubjectEntity?>(null)
    }

    var permanentDeleteSubject by remember {
        mutableStateOf<SubjectEntity?>(null)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    fun refresh() {

        scope.launch {

            repository.cleanupExpiredDeletedSubjects()

            subjects = repository.getSubjects()

            deletedSubjects =
                repository.getRecentlyDeletedSubjects()
        }
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        AppTopBar(
            title = "Subjects",
            onOpenDrawer = onOpenDrawer
        )

        if (subjects.isEmpty()) {

            FirstSubjectState(
                onCreate = {
                    errorMessage = null
                    showCreate = true
                }
            )

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                item {

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp)
                        ) {

                            Text(
                                text = "Your Subjects",
                                style =
                                    MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text =
                                    "${subjects.size} active subject" +
                                            if (subjects.size == 1)
                                                ""
                                            else
                                                "s",

                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Button(
                                onClick = {
                                    errorMessage = null
                                    showCreate = true
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Add,
                                    contentDescription = null
                                )

                                Spacer(
                                    modifier = Modifier.width(6.dp)
                                )

                                Text("New Subject")
                            }
                        }
                    }
                }

                items(
                    items = subjects,
                    key = { it.id }
                ) { subject ->

                    ManagedSubjectCard(
                        subject = subject,

                        onRename = {
                            errorMessage = null
                            renameSubject = subject
                        },

                        onDelete = {
                            errorMessage = null
                            deleteSubject = subject
                        }
                    )
                }

                if (deletedSubjects.isNotEmpty()) {

                    item {

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        HorizontalDivider()

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        Text(
                            text = "Recently Deleted",
                            style =
                                MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Deleted subjects are kept for 14 days.",
                            style =
                                MaterialTheme.typography.bodyMedium,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }

                    items(
                        items = deletedSubjects,
                        key = { "deleted-${it.id}" }
                    ) { subject ->

                        DeletedSubjectCard(
                            subject = subject,

                            onRestore = {

                                scope.launch {

                                    try {

                                        repository.restoreSubject(
                                            subject
                                        )

                                        refresh()

                                    } catch (exception:
                                        IllegalArgumentException) {

                                        errorMessage =
                                            exception.message
                                                ?: "Unable to restore subject."
                                    }
                                }
                            },

                            onPermanentDelete = {
                                permanentDeleteSubject =
                                    subject
                            }
                        )
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }

    if (showCreate) {

        CreateSubjectDialog(
            errorMessage = errorMessage,

            onDismiss = {
                showCreate = false
                errorMessage = null
            },

            onCreate = { name, description ->

                scope.launch {

                    try {

                        repository.createSubject(
                            name,
                            description
                        )

                        showCreate = false
                        errorMessage = null
                        refresh()

                    } catch (exception:
                        IllegalArgumentException) {

                        errorMessage =
                            exception.message
                                ?: "Unable to create subject."
                    }
                }
            }
        )
    }

    renameSubject?.let { subject ->

        RenameSubjectDialog(
            subject = subject,
            errorMessage = errorMessage,

            onDismiss = {
                renameSubject = null
                errorMessage = null
            },

            onRename = { newName ->

                scope.launch {

                    try {

                        repository.renameSubject(
                            subject,
                            newName
                        )

                        renameSubject = null
                        errorMessage = null
                        refresh()

                    } catch (exception:
                        IllegalArgumentException) {

                        errorMessage =
                            exception.message
                                ?: "Unable to rename subject."
                    }
                }
            }
        )
    }

    deleteSubject?.let { subject ->

        DeleteSubjectDialog(
            subject = subject,

            onDismiss = {
                deleteSubject = null
            },

            onConfirm = {

                scope.launch {

                    repository.deleteSubject(subject)

                    deleteSubject = null

                    refresh()
                }
            }
        )
    }

    permanentDeleteSubject?.let { subject ->

        PermanentDeleteDialog(
            subject = subject,

            onDismiss = {
                permanentDeleteSubject = null
            },

            onConfirm = {

                scope.launch {

                    repository.permanentlyDeleteSubject(
                        subject
                    )

                    permanentDeleteSubject = null

                    refresh()
                }
            }
        )
    }
}

@Composable
private fun FirstSubjectState(
    onCreate: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),

        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
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
                text = "Start your first subject",
                style =
                    MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    "Create a subject to organize your lessons, files, and study materials.",

                style =
                    MaterialTheme.typography.bodyLarge,

                color =
                    MaterialTheme.colorScheme.onSurfaceVariant,

                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = onCreate,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text("Create Your First Subject")
            }
        }
    }
}

@Composable
private fun ManagedSubjectCard(
    subject: SubjectEntity,
    onRename: () -> Unit,
    onDelete: () -> Unit
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
                .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp)
            ) {

                Text(
                    text = subject.name,
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (subject.description.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = subject.description,
                        style =
                            MaterialTheme.typography.bodyMedium,

                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row {

                    TextButton(
                        onClick = onRename
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Edit,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )

                        Text("Rename")
                    }

                    TextButton(
                        onClick = onDelete
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Delete,
                            contentDescription = null,

                            tint =
                                MaterialTheme.colorScheme.error
                        )

                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )

                        Text(
                            text = "Delete",
                            color =
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeletedSubjectCard(
    subject: SubjectEntity,
    onRestore: () -> Unit,
    onPermanentDelete: () -> Unit
) {

    val remaining =
        remainingDays(subject.deletedAt)

    Card(
        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8F8)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = subject.name,
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    "Deleted ${formatDate(subject.deletedAt)}",

                style =
                    MaterialTheme.typography.bodySmall,

                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            Text(
                text =
                    if (remaining <= 0)
                        "Scheduled for permanent deletion."
                    else
                        "$remaining day${if (remaining == 1L) "" else "s"} remaining",

                style =
                    MaterialTheme.typography.bodySmall,

                color =
                    MaterialTheme.colorScheme.error
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row {

                TextButton(
                    onClick = onRestore
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Restore,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Text("Restore")
                }

                TextButton(
                    onClick = onPermanentDelete
                ) {

                    Text(
                        text = "Delete Permanently",
                        color =
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private fun remainingDays(
    deletedAt: Long?
): Long {

    if (deletedAt == null) {
        return 14
    }

    val expiry =
        deletedAt +
                StudyRepository.DELETION_RETENTION_MILLIS

    val remaining =
        expiry - System.currentTimeMillis()

    if (remaining <= 0) {
        return 0
    }

    return (remaining +
            24L * 60L * 60L * 1000L - 1L) /
            (24L * 60L * 60L * 1000L)
}

private fun formatDate(
    timestamp: Long?
): String {

    if (timestamp == null) {
        return "unknown date"
    }

    return SimpleDateFormat(
        "MMM d, yyyy • h:mm a",
        Locale.getDefault()
    ).format(Date(timestamp))
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
            Text("New Subject")
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

                    singleLine = true
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
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = errorMessage,
                        color =
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        },

        confirmButton = {

            TextButton(
                enabled = name.trim().isNotEmpty(),

                onClick = {
                    onCreate(
                        name.trim(),
                        description.trim()
                    )
                }
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

@Composable
private fun RenameSubjectDialog(
    subject: SubjectEntity,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {

    var name by remember {
        mutableStateOf(subject.name)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Rename Subject")
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

                    singleLine = true
                )

                if (errorMessage != null) {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = errorMessage,
                        color =
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        },

        confirmButton = {

            TextButton(
                enabled = name.trim().isNotEmpty(),

                onClick = {
                    onRename(name.trim())
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
private fun DeleteSubjectDialog(
    subject: SubjectEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Delete Subject?")
        },

        text = {

            Text(
                "Are you sure you want to delete \"${subject.name}\"?\n\n" +
                        "The subject will move to Recently Deleted. " +
                        "You can restore it for 14 days before it is permanently deleted."
            )
        },

        confirmButton = {

            TextButton(
                onClick = onConfirm
            ) {

                Text(
                    text = "Move to Recently Deleted",
                    color =
                        MaterialTheme.colorScheme.error
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

@Composable
private fun PermanentDeleteDialog(
    subject: SubjectEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Permanently Delete?")
        },

        text = {

            Text(
                "This is permanent.\n\n" +
                        "\"${subject.name}\" and its subject record " +
                        "will be permanently removed. This action cannot be undone.\n\n" +
                        "Please confirm carefully."
            )
        },

        confirmButton = {

            TextButton(
                onClick = onConfirm
            ) {

                Text(
                    text = "Permanently Delete",
                    color =
                        MaterialTheme.colorScheme.error
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Keep Subject")
            }
        }
    )
}

