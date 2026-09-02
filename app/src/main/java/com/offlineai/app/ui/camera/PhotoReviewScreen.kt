package com.offlineai.app.ui.camera

import android.graphics.BitmapFactory
import android.net.Uri

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun PhotoReviewScreen(
    photoUri: Uri,
    onRetake: () -> Unit,
    onDiscard: () -> Unit,
    onContinue: () -> Unit
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
            .background(Color.White)
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
                    .height(420.dp),
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
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )

                Text(" Discard")
            }

            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier.fillMaxWidth()
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
