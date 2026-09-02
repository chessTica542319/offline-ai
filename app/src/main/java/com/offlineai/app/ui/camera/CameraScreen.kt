package com.offlineai.app.ui.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun CameraScreen(
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    onPhotoReady: (Uri) -> Unit
) {
    val context = LocalContext.current

    var photoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    fun createTemporaryPhoto(): Uri {
        val directory = File(context.cacheDir, "camera")
        directory.mkdirs()

        val file = File.createTempFile(
            "study_photo_",
            ".jpg",
            directory
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    var pendingUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->

            val uri = pendingUri
            pendingUri = null

            if (success && uri != null) {
                photoUri = uri
            } else if (uri != null) {
                deleteTemporaryPhoto(context, uri)
            }
        }

    fun launchCamera() {
        val uri = createTemporaryPhoto()
        pendingUri = uri
        cameraLauncher.launch(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
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

            Text(
                text = "Take Photo",
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF101110)
            )
        }

        if (photoUri == null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = Color(0xFF2DB55D)
                )

                Text(
                    text = "Capture study material",
                    modifier = Modifier.padding(top = 20.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF101110)
                )

                Text(
                    text = "Take a photo of a book page, notes, worksheet, or other study material.",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF68736D)
                )

                Button(
                    onClick = {
                        launchCamera()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null
                    )

                    Text(
                        text = " Take Photo"
                    )
                }

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text("Cancel")
                }
            }

        } else {

            PhotoReview(
                uri = photoUri!!,
                onRetake = {
                    deleteTemporaryPhoto(context, photoUri!!)
                    photoUri = null
                    launchCamera()
                },
                onDiscard = {
                    deleteTemporaryPhoto(context, photoUri!!)
                    photoUri = null
                },
                onContinue = {
                    onPhotoReady(photoUri!!)
                }
            )
        }
    }
}

@Composable
private fun PhotoReview(
    uri: Uri,
    onRetake: () -> Unit,
    onDiscard: () -> Unit,
    onContinue: () -> Unit
) {
    val context = LocalContext.current

    val bitmap = remember(uri) {
        context.contentResolver
            .openInputStream(uri)
            ?.use { BitmapFactory.decodeStream(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Review Photo",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF101110)
        )

        Text(
            text = "Check that the page is clear before continuing.",
            modifier = Modifier.padding(top = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF68736D)
        )

        if (bitmap != null) {

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured study material",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .padding(vertical = 16.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit
            )

        } else {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Unable to display photo.",
                    color = Color(0xFF68736D)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlinedButton(
                onClick = onDiscard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )

                Text(" Discard")
            }

            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null
                )

                Text(" Retake")
            }
        }

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null
            )

            Text(" Continue")
        }
    }
}

private fun deleteTemporaryPhoto(
    context: Context,
    uri: Uri
) {
    try {
        context.contentResolver.delete(uri, null, null)
    } catch (_: Exception) {
    }
}
