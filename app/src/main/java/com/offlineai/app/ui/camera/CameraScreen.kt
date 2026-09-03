package com.offlineai.app.ui.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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

    var capturedPhoto by remember {
        mutableStateOf<Uri?>(null)
    }

    var pendingUri by remember {
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

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->

            val uri = pendingUri
            pendingUri = null

            if (success && uri != null) {
                capturedPhoto = uri
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

        if (capturedPhoto == null) {

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

                    Spacer(
                        modifier = Modifier.size(8.dp)
                    )

                    Text("Take Photo")
                }

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text("Back")
                }
            }

        } else {

            PhotoCapturedActions(
                photoUri = capturedPhoto!!,
                onReview = {
                    onPhotoReady(capturedPhoto!!)
                },
                onDiscard = {
                    deleteTemporaryPhoto(
                        context,
                        capturedPhoto!!
                    )

                    capturedPhoto = null
                }
            )
        }
    }
}

@Composable
private fun PhotoCapturedActions(
    photoUri: Uri,
    onReview: () -> Unit,
    onDiscard: () -> Unit
) {
    val context = LocalContext.current

    val bitmap = remember(photoUri) {
        context.contentResolver
            .openInputStream(photoUri)
            ?.use {
                BitmapFactory.decodeStream(it)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (bitmap != null) {

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured study material",
                modifier = Modifier
                    .size(
                        width = 180.dp,
                        height = 220.dp
                    )
                    .clip(
                        RoundedCornerShape(16.dp)
                    ),
                contentScale = ContentScale.Crop
            )

        } else {

            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = Color(0xFF2DB55D)
            )
        }

        Text(
            text = "Photo captured",
            modifier = Modifier.padding(top = 20.dp),
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF101110)
        )

        Text(
            text = "Your photo is ready. Review the content or discard it.",
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF68736D)
        )

        Button(
            onClick = onReview,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Text("Review Content")
        }

        OutlinedButton(
            onClick = onDiscard,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Text("Discard")
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
