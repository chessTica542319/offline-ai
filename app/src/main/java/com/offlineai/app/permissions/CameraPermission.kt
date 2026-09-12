package com.offlineai.app.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat

class CameraPermissionController(
    private val context: Context,
    private val requestPermission: (Boolean) -> Unit
) {

    fun isGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun request(
        onGranted: () -> Unit
    ) {
        if (isGranted()) {
            onGranted()
            return
        }

        requestPermission(
            true
        )
    }
}

@Composable
fun rememberCameraPermissionController(
    context: Context,
    onGranted: () -> Unit
): CameraPermissionController {

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                onGranted()
            }
        }

    return remember(context) {
        CameraPermissionController(
            context = context,
            requestPermission = {
                permissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        )
    }
}
