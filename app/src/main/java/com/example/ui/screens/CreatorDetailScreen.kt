package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.CreatorProfile
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatorDetailScreen(
    creator: CreatorProfile,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onCollabClick: (CreatorProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("creator_detail_screen"),
        bottomBar = {
            Surface(
                color = NavySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(creator.channelUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("detail_open_yt"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = YouTubeRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, YouTubeRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = YouTubeRed
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "YouTube",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = { onCollabClick(creator) },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                            .testTag("detail_propose_collab"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "सहयोग प्रस्ताव भेजें" else "Propose Collab",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "क्रिएटर प्रोफ़ाइल" else "Creator Profile",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }

            // Hero Profile Banner & Avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                PurpleDark,
                                NavySurfaceVariant,
                                creator.category.color.copy(alpha = 0.4f)
                            )
                        )
                    )
            ) {
                // Large Avatar floating in header
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp)
                        .offset(y = 40.dp)
                        .size(84.dp)
                        .clip(CircleShape)
                        .border(3.dp, NavyBackground, CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(creator.avatarColorHex),
                                    PurplePrimary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = creator.name.split(" ")
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2)
                        .joinToString("")
                    Text(
                        text = initials.ifBlank { "YT" },
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Name, Verified Badge, Channel
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = creator.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified Creator",
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "${creator.channelName} • ${creator.channelHandle}",
                    style = MaterialTheme.typography.titleSmall,
                    color = PurpleSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badges: Category & Sample Notice
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = creator.category.color.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, creator.category.color)
                    ) {
                        Text(
                            text = creator.category.localizedTitle(currentLanguage),
                            color = creator.category.color,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (creator.isSample) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberAccent.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "नमूना प्रोफ़ाइल" else "SAMPLE PROFILE",
                                color = AmberAccent,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Metrics Grid
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = creator.subscriberRange.localizedLabel(currentLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CyanAccent
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "सब्सक्राइबर्स" else "Audience",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }

                        VerticalDivider(modifier = Modifier.height(36.dp), color = NavyBorder)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = creator.language,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "प्राथमिक भाषा" else "Language",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }

                        VerticalDivider(modifier = Modifier.height(36.dp), color = NavyBorder)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${creator.totalVideosSample}+",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldSuccess
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "वीडियोज" else "Uploads",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // About / Bio Section
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "चैनल के बारे में (About)" else "About Channel",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = creator.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Location / City
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${if (currentLanguage == AppLanguage.HINDI) "स्थान: " else "Based in: "} ${creator.city}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Collab Interests
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "सहयोग के अवसर (Collab Opportunities)" else "Collaboration Preferences",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    creator.collaborationInterests.forEach { interest ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavySurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PurplePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = interest,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Channel Link Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, YouTubeRed.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "YouTube Channel",
                                style = MaterialTheme.typography.labelSmall,
                                color = YouTubeRed
                            )
                            Text(
                                text = creator.channelUrl,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(creator.channelUrl))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Open Link",
                                tint = ElectricCyan
                            )
                        }
                    }
                }
            }
        }
    }
}
