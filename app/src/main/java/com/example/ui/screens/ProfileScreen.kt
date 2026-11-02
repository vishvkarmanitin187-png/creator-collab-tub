package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "क्या आप वाकई अपना क्रिएटर खाता हटाना चाहते हैं? यह कार्रवाई अपरिवर्तनीय है। आपकी प्रोफ़ाइल और सभी प्रोजेक्ट्स क्लाउड से मिटा दिए जाएंगे।"
                        else
                            "Are you sure you want to permanently delete your creator profile and data? This action cannot be undone and will erase your cloud records immediately."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteAccountDialog = false
                            onDeleteAccount()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                    ) {
                        Text(if (currentLanguage == AppLanguage.HINDI) "हाँ, हमेशा के लिए हटाएं" else "Yes, Delete Permanently")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAccountDialog = false }) {
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
