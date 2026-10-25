package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.CreatorCategory
import com.example.model.CreatorProfile
import com.example.ui.components.CreatorCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    creators: List<CreatorProfile>,
    currentLanguage: AppLanguage,
    onCategoryClick: (CreatorCategory) -> Unit,
    onCreatorClick: (CreatorProfile) -> Unit,
    onCollabClick: (CreatorProfile) -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToProjects: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF302060),
                                Color(0xFF17152F),
                                NavyBackground
                            )
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0x332DDBFF),
                                    Color.Transparent,
                                    Color(0x554B20A8)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF252345),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            PurplePrimary.copy(alpha = 0.7f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF40E0A0))
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "भारतीय क्रिएटर नेटवर्क"
                                else
                                    "INDIAN CREATOR NETWORK",
                                color = Color(0xFF67E8F9),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "नमस्ते क्रिएटर! 🙏"
                        else
                            "Namaste Creator! 🙏",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.3.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "नए क्रिएटर्स खोजें और साथ मिलकर आगे बढ़ें।"
                        else
                            "Discover creators, collaborate and grow together.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD7D5EF)
                    )
                }
            }
        }

        // Search bar
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        1.dp,
                        NavyBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onNavigateToExplore() }
                    .testTag("home_search_trigger"),
                color = NavySurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search creators",
                        tint = PurplePrimary
                    )

                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "क्रिएटर या कैटेगरी खोजें..."
                        else
                            "Search creators or categories...",
                        color = TextMuted,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filters",
                        tint = ElectricCyan
                    )
                }
            }
        }

        // Sample profile notice
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NavySurfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    AmberAccent.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = AmberAccent,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "ध्यान दें: अभी दिखाए गए क्रिएटर प्रोफ़ाइल नमूना हो सकते हैं। सहयोग करने से पहले प्रोफ़ाइल की जानकारी सत्यापित करें।"
                        else
                            "Notice: Creator profiles may be samples. Verify profile details before starting a collaboration.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp
                        ),
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Categories heading
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 18.dp,
                        bottom = 8.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI)
                        "क्रिएटर कैटेगरी"
                    else
                        "Creator Categories",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )

                TextButton(
                    onClick = onNavigateToExplore,
                    modifier = Modifier.testTag("home_view_all_categories")
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "सभी देखें"
                        else
                            "View All",
                        color = ElectricCyan
                    )
                }
            }
        }

        // Category chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(CreatorCategory.values()) { category ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NavySurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            category.color.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .clickable {
                                onCategoryClick(category)
                            }
                            .testTag("cat_chip_${category.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 11.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(category.color)
                            )

                            Text(
                                text = category.localizedTitle(currentLanguage),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Stats and projects
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        NavyBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onNavigateToExplore()
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(26.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${creators.size}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = TextPrimary
                        )

                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "नमूना क्रिएटर्स"
                            else
                                "Sample Creators",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        NavyBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onNavigateToProjects()
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(26.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "प्रोजेक्ट्स"
                            else
                                "Collaboration",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimary
                        )

                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "काम की स्थिति"
                            else
                                "Track project status",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Featured creators heading
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI)
                        "प्रमुख क्रिएटर्स"
                    else
                        "Featured Creators",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )

                Text(
                    text = "${creators.take(4).size} ${
                        if (currentLanguage == AppLanguage.HINDI)
                            "प्रोफ़ाइल"
                        else
                            "profiles"
                    }",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }

        // Creator cards
        items(creators.take(4)) { creator ->
            CreatorCard(
                creator = creator,
                currentLanguage = currentLanguage,
                onCardClick = {
                    onCreatorClick(creator)
                },
                onCollabClick = {
                    onCollabClick(creator)
                },
                onOpenChannelUrl = { url ->
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                    )

                    runCatching {
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 6.dp
                )
            )
        }
    }
}