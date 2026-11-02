package com.scrollstop.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.scrollstop.app.ScrollStopApp
import com.scrollstop.app.ui.overlay.ReminderOverlayManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScrollStopAccessibilityService : AccessibilityService() {

    private val app by lazy { ScrollStopApp.instance }

    // State Tracking for Instagram
    private var lastInstagramReelFingerprint: String? = null
    private var lastInstagramScrollTime: Long = 0L

    // State Tracking for YouTube Shorts
    private var lastYouTubeShortFingerprint: String? = null
    private var lastYouTubeScrollTime: Long = 0L

    // Last reminder milestone triggered to prevent duplicate alerts for the same reel
    private var lastTriggeredMilestone: Int = -1

    override fun onServiceConnected() {
        super.onServiceConnected()
        DetectionLabState.updateServiceStatus(true)

        // Ensure accessibility service configuration has window content inspection flags
        serviceInfo = serviceInfo.apply {
            flags = flags or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        when (packageName) {
            PACKAGE_INSTAGRAM -> processInstagramEvent(event)
            PACKAGE_YOUTUBE -> processYouTubeEvent(event)
            else -> {
                // Ignore events outside target applications
            }
        }
    }

    private fun processInstagramEvent(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return
        val eventTypeStr = AccessibilityEvent.eventTypeToString(event.eventType)
        val classNameStr = event.className?.toString() ?: "Unknown"

        val detectedIds = mutableListOf<String>()
        val isReelsActive = isInstagramReelsActive(rootNode, detectedIds)

        if (!isReelsActive) {
            DetectionLabState.recordLog(
                AccessibilityInspectionLog(
                    packageName = "Instagram",
                    eventType = eventTypeStr,
                    className = classNameStr,
                    detectedIds = detectedIds,
                    verdict = "IGNORED (Feed or Non-Reel Window)",
                    isReelAction = false
                )
            )
            return
        }

        val now = System.currentTimeMillis()
        val currentFingerprint = extractInstagramFingerprint(rootNode)

        // 1. Confirmed Detection: Fingerprint changed inside Reels viewer
        if (!currentFingerprint.isNullOrBlank()) {
            if (currentFingerprint != lastInstagramReelFingerprint) {
                if (now - lastInstagramScrollTime >= MIN_DEBOUNCE_MS) {
                    lastInstagramReelFingerprint = currentFingerprint
                    lastInstagramScrollTime = now

                    DetectionLabState.recordLog(
                        AccessibilityInspectionLog(
                            packageName = "Instagram",
                            eventType = eventTypeStr,
                            className = classNameStr,
                            detectedIds = detectedIds,
                            verdict = "CONFIRMED REEL (Content Changed: ${currentFingerprint.take(25)}...)",
                            isReelAction = true
                        )
                    )

                    onReelDetected(PLATFORM_INSTAGRAM, CONFIDENCE_CONFIRMED, "Author/Audio: $currentFingerprint")
                    return
                }
            }
        }

        // 2. Inferred Detection: View scrolled in Reels pager
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            if (now - lastInstagramScrollTime >= INFERRED_DEBOUNCE_MS) {
                lastInstagramScrollTime = now

                DetectionLabState.recordLog(
                    AccessibilityInspectionLog(
                        packageName = "Instagram",
                        eventType = eventTypeStr,
                        className = classNameStr,
                        detectedIds = detectedIds,
                        verdict = "INFERRED REEL (Scroll Gesture in Reels Container)",
                        isReelAction = true
                    )
                )

                onReelDetected(PLATFORM_INSTAGRAM, CONFIDENCE_INFERRED, "Vertical pager scroll")
            }
        }
    }

    private fun processYouTubeEvent(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return
        val eventTypeStr = AccessibilityEvent.eventTypeToString(event.eventType)
        val classNameStr = event.className?.toString() ?: "Unknown"

        val detectedIds = mutableListOf<String>()
        val isShortsActive = isYouTubeShortsActive(rootNode, detectedIds)

        if (!isShortsActive) {
            DetectionLabState.recordLog(
                AccessibilityInspectionLog(
                    packageName = "YouTube",
                    eventType = eventTypeStr,
                    className = classNameStr,
                    detectedIds = detectedIds,
                    verdict = "IGNORED (Feed, Search, or Video Window)",
                    isReelAction = false
                )
            )
            return
        }

        val now = System.currentTimeMillis()
        val currentFingerprint = extractYouTubeFingerprint(rootNode)

        // 1. Confirmed Detection: Fingerprint changed inside Shorts viewer
        if (!currentFingerprint.isNullOrBlank()) {
            if (currentFingerprint != lastYouTubeShortFingerprint) {
                if (now - lastYouTubeScrollTime >= MIN_DEBOUNCE_MS) {
                    lastYouTubeShortFingerprint = currentFingerprint
                    lastYouTubeScrollTime = now

                    DetectionLabState.recordLog(
                        AccessibilityInspectionLog(
                            packageName = "YouTube",
                            eventType = eventTypeStr,
                            className = classNameStr,
                            detectedIds = detectedIds,
                            verdict = "CONFIRMED SHORT (Title/Channel Changed: ${currentFingerprint.take(25)}...)",
                            isReelAction = true
                        )
                    )

                    onReelDetected(PLATFORM_YOUTUBE, CONFIDENCE_CONFIRMED, "Title/Channel: $currentFingerprint")
                    return
                }
            }
        }

        // 2. Inferred Detection: View scrolled in Shorts container
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            if (now - lastYouTubeScrollTime >= INFERRED_DEBOUNCE_MS) {
                lastYouTubeScrollTime = now

                DetectionLabState.recordLog(
                    AccessibilityInspectionLog(
                        packageName = "YouTube",
                        eventType = eventTypeStr,
                        className = classNameStr,
                        detectedIds = detectedIds,
                        verdict = "INFERRED SHORT (Scroll in Shorts Recycler)",
                        isReelAction = true
                    )
                )

                onReelDetected(PLATFORM_YOUTUBE, CONFIDENCE_INFERRED, "Shorts recycler scroll")
            }
        }
    }

    private fun isInstagramReelsActive(root: AccessibilityNodeInfo, detectedIds: MutableList<String>): Boolean {
        for (id in INSTAGRAM_REEL_IDS) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes.isNotEmpty()) {
                detectedIds.add(id.substringAfter(":id/"))
            }
        }

        // Also check content descriptions typical of Reels viewer
        val reelsMarkers = listOf("Reel by", "Audio by", "Remix with")
        var hasReelsDescription = false
        for (marker in reelsMarkers) {
            val textNodes = root.findAccessibilityNodeInfosByText(marker)
            if (textNodes.isNotEmpty()) {
                hasReelsDescription = true
                detectedIds.add("desc:$marker")
                break
            }
        }

        return detectedIds.isNotEmpty() || hasReelsDescription
    }

    private fun extractInstagramFingerprint(root: AccessibilityNodeInfo): String? {
        // Look for author or audio text
        for (marker in listOf("Reel by", "Audio by")) {
            val nodes = root.findAccessibilityNodeInfosByText(marker)
            for (node in nodes) {
                val text = node.text?.toString() ?: node.contentDescription?.toString()
                if (!text.isNullOrBlank()) return text.trim()
            }
        }

        // Check clips author view ID
        for (id in listOf("com.instagram.android:id/row_feed_photo_profile_name", "com.instagram.android:id/clips_author_name")) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            for (node in nodes) {
                val text = node.text?.toString()
                if (!text.isNullOrBlank()) return text.trim()
            }
        }
        return null
    }

    private fun isYouTubeShortsActive(root: AccessibilityNodeInfo, detectedIds: MutableList<String>): Boolean {
        for (id in YOUTUBE_SHORTS_IDS) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes.isNotEmpty()) {
                detectedIds.add(id.substringAfter(":id/"))
            }
        }

        val textNodes = root.findAccessibilityNodeInfosByText("Shorts")
        if (textNodes.isNotEmpty()) {
            detectedIds.add("text:Shorts")
        }

        return detectedIds.isNotEmpty()
    }

    private fun extractYouTubeFingerprint(root: AccessibilityNodeInfo): String? {
        val candidateIds = listOf(
            "com.google.android.youtube:id/reel_player_title_text",
            "com.google.android.youtube:id/channel_name",
            "com.google.android.youtube:id/reel_channel_name"
        )
        for (id in candidateIds) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            for (node in nodes) {
                val text = node.text?.toString() ?: node.contentDescription?.toString()
                if (!text.isNullOrBlank()) return text.trim()
            }
        }
        return null
    }

    private fun onReelDetected(platform: String, confidence: String, details: String) {
        DetectionLabState.recordReel(platform)

        app.applicationScope.launch {
            app.statsRepository.recordReelScroll(
                platform = platform,
                confidence = confidence,
                details = details
            )

            // Check if reminder threshold is reached
            checkAndTriggerReminder()
        }
    }

    private suspend fun checkAndTriggerReminder() {
        val prefs = app.userPreferencesRepository.userPreferencesFlow.first()
        val todayStats = app.statsRepository.getTodayStatsSync()
        val totalToday = todayStats.totalReels
        val interval = prefs.reminderInterval

        // Milestones: 5, 10, 15, 20...
        if (totalToday > 0 && totalToday % interval == 0) {
            if (totalToday != lastTriggeredMilestone) {
                lastTriggeredMilestone = totalToday

                app.statsRepository.recordReminderTriggered()

                withContext(Dispatchers.Main) {
                    ReminderOverlayManager.showReminder(
                        context = applicationContext,
                        reelsCount = totalToday
                    )
                }
            }
        }
    }

    override fun onInterrupt() {
        DetectionLabState.updateServiceStatus(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        DetectionLabState.updateServiceStatus(false)
        ReminderOverlayManager.dismissOverlay()
    }

    companion object {
        const val PACKAGE_INSTAGRAM = "com.instagram.android"
        const val PACKAGE_YOUTUBE = "com.google.android.youtube"

        const val PLATFORM_INSTAGRAM = "INSTAGRAM"
        const val PLATFORM_YOUTUBE = "YOUTUBE"

        const val CONFIDENCE_CONFIRMED = "CONFIRMED"
        const val CONFIDENCE_INFERRED = "INFERRED"

        private const val MIN_DEBOUNCE_MS = 1200L
        private const val INFERRED_DEBOUNCE_MS = 1500L

        private val INSTAGRAM_REEL_IDS = listOf(
            "com.instagram.android:id/reel_viewer_root",
            "com.instagram.android:id/clips_video_container",
            "com.instagram.android:id/clips_viewer_view_pager",
            "com.instagram.android:id/video_container",
            "com.instagram.android:id/like_button",
            "com.instagram.android:id/comment_button"
        )

        private val YOUTUBE_SHORTS_IDS = listOf(
            "com.google.android.youtube:id/shorts_container",
            "com.google.android.youtube:id/reel_player_page",
            "com.google.android.youtube:id/reel_recycler",
            "com.google.android.youtube:id/reel_shelf",
            "com.google.android.youtube:id/shorts_player_view"
        )
    }
}
