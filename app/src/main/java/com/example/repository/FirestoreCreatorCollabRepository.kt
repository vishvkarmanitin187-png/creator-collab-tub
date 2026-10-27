package com.example.repository

import android.content.Context
import com.example.R
import com.example.model.*
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class FirestoreCreatorCollabRepository(private val db: FirebaseFirestore) : CreatorCollabRepository {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = com.google.firebase.auth.FirebaseAuth.getInstance(db.app)

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    override fun getCreators(): Flow<List<CreatorProfile>> =
        db.collection("users")
            .snapshots()
            .map { snapshot ->
                val cloudCreators = snapshot.toObjects(FirestoreUserProfile::class.java).map { doc ->
                    CreatorProfile(
                        id = doc.userId.ifBlank { doc.id },
                        name = doc.name,
                        channelName = doc.channelName,
                        channelHandle = doc.channelHandle.ifBlank { "@" + doc.channelName.replace(" ", "") },
                        channelUrl = doc.channelUrl.ifBlank { "https://www.youtube.com" },
                        category = try {
                            CreatorCategory.valueOf(doc.category)
                        } catch (e: Exception) {
                            CreatorCategory.TECH
                        },
                        subscriberRange = try {
                            SubscriberRange.valueOf(doc.subscriberRange)
                        } catch (e: Exception) {
                            SubscriberRange.TIER_100K_500K
                        },
                        language = doc.language,
                        collaborationInterests = doc.collaborationInterests.ifEmpty {
                            listOf("Cross-Promotion", "Guest Podcast", "Reels Series")
                        },
                        bio = doc.bio,
                        city = doc.city,
                        country = doc.country.ifBlank { "India" },
                        photoUri = doc.photoUri.ifBlank { null },
                        totalVideosSample = doc.totalVideosSample.toInt(),
                        isSample = false,
                        sampleNotice = "COMMUNITY CREATOR",
                        avatarColorHex = 0xFF8B5CF6
                    )
                }
                val currentUid = auth.currentUser?.uid
                val filteredCloud = cloudCreators.filter { it.id != currentUid }
                filteredCloud + SampleData.sampleCreators
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, "users")
                emit(SampleData.sampleCreators)
            }

    override fun getCreatorById(id: String): Flow<CreatorProfile?> =
        getCreators().map { list -> list.find { it.id == id } }

    override fun getCollabRequests(): Flow<List<CollabRequest>> = flow {
        val uid = requireUserId()
        val sentFlow = db.collection("collab_requests")
            .whereEqualTo("senderUserId", uid)
            .snapshots()
            .map { snap -> snap.toObjects(FirestoreCollabRequest::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, "collab_requests")
                emit(emptyList())
            }

        val receivedFlow = db.collection("collab_requests")
            .whereEqualTo("targetCreatorId", uid)
            .snapshots()
            .map { snap -> snap.toObjects(FirestoreCollabRequest::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, "collab_requests")
                emit(emptyList())
            }

        emitAll(
            combine(sentFlow, receivedFlow) { sent, received ->
                val combined = (sent + received).distinctBy { it.id }.map { doc ->
                    CollabRequest(
                        id = doc.id,
                        targetCreatorId = doc.targetCreatorId,
                        targetCreatorName = doc.targetCreatorName,
                        targetChannelName = doc.targetChannelName,
                        senderName = doc.senderName,
                        senderChannelName = doc.senderChannelName,
                        senderChannelUrl = doc.senderChannelUrl,
                        collabType = try {
                            CollabType.valueOf(doc.collabType)
                        } catch (e: Exception) {
                            CollabType.CROSS_PROMO
                        },
                        projectTitle = doc.projectTitle,
                        timeline = doc.timeline,
                        message = doc.message,
                        status = try {
                            CollabStatus.valueOf(doc.status)
                        } catch (e: Exception) {
                            CollabStatus.PENDING
                        },
                        createdAtFormatted = "Synced",
                        isIncoming = (doc.targetCreatorId == uid)
                    )
                }
                if (combined.isNotEmpty()) combined else SampleData.initialCollabRequests
            }
        )
    }

    override suspend fun sendCollabRequest(request: CollabRequest) {
        val uid = requireUserId()
        val docRef = db.collection("collab_requests").document()
        val payload = mapOf(
            "senderUserId" to uid,
            "senderName" to request.senderName,
            "senderChannelName" to request.senderChannelName,
            "senderChannelUrl" to request.senderChannelUrl,
            "targetCreatorId" to request.targetCreatorId,
            "targetCreatorName" to request.targetCreatorName,
            "targetChannelName" to request.targetChannelName,
            "collabType" to request.collabType.name,
            "projectTitle" to request.projectTitle,
            "timeline" to request.timeline,
            "message" to request.message,
            "status" to request.status.name,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    override suspend fun updateCollabRequestStatus(requestId: String, newStatus: CollabStatus) {
        val uid = requireUserId()
        val docRef = db.collection("collab_requests").document(requestId)
        try {
            docRef.update(
                mapOf(
                    "status" to newStatus.name,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            if (newStatus == CollabStatus.ACCEPTED) {
                val snapshot = docRef.get().await()
                val req = snapshot.toObject(FirestoreCollabRequest::class.java)
                if (req != null) {
                    val partnerName = if (req.senderUserId == uid) req.targetCreatorName else req.senderName
                    val partnerChannel = if (req.senderUserId == uid) req.targetChannelName else req.senderChannelName
                    val partnerUrl = if (req.senderUserId == uid) "https://www.youtube.com" else req.senderChannelUrl
                    val projectDoc = db.collection("projects").document()
                    val projPayload = mapOf(
                        "creatorUserId" to uid,
                        "title" to req.projectTitle,
                        "partnerName" to partnerName,
                        "partnerChannelName" to partnerChannel,
                        "partnerChannelUrl" to partnerUrl,
                        "category" to CreatorCategory.TECH.name,
                        "collabType" to req.collabType,
                        "status" to CollabStatus.ACCEPTED.name,
                        "progressPercent" to 0.2,
                        "notes" to "Collab Kickoff: ${req.message}",
                        "targetReleaseDate" to req.timeline,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    projectDoc.set(projPayload).await()
                }
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    override fun getProjects(): Flow<List<CollabProject>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("projects")
                .whereEqualTo("creatorUserId", uid)
                .snapshots()
                .map { snapshot ->
                    val cloudProjects = snapshot.toObjects(FirestoreProject::class.java).map { doc ->
                        val percent = doc.progressPercent.toFloat()
                        CollabProject(
                            id = doc.id,
                            title = doc.title,
                            partnerName = doc.partnerName,
                            partnerChannelName = doc.partnerChannelName,
                            partnerChannelUrl = doc.partnerChannelUrl,
                            category = try {
                                CreatorCategory.valueOf(doc.category)
                            } catch (e: Exception) {
                                CreatorCategory.TECH
                            },
                            collabType = try {
                                CollabType.valueOf(doc.collabType)
                            } catch (e: Exception) {
                                CollabType.CROSS_PROMO
                            },
                            status = try {
                                CollabStatus.valueOf(doc.status)
                            } catch (e: Exception) {
                                CollabStatus.IN_PROGRESS
                            },
                            progressPercent = percent,
                            milestones = listOf(
                                ProjectMilestone("m1", "Concept & Outline alignment", true, "Week 1"),
                                ProjectMilestone("m2", "Recording & production", percent >= 0.5f, "Week 2"),
                                ProjectMilestone("m3", "Editing & final review", percent >= 0.75f, "Week 3"),
                                ProjectMilestone("m4", "Cross-upload & promotion teaser", percent >= 1.0f, doc.targetReleaseDate.ifBlank { "Upcoming" })
                            ),
                            notes = doc.notes,
                            targetReleaseDate = doc.targetReleaseDate,
                            lastUpdated = "Recently"
                        )
                    }
                    if (cloudProjects.isNotEmpty()) cloudProjects else SampleData.initialProjects
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, "projects")
                    emit(SampleData.initialProjects)
                }
        )
    }

    override suspend fun toggleMilestone(projectId: String, milestoneId: String) {
        val uid = requireUserId()
        val docRef = db.collection("projects").document(projectId)
        try {
            val snapshot = docRef.get().await()
            val currentPercent = snapshot.getDouble("progressPercent") ?: 0.2
            val newPercent = if (currentPercent >= 1.0) 0.5 else (currentPercent + 0.25).coerceAtMost(1.0)
            val newStatus = if (newPercent >= 1.0) CollabStatus.COMPLETED.name else CollabStatus.IN_PROGRESS.name
            docRef.update(
                mapOf(
                    "progressPercent" to newPercent,
                    "status" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    override suspend fun updateProjectNotes(projectId: String, notes: String) {
        val docRef = db.collection("projects").document(projectId)
        try {
            docRef.update(
                mapOf(
                    "notes" to notes,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    override suspend fun createProject(project: CollabProject) {
        val uid = requireUserId()
        val docRef = db.collection("projects").document()
        val payload = mapOf(
            "creatorUserId" to uid,
            "title" to project.title,
            "partnerName" to project.partnerName,
            "partnerChannelName" to project.partnerChannelName,
            "partnerChannelUrl" to project.partnerChannelUrl,
            "category" to project.category.name,
            "collabType" to project.collabType.name,
            "status" to project.status.name,
            "progressPercent" to project.progressPercent.toDouble(),
            "notes" to project.notes,
            "targetReleaseDate" to project.targetReleaseDate,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    override fun getUserProfile(): Flow<CreatorProfile> = flow {
        val uid = requireUserId()
        val defaultName = auth.currentUser?.displayName ?: "Creator"
        val docRef = db.collection("users").document(uid)

        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    val userDoc = snapshot.toObject(FirestoreUserProfile::class.java)
                    if (userDoc != null && userDoc.name.isNotBlank()) {
                        CreatorProfile(
                            id = uid,
                            name = userDoc.name,
                            channelName = userDoc.channelName,
                            channelHandle = userDoc.channelHandle.ifBlank { "@" + userDoc.channelName.replace(" ", "") },
                            channelUrl = userDoc.channelUrl.ifBlank { "https://www.youtube.com" },
                            category = try {
                                CreatorCategory.valueOf(userDoc.category)
                            } catch (e: Exception) {
                                CreatorCategory.TECH
                            },
                            subscriberRange = try {
                                SubscriberRange.valueOf(userDoc.subscriberRange)
                            } catch (e: Exception) {
                                SubscriberRange.TIER_100K_500K
                            },
                            language = userDoc.language,
                            collaborationInterests = userDoc.collaborationInterests.ifEmpty {
                                listOf("Cross-Promotion", "Guest Podcasts", "Tech Review Exchanges")
                            },
                            bio = userDoc.bio.ifBlank { "Passionate YouTube creator producing videos, tutorials and collaborations." },
                            city = userDoc.city.ifBlank { "India" },
                            country = userDoc.country.ifBlank { "India" },
                            photoUri = userDoc.photoUri.ifBlank { null },
                            isSample = false,
                            sampleNotice = "MY VERIFIED PROFILE",
                            avatarColorHex = 0xFF8B5CF6
                        )
                    } else {
                        // Fallback initial profile populated from auth
                        CreatorProfile(
                            id = uid,
                            name = defaultName,
                            channelName = "$defaultName Studio",
                            channelHandle = "@" + defaultName.replace(" ", "").lowercase(),
                            channelUrl = "https://www.youtube.com",
                            category = CreatorCategory.TECH,
                            subscriberRange = SubscriberRange.TIER_100K_500K,
                            language = "Hindi / Hinglish",
                            collaborationInterests = listOf("Cross-Promotion", "Guest Podcasts", "Tech Review Exchanges"),
                            bio = "YouTube creator building impactful content and exploring collaborations.",
                            city = "New Delhi, India",
                            country = "India",
                            photoUri = auth.currentUser?.photoUrl?.toString(),
                            isSample = false,
                            sampleNotice = "MY VERIFIED PROFILE",
                            avatarColorHex = 0xFF8B5CF6
                        )
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, docRef.path)
                    emit(
                        CreatorProfile(
                            id = uid,
                            name = defaultName,
                            channelName = "$defaultName Studio",
                            channelHandle = "@$defaultName",
                            channelUrl = "https://www.youtube.com",
                            category = CreatorCategory.TECH,
                            subscriberRange = SubscriberRange.TIER_100K_500K,
                            language = "Hindi",
                            collaborationInterests = listOf("Cross-Promotion"),
                            bio = "YouTube creator",
                            city = "India",
                            country = "India",
                            photoUri = null,
                            isSample = false
                        )
                    )
                }
        )
    }

    override suspend fun updateUserProfile(profile: CreatorProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val payload = mapOf(
            "userId" to uid,
            "name" to profile.name,
            "channelName" to profile.channelName,
            "channelHandle" to profile.channelHandle,
            "channelUrl" to profile.channelUrl,
            "category" to profile.category.name,
            "subscriberRange" to profile.subscriberRange.name,
            "language" to profile.language,
            "collaborationInterests" to profile.collaborationInterests,
            "bio" to profile.bio,
            "city" to profile.city,
            "country" to profile.country,
            "photoUri" to (profile.photoUri ?: ""),
            "totalVideosSample" to profile.totalVideosSample.toLong(),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload, SetOptions.merge()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    override suspend fun resetToDemoData() {
        // Keeps cloud data clean while reloading sample set
    }
}
