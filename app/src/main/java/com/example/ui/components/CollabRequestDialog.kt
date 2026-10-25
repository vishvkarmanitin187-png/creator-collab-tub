package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.model.CollabType
import com.example.model.CreatorProfile
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollabRequestDialog(
    targetCreator: CreatorProfile,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onSubmit: (title: String, type: CollabType, timeline: String, message: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(CollabType.CROSS_PROMO) }
    var timeline by remember { mutableStateOf("Next 2 Weeks") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = NavySurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("collab_request_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "सहयोग प्रस्ताव भेजें" else "Propose Collaboration",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dialog_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                // Target Creator Card Info
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NavySurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "सहयोग पार्टनर:" else "Collaborator:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = "${targetCreator.name} (${targetCreator.channelName})",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = ElectricCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Collab Type Selection
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "सहयोग का प्रकार" else "Collaboration Type",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Collab Type Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (rowTypes in CollabType.values().toList().chunked(2)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (type in rowTypes) {
                                val isSelected = type == selectedType
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedType = type },
                                    label = {
                                        Text(
                                            text = type.localizedTitle(currentLanguage),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PurplePrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = NavySurfaceVariant,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (isSelected) PurplePrimary else NavyBorder,
                                        enabled = true,
                                        selected = isSelected
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Project Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) isError = false
                    },
                    label = {
                        Text(if (currentLanguage == AppLanguage.HINDI) "प्रोजेक्ट का शीर्षक *" else "Project Title *")
                    },
                    placeholder = {
                        Text(
                            if (currentLanguage == AppLanguage.HINDI) "जैसे: दिवाली टेक राउंडअप / पॉडकास्ट एपिसोड" else "e.g. Diwali Tech Roundup / Podcast Ep 4"
                        )
                    },
                    isError = isError && title.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_collab_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Timeline Field
                OutlinedTextField(
                    value = timeline,
                    onValueChange = { timeline = it },
                    label = {
                        Text(if (currentLanguage == AppLanguage.HINDI) "प्रस्तावित समय सीमा" else "Proposed Timeline")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = ElectricCyan
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_collab_timeline"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Pitch Message Field
                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it
                        if (it.isNotBlank()) isError = false
                    },
                    label = {
                        Text(if (currentLanguage == AppLanguage.HINDI) "आपका प्रस्ताव व संदेश *" else "Pitch & Expected Deliverables *")
                    },
                    placeholder = {
                        Text(
                            if (currentLanguage == AppLanguage.HINDI)
                                "बताएं आप कैसे साथ मिलकर वीडियो बनाना चाहते हैं..."
                            else
                                "Explain what you want to create together and how both channels benefit..."
                        )
                    },
                    isError = isError && message.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("input_collab_message"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                if (isError) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "कृपया शीर्षक और संदेश भरें" else "Please fill in title and message",
                        color = RoseError,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_cancel_collab"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
                    ) {
                        Text(if (currentLanguage == AppLanguage.HINDI) "रद्द करें" else "Cancel")
                    }

                    Button(
                        onClick = {
                            if (title.isBlank() || message.isBlank()) {
                                isError = true
                            } else {
                                onSubmit(title.trim(), selectedType, timeline.trim(), message.trim())
                            }
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("btn_submit_collab"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurplePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "प्रस्ताव भेजें" else "Send Proposal",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
