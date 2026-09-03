package com.offlineai.app.ui.camera

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PhotoReviewScreen(
    photoUri: Uri,
    onRetake: () -> Unit,
    onDiscard: () -> Unit,
    onContinue: () -> Unit
) {
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
            text = "Review the text extracted from your photo before saving it.",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF68736D)
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Color(0xFFF7F9F7)
                )
                .padding(20.dp)
        ) {
            Text(
                text = "OCR text will appear here.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF68736D)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Text extraction will be added in the next step.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF68736D)
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

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

                Spacer(
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text("Discard")
            }

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
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
