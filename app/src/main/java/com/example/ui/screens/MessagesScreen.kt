package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private val TextTertiary = TextMuted

@Composable
fun MessagesScreen(
    conversations: List<ChatConversation>,
    allCreators: List<CreatorProfile>,
    selectedPartner: CreatorProfile?,
    currentLanguage: AppLanguage,
    onSelectPartner: (CreatorProfile?) -> Unit,
    getMessagesForPartner: (String) -> Flow<List<ChatMessage>>,
    onSendMessage: (String, String, String) -> Unit,
    onNavigateToExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedPartner != null) {
        val messagesFlow = remember(selectedPartner.id) { getMessagesForPartner(selectedPartner.id) }
        val messages by messagesFlow.collectAsState(initial = emptyList())

        OneOnOneChatView(
            partner = selectedPartner,
            messages = messages,
            currentLanguage = currentLanguage,
            onBack = { onSelectPartner(null) },
            onSendMessage = { text ->
                onSendMessage(selectedPartner.id, selectedPartner.name, text)
            },
            modifier = modifier
        )
    } else {
        ConversationListView(
            conversations = conversations,
            allCreators = allCreators,
            currentLanguage = currentLanguage,
            onSelectConversation = { conv ->
                val creator = allCreators.find { it.id == conv.partnerId } ?: CreatorProfile(
                    id = conv.partnerId,
                    name = conv.partnerName,
                    channelName = conv.partnerChannelName,
                    channelHandle = "@" + conv.partnerChannelName.replace(" ", ""),
                    channelUrl = "https://youtube.com",
                    category = conv.partnerCategory,
                    subscriberRange = SubscriberRange.TIER_100K_500K,
                    language = "Hindi",
                    collaborationInterests = listOf("Collab", "Cross-Promo"),
                    bio = "YouTube Creator on Creator Collab Hub",
                    city = "India",
                    avatarColorHex = conv.partnerAvatarColorHex
                )
                onSelectPartner(creator)
            },
            onQuickChatWithCreator = { creator ->
                onSelectPartner(creator)
            },
            onNavigateToExplore = onNavigateToExplore,
            modifier = modifier
        )
    }
}

@Composable
private fun ConversationListView(
    conversations: List<ChatConversation>,
    allCreators: List<CreatorProfile>,
    currentLanguage: AppLanguage,
    onSelectConversation: (ChatConversation) -> Unit,
    onQuickChatWithCreator: (CreatorProfile) -> Unit,
    onNavigateToExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "संदेश व चैट" else "Creator Messages",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "क्रिएटर्स के साथ 1-on-1 बातचीत करें" else "Private 1-on-1 discussions and collab ideation",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NavySurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSuccess)
                            )
                            Text(
                                text = "${conversations.size} " + if (currentLanguage == AppLanguage.HINDI) "सक्रिय" else "Active",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Quick Direct Message Horizontal Carousel
        if (allCreators.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "त्वरित संदेश (ऑनलाइन क्रिएटर्स)" else "Quick Message (Featured Creators)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ElectricCyan
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(allCreators.take(8)) { creator ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(72.dp)
                                    .clickable { onQuickChatWithCreator(creator) }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color(creator.avatarColorHex))
                                        .border(2.dp, PurplePrimary.copy(alpha = 0.6f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = creator.name.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = creator.name.substringBefore(" "),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = creator.category.localizedTitle(currentLanguage),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = TextTertiary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Conversation List Header
        item {
            Text(
                text = if (currentLanguage == AppLanguage.HINDI) "बातचीत सूची" else "Recent Conversations",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextSecondary
            )
        }

        if (conversations.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "अभी कोई सक्रिय बातचीत नहीं है" else "No active conversations yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "क्रिएटर्स खोजें और सहयोग के लिए सीधा संदेश भेजें।"
                            else
                                "Explore creators and tap message to start collaborating on exciting projects.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = onNavigateToExplore,
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (currentLanguage == AppLanguage.HINDI) "क्रिएटर्स खोजें" else "Explore Creators")
                        }
                    }
                }
            }
        } else {
            items(conversations) { conv ->
                Surface(
                    onClick = { onSelectConversation(conv) },
                    shape = RoundedCornerShape(14.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("conversation_item_${conv.partnerId}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(conv.partnerAvatarColorHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = conv.partnerName.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        // Info
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = conv.partnerName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = conv.lastMessageTime,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary
                                )
                            }

                            Text(
                                text = conv.partnerChannelName,
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = conv.lastMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (conv.unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(PurplePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = conv.unreadCount.toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OneOnOneChatView(
    partner: CreatorProfile,
    messages: List<ChatMessage>,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val quickSuggestions = listOf(
        if (currentLanguage == AppLanguage.HINDI) "🎬 क्या हम एक वीडियो पर सहयोग कर सकते हैं?" else "🎬 Would you like to collaborate on a video?",
        if (currentLanguage == AppLanguage.HINDI) "🎙️ हमारे पॉडकास्ट में गेस्ट बनेंगे?" else "🎙️ Interested in being a guest on our podcast?",
        if (currentLanguage == AppLanguage.HINDI) "🙌 आपका हालिया वीडियो बहुत बढ़िया था!" else "🙌 Loved your recent YouTube upload!",
        if (currentLanguage == AppLanguage.HINDI) "🔥 रील्स/शॉर्ट्स क्रॉस-प्रमोशन प्लान करें?" else "🔥 Up for a Shorts/Reels cross-promotion?"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NavyBackground,
        topBar = {
            Surface(
                color = NavySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(partner.avatarColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = partner.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = partner.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = partner.channelHandle,
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan
                            )
                            Text(
                                text = "•",
                                color = TextTertiary,
                                fontSize = 10.sp
                            )
                            Text(
                                text = partner.category.localizedTitle(currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = partner.category.color
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = NavySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick suggestion chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(quickSuggestions) { chipText ->
                            Surface(
                                onClick = {
                                    onSendMessage(chipText)
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = NavySurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = chipText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "संदेश लिखें..." else "Type a message...",
                                    color = TextTertiary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = NavyBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = NavyBackground,
                                unfocusedContainerColor = NavyBackground
                            ),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 4,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("message_input_field")
                        )

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    val textToSend = inputText
                                    inputText = ""
                                    onSendMessage(textToSend)
                                    scope.launch {
                                        if (messages.isNotEmpty()) {
                                            listState.animateScrollToItem(messages.size)
                                        }
                                    }
                                }
                            },
                            enabled = inputText.isNotBlank(),
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (inputText.isNotBlank()) PurplePrimary else NavySurfaceVariant
                                )
                                .testTag("send_message_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank()) Color.White else TextTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Profile Summary Header at top of chat
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(partner.avatarColorHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = partner.name.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Text(
                            text = partner.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = partner.channelName + " (${partner.subscriberRange.localizedLabel(currentLanguage)})",
                            style = MaterialTheme.typography.labelMedium,
                            color = ElectricCyan
                        )
                        Text(
                            text = partner.bio,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "🔒 यह चैट केवल आपके और इनके बीच सुरक्षित व निजी है" else "🔒 Messages are encrypted & private between both creators",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = TextTertiary
                        )
                    }
                }
            }

            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "बातचीत शुरू करने के लिए पहला संदेश भेजें 👋" else "Say hi to start collaborating! 👋",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                    }
                }
            } else {
                items(messages) { msg ->
                    ChatBubbleItem(message = msg)
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(message: ChatMessage) {
    val isMe = message.isFromMe

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            color = if (isMe) PurplePrimary else NavySurface,
            border = if (isMe) null else androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                if (!isMe) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ElectricCyan,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}
