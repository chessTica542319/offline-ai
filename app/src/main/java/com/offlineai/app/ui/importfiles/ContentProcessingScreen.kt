package com.offlineai.app.ui.importfiles

import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.offlineai.app.data.extraction.FileExtractionResult
import com.offlineai.app.data.extraction.FileTextExtractor
import androidx.compose.ui.platform.LocalContext

@Composable
fun ContentProcessingScreen(
    fileUri: Uri,
    currentIndex: Int,
    totalFiles: Int,
    onSuccess: (FileExtractionResult) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var isProcessing by remember(fileUri) {
        mutableStateOf(true)
    }

    var errorMessage by remember(fileUri) {
        mutableStateOf<String?>(null)
    }

    var retryToken by remember(fileUri) {
        mutableIntStateOf(0)
    }

    LaunchedEffect(fileUri, retryToken) {
        isProcessing = true
        errorMessage = null

        try {
            val result = FileTextExtractor(context).extract(fileUri)

            if (result.text.isBlank()) {
                isProcessing = false
                errorMessage = "No readable text was found in this file."
            } else {
                isProcessing = false
                onSuccess(result)
            }
        } catch (exception: Exception) {
            isProcessing = false
            errorMessage = exception.message ?: "Unable to extract this file."
        }
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
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF101110)
                )
            }

            Text(
                text = "Reading Study Material",
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF101110)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = Color(0xFF2DB55D)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "File $currentIndex of $totalFiles",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF101110)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = fileUri.lastPathSegment ?: "Study Material",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF68736D)
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (isProcessing) {
                CircularProgressIndicator(
                    color = Color(0xFF2DB55D)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Extracting text locally...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF101110)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your study material is being processed on this device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF68736D)
                )
            } else if (errorMessage != null) {
                Text(
                    text = "Couldn't read this file",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF101110)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF68736D)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        retryToken++
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Try Again")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back")
                }
            }
        }
    }
}
