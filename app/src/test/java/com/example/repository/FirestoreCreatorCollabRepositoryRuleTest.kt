package com.example.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class FirestoreCreatorCollabRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun unauthenticatedUser_throwsExceptionOnWrite() = runTest {
        auth.signOut()
        val repository = FirestoreCreatorCollabRepository(firestore)

        val profile = CreatorProfile(
            id = "test_user",
            name = "Test Creator",
            channelName = "Test Studio",
            channelHandle = "@teststudio",
            channelUrl = "https://youtube.com/@test",
            category = CreatorCategory.TECH,
            subscriberRange = SubscriberRange.TIER_100K_500K,
            language = "Hindi",
            collaborationInterests = listOf("Tech"),
            bio = "Bio",
            city = "Delhi"
        )

        try {
            repository.updateUserProfile(profile)
            fail("Expected IllegalStateException for unauthenticated user")
        } catch (e: IllegalStateException) {
            assertTrue(e.message?.contains("User must be signed in") == true)
        }
    }

    @Test
    fun authenticatedUser_canCreateAndReadProfile() = runTest {
        val uid = signInTestUser("creator_test@example.com")
        val repository = FirestoreCreatorCollabRepository(firestore)

        val profile = CreatorProfile(
            id = uid,
            name = "Ravi Verma",
            channelName = "Ravi Tech",
            channelHandle = "@ravitech",
            channelUrl = "https://youtube.com/@ravitech",
            category = CreatorCategory.TECH,
            subscriberRange = SubscriberRange.TIER_500K_1M,
            language = "Hindi / Hinglish",
            collaborationInterests = listOf("Smartphone Reviews"),
            bio = "Tech breakdowns and camera comparisons",
            city = "Bengaluru, Karnataka"
        )

        repository.updateUserProfile(profile)

        val observed = repository.getUserProfile().first()
        assertEquals("Ravi Verma", observed.name)
        assertEquals("Ravi Tech", observed.channelName)
    }

    @Test
    fun authenticatedUser_canSendCollabProposal() = runTest {
        val uid = signInTestUser("collab_sender@example.com")
        val repository = FirestoreCreatorCollabRepository(firestore)

        val proposal = CollabRequest(
            id = "req_test_1",
            targetCreatorId = "creator_1",
            targetCreatorName = "Vikram Verma",
            targetChannelName = "CineVibes Hindi",
            senderName = "Collab Sender",
            senderChannelName = "Sender Channel",
            senderChannelUrl = "https://youtube.com/@sender",
            collabType = CollabType.CROSS_PROMO,
            projectTitle = "Top 10 Bollywood Easter Eggs",
            timeline = "2 Weeks",
            message = "Let's collaborate on hidden movie details!",
            status = CollabStatus.PENDING
        )

        repository.sendCollabRequest(proposal)

        val requests = repository.getCollabRequests().first()
        assertTrue(requests.any { it.projectTitle == "Top 10 Bollywood Easter Eggs" })
    }

    @Test
    fun authenticatedUser_canCreateProjectAndUpdateNotes() = runTest {
        val uid = signInTestUser("project_owner@example.com")
        val repository = FirestoreCreatorCollabRepository(firestore)

        val project = CollabProject(
            id = "proj_test_1",
            title = "Acoustic Folk Fusion Collab",
            partnerName = "Neha Rathore",
            partnerChannelName = "DhunAcoustic",
            partnerChannelUrl = "https://youtube.com/@dhun",
            category = CreatorCategory.MUSIC,
            collabType = CollabType.SHORTS_REEL,
            status = CollabStatus.IN_PROGRESS,
            progressPercent = 0.5f,
            milestones = emptyList(),
            notes = "Initial track outline agreed",
            targetReleaseDate = "Nov 15",
            lastUpdated = "Today"
        )

        repository.createProject(project)

        val projects = repository.getProjects().first()
        val created = projects.find { it.title == "Acoustic Folk Fusion Collab" }
        assertNotNull(created)
        assertEquals("Neha Rathore", created?.partnerName)
    }
}
