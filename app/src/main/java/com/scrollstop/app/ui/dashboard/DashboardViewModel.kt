package com.scrollstop.app.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scrollstop.app.ScrollStopApp
import com.scrollstop.app.data.local.entity.DailyStatEntity
import com.scrollstop.app.data.preferences.UserPreferences
import com.scrollstop.app.service.ScrollStopAccessibilityService
import com.scrollstop.app.ui.overlay.ReminderOverlayManager
import com.scrollstop.app.util.RedmiMiuiHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MonitoringStatus(
    val isAccessibilityEnabled: Boolean = false,
    val isOverlayEnabled: Boolean = false,
    val isMiuiDevice: Boolean = false
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as ScrollStopApp
    private val statsRepo = app.statsRepository
    private val prefsRepo = app.userPreferencesRepository

    val todayStats: StateFlow<DailyStatEntity> = statsRepo.getTodayStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyStatEntity(date = statsRepo.getTodayDate())
        )

    val userPreferences: StateFlow<UserPreferences> = prefsRepo.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    private val _status = MutableStateFlow(checkStatus())
    val status: StateFlow<MonitoringStatus> = _status.asStateFlow()

    fun refreshStatus() {
        _status.value = checkStatus()
    }

    private fun checkStatus(): MonitoringStatus {
        val context = getApplication<Application>()
        return MonitoringStatus(
            isAccessibilityEnabled = RedmiMiuiHelper.isAccessibilityEnabled(
                context,
                ScrollStopAccessibilityService::class.java
            ),
            isOverlayEnabled = RedmiMiuiHelper.canDrawOverlays(context),
            isMiuiDevice = RedmiMiuiHelper.isXiaomiOrRedmi()
        )
    }

    fun triggerTestIntervention() {
        ReminderOverlayManager.showReminder(
            context = getApplication(),
            reelsCount = todayStats.value.totalReels.coerceAtLeast(5)
        )
    }

    fun updateDailyLimit(newLimit: Int) {
        viewModelScope.launch {
            prefsRepo.updateDailyLimit(newLimit)
        }
    }

    fun updateReminderInterval(newInterval: Int) {
        viewModelScope.launch {
            prefsRepo.updateReminderInterval(newInterval)
        }
    }
}
