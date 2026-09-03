package com.offlineai.app.ui.navigation

import android.net.Uri

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.offlineai.app.data.database.AppDatabase
import com.offlineai.app.data.database.SubjectEntity
import com.offlineai.app.data.repository.StudyRepository
import com.offlineai.app.ui.camera.CameraScreen
import com.offlineai.app.ui.chat.ChatScreen
import com.offlineai.app.ui.components.AppDrawer
import com.offlineai.app.ui.importfiles.ContentProcessingScreen
import com.offlineai.app.ui.importfiles.ContentReviewScreen
import com.offlineai.app.ui.importfiles.ImportFilesScreen
import com.offlineai.app.ui.importfiles.ImportReviewScreen
import com.offlineai.app.ui.subjects.SubjectSelectionScreen
import com.offlineai.app.ui.subjects.SubjectsScreen

import com.offlineai.app.data.extraction.PendingStudyContent

import android.app.Activity
import androidx.activity.compose.BackHandler

import kotlinx.coroutines.launch

import com.offlineai.app.ui.importfiles.ContentProcessingScreen
import com.offlineai.app.data.extraction.FileExtractionResult

enum class AppScreen {
    CHAT,
    IMPORT_FILES,
    CONTENT_PROCESSING,
    CONTENT_REVIEW,
    SUBJECT_SELECTION,
    IMPORT_REVIEW,
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

    var extractionError by remember {
        mutableStateOf<String?>(null)
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
            database.lessonDao()
        )
    }

    var showQuitDialog by remember {
    mutableStateOf(false)
}

BackHandler {
    showQuitDialog = true
}

    if (showQuitDialog) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = {
            showQuitDialog = false
        },
        title = {
            Text(
                text = "Quit Offline AI?"
            )
        },
        text = {
            Text(
                text = "Your current session will be closed. Unsaved import and review progress will be lost."
            )
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = {
                    val activity =
                        context as? Activity

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
            androidx.compose.material3.TextButton(
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
    extractionError = null

    currentScreen = AppScreen.CONTENT_PROCESSING
}

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentScreen = currentScreen,
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
        selectedFiles.getOrNull(
            currentImportIndex
        )

    if (currentFile != null) {

        ContentProcessingScreen(
            fileUri = currentFile,
            currentIndex =
                currentImportIndex + 1,
            totalFiles =
                selectedFiles.size,

            onSuccess = { result ->

                currentExtractedText =
                    result.text

                currentSourceType =
                    result.sourceType

                currentFileName =
                    result.fileName

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
        reviewedContents.getOrNull(
            currentImportIndex
        )

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

            val content =
                PendingStudyContent(
                    fileUri =
                        selectedFiles[currentImportIndex],
                    title = title,
                    text = text,
                    sourceType = currentSourceType,
                    originalFileName = currentFileName
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
                            currentImportIndex =
                                selectedFiles.lastIndex

                            currentScreen =
                                AppScreen.CONTENT_REVIEW
                        } else {
                            currentScreen =
                                AppScreen.IMPORT_FILES
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
            }
        )
    } else {
        currentScreen =
            AppScreen.SUBJECT_SELECTION
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

    val updatedFiles =
        (selectedFiles + uri).distinctBy {
            it.toString()
        }

    selectedFiles = updatedFiles

    currentScreen = AppScreen.IMPORT_FILES
}
                )
            }

            AppScreen.SUBJECTS -> {
                SubjectsScreen(
                    repository = repository,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
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

@Composable
private fun ExtractionErrorOverlay(
    message: String,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = {
        },
        title = {
            Text("Unable to read file")
        },
        text = {
            Text(message)
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = onRetry
            ) {
                Text("Try Again")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(
                onClick = onBack
            ) {
                Text("Back")
            }
        }
    )
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
