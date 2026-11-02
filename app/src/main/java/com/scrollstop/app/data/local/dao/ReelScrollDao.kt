package com.scrollstop.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.scrollstop.app.data.local.entity.ReelScrollEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelScrollDao {

    @Insert
    suspend fun insertEvent(event: ReelScrollEventEntity)

    @Query("SELECT * FROM reel_scroll_events ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEvents(limit: Int = 50): Flow<List<ReelScrollEventEntity>>

    @Query("DELETE FROM reel_scroll_events WHERE timestamp < :beforeTimestamp")
    suspend fun clearOldEvents(beforeTimestamp: Long)

    @Query("DELETE FROM reel_scroll_events")
    suspend fun clearAllEvents()
}
