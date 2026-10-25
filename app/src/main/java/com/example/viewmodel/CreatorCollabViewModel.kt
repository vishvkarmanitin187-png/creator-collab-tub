package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.repository.CreatorCollabRepository
import com.example.repository.LocalCreatorCollabRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface CollabsUiState {
    object Loading : CollabsUiState
    data class Error(val message: String) : CollabsUiState
}

sealed interface ProfileSaveState {
    object Idle : ProfileSaveState
    object Loading : ProfileSaveState
    data class Success(val message: String) : ProfileSaveState
    data class Error(val message: String) : ProfileSaveState
}

class CreatorCollabViewModel(
    private val repository: CreatorCollabRepository = LocalCreatorCollabRepository()
) : ViewModel() {

    private val _appLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<CreatorCategory?>(null)
    val selectedCategory: StateFlow<CreatorCategory?> = _selectedCategory.asStateFlow()

    private val _selectedLanguageFilter = MutableStateFlow<String?>(null)
    val selectedLanguageFilter: StateFlow<String?> = _selectedLanguageFilter.asStateFlow()

    private val _selectedSubscriberRange = MutableStateFlow<SubscriberRange?>(null)
    val selectedSubscriberRange: StateFlow<SubscriberRange?> = _selectedSubscriberRange.asStateFlow()

    private val _selectedCreator = MutableStateFlow<CreatorProfile?>(null)
    val selectedCreator: StateFlow<CreatorProfile?> = _selectedCreator.asStateFlow()

    private val _collabTargetCreator = MutableStateFlow<CreatorProfile?>(null)
    val collabTargetCreator: StateFlow<CreatorProfile?> = _collabTargetCreator.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val rawCreators = repository.getCreators()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredCreators: StateFlow<List<CreatorProfile>> = combine(
        rawCreators,
        _searchQuery,
        _selectedCategory,
        _selectedLanguageFilter,
        _selectedSubscriberRange
    ) { creators, query, category, langFilter, subRange ->
        creators.filter { creator ->
            val matchesQuery = query.isBlank() ||
                    creator.name.contains(query, ignoreCase = true) ||
                    creator.channelName.contains(query, ignoreCase = true) ||
                    creator.bio.contains(query, ignoreCase = true) ||
                    creator.collaborationInterests.any { it.contains(query, ignoreCase = true) }

            val matchesCategory = category == null || creator.category == category

            val matchesLanguage = langFilter.isNullOrBlank() ||
                    creator.language.contains(langFilter, ignoreCase = true)

            val matchesRange = subRange == null || creator.subscriberRange == subRange

            matchesQuery && matchesCategory && matchesLanguage && matchesRange
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val collabRequests = repository.getCollabRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects = repository.getProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile = repository.getUserProfile()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            CreatorProfile(
                id = "user_me",
                name = "Ravi Verma",
                channelName = "Desi Creator Studio",
                channelHandle = "@DesiCreatorStudio",
                channelUrl = "https://www.youtube.com/@DesiCreatorStudio",
                category = CreatorCategory.TECH,
                subscriberRange = SubscriberRange.TIER_100K_500K,
                language = "Hindi / Hinglish",
                collaborationInterests = listOf("Cross-Promotion", "Guest Podcasts", "Tech Review Exchanges"),
                bio = "Independent creator crafting tech tutorials, filmmaking workflows, and creator studio breakdowns.",
                city = "New Delhi, India",
                isSample = false
            )
        )

    fun toggleLanguage() {
        _appLanguage.value = if (_appLanguage.value == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
    }

    fun setLanguage(lang: AppLanguage) {
        _appLanguage.value = lang
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: CreatorCategory?) {
        _selectedCategory.value = category
    }

    fun selectLanguageFilter(lang: String?) {
        _selectedLanguageFilter.value = lang
    }

    fun selectSubscriberRange(range: SubscriberRange?) {
        _selectedSubscriberRange.value = range
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        _selectedLanguageFilter.value = null
        _selectedSubscriberRange.value = null
    }

    fun selectCreator(creator: CreatorProfile?) {
        _selectedCreator.value = creator
    }

    fun openCollabDialog(creator: CreatorProfile) {
        _collabTargetCreator.value = creator
    }

    fun closeCollabDialog() {
        _collabTargetCreator.value = null
    }

    fun sendCollabProposal(
        title: String,
        collabType: CollabType,
        timeline: String,
        message: String
    ) {
        val target = _collabTargetCreator.value ?: return
        val currentSender = userProfile.value

        viewModelScope.launch {
            val newRequest = CollabRequest(
                id = "req_" + UUID.randomUUID().toString().take(8),
                targetCreatorId = target.id,
                targetCreatorName = target.name,
                targetChannelName = target.channelName,
                senderName = currentSender.name,
                senderChannelName = currentSender.channelName,
                senderChannelUrl = currentSender.channelUrl,
                collabType = collabType,
                projectTitle = title.ifBlank { "Collaboration: ${target.channelName}" },
                timeline = timeline.ifBlank { "Next 2-3 Weeks" },
                message = message.ifBlank { "Hey! I love your content and would love to collaborate." },
                status = CollabStatus.PENDING,
                createdAtFormatted = "Just now",
                isIncoming = false
            )
            repository.sendCollabRequest(newRequest)
            _collabTargetCreator.value = null
            _snackbarMessage.value = if (_appLanguage.value == AppLanguage.HINDI) {
                "${target.channelName} को सहयोग प्रस्ताव सफलतापूर्वक भेजा गया!"
            } else {
                "Collaboration proposal sent to ${target.channelName}!"
            }
        }
    }

    fun updateRequestStatus(requestId: String, newStatus: CollabStatus) {
        viewModelScope.launch {
            repository.updateCollabRequestStatus(requestId, newStatus)
            _snackbarMessage.value = if (_appLanguage.value == AppLanguage.HINDI) {
                "अनुरोध स्थिति अपडेट हुई: ${newStatus.labelHi}"
            } else {
                "Request status updated: ${newStatus.labelEn}"
            }
        }
    }

    fun toggleMilestone(projectId: String, milestoneId: String) {
        viewModelScope.launch {
            repository.toggleMilestone(projectId, milestoneId)
        }
    }

    fun updateProjectNotes(projectId: String, notes: String) {
        viewModelScope.launch {
            repository.updateProjectNotes(projectId, notes)
            _snackbarMessage.value = if (_appLanguage.value == AppLanguage.HINDI) "नोट्स सेव हुए" else "Project notes saved"
        }
    }

    private val _profileSaveState = MutableStateFlow<ProfileSaveState>(ProfileSaveState.Idle)
    val profileSaveState: StateFlow<ProfileSaveState> = _profileSaveState.asStateFlow()

    fun resetProfileSaveState() {
        _profileSaveState.value = ProfileSaveState.Idle
    }

    fun updateUserProfile(
        name: String,
        channelHandle: String,
        channelName: String,
        channelUrl: String,
        category: CreatorCategory,
        subRange: SubscriberRange,
        language: String,
        country: String,
        bio: String,
        interests: List<String>,
        photoUri: String? = null
    ) {
        if (name.isBlank()) {
            _profileSaveState.value = ProfileSaveState.Error(
                if (_appLanguage.value == AppLanguage.HINDI) "कृपया अपना नाम दर्ज करें।" else "Display name cannot be empty."
            )
            return
        }
        val cleanHandle = if (channelHandle.startsWith("@")) channelHandle else "@$channelHandle"
        if (cleanHandle.length < 3) {
            _profileSaveState.value = ProfileSaveState.Error(
                if (_appLanguage.value == AppLanguage.HINDI) "यूज़रनेम कम से कम 3 अक्षरों का होना चाहिए।" else "Username must be at least 3 characters."
            )
            return
        }
        if (channelUrl.isNotBlank() && !channelUrl.startsWith("http://") && !channelUrl.startsWith("https://")) {
            _profileSaveState.value = ProfileSaveState.Error(
                if (_appLanguage.value == AppLanguage.HINDI) "वैध यूट्यूब URL दर्ज करें (जैसे https://...)" else "Please enter a valid URL starting with https://"
            )
            return
        }

        viewModelScope.launch {
            _profileSaveState.value = ProfileSaveState.Loading
            try {
                val updated = userProfile.value.copy(
                    name = name.trim(),
                    channelHandle = cleanHandle.trim(),
                    channelName = channelName.trim().ifBlank { name.trim() },
                    channelUrl = channelUrl.trim().ifBlank { "https://www.youtube.com" },
                    category = category,
                    subscriberRange = subRange,
                    language = language.trim().ifBlank { "Hindi / English" },
                    country = country.trim().ifBlank { "India" },
                    bio = bio.trim(),
                    collaborationInterests = interests,
                    photoUri = photoUri ?: userProfile.value.photoUri
                )
                repository.updateUserProfile(updated)
                val successMsg = if (_appLanguage.value == AppLanguage.HINDI) {
                    "आपकी क्रिएटर प्रोफ़ाइल सफलतापूर्वक अपडेट हो गई!"
                } else {
                    "Creator profile updated successfully!"
                }
                _profileSaveState.value = ProfileSaveState.Success(successMsg)
                _snackbarMessage.value = successMsg
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: if (_appLanguage.value == AppLanguage.HINDI) {
                    "प्रोफ़ाइल अपडेट करने में त्रुटि हुई।"
                } else {
                    "Failed to update profile. Please try again."
                }
                _profileSaveState.value = ProfileSaveState.Error(errorMsg)
                _snackbarMessage.value = errorMsg
            }
        }
    }

    fun resetToDemoData() {
        viewModelScope.launch {
            repository.resetToDemoData()
            resetFilters()
            _snackbarMessage.value = if (_appLanguage.value == AppLanguage.HINDI) {
                "डेमो डेटा सफलतापूर्वक रीसेट किया गया"
            } else {
                "Demo data reset successfully"
            }
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
