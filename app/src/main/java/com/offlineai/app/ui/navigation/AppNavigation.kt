package com.offlineai.app.ui.navigation

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.offlineai.app.data.database.AppDatabase
import com.offlineai.app.data.database.LessonEntity
import com.offlineai.app.data.database.SubjectEntity
import com.offlineai.app.data.database.StudyContentEntity
import com.offlineai.app.data.extraction.PendingStudyContent
import com.offlineai.app.data.repository.StudyRepository
import com.offlineai.app.ui.camera.CameraScreen
import com.offlineai.app.ui.chat.ChatScreen
import com.offlineai.app.ui.components.AppDrawer
import com.offlineai.app.ui.importfiles.ContentProcessingScreen
import com.offlineai.app.ui.importfiles.ContentReviewScreen
import com.offlineai.app.ui.importfiles.ImportFilesScreen
import com.offlineai.app.ui.importfiles.ImportReviewScreen
import com.offlineai.app.ui.subjects.LessonContentsScreen
import com.offlineai.app.ui.subjects.LessonSelectionScreen
import com.offlineai.app.ui.subjects.SubjectLessonsScreen
import com.offlineai.app.ui.subjects.SubjectSelectionScreen
import com.offlineai.app.ui.subjects.SubjectsScreen
import com.offlineai.app.ui.subjects.StudyContentEditScreen
import com.offlineai.app.ui.subjects.CreateTextFileScreen

import kotlinx.coroutines.launch

import android.content.Context

enum class AppScreen {
    CHAT,
    IMPORT_FILES,
    CONTENT_PROCESSING,
    CONTENT_REVIEW,
    SUBJECT_SELECTION,
    IMPORT_REVIEW,
    SUBJECT_LESSONS,
    STUDY_CONTENT_EDIT,
    CREATE_TEXT_FILE,
    LESSON_CONTENTS,
    CAMERA,
    SUBJECTS,
    SETTINGS,
    MORE
}

@Composable
fun AppNavigation() {
    var currentScreen by remember {
        mutableStateOf(AppScreen.CHAT)
    }

    var selectedFiles by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    var currentImportIndex by remember {
        mutableStateOf(0)
    }

    var selectedSubject by remember {
        mutableStateOf<SubjectEntity?>(null)
    }

    var selectedLesson by remember {
        mutableStateOf<LessonEntity?>(null)
    }

    var reviewedContents by remember {
        mutableStateOf<List<PendingStudyContent>>(emptyList())
    }

    var currentExtractedText by remember {
        mutableStateOf("")
    }

    var currentSourceType by remember {
        mutableStateOf("")
    }

    var currentFileName by remember {
        mutableStateOf("")
    }

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val database = remember {
        AppDatabase.getInstance(context)
    }

    val repository = remember {
        StudyRepository(
            database.subjectDao(),
            database.lessonDao(),
            database.studyContentDao()
        )
    }

    val knowledgeStats by repository
        .observeKnowledgeStats()
        .collectAsState(
            initial = com.offlineai.app.data.repository.KnowledgeStats()
        )

    var selectedStudyContent: StudyContentEntity? by remember {
            mutableStateOf(null)
        }

    var selectedSubjectForTextFile by remember {
            mutableStateOf<SubjectEntity?>(null)
        }



    var showQuitDialog by remember {
        mutableStateOf(false)
    }

    BackHandler {
        showQuitDialog = true
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = {
                showQuitDialog = false
            },
            title = {
                Text("Quit Offline AI?")
            },
            text = {
                Text(
                    "Your current session will be closed. " +
                        "Unsaved import and review progress will be lost."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val activity = context as? Activity
                        activity?.finishAndRemoveTask()
                    }
                ) {
                    Text(
                        text = "QUIT",
                        color = Color(0xFFD32F2F)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showQuitDialog = false
                    }
                ) {
                    Text("CANCEL")
                }
            }
        )
    }

    fun startImport(files: List<Uri>) {
        if (files.isEmpty()) {
            return
        }

        selectedFiles = files.distinctBy {
            it.toString()
        }

        currentImportIndex = 0
        reviewedContents = emptyList()
        currentExtractedText = ""
        currentSourceType = ""
        currentFileName = ""

        selectedSubject = null
        selectedLesson = null

        currentScreen = AppScreen.CONTENT_PROCESSING
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentScreen = currentScreen,
                knowledgeStats = knowledgeStats,
                onScreenSelected = { screen ->
                    currentScreen = screen

                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        when (currentScreen) {

            AppScreen.CHAT -> {
                ChatScreen(
                    knowledgeStats = knowledgeStats,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onImportFiles = {
                        currentScreen = AppScreen.IMPORT_FILES
                    }
                )
            }

            AppScreen.IMPORT_FILES -> {
                ImportFilesScreen(
                    selectedFiles = selectedFiles,
                    onFilesChanged = { files ->
                        selectedFiles = files
                    },
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onTakePhoto = {
                        currentScreen = AppScreen.CAMERA
                    },
                    onContinue = { files ->
                        startImport(files)
                    }
                )
            }

            AppScreen.CONTENT_PROCESSING -> {
                val currentFile =
                    selectedFiles.getOrNull(currentImportIndex)

                if (currentFile != null) {
                    ContentProcessingScreen(
                        fileUri = currentFile,
                        currentIndex = currentImportIndex + 1,
                        totalFiles = selectedFiles.size,
                        onSuccess = { result ->
                            currentExtractedText = result.text
                            currentSourceType = result.sourceType
                            currentFileName = result.fileName

                            currentScreen =
                                AppScreen.CONTENT_REVIEW
                        },
                        onBack = {
                            if (currentImportIndex > 0) {
                                currentImportIndex -= 1
                                currentScreen =
                                    AppScreen.CONTENT_REVIEW
                            } else {
                                currentScreen =
                                    AppScreen.IMPORT_FILES
                            }
                        }
                    )
                } else {
                    currentScreen =
                        AppScreen.IMPORT_FILES
                }
            }

            AppScreen.CONTENT_REVIEW -> {
                val currentReviewedContent =
                    reviewedContents.getOrNull(currentImportIndex)

                ContentReviewScreen(
                    fileName = currentFileName,
                    sourceType = currentSourceType,
                    extractedText = currentExtractedText,
                    currentIndex = currentImportIndex + 1,
                    totalFiles = selectedFiles.size,
                    initialTitle = currentReviewedContent?.title,
                    onBack = {
                        if (currentImportIndex > 0) {
                            val previousIndex =
                                currentImportIndex - 1

                            val previousContent =
                                reviewedContents[previousIndex]

                            currentImportIndex =
                                previousIndex

                            currentExtractedText =
                                previousContent.text

                            currentSourceType =
                                previousContent.sourceType

                            currentFileName =
                                previousContent.originalFileName

                            currentScreen =
                                AppScreen.CONTENT_REVIEW
                        } else {
                            currentScreen =
                                AppScreen.IMPORT_FILES
                        }
                    },
                    onContinue = { title, text ->

                      val fileUri =
    selectedFiles[currentImportIndex]

val content =
    PendingStudyContent(
        fileUri = fileUri,
        title = title,
        text = text,
        sourceType = currentSourceType,
        originalFileName = currentFileName,
        fileSize = getFileSize(
            context = context,
            uri = fileUri
        )
    ) 

                        reviewedContents =
                            if (
                                currentImportIndex <
                                reviewedContents.size
                            ) {
                                reviewedContents.mapIndexed {
                                        index,
                                        existingContent ->
                                    if (
                                        index ==
                                        currentImportIndex
                                    ) {
                                        content
                                    } else {
                                        existingContent
                                    }
                                }
                            } else {
                                reviewedContents + content
                            }

                        if (
                            currentImportIndex + 1 <
                            selectedFiles.size
                        ) {
                            currentImportIndex += 1

                            currentExtractedText = ""
                            currentSourceType = ""
                            currentFileName = ""

                            currentScreen =
                                AppScreen.CONTENT_PROCESSING
                        } else {
                            currentScreen =
                                AppScreen.SUBJECT_SELECTION
                        }
                    }
                )
            }

          AppScreen.SUBJECT_SELECTION -> {
    SubjectSelectionScreen(
        selectedFilesCount = reviewedContents.size,
        repository = repository,
        onOpenDrawer = {
            scope.launch {
                drawerState.open()
            }
        },
        onBack = {
            if (selectedFiles.isNotEmpty()) {
                currentImportIndex = selectedFiles.lastIndex
                currentScreen = AppScreen.CONTENT_REVIEW
            } else {
                currentScreen = AppScreen.IMPORT_FILES
            }
        },
        onSubjectSelected = { subject ->
            selectedSubject = subject
            currentScreen = AppScreen.IMPORT_REVIEW
        }
    )
}   

          AppScreen.IMPORT_REVIEW -> {
    val subject = selectedSubject

    if (subject != null) {
        ImportReviewScreen(
            reviewedContents = reviewedContents,
            subject = subject,
            onOpenDrawer = {
                scope.launch {
                    drawerState.open()
                }
            },
            onBack = {
                currentScreen = AppScreen.SUBJECT_SELECTION
            },
            onConfirm = {
                scope.launch {
                    try {
                        val generalLesson =
                            repository.getOrCreateGeneralLesson(
                                subjectId = subject.id
                            )

                        repository.saveStudyContents(
                            lessonId = generalLesson.id,
                            contents = reviewedContents
                        )

                        selectedFiles = emptyList()
                        reviewedContents = emptyList()
                        currentImportIndex = 0
                        currentExtractedText = ""
                        currentSourceType = ""
                        currentFileName = ""
                        selectedSubject = null
                        selectedLesson = null

                        currentScreen = AppScreen.CHAT
                    } catch (exception: Exception) {
                        currentScreen = AppScreen.IMPORT_REVIEW
                    }
                }
            }
        )
    } else {
        currentScreen = AppScreen.SUBJECT_SELECTION
    }
} 

            AppScreen.SUBJECTS -> {
                SubjectsScreen(
                    repository = repository,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onOpenSubject = { subject ->
                        selectedSubject = subject
                        selectedLesson = null

                        currentScreen =
                            AppScreen.SUBJECT_LESSONS
                    }
                )
            }

          AppScreen.SUBJECT_LESSONS -> {
    val subject = selectedSubject

    if (subject != null) {
        SubjectLessonsScreen(
            subject = subject,
            repository = repository,
            onOpenDrawer = {
                scope.launch {
                    drawerState.open()
                }
            },
            onBack = {
                selectedSubject = null
                currentScreen = AppScreen.SUBJECTS
            },
            onOpenLesson = { lesson ->
                selectedLesson = lesson
                currentScreen = AppScreen.LESSON_CONTENTS
            },
            onEditContent = { content ->
                selectedStudyContent = content
                currentScreen = AppScreen.STUDY_CONTENT_EDIT
            },
            onCreateTextFile = {
                selectedSubjectForTextFile = subject
                     currentScreen = AppScreen.CREATE_TEXT_FILE
                }
        )
    } else {
        currentScreen = AppScreen.SUBJECTS
    }
}

            AppScreen.STUDY_CONTENT_EDIT -> {
    val content = selectedStudyContent

    if (content != null) {
        StudyContentEditScreen(
            content = content,
            onBack = {
                selectedStudyContent = null
                currentScreen = AppScreen.SUBJECT_LESSONS
            },
            onSave = { updatedContent ->
                scope.launch {
                    repository.updateStudyContent(updatedContent)
                    selectedStudyContent = null
                    currentScreen = AppScreen.SUBJECT_LESSONS
                }
            }
        )
    } else {
        currentScreen = AppScreen.SUBJECT_LESSONS
    }
}

            AppScreen.CREATE_TEXT_FILE -> {
    val subject = selectedSubjectForTextFile

    if (subject != null) {
        CreateTextFileScreen(
            subject = subject,
            onBack = {
                selectedSubjectForTextFile = null
                currentScreen = AppScreen.SUBJECT_LESSONS
            },
            onSave = { title, text ->
                scope.launch {
                    val lesson =
                        repository.getOrCreateGeneralLesson(
                            subject.id
                        )

                    repository.saveStudyContent(
                        lessonId = lesson.id,
                        title = title,
                        text = text,
                        sourceType = "TEXT",
                        originalFileName = "$title.txt",
                        fileSize = text.length.toLong()
                    )

                    selectedSubjectForTextFile = null
                    currentScreen = AppScreen.SUBJECT_LESSONS
                }
            }
        )
    } else {
        currentScreen = AppScreen.SUBJECTS
    }
}

            AppScreen.LESSON_CONTENTS -> {
                val subject = selectedSubject
                val lesson = selectedLesson

                if (
                    subject != null &&
                    lesson != null
                ) {
                    LessonContentsScreen(
                        subject = subject,
                        lesson = lesson,
                        repository = repository,
                        onOpenDrawer = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        onBack = {
                            selectedLesson = null

                            currentScreen =
                                AppScreen.SUBJECT_LESSONS
                        },
                        onOpenContent = { content ->
                            // Content viewer will be added next.
                        }
                    )
                } else {
                    currentScreen =
                        AppScreen.SUBJECTS
                }
            }

            AppScreen.CAMERA -> {
                CameraScreen(
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onBack = {
                        currentScreen =
                            AppScreen.IMPORT_FILES
                    },
                    onPhotoReady = { uri ->

                        selectedFiles =
                            (selectedFiles + uri)
                                .distinctBy {
                                    it.toString()
                                }

                        currentScreen =
                            AppScreen.IMPORT_FILES
                    }
                )
            }

            AppScreen.SETTINGS -> {
                PlaceholderScreen(
                    title = "Settings",
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }

            AppScreen.MORE -> {
                PlaceholderScreen(
                    title = "More",
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        }
    }
}

private fun getFileSize(
    context: Context,
    uri: Uri
): Long {
    return try {
        context.contentResolver
            .openAssetFileDescriptor(uri, "r")
            ?.use { descriptor ->
                descriptor.length
            }
            ?.takeIf { it >= 0L }
            ?: 0L
    } catch (_: Exception) {
        0L
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    onOpenDrawer: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        com.offlineai.app.ui.components.AppTopBar(
            title = title,
            onOpenDrawer = onOpenDrawer
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$title\nComing soon",
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Center,
                color = Color(0xFF68736D),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
