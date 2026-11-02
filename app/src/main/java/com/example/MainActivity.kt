package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppLanguage
import com.example.repository.FirestoreCreatorCollabRepository
import com.example.ui.auth.AuthManager
                                        channelName = chName,
                                        channelUrl = chUrl,
                                        category = cat,
                                        subRange = sub,
                                        language = lang,
                                        country = country,
                                        bio = bio,
                                        interests = interests,
                                        photoUri = photoUri
                                    )
                                }
                            )
                        }
                    }
                }

                if (collabTargetCreator != null) {
                    CollabRequestDialog(
                        targetCreator = collabTargetCreator!!,
                        currentLanguage = currentLanguage,
                        onDismiss = { viewModel.closeCollabDialog() },
                        onSubmit = { title, type, timeline, message ->
                            viewModel.sendCollabProposal(title, type, timeline, message)
                        }
                    )
                }
            }
        }
    }
}
