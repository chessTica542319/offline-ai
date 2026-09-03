package com.offlineai.app.data.ocr

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text.TextBlock
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OcrTextRecognizer(
    private val context: Context
) {

    fun recognize(
        photoUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val image =
                InputImage.fromFilePath(
                    context,
                    photoUri
                )

            processImage(
                image = image,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        } catch (exception: Exception) {
            onFailure(exception)
        }
    }

    suspend fun recognizeSuspending(
        photoUri: Uri
    ): String {
        val image =
            InputImage.fromFilePath(
                context,
                photoUri
            )

        return recognizeImageSuspending(
            image
        )
    }

    suspend fun recognizeBitmapSuspending(
        bitmap: Bitmap
    ): String {
        val image =
            InputImage.fromBitmap(
                bitmap,
                0
            )

        return recognizeImageSuspending(
            image
        )
    }

    private suspend fun recognizeImageSuspending(
        image: InputImage
    ): String =
        suspendCancellableCoroutine { continuation ->

            val recognizer =
                TextRecognition.getClient(
                    TextRecognizerOptions.DEFAULT_OPTIONS
                )

            recognizer
                .process(image)
                .addOnSuccessListener { result ->

                    val text =
                        formatText(
                            result.textBlocks
                        )

                    recognizer.close()

                    if (
                        continuation.isActive
                    ) {
                        continuation.resume(text)
                    }
                }
                .addOnFailureListener { exception ->

                    recognizer.close()

                    if (
                        continuation.isActive
                    ) {
                        continuation.resumeWithException(
                            exception
                        )
                    }
                }
        }

    private fun processImage(
        image: InputImage,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val recognizer =
            TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
            )

        recognizer
            .process(image)
            .addOnSuccessListener { result ->

                val formattedText =
                    formatText(
                        result.textBlocks
                    )

                onSuccess(
                    formattedText
                )

                recognizer.close()
            }
            .addOnFailureListener { exception ->

                onFailure(exception)

                recognizer.close()
            }
    }

    private fun formatText(
        blocks: List<TextBlock>
    ): String {

        if (blocks.isEmpty()) {
            return ""
        }

        val sortedBlocks =
            blocks.sortedWith(
                compareBy<TextBlock> {
                    it.boundingBox?.top
                        ?: Int.MAX_VALUE
                }.thenBy {
                    it.boundingBox?.left
                        ?: Int.MAX_VALUE
                }
            )

        val paragraphs =
            mutableListOf<String>()

        for (block in sortedBlocks) {

            val lines =
                block.lines

            if (lines.isEmpty()) {

                val text =
                    block.text.trim()

                if (text.isNotEmpty()) {
                    paragraphs.add(text)
                }

                continue
            }

            val blockText =
                lines
                    .sortedWith(
                        compareBy {
                            it.boundingBox?.top
                                ?: Int.MAX_VALUE
                        }
                    )
                    .joinToString("\n") {
                        it.text.trim()
                    }
                    .trim()

            if (blockText.isNotEmpty()) {
                paragraphs.add(
                    blockText
                )
            }
        }

        return paragraphs
            .joinToString("\n\n")
            .replace(
                Regex("[ \t]+"),
                " "
            )
            .replace(
                Regex("\n{3,}"),
                "\n\n"
            )
            .trim()
    }

    fun close() {
    }
}
