package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun ProjectsScreen(
    projects: List<CollabProject>,
    collabRequests: List<CollabRequest>,
    currentLanguage: AppLanguage,
    onToggleMilestone: (projectId: String, milestoneId: String) -> Unit,
    onUpdateProjectNotes: (projectId: String, notes: String) -> Unit,
    onUpdateRequestStatus: (requestId: String, status: CollabStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Projects, 1 = Collab Requests
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("projects_screen")
    ) {
        // Tab Header
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = NavySurface,
            contentColor = PurplePrimary,
            divider = { HorizontalDivider(color = NavyBorder) }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "प्रोजेक्ट्स (${projects.size})" else "Active Projects (${projects.size})",
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.testTag("tab_projects")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "सहयोग अनुरोध (${collabRequests.size})" else "Collab Requests (${collabRequests.size})",
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.testTag("tab_requests")
            )
        }

        if (selectedTab == 0) {
            // Projects Tab
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (projects.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NavySurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Handshake,
                                    contentDescription = null,
                                    tint = PurpleSecondary,
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "कोई सक्रिय प्रोजेक्ट नहीं" else "No Active Projects Yet",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "क्रिएटर्स को सहयोग प्रस्ताव भेजें या प्राप्त अनुरोध स्वीकार करें।"
                                    else
                                        "Propose a collab from Explore or accept an incoming proposal to launch a project pipeline.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(projects, key = { it.id }) { project ->
                        ProjectCard(
                            project = project,
                            currentLanguage = currentLanguage,
                            onToggleMilestone = { milestoneId ->
                                onToggleMilestone(project.id, milestoneId)
                            },
                            onSaveNotes = { newNotes ->
                                onUpdateProjectNotes(project.id, newNotes)
                            },
                            onOpenChannel = { url ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        } else {
            // Collab Requests Tab
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (collabRequests.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NavySurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MailOutline,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "कोई सहयोग अनुरोध नहीं" else "No Collab Proposals",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "क्रिएटर खोजें और अपना पहला सहयोग प्रस्ताव भेजें!"
                                    else
                                        "Send your first collaboration pitch from the Explore tab!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(collabRequests, key = { it.id }) { request ->
                        CollabRequestCard(
                            request = request,
                            currentLanguage = currentLanguage,
                            onAccept = {
                                onUpdateRequestStatus(request.id, CollabStatus.ACCEPTED)
                            },
                            onDecline = {
                                onUpdateRequestStatus(request.id, CollabStatus.DECLINED)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectCard(
    project: CollabProject,
    currentLanguage: AppLanguage,
    onToggleMilestone: (String) -> Unit,
    onSaveNotes: (String) -> Unit,
    onOpenChannel: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var notesText by remember(project.notes) { mutableStateOf(project.notes) }
    var isEditingNotes by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Partner & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = project.status.color.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, project.status.color)
                ) {
                    Text(
                        text = project.status.localizedLabel(currentLanguage),
                        color = project.status.color,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "${if (currentLanguage == AppLanguage.HINDI) "रिलीज़: " else "Target: "} ${project.targetReleaseDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Project Title
            Text(
                text = project.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )

            // Partner Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "${if (currentLanguage == AppLanguage.HINDI) "सहयोगी: " else "Partner: "} ${project.partnerName}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = ElectricCyan
                )
                Text(
                    text = "• ${project.collabType.localizedTitle(currentLanguage)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "प्रगति (Milestones)" else "Pipeline Progress",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = "${(project.progressPercent * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = PurplePrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { project.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = PurplePrimary,
                trackColor = NavySurfaceVariant,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Toggle Expand for Milestones & Notes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded)
                        (if (currentLanguage == AppLanguage.HINDI) "विवरण छिपाएं ▲" else "Hide Details ▲")
                    else
                        (if (currentLanguage == AppLanguage.HINDI) "माइलस्टोन्स व नोट्स देखें ▼" else "View Milestones & Notes ▼"),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = PurpleSecondary
                )

                IconButton(
                    onClick = { onOpenChannel(project.partnerChannelUrl) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Partner Channel",
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = NavyBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Milestones Checklist
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "प्रोजेक्ट चरण (Checklist)" else "Checklist Milestones",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                project.milestones.forEach { milestone ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleMilestone(milestone.id) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = milestone.isCompleted,
                            onCheckedChange = { onToggleMilestone(milestone.id) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = EmeraldSuccess,
                                uncheckedColor = TextSecondary,
                                checkmarkColor = Color.Black
                            )
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = milestone.title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (milestone.isCompleted) FontWeight.Normal else FontWeight.Medium
                                ),
                                color = if (milestone.isCompleted) TextMuted else TextPrimary
                            )
                            Text(
                                text = "${if (currentLanguage == AppLanguage.HINDI) "नियत तिथि: " else "Due: "} ${milestone.dueDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Collaborative Notes Box
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "सहयोग नोट्स (Collaboration Notes)" else "Collaboration Notes",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (isEditingNotes) {
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { isEditingNotes = false }) {
                            Text(if (currentLanguage == AppLanguage.HINDI) "रद्द करें" else "Cancel")
                        }
                        Button(
                            onClick = {
                                onSaveNotes(notesText)
                                isEditingNotes = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                        ) {
                            Text(if (currentLanguage == AppLanguage.HINDI) "सेव करें" else "Save Notes")
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NavySurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isEditingNotes = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (notesText.isNotBlank()) notesText else (if (currentLanguage == AppLanguage.HINDI) "नोट्स जोड़ने के लिए टैप करें..." else "Tap to add collab agreements, script links or dates..."),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (notesText.isNotBlank()) TextSecondary else TextMuted,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Notes",
                                tint = PurpleSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CollabRequestCard(
    request: CollabRequest,
    currentLanguage: AppLanguage,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Type & Direction & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (request.isIncoming) ElectricCyan.copy(alpha = 0.15f) else PurplePrimary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (request.isIncoming) ElectricCyan else PurplePrimary
                    )
                ) {
                    Text(
                        text = if (request.isIncoming)
                            (if (currentLanguage == AppLanguage.HINDI) "प्राप्त अनुरोध" else "Incoming")
                        else
                            (if (currentLanguage == AppLanguage.HINDI) "भेजा गया अनुरोध" else "Sent by You"),
                        color = if (request.isIncoming) ElectricCyan else PurpleSecondary,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = request.status.color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = request.status.localizedLabel(currentLanguage),
                        color = request.status.color,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Project Title
            Text(
                text = request.projectTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )

            // Partner details
            Text(
                text = if (request.isIncoming)
                    "${if (currentLanguage == AppLanguage.HINDI) "प्रेषक: " else "From: "} ${request.senderName} (${request.senderChannelName})"
                else
                    "${if (currentLanguage == AppLanguage.HINDI) "प्राप्तकर्ता: " else "To: "} ${request.targetCreatorName} (${request.targetChannelName})",
                style = MaterialTheme.typography.bodySmall,
                color = ElectricCyan
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Timeline & Collab Type
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = request.timeline,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Text(
                    text = "• ${request.collabType.localizedTitle(currentLanguage)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PurpleSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Message Pitch
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NavySurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\"${request.message}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(10.dp),
                    lineHeight = 18.sp
                )
            }

            // Interactive Actions for Incoming Pending Requests
            if (request.isIncoming && request.status == CollabStatus.PENDING) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoseError)
                    ) {
                        Text(if (currentLanguage == AppLanguage.HINDI) "अस्वीकार" else "Decline")
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "स्वीकार करें" else "Accept Collab",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
