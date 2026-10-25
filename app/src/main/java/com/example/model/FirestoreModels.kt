package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreUserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val channelName: String = "",
    val channelHandle: String = "",
    val channelUrl: String = "",
    val category: String = CreatorCategory.TECH.name,
    val subscriberRange: String = SubscriberRange.TIER_100K_500K.name,
    val language: String = "Hindi / Hinglish",
    val collaborationInterests: List<String> = emptyList(),
    val bio: String = "",
    val city: String = "New Delhi, India",
    val country: String = "India",
    val photoUri: String = "",
    val totalVideosSample: Long = 50L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreCollabRequest(
    @DocumentId val id: String = "",
    val senderUserId: String = "",
    val senderName: String = "",
    val senderChannelName: String = "",
    val senderChannelUrl: String = "",
    val targetCreatorId: String = "",
    val targetCreatorName: String = "",
    val targetChannelName: String = "",
    val collabType: String = CollabType.CROSS_PROMO.name,
    val projectTitle: String = "",
    val timeline: String = "",
    val message: String = "",
    val status: String = CollabStatus.PENDING.name,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreProject(
    @DocumentId val id: String = "",
    val creatorUserId: String = "",
    val title: String = "",
    val partnerName: String = "",
    val partnerChannelName: String = "",
    val partnerChannelUrl: String = "",
    val category: String = CreatorCategory.TECH.name,
    val collabType: String = CollabType.CROSS_PROMO.name,
    val status: String = CollabStatus.IN_PROGRESS.name,
    val progressPercent: Double = 0.0,
    val notes: String = "",
    val targetReleaseDate: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
