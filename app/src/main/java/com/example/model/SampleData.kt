package com.example.model

object SampleData {

    val sampleCreators = listOf(
        CreatorProfile(
            id = "creator_1",
            name = "Vikram Verma",
            channelName = "CineVibes Hindi",
            channelHandle = "@CineVibesHindi",
            channelUrl = "https://www.youtube.com/@CineVibesHindi",
            category = CreatorCategory.MOVIE_FACTS,
            subscriberRange = SubscriberRange.TIER_500K_1M,
            language = "Hindi",
            collaborationInterests = listOf("Movie Analysis Podcast", "Cameo Breakdown", "Shorts Trivia Collab"),
            bio = "Deep-dive video essayist exploring Bollywood & world cinema Easter eggs, hidden cinematic framing, and classic movie lore.",
            city = "Mumbai, Maharashtra",
            totalVideosSample = 210,
            isSample = true,
            avatarColorHex = 0xFFE11D48
        ),
        CreatorProfile(
            id = "creator_2",
            name = "Aditya Nair",
            channelName = "SiliconDosa Tech",
            channelHandle = "@SiliconDosaTech",
            channelUrl = "https://www.youtube.com/@SiliconDosaTech",
            category = CreatorCategory.TECH,
            subscriberRange = SubscriberRange.TIER_100K_500K,
            language = "Hinglish / English",
            collaborationInterests = listOf("Smartphone Blind Camera Test", "AI Tools Podcast", "Setup Tour Collab"),
            bio = "Engineering student turned tech reviewer dissecting gadgets, consumer electronics, and future tech trends from Bengaluru.",
            city = "Bengaluru, Karnataka",
            totalVideosSample = 145,
            isSample = true,
            avatarColorHex = 0xFF06B6D4
        ),
        CreatorProfile(
            id = "creator_3",
            name = "Simran Kaur",
            channelName = "SimmiSays Comedy",
            channelHandle = "@SimmiSays",
            channelUrl = "https://www.youtube.com/@SimmiSays",
            category = CreatorCategory.COMEDY,
            subscriberRange = SubscriberRange.TIER_100K_500K,
            language = "Hindi / Punjabi",
            collaborationInterests = listOf("Relatable Sketch Parody", "Duo Roast Reels", "Podcast Guest"),
            bio = "Satirical sketches on north Indian family dynamics, exam struggles, and viral trend parodies.",
            city = "Chandigarh",
            totalVideosSample = 98,
            isSample = true,
            avatarColorHex = 0xFFF59E0B
        ),
        CreatorProfile(
            id = "creator_4",
            name = "Rohan Malhotra",
            channelName = "BharatGamerLive",
            channelHandle = "@BharatGamerLive",
            channelUrl = "https://www.youtube.com/@BharatGamerLive",
            category = CreatorCategory.GAMING,
            subscriberRange = SubscriberRange.TIER_500K_1M,
            language = "Hindi / Hinglish",
            collaborationInterests = listOf("Co-op Livestream", "Esports Challenge", "Indie Game Showcase"),
            bio = "Competitive streamer hosting custom lobby tournaments, horror game co-ops, and gaming gear banter.",
            city = "Noida, Uttar Pradesh",
            totalVideosSample = 340,
            isSample = true,
            avatarColorHex = 0xFF8B5CF6
        ),
        CreatorProfile(
            id = "creator_5",
            name = "Dr. Swati Mehra",
            channelName = "VigyanSphere",
            channelHandle = "@VigyanSphere",
            channelUrl = "https://www.youtube.com/@VigyanSphere",
            category = CreatorCategory.EDUCATION,
            subscriberRange = SubscriberRange.TIER_100K_500K,
            language = "Hindi / English",
            collaborationInterests = listOf("Science Mythbusters", "Space Exploration Collab", "Student Q&A"),
            bio = "Astrophysics researcher explaining quantum mysteries, ISRO missions, and daily science queries in plain language.",
            city = "New Delhi",
            totalVideosSample = 112,
            isSample = true,
            avatarColorHex = 0xFF10B981
        ),
        CreatorProfile(
            id = "creator_6",
            name = "Neha Rathore",
            channelName = "DhunAcoustic",
            channelHandle = "@DhunAcoustic",
            channelUrl = "https://www.youtube.com/@DhunAcoustic",
            category = CreatorCategory.MUSIC,
            subscriberRange = SubscriberRange.TIER_50K_100K,
            language = "Hindi",
            collaborationInterests = listOf("Duet Vocal Cover", "Acoustic Jam Session", "Beat Production Collab"),
            bio = "Singer-songwriter fusing Indian folk classical melodies with fingerstyle acoustic guitar arrangements.",
            city = "Jaipur, Rajasthan",
            totalVideosSample = 76,
            isSample = true,
            avatarColorHex = 0xFFEC4899
        ),
        CreatorProfile(
            id = "creator_7",
            name = "Aarav Sharma",
            channelName = "DesiPop Culture",
            channelHandle = "@DesiPopCulture",
            channelUrl = "https://www.youtube.com/@DesiPopCulture",
            category = CreatorCategory.ENTERTAINMENT,
            subscriberRange = SubscriberRange.TIER_1M_PLUS,
            language = "Hindi / English",
            collaborationInterests = listOf("Celebrity Trivia Show", "Roundtable Discussion", "Reaction Video"),
            bio = "Covering trending movies, OTT shows, viral internet drama, and interviewing Indian creators and filmmakers.",
            city = "Mumbai, Maharashtra",
            totalVideosSample = 420,
            isSample = true,
            avatarColorHex = 0xFFF97316
        ),
        CreatorProfile(
            id = "creator_8",
            name = "Karan Patel",
            channelName = "GizmoGyaan Hindi",
            channelHandle = "@GizmoGyaanHindi",
            channelUrl = "https://www.youtube.com/@GizmoGyaanHindi",
            category = CreatorCategory.TECH,
            subscriberRange = SubscriberRange.TIER_1M_PLUS,
            language = "Hindi",
            collaborationInterests = listOf("Flagship vs Budget Shootout", "Creator Studio Setup", "Podcast Host"),
            bio = "Practical buying guides, smartphone unboxings, long-term gadget reviews, and camera shootouts.",
            city = "Ahmedabad, Gujarat",
            totalVideosSample = 510,
            isSample = true,
            avatarColorHex = 0xFF38BDF8
        ),
        CreatorProfile(
            id = "creator_9",
            name = "Priya Sen",
            channelName = "ReelTruth Bollywood",
            channelHandle = "@ReelTruthBollywood",
            channelUrl = "https://www.youtube.com/@ReelTruthBollywood",
            category = CreatorCategory.MOVIE_FACTS,
            subscriberRange = SubscriberRange.TIER_100K_500K,
            language = "Hinglish",
            collaborationInterests = listOf("Cinema History Podcast", "Screenplay Breakdown", "Director Tribute"),
            bio = "Archivist of Indian cinema history, vintage posters, soundtrack trivia, and unseen behind-the-scenes stories.",
            city = "Kolkata, West Bengal",
            totalVideosSample = 130,
            isSample = true,
            avatarColorHex = 0xFFFB7185
        )
    )

    val initialCollabRequests = listOf(
        CollabRequest(
            id = "req_1",
            targetCreatorId = "user_me",
            targetCreatorName = "My Creator Profile",
            targetChannelName = "Collab Creator Studio",
            senderName = "Aditya Nair",
            senderChannelName = "SiliconDosa Tech",
            senderChannelUrl = "https://www.youtube.com/@SiliconDosaTech",
            collabType = CollabType.PODCAST_GUEST,
            projectTitle = "Next-Gen Creators: AI & Studio Gear",
            timeline = "Next 2 Weeks",
            message = "Hey! Would love to invite you for a 30-min guest segment on my podcast to discuss modern creator workflows and video editing setups.",
            status = CollabStatus.PENDING,
            createdAtFormatted = "2 hours ago",
            isIncoming = true
        ),
        CollabRequest(
            id = "req_2",
            targetCreatorId = "creator_1",
            targetCreatorName = "Vikram Verma",
            targetChannelName = "CineVibes Hindi",
            senderName = "My Creator Profile",
            senderChannelName = "Collab Creator Studio",
            senderChannelUrl = "https://www.youtube.com/@CollabCreatorStudio",
            collabType = CollabType.CROSS_PROMO,
            projectTitle = "Top 10 Hidden Cinematic Clues in Indian Cinema",
            timeline = "End of this month",
            message = "Hi Vikram, I'm producing a video essay on visual foreshadowing and would love a 90-second guest cameo from CineVibes!",
            status = CollabStatus.ACCEPTED,
            createdAtFormatted = "1 day ago",
            isIncoming = false
        )
    )

    val initialProjects = listOf(
        CollabProject(
            id = "proj_1",
            title = "Top 10 Hidden Cinematic Clues",
            partnerName = "Vikram Verma",
            partnerChannelName = "CineVibes Hindi",
            partnerChannelUrl = "https://www.youtube.com/@CineVibesHindi",
            category = CreatorCategory.MOVIE_FACTS,
            collabType = CollabType.CROSS_PROMO,
            status = CollabStatus.IN_PROGRESS,
            progressPercent = 0.6f,
            milestones = listOf(
                ProjectMilestone("m1", "Script outline & scene selection", true, "Oct 15"),
                ProjectMilestone("m2", "Guest audio & voiceover recording", true, "Oct 18"),
                ProjectMilestone("m3", "Video rough cut assembly", false, "Oct 25"),
                ProjectMilestone("m4", "Final audio master & cross-upload teaser", false, "Nov 02")
            ),
            notes = "Vikram sent 4K recorded segment via Google Drive. Adding color correction and motion graphics callouts.",
            targetReleaseDate = "Nov 05, 2026",
            lastUpdated = "Yesterday"
        ),
        CollabProject(
            id = "proj_2",
            title = "Acoustic Folk Fusion Collab",
            partnerName = "Neha Rathore",
            partnerChannelName = "DhunAcoustic",
            partnerChannelUrl = "https://www.youtube.com/@DhunAcoustic",
            category = CreatorCategory.MUSIC,
            collabType = CollabType.SHORTS_REEL,
            status = CollabStatus.ACCEPTED,
            progressPercent = 0.25f,
            milestones = listOf(
                ProjectMilestone("m2_1", "Song key and tempo agreement", true, "Oct 16"),
                ProjectMilestone("m2_2", "Guitar chord progression stems", false, "Oct 24"),
                ProjectMilestone("m2_3", "Vocal harmonies mix", false, "Oct 30")
            ),
            notes = "Collaborative reel planned for Instagram & YouTube Shorts with split-screen video frame.",
            targetReleaseDate = "Nov 12, 2026",
            lastUpdated = "3 days ago"
        )
    )
}
