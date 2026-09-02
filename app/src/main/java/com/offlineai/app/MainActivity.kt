package com.offlineai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.offlineai.app.ui.navigation.AppNavigation
import com.offlineai.app.ui.splash.SplashScreen
import com.offlineai.app.ui.theme.OfflineAITheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OfflineAITheme {
                OfflineAIApp()
            }
        }
    }
}

@Composable
fun OfflineAIApp() {

    var isInitializing by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {

        /*
         * Temporary initialization.
         *
         * Later this will be replaced with the real
         * application initialization:
         *
         * - Database
         * - Knowledge loading
         * - Search index
         * - OCR availability
         * - Local AI model
         * - llama.cpp
         */

        delay(1800)

        isInitializing = false
    }

    if (isInitializing) {
        SplashScreen()
    } else {
        AppNavigation()
    }
}
