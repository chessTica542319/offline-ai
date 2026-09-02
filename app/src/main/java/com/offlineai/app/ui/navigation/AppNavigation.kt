package com.offlineai.app.ui.navigation

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.offlineai.app.ui.chat.ChatScreen
import com.offlineai.app.ui.components.AppDrawer
import com.offlineai.app.ui.components.AppTopBar
import kotlinx.coroutines.launch
import android.net.Uri
import com.offlineai.app.ui.importfiles.ImportFilesScreen

enum class AppScreen {
    CHAT,
    IMPORT_FILES,
    SUBJECTS,
    SETTINGS,
    MORE
}

@Composable
fun AppNavigation() {

    var currentScreen by remember {
        mutableStateOf(AppScreen.CHAT)
    }

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

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

        onContinue = { files: List<Uri> ->

            // Subject selection will be implemented next.
        }
    )
}

            AppScreen.SUBJECTS -> {

                PlaceholderScreen(
                    title = "Subjects",

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
