package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.model.AppLanguage
import com.example.model.CreatorProfile
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatorCard(
    creator: CreatorProfile,
    currentLanguage: AppLanguage,
    onCardClick: () -> Unit,
    onCollabClick: () -> Unit,
    onOpenChannelUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onCardClick)
            .testTag("creator_card_${creator.id}"),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Bar of Card: Sample Profile Badge & Category Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = creator.category.color.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, creator.category.color.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = creator.category.localizedTitle(currentLanguage),
                        color = creator.category.color,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Sample Profile Notice
                if (creator.isSample) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AmberAccent.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, AmberAccent.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "नमूना प्रोफ़ाइल" else "SAMPLE PROFILE",
                                color = AmberAccent,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Creator Header: Avatar + Name + Channel Handle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
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
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = creator.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Creator",
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "${creator.channelName} • ${creator.channelHandle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bio / Tagline
            Text(
                text = creator.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Stats / Badges Row (Subscribers, Language, City)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Subs
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = creator.subscriberRange.localizedLabel(currentLanguage),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                }

                // Language
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = PurpleSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = creator.language,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }

                // City
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = creator.city.split(",").firstOrNull()?.trim() ?: creator.city,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Collab Interests Tags
            Text(
                text = if (currentLanguage == AppLanguage.HINDI) "सहयोग रुचियां:" else "Collab Interests:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                creator.collaborationInterests.take(3).forEach { interest ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NavySurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
                    ) {
                        Text(
                            text = interest,
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Channel Link Button
                OutlinedButton(
                    onClick = { onOpenChannelUrl(creator.channelUrl) },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("btn_channel_${creator.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = YouTubeRed
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, YouTubeRed.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "YouTube Channel",
                        modifier = Modifier.size(16.dp),
                        tint = YouTubeRed
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "YouTube",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                // Propose Collab Button
                Button(
                    onClick = onCollabClick,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(42.dp)
                        .testTag("btn_collab_${creator.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Handshake,
                        contentDescription = "Propose Collab",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "सहयोग प्रस्ताव" else "Propose Collab",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
