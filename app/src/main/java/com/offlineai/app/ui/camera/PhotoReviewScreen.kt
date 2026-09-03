package com.offlineai.app.ui.camera

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.offlineai.app.data.ocr.OcrTextRecognizer

@Composable
fun PhotoReviewScreen(
    photoUri: Uri,
    onRetake: () -> Unit,
    onDiscard: () -> Unit,
    onContinue: (String) -> Unit
) {
    val context = LocalContext.current

    var extractedText by remember(photoUri) {
        mutableStateOf("")
    }

    var isProcessing by remember(photoUri) {
        mutableStateOf(true)
    }

    var errorMessage by remember(photoUri) {
        mutableStateOf<String?>(null)
    }

    fun runOcr() {
        isProcessing = true
        errorMessage = null
        extractedText = ""

        OcrTextRecognizer(context).recognize(
            photoUri = photoUri,

            onSuccess = { text ->
                extractedText = text
                isProcessing = false
            },

            onFailure = { exception ->
                errorMessage =
                    exception.message
                        ?: "Unable to read the photo."

                isProcessing = false
            }
        )
    }

    LaunchedEffect(photoUri) {
        runOcr()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {

        Text(
            text = "Review Content",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF101110)
        )

        Text(
            text = "Review and edit the text extracted from your photo.",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF68736D)
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        when {

            isProcessing -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        CircularProgressIndicator(
                            color = Color(0xFF2DB55D)
                        )

                        Text(
                            text = "Reading your photo...",
                            modifier = Modifier.padding(top = 18.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF101110)
                        )

                        Text(
                            text = "Extracting text locally",
                            modifier = Modifier.padding(top = 6.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF68736D)
                        )
                    }
                }
            }

            errorMessage != null -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "Couldn't read this photo",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF101110)
                        )

                        Text(
                            text = errorMessage!!,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF68736D)
                        )

                        OutlinedButton(
                            onClick = {
                                runOcr()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            Text("Try Again")
                        }
                    }
                }
            }

            extractedText.isBlank() -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "No text detected",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF101110)
                        )

                        Text(
                            text = "Try another photo with clearer text and better lighting.",
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF68736D)
                        )

                        OutlinedButton(
                            onClick = onRetake,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            Text("Retake Photo")
                        }
                    }
                }
            }

            else -> {

                OutlinedTextField(
                    value = extractedText,
                    onValueChange = {
                        extractedText = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    label = {
                        Text("Extracted text")
                    },
                    placeholder = {
                        Text("OCR text")
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        if (!isProcessing) {

            OutlinedButton(
                onClick = onDiscard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text("Discard")
            }

            if (extractedText.isNotBlank()) {

                Button(
                    onClick = {
                        onContinue(extractedText)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Text("Save")
                }
            }
        }
    }
}
