package com.offlineai.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.offlineai.app.ui.navigation.AppScreen

@Composable
fun AppDrawer(
    currentScreen: AppScreen,
        knowledgeStats: com.offlineai.app.data.repository.KnowledgeStats,
        onScreenSelected: (AppScreen) -> Unit
    ){
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        drawerContentColor = Color(0xFF101110),
        modifier = Modifier.width(320.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 20.dp)
        ) {
            DrawerHeader()

            Spacer(modifier = Modifier.height(12.dp))

            NavigationDrawerItem(
                label = { Text("Chat") },
                selected = currentScreen == AppScreen.CHAT,
                onClick = { onScreenSelected(AppScreen.CHAT) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Chat"
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            NavigationDrawerItem(
                label = { Text("Import Files") },
                selected = currentScreen == AppScreen.IMPORT_FILES,
                onClick = { onScreenSelected(AppScreen.IMPORT_FILES) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Import Files"
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            NavigationDrawerItem(
                label = { Text("Subjects") },
                selected = currentScreen == AppScreen.SUBJECTS,
                onClick = { onScreenSelected(AppScreen.SUBJECTS) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "Subjects"
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            NavigationDrawerItem(
                label = { Text("Settings") },
                selected = currentScreen == AppScreen.SETTINGS,
                onClick = { onScreenSelected(AppScreen.SETTINGS) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            NavigationDrawerItem(
                label = { Text("More") },
                selected = currentScreen == AppScreen.MORE,
                onClick = { onScreenSelected(AppScreen.MORE) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More"
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            KnowledgeSummary(
                knowledgeStats = knowledgeStats
            )
        }
    }
}

@Composable
private fun DrawerHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Book,
            contentDescription = "Offline AI",
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = "Offline AI",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF101110)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Your private tutor",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF68736D)
            )
        }
    }
}

@Composable
private fun KnowledgeSummary(
    knowledgeStats: com.offlineai.app.data.repository.KnowledgeStats
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Knowledge",
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFF101110)
        )

        Text(
            text = "${knowledgeStats.files} files",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF68736D)
        )

        Text(
            text = "${knowledgeStats.subjects} subjects",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF68736D)
        )
    }
}
