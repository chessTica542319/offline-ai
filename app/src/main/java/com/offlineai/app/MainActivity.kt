package com.offlineai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme
import com.offlineai.app.ui.theme.OfflineAITheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OfflineAITheme {
                OfflineAIApp()
            }
        }
    }
}

@Composable
fun OfflineAIApp() {

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(

        drawerState = drawerState,

        drawerContent = {

            ModalDrawerSheet(
                drawerContainerColor = Color.White
            ) {

                DrawerHeader()

                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = 20.dp
                    ),
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text("Chat")
                    },
                    selected = true,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor =
                            MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor =
                            MaterialTheme.colorScheme.primary,
                        selectedTextColor =
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.FolderOpen,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text("Import Files")
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text("Subjects")
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text("Settings")
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text("More")
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                KnowledgeSummary()

                Spacer(
                    modifier = Modifier.height(20.dp)
                )
            }
        }
    ) {

        Scaffold(

            containerColor =
                MaterialTheme.colorScheme.background,

            topBar = {

                TopBar(
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            },

            bottomBar = {
                ChatInput()
            }

        ) { paddingValues ->

            DashboardContent(
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun DrawerHeader() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                top = 32.dp,
                end = 24.dp,
                bottom = 24.dp
            )
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        MaterialTheme.colorScheme.primary
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(27.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column {

                Text(
                    text = "Offline AI",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Your private tutor",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun KnowledgeSummary() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {

        Text(
            text = "Knowledge",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "0 documents",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "0 lessons • 0 subjects",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    onMenuClick: () -> Unit
) {

    Surface(
        color = Color.White
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onMenuClick
            ) {

                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open menu"
                )
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text = "Offline AI",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {}
            ) {

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {

            Spacer(
                modifier = Modifier.height(70.dp)
            )

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Welcome to Offline AI",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Your private study assistant",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            KnowledgeCard()

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            QuickActionCard()

            Spacer(
                modifier = Modifier.height(40.dp)
            )
        }
    }
}

@Composable
private fun KnowledgeCard() {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Your knowledge",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    Text(
                        text = "Your imported study materials",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Import your notes, textbooks, slides, and other study materials to build your private knowledge base.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun QuickActionCard() {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 2.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Import your first files",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )

                Text(
                    text = "Build your offline knowledge base",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ChatInput() {

    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 10.dp,
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Ask about your lessons...")
                },
                shape = RoundedCornerShape(18.dp),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary
            ) {

                IconButton(
                    onClick = {}
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
