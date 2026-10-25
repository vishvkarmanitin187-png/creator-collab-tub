package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppLanguage
import com.example.model.CreatorCategory
import com.example.repository.FirestoreCreatorCollabRepository
import com.example.ui.auth.AuthManager
import com.example.ui.components.AppTopBar
import com.example.ui.components.CollabRequestDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.CreatorCollabViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

enum class NavigationTab(
    val titleEn: String,
    val titleHi: String,
    val icon: ImageVector,
    val testTag: String
) {
    HOME("Home", "होम", Icons.Default.Home, "nav_tab_home"),
    EXPLORE("Explore", "खोजें", Icons.Default.Explore, "nav_tab_explore"),
    PROJECTS("Projects", "प्रोजेक्ट्स", Icons.Default.Assignment, "nav_tab_projects"),
    PROFILE("Profile", "प्रोफ़ाइल", Icons.Default.Person, "nav_tab_profile")
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Attempt silent background auto sign-in for returning users
        val credentialManager = CredentialManager.create(this)
        AuthManager.attemptAutoSignIn(
            context = this,
            credentialManager = credentialManager,
            onAuthSuccess = {},
            onUnauthenticated = {},
            scope = lifecycleScope
        )

        setContent {
            MyApplicationTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isGuestMode by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf(AppLanguage.ENGLISH) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
            if (auth.currentUser != null) {
                isGuestMode = false
            }
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null && !isGuestMode) {
        SignInScreen(
            currentLanguage = currentLanguage,
            onToggleLanguage = {
                currentLanguage = if (currentLanguage == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
            },
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
                isGuestMode = false
            },
            onContinueAsGuest = {
                isGuestMode = true
            }
        )
    } else {
        val viewModel: CreatorCollabViewModel = viewModel(
            key = currentUser?.uid ?: "guest_user",
            factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = if (currentUser != null) {
                        FirestoreCreatorCollabRepository(context)
                    } else {
                        com.example.repository.LocalCreatorCollabRepository()
                    }
                    @Suppress("UNCHECKED_CAST")
                    return CreatorCollabViewModel(repository) as T
                }
            }
        )

        CreatorCollabApp(
            viewModel = viewModel,
            isGuest = (currentUser == null),
            onRequestSignIn = {
                isGuestMode = false
            },
            onSignOut = {
                if (currentUser != null) {
                    AuthManager.signOut(
                        context = context,
                        credentialManager = credentialManager,
                        onSignOutComplete = {
                            currentUser = null
                            isGuestMode = false
                        },
                        scope = scope
                    )
                } else {
                    isGuestMode = false
                }
            }
        )
    }
}

@Composable
fun CreatorCollabApp(
    viewModel: CreatorCollabViewModel,
    isGuest: Boolean = false,
    onRequestSignIn: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }

    val currentLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val filteredCreators by viewModel.filteredCreators.collectAsStateWithLifecycle()
    val rawCreators by viewModel.rawCreators.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedLanguageFilter by viewModel.selectedLanguageFilter.collectAsStateWithLifecycle()
    val selectedSubscriberRange by viewModel.selectedSubscriberRange.collectAsStateWithLifecycle()
    val selectedCreator by viewModel.selectedCreator.collectAsStateWithLifecycle()
    val collabTargetCreator by viewModel.collabTargetCreator.collectAsStateWithLifecycle()
    val collabRequests by viewModel.collabRequests.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Handle System Back Press:
    // 1. If detail view is open, close it
    BackHandler(enabled = selectedCreator != null) {
        viewModel.selectCreator(null)
    }

    // 2. If on secondary tabs, return to HOME tab instead of closing the app
    BackHandler(enabled = selectedCreator == null && currentTab != NavigationTab.HOME) {
        currentTab = NavigationTab.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = NavyBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (selectedCreator == null) {
                Column {
                    val screenTitle = when (currentTab) {
                        NavigationTab.HOME -> if (currentLanguage == AppLanguage.HINDI) "क्रिएटर कोलैब हब" else "Creator Collab Hub"
                        NavigationTab.EXPLORE -> if (currentLanguage == AppLanguage.HINDI) "क्रिएटर खोजें" else "Explore Creators"
                        NavigationTab.PROJECTS -> if (currentLanguage == AppLanguage.HINDI) "सहयोग व प्रोजेक्ट्स" else "Projects & Collabs"
                        NavigationTab.PROFILE -> if (currentLanguage == AppLanguage.HINDI) "मेरी क्रिएटर प्रोफ़ाइल" else "My Creator Profile"
                    }

                    val subtitle = when (currentTab) {
                        NavigationTab.HOME -> if (currentLanguage == AppLanguage.HINDI) "यूट्यूब सहयोग नेटवर्क" else "YouTube Collab Platform"
                        NavigationTab.EXPLORE -> "${filteredCreators.size} ${if (currentLanguage == AppLanguage.HINDI) "क्रिएटर्स उपलब्ध" else "creators active"}"
                        NavigationTab.PROJECTS -> "${projects.size} ${if (currentLanguage == AppLanguage.HINDI) "प्रोजेक्ट्स" else "pipelines"}"
                        NavigationTab.PROFILE -> userProfile.channelName
                    }

                    AppTopBar(
                        title = screenTitle,
                        subtitle = subtitle,
                        currentLanguage = currentLanguage,
                        onToggleLanguage = { viewModel.toggleLanguage() }
                    )

                    if (isGuest) {
                        Surface(
                            color = PurplePrimary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI)
                                            "गेस्ट मोड: क्लाउड सिंक हेतु साइन-इन करें"
                                        else
                                            "Guest Mode: Sign in to sync data",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = TextPrimary
                                    )
                                }
                                TextButton(
                                    onClick = onRequestSignIn,
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "साइन इन" else "Sign In",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ElectricCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (selectedCreator == null) {
                NavigationBar(
                    containerColor = NavySurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .border(1.dp, NavyBorder)
                        .testTag("bottom_nav_bar")
                ) {
                    NavigationTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        val label = if (currentLanguage == AppLanguage.HINDI) tab.titleHi else tab.titleEn

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                if (tab == NavigationTab.PROJECTS && collabRequests.any { it.isIncoming && it.status == com.example.model.CollabStatus.PENDING }) {
                                    BadgedBox(
                                        badge = {
                                            Badge(containerColor = AmberAccent) {
                                                Text("1", color = Color.Black)
                                            }
                                        }
                                    ) {
                                        Icon(imageVector = tab.icon, contentDescription = label)
                                    }
                                } else {
                                    Icon(imageVector = tab.icon, contentDescription = label)
                                }
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = ElectricCyan,
                                indicatorColor = PurplePrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = if (selectedCreator == null) 80.dp else 16.dp)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 760.dp)
            ) {
                if (selectedCreator != null) {
                    CreatorDetailScreen(
                        creator = selectedCreator!!,
                        currentLanguage = currentLanguage,
                        onBack = { viewModel.selectCreator(null) },
                        onCollabClick = { creator -> viewModel.openCollabDialog(creator) }
                    )
                } else {
                    when (currentTab) {
                        NavigationTab.HOME -> {
                            HomeScreen(
                                creators = rawCreators,
                                currentLanguage = currentLanguage,
                                onCategoryClick = { category ->
                                    viewModel.selectCategory(category)
                                    currentTab = NavigationTab.EXPLORE
                                },
                                onCreatorClick = { creator -> viewModel.selectCreator(creator) },
                                onCollabClick = { creator -> viewModel.openCollabDialog(creator) },
                                onNavigateToExplore = { currentTab = NavigationTab.EXPLORE },
                                onNavigateToProjects = { currentTab = NavigationTab.PROJECTS }
                            )
                        }

                        NavigationTab.EXPLORE -> {
                            ExploreScreen(
                                creators = filteredCreators,
                                searchQuery = searchQuery,
                                selectedCategory = selectedCategory,
                                selectedLanguageFilter = selectedLanguageFilter,
                                selectedSubscriberRange = selectedSubscriberRange,
                                currentLanguage = currentLanguage,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onCategorySelect = { viewModel.selectCategory(it) },
                                onLanguageFilterSelect = { viewModel.selectLanguageFilter(it) },
                                onSubscriberRangeSelect = { viewModel.selectSubscriberRange(it) },
                                onResetFilters = { viewModel.resetFilters() },
                                onCreatorClick = { creator -> viewModel.selectCreator(creator) },
                                onCollabClick = { creator -> viewModel.openCollabDialog(creator) }
                            )
                        }

                        NavigationTab.PROJECTS -> {
                            ProjectsScreen(
                                projects = projects,
                                collabRequests = collabRequests,
                                currentLanguage = currentLanguage,
                                onToggleMilestone = { pId, mId -> viewModel.toggleMilestone(pId, mId) },
                                onUpdateProjectNotes = { pId, notes -> viewModel.updateProjectNotes(pId, notes) },
                                onUpdateRequestStatus = { rId, status -> viewModel.updateRequestStatus(rId, status) }
                            )
                        }

                        NavigationTab.PROFILE -> {
                            ProfileScreen(
                                userProfile = userProfile,
                                currentLanguage = currentLanguage,
                                onToggleLanguage = { viewModel.toggleLanguage() },
                                onResetDemoData = { viewModel.resetToDemoData() },
                                onSignOut = onSignOut,
                                onUpdateProfile = { name, chName, chUrl, cat, sub, lang, bio, interests ->
                                    viewModel.updateUserProfile(name, chName, chUrl, cat, sub, lang, bio, interests)
                                }
                            )
                        }
                    }
                }

                // Collab Request Dialog
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
