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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

    var showCreateForm by remember {
        mutableStateOf(false)
    }

    var renameSubject by remember {
        mutableStateOf<SubjectEntity?>(null)
    }

    var deleteSubject by remember {
        mutableStateOf<SubjectEntity?>(null)
    }

    var restoreSubject by remember {
        mutableStateOf<SubjectEntity?>(null)
    }

    var permanentDeleteSubject by remember {
        mutableStateOf<SubjectEntity?>(null)
    }

    var showDeleteAllConfirmation by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    fun refresh() {
        scope.launch {

            try {

                repository.cleanupExpiredDeletedSubjects()

                subjects = repository.getSubjects()

                deletedSubjects =
                    repository.getRecentlyDeletedSubjects()

            } catch (exception: Exception) {

                errorMessage =
                    exception.message
                        ?: "Unable to load subjects."
            }
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            /*
             * ---------------------------------------------------------
             * CREATE SUBJECT FORM
             * ---------------------------------------------------------
             */

            if (showCreateForm) {

                item {

                    CreateSubjectForm(
                        errorMessage = errorMessage,

                        onCancel = {
                            showCreateForm = false
                            errorMessage = null
                        },

                        onCreate = { name, description ->

                            scope.launch {

                                try {

                                    repository.createSubject(
                                        name = name,
                                        description = description
                                    )

                                    showCreateForm = false
                                    errorMessage = null

                                    refresh()

                                } catch (
                                    exception:
                                    IllegalArgumentException
                                ) {

                                    errorMessage =
                                        exception.message
                                            ?: "Unable to create subject."
                                }
                            }
                        }
                    )
                }

            } else {

                /*
                 * -----------------------------------------------------
                 * ACTIVE SUBJECT HEADER
                 * -----------------------------------------------------
                 */

                item {

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            if (subjects.isEmpty())
                                "Your Subjects"
                            else
                                "Your Subjects",

                        style =
                            MaterialTheme.typography.headlineSmall,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
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
                            showCreateForm = true

                        },

                        modifier =
                            Modifier.fillMaxWidth()
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

            /*
             * ---------------------------------------------------------
             * EMPTY ACTIVE SUBJECT STATE
             * ---------------------------------------------------------
             */

            if (subjects.isEmpty() && !showCreateForm) {

                item {

                    FirstSubjectState(
                        onCreate = {

                            errorMessage = null
                            showCreateForm = true

                        }
                    )
                }
            }

            /*
             * ---------------------------------------------------------
             * ACTIVE SUBJECTS
             * ---------------------------------------------------------
             */

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

            /*
             * ---------------------------------------------------------
             * RECENTLY DELETED
             * ---------------------------------------------------------
             *
             * IMPORTANT:
             * This section is independent of `subjects.isEmpty()`.
             *
             * Therefore it remains visible even after the user deletes
             * every active subject.
             */

            if (deletedSubjects.isNotEmpty()) {

                item {

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    HorizontalDivider()

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = "Recently Deleted",

                                style =
                                    MaterialTheme.typography
                                        .titleLarge,

                                fontWeight =
                                    FontWeight.Bold
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
                                modifier = Modifier.height(10.dp)
                            )

                            OutlinedButton(
                                onClick = {
                                    showDeleteAllConfirmation = true
                                },

                                modifier =
                                    Modifier.fillMaxWidth()
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Delete,

                                    contentDescription = null,

                                    tint =
                                        MaterialTheme.colorScheme.error
                                )

                                Spacer(
                                    modifier = Modifier.width(6.dp)
                                )

                                Text(
                                    text = "Delete All Permanently",

                                    color =
                                        MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                items(
                    items = deletedSubjects,

                    key = {
                        "deleted-${it.id}"
                    }
                ) { subject ->

                    DeletedSubjectCard(
                        subject = subject,

                        onRestore = {

                            restoreSubject = subject

                        },

                        onPermanentDelete = {

                            permanentDeleteSubject = subject

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

    /*
     * -------------------------------------------------------------
     * RENAME
     * -------------------------------------------------------------
     */

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

                    } catch (
                        exception:
                        IllegalArgumentException
                    ) {

                        errorMessage =
                            exception.message
                                ?: "Unable to rename subject."
                    }
                }
            }
        )
    }

    /*
     * -------------------------------------------------------------
     * DELETE ACTIVE SUBJECT CONFIRMATION
     * -------------------------------------------------------------
     */

    deleteSubject?.let { subject ->

        DeleteSubjectDialog(
            subject = subject,

            onDismiss = {

                deleteSubject = null

            },

            onConfirm = {

                scope.launch {

                    try {

                        repository.deleteSubject(
                            subject
                        )

                        deleteSubject = null

                        refresh()

                    } catch (exception: Exception) {

                        deleteSubject = null

                        errorMessage =
                            exception.message
                                ?: "Unable to delete subject."
                    }
                }
            }
        )
    }

    /*
     * -------------------------------------------------------------
     * RESTORE CONFIRMATION
     * -------------------------------------------------------------
     */

    restoreSubject?.let { subject ->

        RestoreSubjectDialog(
            subject = subject,

            onDismiss = {

                restoreSubject = null

            },

            onConfirm = {

                scope.launch {

                    try {

                        repository.restoreSubject(
                            subject
                        )

                        restoreSubject = null
                        errorMessage = null

                        refresh()

                    } catch (
                        exception:
                        IllegalArgumentException
                    ) {

                        restoreSubject = null

                        errorMessage =
                            exception.message
                                ?: "Unable to restore subject."
                    }
                }
            }
        )
    }

    /*
     * -------------------------------------------------------------
     * PERMANENT DELETE ONE
     * -------------------------------------------------------------
     */

    permanentDeleteSubject?.let { subject ->

        PermanentDeleteDialog(
            subject = subject,

            onDismiss = {

                permanentDeleteSubject = null

            },

            onConfirm = {

                scope.launch {

                    try {

                        repository.permanentlyDeleteSubject(
                            subject
                        )

                        permanentDeleteSubject = null

                        refresh()

                    } catch (exception: Exception) {

                        permanentDeleteSubject = null

                        errorMessage =
                            exception.message
                                ?: "Unable to permanently delete subject."
                    }
                }
            }
        )
    }

    /*
     * -------------------------------------------------------------
     * DELETE ALL RECENTLY DELETED
     * -------------------------------------------------------------
     */

    if (showDeleteAllConfirmation) {

        DeleteAllSubjectsDialog(

            count = deletedSubjects.size,

            onDismiss = {

                showDeleteAllConfirmation = false

            },

            onConfirm = {

                scope.launch {

                    try {

                        repository.permanentlyDeleteAllDeletedSubjects()

                        showDeleteAllConfirmation = false

                        refresh()

                    } catch (exception: Exception) {

                        showDeleteAllConfirmation = false

                        errorMessage =
                            exception.message
                                ?: "Unable to permanently delete subjects."
                    }
                }
            }
        )
    }
}

/*
 * =================================================================
 * CREATE SUBJECT FORM
 * =================================================================
 */

@Composable
private fun CreateSubjectForm(
    errorMessage: String?,
    onCancel: () -> Unit,
    onCreate: (String, String) -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFF7F9F7)
            )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = "Create New Subject",

                style =
                    MaterialTheme.typography
                        .titleLarge,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            OutlinedTextField(
                value = name,

                onValueChange = {
                    name = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

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

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Description (optional)")
                },

                minLines = 3
            )

            if (errorMessage != null) {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = errorMessage,

                    color =
                        MaterialTheme.colorScheme.error,

                    style =
                        MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.End
            ) {

                TextButton(
                    onClick = onCancel
                ) {
                    Text("Cancel")
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Button(
                    enabled =
                        name.trim().isNotEmpty(),

                    onClick = {

                        onCreate(
                            name.trim(),
                            description.trim()
                        )

                    }
                ) {

                    Text("Create Subject")
                }
            }
        }
    }
}

/*
 * =================================================================
 * FIRST SUBJECT STATE
 * =================================================================
 */

@Composable
private fun FirstSubjectState(
    onCreate: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFF7F9F7)
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector =
                    Icons.Default.Book,

                contentDescription = null,

                modifier = Modifier
                    .width(56.dp)
                    .height(56.dp),

                tint =
                    MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Start your first subject",

                style =
                    MaterialTheme.typography
                        .titleLarge,

                fontWeight =
                    FontWeight.Bold,

                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    "Create a subject to organize your lessons, files, and study materials.",

                style =
                    MaterialTheme.typography.bodyMedium,

                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant,

                textAlign =
                    TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Button(
                onClick = onCreate,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Add,

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

/*
 * =================================================================
 * ACTIVE SUBJECT CARD
 * =================================================================
 */

@Composable
private fun ManagedSubjectCard(
    subject: SubjectEntity,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFF7F9F7)
            )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.Top
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Book,

                    contentDescription = null,

                    tint =
                        MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = subject.name,

                        style =
                            MaterialTheme.typography
                                .titleMedium,

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    if (subject.description.isNotBlank()) {

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text =
                                subject.description,

                            style =
                                MaterialTheme.typography
                                    .bodyMedium,

                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }
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

/*
 * =================================================================
 * DELETED SUBJECT CARD
 * =================================================================
 */

@Composable
private fun DeletedSubjectCard(
    subject: SubjectEntity,
    onRestore: () -> Unit,
    onPermanentDelete: () -> Unit
) {

    val remaining =
        remainingDays(subject.deletedAt)

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFFFF8F8)
            )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = subject.name,

                style =
                    MaterialTheme.typography
                        .titleMedium,

                fontWeight =
                    FontWeight.SemiBold
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
                        "$remaining day${
                            if (remaining == 1L)
                                ""
                            else
                                "s"
                        } remaining",

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
                        text =
                            "Delete Permanently",

                        color =
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/*
 * =================================================================
 * RENAME DIALOG
 * =================================================================
 */

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

                    modifier =
                        Modifier.fillMaxWidth(),

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
                enabled =
                    name.trim().isNotEmpty(),

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

/*
 * =================================================================
 * DELETE SUBJECT CONFIRMATION
 * =================================================================
 */

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
                    text =
                        "Move to Recently Deleted",

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

/*
 * =================================================================
 * RESTORE CONFIRMATION
 * =================================================================
 */

@Composable
private fun RestoreSubjectDialog(
    subject: SubjectEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Restore Subject?")
        },

        text = {

            Text(
                "\"${subject.name}\" will be returned to your active subjects.\n\n" +
                        "Its subject data will no longer be in Recently Deleted."
            )
        },

        confirmButton = {

            TextButton(
                onClick = onConfirm
            ) {

                Text("Restore")
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

/*
 * =================================================================
 * PERMANENT DELETE ONE
 * =================================================================
 */

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
                        "will be permanently removed.\n\n" +
                        "This action cannot be undone. Please confirm carefully."
            )
        },

        confirmButton = {

            TextButton(
                onClick = onConfirm
            ) {

                Text(
                    text =
                        "Permanently Delete",

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

/*
 * =================================================================
 * DELETE ALL CONFIRMATION
 * =================================================================
 */

@Composable
private fun DeleteAllSubjectsDialog(
    count: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Delete All Permanently?")
        },

        text = {

            Text(
                "You are about to permanently delete " +
                        "$count recently deleted subject" +
                        if (count == 1) "." else "s." +
                        "\n\nThis action cannot be undone."
            )
        },

        confirmButton = {

            TextButton(
                onClick = onConfirm
            ) {

                Text(
                    text =
                        "Delete All Permanently",

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

/*
 * =================================================================
 * HELPERS
 * =================================================================
 */

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

    return (
            remaining +
                    24L * 60L * 60L * 1000L -
                    1L
            ) /
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
    ).format(
        Date(timestamp)
    )
}
