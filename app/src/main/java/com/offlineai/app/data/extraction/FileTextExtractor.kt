package com.offlineai.app.data.extraction

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import com.offlineai.app.data.ocr.OcrTextRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.IOException
import java.util.Locale
import org.apache.poi.hslf.usermodel.HSLFSlideShow
import org.apache.poi.hslf.usermodel.HSLFTextShape
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFTextShape

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFWorkbook

data class FileExtractionResult(
    val fileName: String,
    val text: String,
    val sourceType: String
)

class FileTextExtractor(
    private val context: Context
) {

    suspend fun extract(uri: Uri): FileExtractionResult {
        val fileName = getDisplayName(uri)
        val extension = getExtension(fileName)
        val mimeType = context.contentResolver.getType(uri)?.lowercase(Locale.US)

        return when {
            isImage(extension, mimeType) -> {
                val text = OcrTextRecognizer(context).recognizeSuspending(uri)

                FileExtractionResult(
                    fileName = fileName,
                    text = text,
                    sourceType = "OCR"
                )
            }

            extension == "pdf" || mimeType == "application/pdf" -> {
                FileExtractionResult(
                    fileName = fileName,
                    text = extractPdf(uri),
                    sourceType = "PDF OCR"
                )
            }

            extension == "doc" ||
                mimeType == "application/msword" -> {
                FileExtractionResult(
                    fileName = fileName,
                    text = extractDoc(uri),
                    sourceType = "DOC"
                )
            }

            extension == "docx" ||
                mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> {
                FileExtractionResult(
                    fileName = fileName,
                    text = extractDocx(uri),
                    sourceType = "DOCX"
                )
            }

            extension == "txt" ||
                mimeType == "text/plain" -> {
                FileExtractionResult(
                    fileName = fileName,
                    text = readTextFile(uri),
                    sourceType = "Text file"
                )
            }

            extension == "csv" ||
                mimeType == "text/csv" -> {
                FileExtractionResult(
                    fileName = fileName,
                    text = readCsvFile(uri),
                    sourceType = "CSV"
                )
            }

            extension == "rtf" ||
                mimeType == "text/rtf" ||
                mimeType == "application/rtf" -> {
                FileExtractionResult(
                    fileName = fileName,
                    text = readRtfFile(uri),
                    sourceType = "RTF"
                )
            }

            extension == "ppt" ||
    mimeType == "application/vnd.ms-powerpoint" -> {
    FileExtractionResult(
        fileName = fileName,
        text = extractPpt(uri),
        sourceType = "PPT"
    )
}

extension == "pptx" ||
    mimeType == "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> {
    FileExtractionResult(
        fileName = fileName,
        text = extractPptx(uri),
        sourceType = "PPTX"
    )
}

            extension == "xls" ||
    mimeType == "application/vnd.ms-excel" -> {
    FileExtractionResult(
        fileName = fileName,
        text = extractXls(uri),
        sourceType = "XLS"
    )
}

extension == "xlsx" ||
    mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> {
    FileExtractionResult(
        fileName = fileName,
        text = extractXlsx(uri),
        sourceType = "XLSX"
    )
}


            else -> {
                throw UnsupportedOperationException(
                    "Text extraction for .$extension files is not available yet."
                )
            }
        }
    }

    private suspend fun extractDoc(uri: Uri): String =
        withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("Unable to open DOC file.")

            inputStream.use { stream ->
                val document = HWPFDocument(stream)

                WordExtractor(document).use { extractor ->
                    extractor.text.trim()
                }
            }
        }

    private suspend fun extractDocx(uri: Uri): String =
        withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("Unable to open DOCX file.")

            inputStream.use { stream ->
                val document = XWPFDocument(stream)

                XWPFWordExtractor(document).use { extractor ->
                    extractor.text.trim()
                }
            }
        }

    private suspend fun extractPdf(uri: Uri): String =
        withContext(Dispatchers.IO) {
            val descriptor = context.contentResolver.openFileDescriptor(uri, "r")
                ?: throw IOException("Unable to open PDF.")

            val renderer = PdfRenderer(descriptor)
            val pageTexts = mutableListOf<String>()

            try {
                val pageCount = renderer.pageCount

                if (pageCount == 0) {
                    return@withContext ""
                }

                val recognizer = OcrTextRecognizer(context)

                try {
                    for (pageIndex in 0 until pageCount) {
                        val page = renderer.openPage(pageIndex)

                        try {
                            val bitmap = createPdfBitmap(page)

                            try {
                                val text =
                                    recognizer.recognizeBitmapSuspending(bitmap)

                                if (text.isNotBlank()) {
                                    pageTexts.add(
                                        buildString {
                                            append("Page ${pageIndex + 1}")
                                            append("\n")
                                            append(text.trim())
                                        }
                                    )
                                }
                            } finally {
                                bitmap.recycle()
                            }
                        } finally {
                            page.close()
                        }
                    }
                } finally {
                    recognizer.close()
                }
            } finally {
                renderer.close()
            }

            pageTexts.joinToString("\n\n")
        }

    private fun createPdfBitmap(
        page: PdfRenderer.Page
    ): Bitmap {
        val sourceWidth = page.width.coerceAtLeast(1)
        val sourceHeight = page.height.coerceAtLeast(1)

        val targetWidth = 1600
        val scale = targetWidth.toFloat() / sourceWidth.toFloat()
        val targetHeight =
            (sourceHeight * scale).toInt().coerceAtLeast(1)

        val bitmap = Bitmap.createBitmap(
            targetWidth,
            targetHeight,
            Bitmap.Config.ARGB_8888
        )

        page.render(
            bitmap,
            null,
            null,
            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
        )

        return bitmap
    }

    private suspend fun readTextFile(uri: Uri): String =
        withContext(Dispatchers.IO) {
            context.contentResolver
                .openInputStream(uri)
                ?.bufferedReader(Charsets.UTF_8)
                ?.use { it.readText() }
                ?: throw IOException("Unable to read text file.")
        }

    private suspend fun readCsvFile(uri: Uri): String =
        withContext(Dispatchers.IO) {
            context.contentResolver
                .openInputStream(uri)
                ?.bufferedReader(Charsets.UTF_8)
                ?.use { it.readText() }
                ?: throw IOException("Unable to read CSV file.")
        }

    private suspend fun readRtfFile(uri: Uri): String =
        withContext(Dispatchers.IO) {
            val raw = context.contentResolver
                .openInputStream(uri)
                ?.bufferedReader(Charsets.UTF_8)
                ?.use { it.readText() }
                ?: throw IOException("Unable to read RTF file.")

            cleanRtf(raw)
        }

    private fun cleanRtf(value: String): String {
        return value
            .replace(Regex("""\\'[0-9a-fA-F]{2}"""), "")
            .replace(Regex("""\\[a-zA-Z]+-?\d* ?"""), "")
            .replace(Regex("""[{}]"""), "")
            .replace(Regex("""\r\n|\r"""), "\n")
            .replace(Regex("""[ \t]+"""), " ")
            .replace(Regex("""\n{3,}"""), "\n\n")
            .trim()
    }

    private fun getDisplayName(uri: Uri): String {
        val cursor: Cursor? = context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val index =
                    it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                if (index >= 0) {
                    val name = it.getString(index)

                    if (!name.isNullOrBlank()) {
                        return name
                    }
                }
            }
        }

        return uri.lastPathSegment
            ?.substringAfterLast("/")
            ?.takeIf { it.isNotBlank() }
            ?: "Study Material"
    }

    private fun getExtension(fileName: String): String {
        return fileName
            .substringAfterLast(".", "")
            .lowercase(Locale.US)
    }

    private fun isImage(
        extension: String,
        mimeType: String?
    ): Boolean {
        return mimeType?.startsWith("image/") == true ||
            extension in setOf(
                "jpg",
                "jpeg",
                "png",
                "webp",
                "heic"
            )
    }

    private suspend fun extractPpt(uri: Uri): String =
    withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Unable to open PPT file.")

        inputStream.use { stream ->
            val presentation = HSLFSlideShow(stream)
            val output = StringBuilder()

            try {
                presentation.slides.forEachIndexed { index, slide ->
                    val slideText = StringBuilder()

                    slide.shapes.forEach { shape ->
                        if (shape is HSLFTextShape) {
                            val text = shape.text?.trim()

                            if (!text.isNullOrBlank()) {
                                slideText.append(text)
                                slideText.append("\n")
                            }
                        }
                    }

                    if (slideText.isNotBlank()) {
                        output.append("Slide ${index + 1}")
                        output.append("\n")
                        output.append(slideText.toString().trim())
                        output.append("\n\n")
                    }
                }
            } finally {
                presentation.close()
            }

            output.toString().trim()
        }
    }

private suspend fun extractPptx(uri: Uri): String =
    withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Unable to open PPTX file.")

        inputStream.use { stream ->
            val presentation = XMLSlideShow(stream)
            val output = StringBuilder()

            try {
                presentation.slides.forEachIndexed { index, slide ->
                    val slideText = StringBuilder()

                    slide.shapes.forEach { shape ->
                        if (shape is XSLFTextShape) {
                            val text = shape.text?.trim()

                            if (!text.isNullOrBlank()) {
                                slideText.append(text)
                                slideText.append("\n")
                            }
                        }
                    }

                    if (slideText.isNotBlank()) {
                        output.append("Slide ${index + 1}")
                        output.append("\n")
                        output.append(slideText.toString().trim())
                        output.append("\n\n")
                    }
                }
            } finally {
                presentation.close()
            }

            output.toString().trim()
        }
    }

    private suspend fun extractXls(uri: Uri): String =
    withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Unable to open XLS file.")

        inputStream.use { stream ->
            val workbook = HSSFWorkbook(stream)

            try {
                extractWorkbookText(workbook)
            } finally {
                workbook.close()
            }
        }
    }

private suspend fun extractXlsx(uri: Uri): String =
    withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Unable to open XLSX file.")

        inputStream.use { stream ->
            val workbook = XSSFWorkbook(stream)

            try {
                extractWorkbookText(workbook)
            } finally {
                workbook.close()
            }
        }
    }

private fun extractWorkbookText(
    workbook: org.apache.poi.ss.usermodel.Workbook
): String {
    val formatter = DataFormatter()
    val output = StringBuilder()

    for (sheet in workbook) {
        output.append("Sheet: ")
        output.append(sheet.sheetName)
        output.append("\n\n")

        var hasRows = false

        for (row in sheet) {
            val values = mutableListOf<String>()

            for (cell in row) {
                values.add(
                    formatter.formatCellValue(cell).trim()
                )
            }

            while (values.isNotEmpty() && values.last().isEmpty()) {
                values.removeAt(values.lastIndex)
            }

            if (values.isNotEmpty()) {
                hasRows = true

                output.append(
                    values.joinToString(" | ")
                )

                output.append("\n")
            }
        }

        if (hasRows) {
            output.append("\n")
        }
    }

    return output.toString().trim()
}
}
