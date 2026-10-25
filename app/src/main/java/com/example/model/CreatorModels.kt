package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिंदी")
}

enum class CreatorCategory(
    val titleEn: String,
    val titleHi: String,
    val color: Color
) {
    MOVIE_FACTS("Movie Facts", "मूवी फैक्ट्स", CatMovie),
    ENTERTAINMENT("Entertainment", "मनोरंजन", CatEntertainment),
    GAMING("Gaming", "गेमिंग", CatGaming),
    COMEDY("Comedy", "कॉमेडी", CatComedy),
    TECH("Tech", "टेक्नोलॉजी", CatTech),
    MUSIC("Music", "म्यूजिक", CatMusic),
    EDUCATION("Education", "शिक्षा", CatEducation),
    MOVIE_REVIEWS("Movie Reviews", "मूवी रिव्यू", CatMovie);

    fun localizedTitle(lang: AppLanguage): String {
        return if (lang == AppLanguage.HINDI) titleHi else titleEn
    }
}

enum class SubscriberRange(
    val labelEn: String,
    val labelHi: String
) {
    UNDER_50K("< 50K", "< ५०K"),
    TIER_50K_100K("50K - 100K", "५०K - १००K"),
    TIER_100K_500K("100K - 500K", "१००K - ५००K"),
    TIER_500K_1M("500K - 1M", "५००K - १M"),
    TIER_1M_PLUS("1M+", "१M+");

    fun localizedLabel(lang: AppLanguage): String {
        return if (lang == AppLanguage.HINDI) labelHi else labelEn
    }
}

enum class CollabType(
    val titleEn: String,
    val titleHi: String
) {
    CROSS_PROMO("Cross-Promo Video", "क्रॉस-प्रमोशन वीडियो"),
    PODCAST_GUEST("Podcast / Interview", "पॉडकास्ट / साक्षात्कार"),
    SHORTS_REEL("Shorts / Reel Series", "शॉर्ट्स / रील सीरीज"),
    LIVE_STREAM("Live Stream Collab", "लाइव स्ट्रीम सहयोग"),
    REVIEW_EXCHANGE("Product Review Exchange", "प्रोडक्ट रिव्यू एक्सचेंज"),
    SCRIPT_IDEATION("Script & Brainstorming", "स्क्रिप्ट व आइडिया मंथन");

    fun localizedTitle(lang: AppLanguage): String {
        return if (lang == AppLanguage.HINDI) titleHi else titleEn
    }
}

enum class CollabStatus(
    val labelEn: String,
    val labelHi: String,
    val color: Color
) {
    PENDING("Pending", "लंबित", AmberAccent),
    ACCEPTED("Accepted", "स्वीकृत", ElectricCyan),
    IN_PROGRESS("In Progress", "प्रगति पर", PurplePrimary),
    COMPLETED("Completed", "पूर्ण", EmeraldSuccess),
    DECLINED("Declined", "अस्वीकृत", RoseError);

    fun localizedLabel(lang: AppLanguage): String {
        return if (lang == AppLanguage.HINDI) labelHi else labelEn
    }
}

data class CreatorProfile(
    val id: String,
    val name: String,
    val channelName: String,
    val channelHandle: String,
    val channelUrl: String,
    val category: CreatorCategory,
    val subscriberRange: SubscriberRange,
    val language: String,
    val collaborationInterests: List<String>,
    val bio: String,
    val city: String,
    val country: String = "India",
    val photoUri: String? = null,
    val totalVideosSample: Int = 120,
    val isSample: Boolean = true,
    val sampleNotice: String = "SAMPLE PROFILE / उदाहरण प्रोफ़ाइल",
    val avatarColorHex: Long = 0xFF7C3AED
)

data class CollabRequest(
    val id: String,
    val targetCreatorId: String,
    val targetCreatorName: String,
    val targetChannelName: String,
    val senderName: String,
    val senderChannelName: String,
    val senderChannelUrl: String,
    val collabType: CollabType,
    val projectTitle: String,
    val timeline: String,
    val message: String,
    val status: CollabStatus = CollabStatus.PENDING,
    val createdAtFormatted: String = "Just now",
    val isIncoming: Boolean = false
)

data class ProjectMilestone(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val dueDate: String
)

data class CollabProject(
    val id: String,
    val title: String,
    val partnerName: String,
    val partnerChannelName: String,
    val partnerChannelUrl: String,
    val category: CreatorCategory,
    val collabType: CollabType,
    val status: CollabStatus,
    val progressPercent: Float,
    val milestones: List<ProjectMilestone>,
    val notes: String,
    val targetReleaseDate: String,
    val lastUpdated: String
)
