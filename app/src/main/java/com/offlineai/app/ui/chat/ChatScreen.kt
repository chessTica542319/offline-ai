package com.offlineai.app.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.width

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.offlineai.app.ui.components.AppTopBar

import com.offlineai.app.data.repository.KnowledgeStats

@Composable
fun ChatScreen(
    knowledgeStats: KnowledgeStats,
    onOpenDrawer: () -> Unit,
    onImportFiles: () -> Unit
) {
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = "Offline AI",
            onOpenDrawer = onOpenDrawer
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Welcome! 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF101110)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ask me about your study materials.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF68736D)
                )

                Spacer(modifier = Modifier.height(24.dp))

                ImportLessonCard(
                    knowledgeStats = knowledgeStats,
                    onImportFiles = onImportFiles
                )

                Spacer(modifier = Modifier.height(20.dp))

                KnowledgeCard(
                    knowledgeStats = knowledgeStats
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ChatInput(
            value = message,
            onValueChange = { message = it },
            onSend = {
                if (message.isNotBlank()) {
                    message = ""
                }
            }
        )
    }
}


@Composable
private fun ImportLessonCard(
    knowledgeStats: KnowledgeStats,
    onImportFiles: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F7F4)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Import lesson",
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

           Text(
              text = if (knowledgeStats.files == 0) {
                   "Start with your first lesson"
             } else {
                 "Import your lesson to train AI more"
               },
               style = MaterialTheme.typography.titleMedium,
              color = Color(0xFF101110)
            ) 

            Spacer(modifier = Modifier.height(6.dp))

           Text(
              text = if (knowledgeStats.files == 0) {
                    "Import your lesson materials and Offline AI will use them to answer your questions."
             } else {
                   "Import more lesson materials to expand your knowledge and improve your study experience."
               },
             style = MaterialTheme.typography.bodyMedium,
             color = Color(0xFF68736D)
            ) 

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onImportFiles,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

               Text(
                 text = if (knowledgeStats.files == 0) {
                       "Import Your First Lesson"
                  } else {
                      "Import Your Lesson"
                  }
                ) 
            }
        }
    }
}

@Composable
private fun KnowledgeCard(
    knowledgeStats: KnowledgeStats
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F7F4)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Your Knowledge",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF101110)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${knowledgeStats.files} files • ${knowledgeStats.subjects} subjects",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF68736D)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Import your study materials to start learning with Offline AI.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF68736D)
            )
        }
    }
}

@Composable
private fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 12.dp,
                end = 12.dp,
                bottom = 12.dp
            )
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 56.dp),
            placeholder = {
                Text("Ask anything...")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            )
        )

        IconButton(
            onClick = onSend,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
