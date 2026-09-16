package com.offlineai.app.ui.chat

import android.widget.Toast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

import com.offlineai.app.ai.AIEngineStatus
import com.offlineai.app.data.repository.KnowledgeStats

import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    messages: androidx.compose.runtime.snapshots.SnapshotStateList<ChatMessage>,
    responseCount: Int,
    aiEngineStatus: AIEngineStatus,
    message: String,
    onMessageChange: (String) -> Unit,
    isGenerating: Boolean,
    chatListState: LazyListState,
    scrollDistanceFromBottom: Float,
    onScrollDistanceChange: (Float) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    knowledgeStats: KnowledgeStats,
    onOpenDrawer: () -> Unit,
    onImportFiles: () -> Unit,
    onClearChat: () -> Unit,
    onResetSession: () -> Unit,
    onSessionLimitReached: () -> Unit,
    onRetry: (ChatMessage) -> Unit
) {

    val clipboardManager =
        LocalClipboardManager.current

    val context =
        LocalContext.current

    val scrollScope =
        rememberCoroutineScope()

    var localScrollDistance by remember {
        mutableFloatStateOf(
            scrollDistanceFromBottom
        )
    }

    var chatInputReservedHeight by remember {
        mutableIntStateOf(0)
    }

    val density =
        LocalDensity.current

    val viewportHeight =
        chatListState.layoutInfo.viewportEndOffset -
            chatListState.layoutInfo.viewportStartOffset

    val latestResponseIsAway by remember {
        derivedStateOf {
            val latestResponse =
                messages.lastOrNull {
                    !it.isUser
                }

            if (latestResponse == null) {
                false
            } else {
                val latestItem =
                    chatListState.layoutInfo.visibleItemsInfo.firstOrNull {
                        it.key == latestResponse.id
                    }

                if (latestItem == null) {
                    true
                } else {
                    val viewportStart =
                        chatListState.layoutInfo.viewportStartOffset

                    val viewportEnd =
                        chatListState.layoutInfo.viewportEndOffset

                    latestItem.offset < viewportStart ||
                        latestItem.offset + latestItem.size > viewportEnd
                }
            }
        }
    }

    LaunchedEffect(
        scrollDistanceFromBottom
    ) {
        localScrollDistance =
            scrollDistanceFromBottom
    }

    LaunchedEffect(chatListState) {

        var previousIndex =
            chatListState.firstVisibleItemIndex

        var previousOffset =
            chatListState.firstVisibleItemScrollOffset

        snapshotFlow {

            Pair(
                chatListState.firstVisibleItemIndex,
                chatListState.firstVisibleItemScrollOffset
            )

        }.collect { position ->

            val currentIndex =
                position.first

            val currentOffset =
                position.second

            val indexDelta =
                currentIndex -
                    previousIndex

            val offsetDelta =
                currentOffset -
                    previousOffset

            val newDistance =
                if (indexDelta != 0) {

                    val estimatedItemSize =
                        chatListState
                            .layoutInfo
                            .visibleItemsInfo
                            .map {
                                it.size
                            }
                            .filter {
                                it > 0
                            }
                            .average()
                            .toFloat()
                            .takeIf {
                                it > 0
                            }
                            ?: 1f

                    localScrollDistance +
                        -(
                            (
                                indexDelta *
                                    estimatedItemSize
                            ) +
                                offsetDelta
                        )

                } else {

                    localScrollDistance -
                        offsetDelta.toFloat()
                }

            val clampedDistance =
                newDistance.coerceAtLeast(0f)

            localScrollDistance =
                clampedDistance

            onScrollDistanceChange(
                clampedDistance
            )

            previousIndex =
                currentIndex

            previousOffset =
                currentOffset
        }
    }

    LaunchedEffect(
        messages.size,
        isGenerating
    ) {

        if (
            messages.isNotEmpty() &&
            localScrollDistance < 1f
        ) {

            chatListState.animateScrollToItem(
                messages.lastIndex
            )

            localScrollDistance =
                0f

            onScrollDistanceChange(
                0f
            )
        }
    }

    LaunchedEffect(
        chatListState.layoutInfo.totalItemsCount
    ) {

        if (
            chatListState.layoutInfo.totalItemsCount > 0 &&
            !chatListState.canScrollForward
        ) {

            localScrollDistance =
                0f

            onScrollDistanceChange(
                0f
            )
        }
    }

    Scaffold(
        modifier =
            Modifier.fillMaxSize(),

        bottomBar = {

            FloatingChatInput(
                value = message,

                onValueChange =
                    onMessageChange,

                onSend =
                    onSend,

                onStop =
                    onStop,

                onPlus = {
                    // Plus action will be implemented next.
                },

                onLimitReached =
                    onSessionLimitReached,

                onScrollToBottom = {

                    scrollScope.launch {

                        scrollToLatestAiResponse(
                            listState =
                                chatListState,
                            messages =
                                messages
                        )

                        localScrollDistance =
                            0f

                        onScrollDistanceChange(
                            0f
                        )
                    }
                },

                isGenerating =
                    isGenerating,

                enabled =
                    responseCount < 50,

               showScrollToBottom =
                    latestResponseIsAway &&
                    messages.isNotEmpty(), 

                onHeightChanged = {
                    chatInputReservedHeight =
                        it
                }
            )
        }

    )

{ scaffoldPadding ->

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        scaffoldPadding
                    )
        ) {

            ChatTopBar(
                title =
                    "Offline AI",

                onOpenDrawer =
                    onOpenDrawer
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 8.dp
                        )
            ) {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text =
                                "Responses this session: " +
                                    "$responseCount / 50",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                Color(0xFF68736D)
                        )

                        TextButton(
                            onClick = {
                                onResetSession()
                            },

                            contentPadding =
                                PaddingValues(
                                    0.dp
                                )
                        ) {

                            Text(
                                text =
                                    "Reset session",

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall
                            )
                        }
                    }

                    Text(
                        text =
                            when (
                                aiEngineStatus
                            ) {

                                AIEngineStatus.IDLE ->
                                    "AI idle"

                                AIEngineStatus.LOADING ->
                                    "Loading AI..."

                                AIEngineStatus.READY ->
                                    "AI ready"

                                AIEngineStatus.GENERATING ->
                                    "AI thinking..."

                                AIEngineStatus.STOPPING ->
                                    "Stopping..."

                                AIEngineStatus.ERROR ->
                                    "AI error"
                            },

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            when (
                                aiEngineStatus
                            ) {

                                AIEngineStatus.ERROR ->
                                    MaterialTheme
                                        .colorScheme
                                        .error

                                AIEngineStatus.GENERATING,
                                AIEngineStatus.LOADING,
                                AIEngineStatus.STOPPING ->
                                    MaterialTheme
                                        .colorScheme
                                        .primary

                                else ->
                                    Color(0xFF68736D)
                            }
                    )
                }
            }

            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
            ) {

                LazyColumn(
                    state =
                        chatListState,

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = 20.dp
                            ),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            12.dp
                        ),

                    contentPadding =
                        PaddingValues(
                            top = 16.dp,

                            bottom =
                                24.dp +
                                    with(density) {
                                        chatInputReservedHeight
                                            .toDp()
                                    }
                        )
                ) {

                    item {

                        Text(
                            text =
                                "Welcome! 👋",

                            style =
                                MaterialTheme
                                    .typography
                                    .headlineMedium,

                            color =
                                Color(0xFF101110)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )

                        Text(
                            text =
                                "Ask me about your study materials.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyLarge,

                            color =
                                Color(0xFF68736D)
                        )
                    }

                    if (
                        messages.isEmpty()
                    ) {

                        item {

                            ImportLessonCard(
                                knowledgeStats =
                                    knowledgeStats,

                                onImportFiles =
                                    onImportFiles
                            )
                        }

                        item {

                            KnowledgeCard(
                                knowledgeStats =
                                    knowledgeStats
                            )
                        }
                    }

                    items(
                        items =
                            messages,

                        key = {
                            it.id
                        }

                    ) { chatMessage ->

                        ChatMessageCard(
                            message =
                                chatMessage,

                            isGenerating =
                                isGenerating &&
                                    !chatMessage.isUser &&
                                    chatMessage.id ==
                                        messages
                                            .lastOrNull()
                                            ?.id &&
                                    chatMessage.text.isBlank(),

                            onCopy = {

                                clipboardManager.setText(
                                    AnnotatedString(
                                        chatMessage.text
                                    )
                                )

                                Toast.makeText(
                                    context,
                                    "Copied to clipboard",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },

                            onRetry =  {
                                onRetry(chatMessage)
                            }
                        )
                    }

                    item(
                        key = "chat_disclaimer"
                        ) {
                            ChatDisclaimer()
                        }

                }
            }
        }
    }

}

@Composable
private fun ChatMessageCard(
    message: ChatMessage,
    isGenerating: Boolean,
    onCopy: () -> Unit,
    onRetry: () -> Unit
) {

    val isStoppedResponse =
        message.text ==
            "You stopped the response"

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (message.isUser) {
                        Color(0xFFE8F1EB)
                    } else {
                        Color(0xFFF3F7F4)
                    }
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Text(
                text =
                    if (message.isUser) {
                        "You"
                    } else {
                        "Offline AI"
                    },
                style =
                    MaterialTheme.typography.titleSmall,
                color =
                    if (message.isUser) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color(0xFF101110)
                    }
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            if (message.isUser) {

    Text(
        text = message.text,
        style =
            MaterialTheme.typography.bodyLarge,
        color =
            Color(0xFF101110)
    )

} else if (
    isGenerating &&
        message.text.isBlank()
) {

    ChatResponseShimmer()

} else {

    ChatMessageRenderer(
        text = message.text
    )
}

            if (
                message.isUser &&
                message.text.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                IconButton(
                    onClick = onCopy,
                    modifier =
                        Modifier.size(34.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ContentCopy,
                        contentDescription =
                            "Copy prompt",
                        tint =
                            Color(0xFF4CAF50),
                        modifier =
                            Modifier.size(18.dp)
                    )
                }

            } else if (
                !message.isUser &&
                message.text.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(2.dp)
                ) {

                    IconButton(
                        onClick = onCopy,
                        modifier =
                            Modifier.size(34.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ContentCopy,
                            contentDescription =
                                "Copy response",
                            tint =
                                Color(0xFF4CAF50),
                            modifier =
                                Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onRetry,
                        modifier =
                            Modifier.size(34.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Refresh,
                            contentDescription =
                                "Regenerate response",
                            tint =
                                MaterialTheme.colorScheme.primary,
                            modifier =
                                Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImportLessonCard(
    knowledgeStats: KnowledgeStats,
    onImportFiles: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFF3F7F4)
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
        ) {

            Icon(
                imageVector =
                    Icons.Default.Add,
                contentDescription =
                    "Import lesson",
                tint =
                    MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text =
                    if (knowledgeStats.files == 0) {
                        "Start with your first lesson"
                    } else {
                        "Import your lesson to train AI more"
                    },
                style =
                    MaterialTheme.typography.titleMedium,
                color =
                    Color(0xFF101110)
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    if (knowledgeStats.files == 0) {
                        "Import your lesson materials and Offline AI will use them to answer your questions."
                    } else {
                        "Import more lesson materials to expand your knowledge and improve your study experience."
                    },
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    Color(0xFF68736D)
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Button(
                onClick = onImportFiles,
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        if (knowledgeStats.files == 0) {
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
        modifier =
            Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFF3F7F4)
            )
    ) {

        Column(
            modifier =
                Modifier.padding(20.dp)
        ) {

            Text(
                text = "Your Knowledge",
                style =
                    MaterialTheme.typography.titleMedium,
                color =
                    Color(0xFF101110)
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "${knowledgeStats.files} files • " +
                        "${knowledgeStats.subjects} subjects",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    Color(0xFF68736D)
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Text(
                text =
                    "Import your study materials to start learning with Offline AI.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    Color(0xFF68736D)
            )
        }
    }
}

