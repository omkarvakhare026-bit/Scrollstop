package com.scrollstop.app.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AccessibilityInspectionLog(
    val id: Long = System.currentTimeMillis(),
    val timestamp: String = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date()),
    val packageName: String,
    val eventType: String,
    val className: String,
    val detectedIds: List<String> = emptyList(),
    val verdict: String,
    val isReelAction: Boolean = false
)

object DetectionLabState {
    private val _logs = MutableStateFlow<List<AccessibilityInspectionLog>>(emptyList())
    val logs: StateFlow<List<AccessibilityInspectionLog>> = _logs.asStateFlow()

    private val _isServiceActive = MutableStateFlow(false)
    val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()

    private val _lastDetectedPlatform = MutableStateFlow<String?>(null)
    val lastDetectedPlatform: StateFlow<String?> = _lastDetectedPlatform.asStateFlow()

    private val _sessionReelsCount = MutableStateFlow(0)
    val sessionReelsCount: StateFlow<Int> = _sessionReelsCount.asStateFlow()

    fun updateServiceStatus(active: Boolean) {
        _isServiceActive.value = active
    }

    fun recordLog(log: AccessibilityInspectionLog) {
        val current = _logs.value.toMutableList()
        current.add(0, log)
        if (current.size > 100) {
            current.removeAt(current.lastIndex)
        }
        _logs.value = current
    }

    fun recordReel(platform: String) {
        _lastDetectedPlatform.value = platform
        _sessionReelsCount.value += 1
    }

    fun clearLogs() {
        _logs.value = emptyList()
        _sessionReelsCount.value = 0
    }
}
