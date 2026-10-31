package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onToggleLanguage: () -> Unit = {}
) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_top_bar"),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NavyBackground,
            titleContentColor = TextPrimary,
            navigationIconContentColor = TextPrimary,
            actionIconContentColor = TextPrimary
        ),
        navigationIcon = {
            if (showBackButton) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                        tint = TextPrimary
                    )
                }
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_creator_collab_logo),
                    contentDescription = "Creator Collab Hub Logo",
                    modifier = Modifier
                        .padding(start = 12.dp, end = 6.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .testTag("top_bar_brand_logo")
                )
            }
        },
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = TextPrimary
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricCyan
                    )
                }
            }
        },
        actions = {
            // Language Switcher Badge Button
            Surface(
                onClick = onToggleLanguage,
                shape = RoundedCornerShape(20.dp),
                color = NavySurfaceVariant,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .border(1.dp, PurplePrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .testTag("language_toggle_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Toggle Language",
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "हिंदी" else "English",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextPrimary
                    )
                }
            }
        }
    )
}
