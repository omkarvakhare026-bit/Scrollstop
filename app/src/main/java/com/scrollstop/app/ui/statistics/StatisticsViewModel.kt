package com.scrollstop.app.ui.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scrollstop.app.ScrollStopApp
import com.scrollstop.app.data.local.entity.DailyStatEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class LifetimeStats(
    val totalReels: Int = 0,
    val reminders: Int = 0,
    val stops: Int = 0,
    val continues: Int = 0,
    val disciplineRate: Int = 0
)

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as ScrollStopApp
    private val statsRepo = app.statsRepository

    val allDaysStats: StateFlow<List<DailyStatEntity>> = statsRepo.getAllStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val lifetimeStats: StateFlow<LifetimeStats> = combine(
        statsRepo.getLifetimeTotal(),
        statsRepo.getLifetimeReminders(),
        statsRepo.getLifetimeStops(),
        statsRepo.getLifetimeContinues()
    ) { total, reminders, stops, continues ->
        val totalDecisions = stops + continues
        val rate = if (totalDecisions > 0) (stops * 100) / totalDecisions else 0
        LifetimeStats(
            totalReels = total,
            reminders = reminders,
            stops = stops,
            continues = continues,
            disciplineRate = rate
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LifetimeStats()
    )
}
