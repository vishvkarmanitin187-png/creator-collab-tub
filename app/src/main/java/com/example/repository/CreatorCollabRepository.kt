package com.example.repository

import com.example.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Repository interface designed for pluggable backends.
 * Ready for drop-in Firebase Firestore collections:
 * - /creators/{creatorId}
 * - /collab_requests/{requestId}
 * - /projects/{projectId}
 * - /users/{userId}
 */
interface CreatorCollabRepository {
    fun getCreators(): Flow<List<CreatorProfile>>
    fun getCreatorById(id: String): Flow<CreatorProfile?>
    fun getCollabRequests(): Flow<List<CollabRequest>>
    suspend fun sendCollabRequest(request: CollabRequest)
    suspend fun updateCollabRequestStatus(requestId: String, newStatus: CollabStatus)
    fun getProjects(): Flow<List<CollabProject>>
    suspend fun toggleMilestone(projectId: String, milestoneId: String)
    suspend fun updateProjectNotes(projectId: String, notes: String)
    suspend fun createProject(project: CollabProject)
    fun getUserProfile(): Flow<CreatorProfile>
    suspend fun updateUserProfile(profile: CreatorProfile)
    suspend fun resetToDemoData()
    fun getConversations(): Flow<List<ChatConversation>>
    fun getMessages(chatPartnerId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(targetCreatorId: String, targetCreatorName: String, text: String)
}

class LocalCreatorCollabRepository : CreatorCollabRepository {

    private val _creators = MutableStateFlow<List<CreatorProfile>>(SampleData.sampleCreators)
    private val _collabRequests = MutableStateFlow<List<CollabRequest>>(SampleData.initialCollabRequests)
    private val _projects = MutableStateFlow<List<CollabProject>>(SampleData.initialProjects)
    private val _conversations = MutableStateFlow<List<ChatConversation>>(SampleData.sampleConversations)
    private val _messages = MutableStateFlow<List<ChatMessage>>(SampleData.sampleMessages)

    private val _userProfile = MutableStateFlow(
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
            totalVideosSample = 88,
            isSample = false,
            sampleNotice = "MY CREATOR PROFILE",
            avatarColorHex = 0xFF8B5CF6
        )
    )

    override fun getCreators(): Flow<List<CreatorProfile>> = _creators.asStateFlow()

    override fun getCreatorById(id: String): Flow<CreatorProfile?> =
        _creators.map { list -> list.find { it.id == id } }

    override fun getCollabRequests(): Flow<List<CollabRequest>> = _collabRequests.asStateFlow()

    override suspend fun sendCollabRequest(request: CollabRequest) {
        val current = _collabRequests.value.toMutableList()
        current.add(0, request)
        _collabRequests.value = current
    }

    override suspend fun updateCollabRequestStatus(requestId: String, newStatus: CollabStatus) {
        val current = _collabRequests.value.toMutableList()
        val index = current.indexOfFirst { it.id == requestId }
        if (index != -1) {
            val req = current[index]
            current[index] = req.copy(status = newStatus)
            _collabRequests.value = current

            // If accepted, auto-create a collaborative tracking project
            if (newStatus == CollabStatus.ACCEPTED) {
                val existing = _projects.value.find { it.title.contains(req.projectTitle, ignoreCase = true) }
                if (existing == null) {
                    val newProj = CollabProject(
                        id = "proj_" + UUID.randomUUID().toString().take(8),
                        title = req.projectTitle,
                        partnerName = if (req.isIncoming) req.senderName else req.targetCreatorName,
                        partnerChannelName = if (req.isIncoming) req.senderChannelName else req.targetChannelName,
                        partnerChannelUrl = if (req.isIncoming) req.senderChannelUrl else "https://www.youtube.com",
                        category = CreatorCategory.ENTERTAINMENT,
                        collabType = req.collabType,
                        status = CollabStatus.ACCEPTED,
                        progressPercent = 0.2f,
                        milestones = listOf(
                            ProjectMilestone("m_init_1", "Collab kickoff & concept alignment", true, "Today"),
                            ProjectMilestone("m_init_2", "Recording & footage production", false, req.timeline),
                            ProjectMilestone("m_init_3", "Final cross-promotion upload", false, "Upcoming")
                        ),
                        notes = "Proposal note: ${req.message}",
                        targetReleaseDate = req.timeline,
                        lastUpdated = "Just now"
                    )
                    createProject(newProj)
                }
            }
        }
    }

    override fun getProjects(): Flow<List<CollabProject>> = _projects.asStateFlow()

    override suspend fun toggleMilestone(projectId: String, milestoneId: String) {
        val current = _projects.value.toMutableList()
        val pIndex = current.indexOfFirst { it.id == projectId }
        if (pIndex != -1) {
            val proj = current[pIndex]
            val updatedMilestones = proj.milestones.map { m ->
                if (m.id == milestoneId) m.copy(isCompleted = !m.isCompleted) else m
            }
            val completedCount = updatedMilestones.count { it.isCompleted }
            val progress = if (updatedMilestones.isNotEmpty()) completedCount.toFloat() / updatedMilestones.size else 0f
            val updatedStatus = if (progress >= 1f) CollabStatus.COMPLETED else CollabStatus.IN_PROGRESS
            current[pIndex] = proj.copy(
                milestones = updatedMilestones,
                progressPercent = progress,
                status = updatedStatus,
                lastUpdated = "Just now"
            )
            _projects.value = current
        }
    }

    override suspend fun updateProjectNotes(projectId: String, notes: String) {
        val current = _projects.value.toMutableList()
        val pIndex = current.indexOfFirst { it.id == projectId }
        if (pIndex != -1) {
            val proj = current[pIndex]
            current[pIndex] = proj.copy(notes = notes, lastUpdated = "Just now")
            _projects.value = current
        }
    }

    override suspend fun createProject(project: CollabProject) {
        val current = _projects.value.toMutableList()
        current.add(0, project)
        _projects.value = current
    }

    override fun getUserProfile(): Flow<CreatorProfile> = _userProfile.asStateFlow()

    override suspend fun updateUserProfile(profile: CreatorProfile) {
        _userProfile.value = profile
    }

    override suspend fun resetToDemoData() {
        _creators.value = SampleData.sampleCreators
        _collabRequests.value = SampleData.initialCollabRequests
        _projects.value = SampleData.initialProjects
        _conversations.value = SampleData.sampleConversations
        _messages.value = SampleData.sampleMessages
    }

    override fun getConversations(): Flow<List<ChatConversation>> = _conversations.asStateFlow()

    override fun getMessages(chatPartnerId: String): Flow<List<ChatMessage>> =
        _messages.map { list ->
            list.filter {
                (it.senderId == chatPartnerId && it.receiverId == "user_me") ||
                (it.senderId == "user_me" && it.receiverId == chatPartnerId)
            }.sortedBy { it.timestampMillis }
        }

    override suspend fun sendMessage(targetCreatorId: String, targetCreatorName: String, text: String) {
        val newMsg = ChatMessage(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            conversationId = "conv_$targetCreatorId",
            senderId = "user_me",
            receiverId = targetCreatorId,
            senderName = _userProfile.value.name,
            receiverName = targetCreatorName,
            text = text,
            timestampMillis = System.currentTimeMillis(),
            isFromMe = true
        )
        val currentMsgs = _messages.value.toMutableList()
        currentMsgs.add(newMsg)
        _messages.value = currentMsgs

        val currentConvs = _conversations.value.toMutableList()
        val convIndex = currentConvs.indexOfFirst { it.partnerId == targetCreatorId }
        val partnerCreator = _creators.value.find { it.id == targetCreatorId }
        val updatedConv = ChatConversation(
            partnerId = targetCreatorId,
            partnerName = targetCreatorName,
            partnerChannelName = partnerCreator?.channelName ?: targetCreatorName,
            partnerCategory = partnerCreator?.category ?: CreatorCategory.TECH,
            partnerAvatarColorHex = partnerCreator?.avatarColorHex ?: 0xFF8B5CF6,
            lastMessage = text,
            lastMessageTime = "Just now",
            unreadCount = 0
        )
        if (convIndex != -1) {
            currentConvs[convIndex] = updatedConv
        } else {
            currentConvs.add(0, updatedConv)
        }
        _conversations.value = currentConvs
    }
}

