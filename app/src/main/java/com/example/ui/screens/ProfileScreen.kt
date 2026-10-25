package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: CreatorProfile,
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onResetDemoData: () -> Unit,
    onSignOut: () -> Unit = {},
    onUpdateProfile: (
        name: String,
        channelName: String,
        channelUrl: String,
        category: CreatorCategory,
        subRange: SubscriberRange,
        language: String,
        bio: String,
        interests: List<String>
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var collabNotificationsEnabled by remember { mutableStateOf(true) }
    var milestoneAlertsEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Edit form states
    var nameState by remember(userProfile.name) { mutableStateOf(userProfile.name) }
    var channelNameState by remember(userProfile.channelName) { mutableStateOf(userProfile.channelName) }
    var channelUrlState by remember(userProfile.channelUrl) { mutableStateOf(userProfile.channelUrl) }
    var categoryState by remember(userProfile.category) { mutableStateOf(userProfile.category) }
    var subRangeState by remember(userProfile.subscriberRange) { mutableStateOf(userProfile.subscriberRange) }
    var languageState by remember(userProfile.language) { mutableStateOf(userProfile.language) }
    var bioState by remember(userProfile.bio) { mutableStateOf(userProfile.bio) }
    var newInterestInput by remember { mutableStateOf("") }
    var interestsList by remember(userProfile.collaborationInterests) {
        mutableStateOf(userProfile.collaborationInterests)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 96.dp)
            .testTag("profile_screen")
    ) {
        // Profile Header Card
        Surface(
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            color = NavySurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(PurplePrimary, ElectricCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = userProfile.name.split(" ")
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2)
                        .joinToString("")
                    Text(
                        text = initials.ifBlank { "ME" },
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = userProfile.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )

                Text(
                    text = "${userProfile.channelName} • ${userProfile.channelHandle}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElectricCyan
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = userProfile.category.color.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, userProfile.category.color)
                ) {
                    Text(
                        text = userProfile.category.localizedTitle(currentLanguage),
                        color = userProfile.category.color,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Edit Profile Toggle Button
                Button(
                    onClick = { isEditing = !isEditing },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) NavySurfaceVariant else PurplePrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_toggle_edit_profile")
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEditing)
                            (if (currentLanguage == AppLanguage.HINDI) "संपादन बंद करें" else "Cancel Edit")
                        else
                            (if (currentLanguage == AppLanguage.HINDI) "प्रोफ़ाइल संपादित करें" else "Edit Profile"),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing) {
            // Edit Profile Form
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "क्रिएटर विवरण संपादित करें" else "Edit Creator Details",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    OutlinedTextField(
                        value = nameState,
                        onValueChange = { nameState = it },
                        label = { Text(if (currentLanguage == AppLanguage.HINDI) "आपका नाम" else "Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = channelNameState,
                        onValueChange = { channelNameState = it },
                        label = { Text(if (currentLanguage == AppLanguage.HINDI) "यूट्यूब चैनल का नाम" else "YouTube Channel Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_channel_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = channelUrlState,
                        onValueChange = { channelUrlState = it },
                        label = { Text("YouTube URL") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_channel_url"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "कैटेगरी चुनें:" else "Select Category:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CreatorCategory.values().forEach { cat ->
                            val isSelected = categoryState == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { categoryState = cat },
                                label = { Text(cat.localizedTitle(currentLanguage), style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = cat.color,
                                    selectedLabelColor = Color.White,
                                    containerColor = NavySurfaceVariant,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "सब्सक्राइबर स्तर:" else "Subscriber Tier:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SubscriberRange.values().forEach { range ->
                            val isSelected = subRangeState == range
                            FilterChip(
                                selected = isSelected,
                                onClick = { subRangeState = range },
                                label = { Text(range.localizedLabel(currentLanguage), style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PurplePrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = NavySurfaceVariant,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = languageState,
                        onValueChange = { languageState = it },
                        label = { Text(if (currentLanguage == AppLanguage.HINDI) "प्राथमिक भाषा" else "Primary Language") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = bioState,
                        onValueChange = { bioState = it },
                        label = { Text(if (currentLanguage == AppLanguage.HINDI) "बायो व पिच" else "Channel Bio & Pitch") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("input_edit_bio"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    // Collaboration Interests tags
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "सहयोग रुचियां (Collab Interests):" else "Collaboration Interests:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        interestsList.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NavySurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = tag, style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove tag",
                                        tint = RoseError,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable {
                                                interestsList = interestsList.filter { it != tag }
                                            }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newInterestInput,
                            onValueChange = { newInterestInput = it },
                            placeholder = { Text(if (currentLanguage == AppLanguage.HINDI) "नया टैग (जैसे पॉडकास्ट)" else "Add interest tag...", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = NavyBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (newInterestInput.isNotBlank()) {
                                    interestsList = interestsList + newInterestInput.trim()
                                    newInterestInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                        ) {
                            Text(if (currentLanguage == AppLanguage.HINDI) "जोड़ें" else "Add", color = Color.Black)
                        }
                    }

                    // Save Button
                    Button(
                        onClick = {
                            onUpdateProfile(
                                nameState.trim(),
                                channelNameState.trim(),
                                channelUrlState.trim(),
                                categoryState,
                                subRangeState,
                                languageState.trim(),
                                bioState.trim(),
                                interestsList
                            )
                            isEditing = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_save_profile")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "प्रोफ़ाइल सुरक्षित करें" else "Save Profile",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            // View Profile Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bio Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "बायो व पिच" else "Bio & Pitch",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = userProfile.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = PurpleSecondary, modifier = Modifier.size(16.dp))
                            Text(text = userProfile.language, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                            Text(text = userProfile.city, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }

                // Collaboration Interests Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "मेरी सहयोग रुचियां" else "My Collaboration Interests",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            userProfile.collaborationInterests.forEach { interest ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = NavySurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = interest,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // YouTube Channel Preview Box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, YouTubeRed.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = YouTubeRed, modifier = Modifier.size(24.dp))
                            Column {
                                Text(text = userProfile.channelName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                Text(text = userProfile.channelUrl, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                        }
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(userProfile.channelUrl))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "Open Channel", tint = ElectricCyan)
                        }
                    }
                }

                // Architecture & Future Cloud Prep Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavySurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CloudQueue, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "आर्किटेक्चर व क्लाउड रेडीनेस" else "Architecture & Cloud Ready",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "यह ऐप Firebase Authentication (गूगल साइन-इन) व Cloud Firestore सिंक के लिए तैयार आर्किटेक्चर पर बना है। सभी एपीआई कीज सुरक्षित रूप से अलग रखी गई हैं।"
                            else
                                "Clean repository layer ready for Firestore sync and Google Sign-In. Secrets and API keys remain securely detached from client bundles.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }

                // App Language Quick Switcher in Profile
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleLanguage() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Translate, contentDescription = null, tint = PurplePrimary)
                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "ऐप भाषा (Language)" else "App Language",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "वर्तमान: हिंदी (English में बदलें)" else "Current: English (Switch to हिंदी)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = ElectricCyan)
                    }
                }

                // Settings & Notification Controls Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "सेटिंग्स व प्राथमिकताएं" else "Settings & Preferences",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )

                        // Collab notification switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "सहयोग प्रस्ताव अलर्ट" else "Collab Proposal Alerts",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "नए प्रस्ताव प्राप्त होने पर सूचना दें" else "Notify when incoming pitches arrive",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                            Switch(
                                checked = collabNotificationsEnabled,
                                onCheckedChange = { collabNotificationsEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PurplePrimary
                                )
                            )
                        }

                        HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f))

                        // Milestone update switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "प्रोजेक्ट माइलस्टोन अपडेट्स" else "Project Milestone Updates",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "कार्य पूर्ण होने पर स्टेटस अपडेट" else "Alert on checklist milestone completion",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                            Switch(
                                checked = milestoneAlertsEnabled,
                                onCheckedChange = { milestoneAlertsEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = ElectricCyan
                                )
                            )
                        }
                    }
                }

                // Google Account / Sign Out Card
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "Google खाता (Account)" else "Google Account",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "क्लाउड सिंक सक्रिय है" else "Firestore Cloud Sync Active",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldSuccess
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = onSignOut,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (currentLanguage == AppLanguage.HINDI) "लॉग आउट" else "Sign Out")
                        }
                    }
                }

                // Demo Reset Action Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoseError.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
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
                                text = if (currentLanguage == AppLanguage.HINDI) "प्रोटोटाइप डेटा रीसेट करें" else "Reset Demo Data",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "शुरुआती नमूना प्रोफाइल व प्रोजेक्ट्स वापस लाएं" else "Restore default demo creators & project pipelines",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoseError.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (currentLanguage == AppLanguage.HINDI) "रीसेट" else "Reset")
                        }
                    }
                }
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "डेमो डेटा रीसेट करें?" else "Reset Demo Data?",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "यह सभी सहयोग अनुरोधों और प्रोजेक्ट्स को उनकी प्रारंभिक स्थिति में रीसेट कर देगा।"
                        else
                            "This will restore sample creator profiles, collab requests, and project pipelines to default values."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onResetDemoData()
                            showResetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                    ) {
                        Text(if (currentLanguage == AppLanguage.HINDI) "हाँ, रीसेट करें" else "Yes, Reset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text(if (currentLanguage == AppLanguage.HINDI) "रद्द करें" else "Cancel")
                    }
                },
                containerColor = NavySurface,
                titleContentColor = TextPrimary,
                textContentColor = TextSecondary
            )
        }
    }
}
