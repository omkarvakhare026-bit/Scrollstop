package com.scrollstop.app.data.repository

import com.scrollstop.app.data.local.dao.DailyStatDao
import com.scrollstop.app.data.local.dao.ReelScrollDao
import com.scrollstop.app.data.local.entity.DailyStatEntity
import com.scrollstop.app.data.local.entity.ReelScrollEventEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatsRepository(
    private val dailyStatDao: DailyStatDao,
    private val reelScrollDao: ReelScrollDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodayDate(): String = dateFormat.format(Date())

    fun getTodayStats(): Flow<DailyStatEntity> {
        val today = getTodayDate()
        return dailyStatDao.getTodayStat(today).map { it ?: DailyStatEntity(date = today) }
    }

    suspend fun getTodayStatsSync(): DailyStatEntity {
        val today = getTodayDate()
        return dailyStatDao.getTodayStatSync(today) ?: DailyStatEntity(date = today)
    }

    fun getAllStats(): Flow<List<DailyStatEntity>> = dailyStatDao.getAllStats()

    fun getRecentEvents(limit: Int = 50): Flow<List<ReelScrollEventEntity>> =
        reelScrollDao.getRecentEvents(limit)

    suspend fun recordReelScroll(
        platform: String,
        confidence: String,
        details: String
    ) {
        val today = getTodayDate()
        val now = System.currentTimeMillis()

        // Insert event into raw log
        reelScrollDao.insertEvent(
            ReelScrollEventEntity(
                timestamp = now,
                platform = platform,
                confidence = confidence,
                details = details
            )
        )

        // Increment aggregate daily stat
        if (platform.equals("INSTAGRAM", ignoreCase = true)) {
            dailyStatDao.incrementInstagram(today, now)
        } else {
            dailyStatDao.incrementYouTube(today, now)
        }
    }

    suspend fun recordReminderTriggered() {
        val today = getTodayDate()
        dailyStatDao.recordReminderTriggered(today)
    }

    suspend fun recordUserDecision(stopped: Boolean) {
        val today = getTodayDate()
        dailyStatDao.recordDecision(today, stopped)
    }

    fun getLifetimeTotal(): Flow<Int> = dailyStatDao.getLifetimeTotalReels().map { it ?: 0 }
    fun getLifetimeReminders(): Flow<Int> = dailyStatDao.getLifetimeReminders().map { it ?: 0 }
    fun getLifetimeStops(): Flow<Int> = dailyStatDao.getLifetimeStops().map { it ?: 0 }
    fun getLifetimeContinues(): Flow<Int> = dailyStatDao.getLifetimeContinues().map { it ?: 0 }

    suspend fun clearLogs() {
        reelScrollDao.clearAllEvents()
    }
}
