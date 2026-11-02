package com.scrollstop.app.ui.detectionlab

import androidx.lifecycle.ViewModel
import com.scrollstop.app.service.AccessibilityInspectionLog
import com.scrollstop.app.service.DetectionLabState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

class DetectionLabViewModel : ViewModel() {

    private val _filterReelsOnly = MutableStateFlow(false)
    val filterReelsOnly: StateFlow<Boolean> = _filterReelsOnly.asStateFlow()

    val isServiceActive: StateFlow<Boolean> = DetectionLabState.isServiceActive
    val sessionReelsCount: StateFlow<Int> = DetectionLabState.sessionReelsCount

    val logs = combine(DetectionLabState.logs, _filterReelsOnly) { allLogs, reelsOnly ->
        if (reelsOnly) {
            allLogs.filter { it.isReelAction }
        } else {
            allLogs
        }
    }

    fun toggleFilter() {
        _filterReelsOnly.value = !_filterReelsOnly.value
    }

    fun clearLogs() {
        DetectionLabState.clearLogs()
    }
}
