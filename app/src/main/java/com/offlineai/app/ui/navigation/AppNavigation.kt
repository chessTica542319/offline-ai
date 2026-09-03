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
import com.offlineai.app.data.repository.StudyRepository
import com.offlineai.app.ui.camera.CameraScreen
import com.offlineai.app.ui.camera.PhotoReviewScreen
import com.offlineai.app.ui.chat.ChatScreen
import com.offlineai.app.ui.components.AppDrawer
import com.offlineai.app.ui.components.AppTopBar
import com.offlineai.app.ui.importfiles.ImportFilesScreen
import com.offlineai.app.ui.importfiles.ImportReviewScreen
import com.offlineai.app.ui.subjects.SubjectSelectionScreen
import com.offlineai.app.ui.subjects.SubjectsScreen

import kotlinx.coroutines.launch

enum class AppScreen {
    CHAT,
    IMPORT_FILES,
    SUBJECT_SELECTION,
    IMPORT_REVIEW,
    CAMERA,
    PHOTO_REVIEW,
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

    var selectedSubject by remember {
        mutableStateOf<com.offlineai.app.data.database.SubjectEntity?>(null)
    }

    var capturedPhotoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val repository = remember {
        StudyRepository(
            AppDatabase.getInstance(context).subjectDao()
        )
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
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onTakePhoto = {
                        currentScreen = AppScreen.CAMERA
                    },
                    onContinue = { files ->
                        selectedFiles = files
                        currentScreen = AppScreen.SUBJECT_SELECTION
                    }
                )
            }

            AppScreen.SUBJECT_SELECTION -> {
                SubjectSelectionScreen(
                    selectedFilesCount = selectedFiles.size,
                    repository = repository,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onBack = {
                        currentScreen = AppScreen.IMPORT_FILES
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
                        selectedFiles = selectedFiles,
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
                    currentScreen = AppScreen.SUBJECT_SELECTION
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
                        currentScreen = AppScreen.IMPORT_FILES
                    },
                    onPhotoReady = { uri ->
                        capturedPhotoUri = uri
                        currentScreen = AppScreen.PHOTO_REVIEW
                    }
                )
            }

            AppScreen.PHOTO_REVIEW -> {
                val photoUri = capturedPhotoUri

                if (photoUri != null) {
                    PhotoReviewScreen(
                        photoUri = photoUri,
                        onRetake = {
                            capturedPhotoUri = null
                            currentScreen = AppScreen.CAMERA
                        },
                        onDiscard = {
                            capturedPhotoUri = null
                            currentScreen = AppScreen.CAMERA
                        },
                        onContinue = {
                            currentScreen = AppScreen.IMPORT_FILES
                        }
                    )
                } else {
                    currentScreen = AppScreen.CAMERA
                }
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
private fun PlaceholderScreen(
    title: String,
    onOpenDrawer: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppTopBar(
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
