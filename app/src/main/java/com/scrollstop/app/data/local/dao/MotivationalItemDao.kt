package com.scrollstop.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.scrollstop.app.data.local.entity.MotivationalItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MotivationalItemDao {

    @Query("SELECT * FROM motivational_items ORDER BY isUserAdded DESC, id DESC")
    fun getAllItems(): Flow<List<MotivationalItemEntity>>

    @Query("SELECT * FROM motivational_items WHERE isActive = 1 LIMIT 1")
    fun getActiveItem(): Flow<MotivationalItemEntity?>

    @Query("SELECT * FROM motivational_items WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveItemSync(): MotivationalItemEntity?

    @Query("SELECT * FROM motivational_items ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomItem(): MotivationalItemEntity?

    @Query("SELECT COUNT(*) FROM motivational_items")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MotivationalItemEntity): Long

    @Query("DELETE FROM motivational_items WHERE id = :id")
    suspend fun deleteItem(id: Long)

    @Query("UPDATE motivational_items SET isActive = 0")
    suspend fun clearActive()

    @Query("UPDATE motivational_items SET isActive = 1 WHERE id = :id")
    suspend fun markActive(id: Long)

    @Transaction
    suspend fun setActiveItem(id: Long) {
        clearActive()
        markActive(id)
    }
}
