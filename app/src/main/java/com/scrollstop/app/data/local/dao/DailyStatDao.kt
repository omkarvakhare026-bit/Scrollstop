package com.scrollstop.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.scrollstop.app.data.local.entity.DailyStatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatDao {

    @Query("SELECT * FROM daily_stats WHERE date = :date LIMIT 1")
    fun getTodayStat(date: String): Flow<DailyStatEntity?>

    @Query("SELECT * FROM daily_stats WHERE date = :date LIMIT 1")
    suspend fun getTodayStatSync(date: String): DailyStatEntity?

    @Query("SELECT * FROM daily_stats ORDER BY date DESC")
    fun getAllStats(): Flow<List<DailyStatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stat: DailyStatEntity)

    @Transaction
    suspend fun incrementInstagram(date: String, now: Long = System.currentTimeMillis()) {
        val existing = getTodayStatSync(date) ?: DailyStatEntity(date = date)
        val updated = existing.copy(
            instagramCount = existing.instagramCount + 1,
            totalReels = existing.totalReels + 1,
            lastUpdated = now
        )
        insertOrUpdate(updated)
    }

    @Transaction
    suspend fun incrementYouTube(date: String, now: Long = System.currentTimeMillis()) {
        val existing = getTodayStatSync(date) ?: DailyStatEntity(date = date)
        val updated = existing.copy(
            youtubeCount = existing.youtubeCount + 1,
            totalReels = existing.totalReels + 1,
            lastUpdated = now
        )
        insertOrUpdate(updated)
    }

    @Transaction
    suspend fun recordReminderTriggered(date: String, now: Long = System.currentTimeMillis()) {
        val existing = getTodayStatSync(date) ?: DailyStatEntity(date = date)
        val updated = existing.copy(
            reminderCount = existing.reminderCount + 1,
            lastUpdated = now
        )
        insertOrUpdate(updated)
    }

    @Transaction
    suspend fun recordDecision(date: String, stopped: Boolean, now: Long = System.currentTimeMillis()) {
        val existing = getTodayStatSync(date) ?: DailyStatEntity(date = date)
        val updated = if (stopped) {
            existing.copy(stopCount = existing.stopCount + 1, lastUpdated = now)
        } else {
            existing.copy(continueCount = existing.continueCount + 1, lastUpdated = now)
        }
        insertOrUpdate(updated)
    }

    @Query("SELECT SUM(totalReels) FROM daily_stats")
    fun getLifetimeTotalReels(): Flow<Int?>

    @Query("SELECT SUM(reminderCount) FROM daily_stats")
    fun getLifetimeReminders(): Flow<Int?>

    @Query("SELECT SUM(stopCount) FROM daily_stats")
    fun getLifetimeStops(): Flow<Int?>

    @Query("SELECT SUM(continueCount) FROM daily_stats")
    fun getLifetimeContinues(): Flow<Int?>
}
