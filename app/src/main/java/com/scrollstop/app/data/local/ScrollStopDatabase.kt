package com.scrollstop.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.scrollstop.app.data.local.dao.DailyStatDao
import com.scrollstop.app.data.local.dao.MotivationalItemDao
import com.scrollstop.app.data.local.dao.ReelScrollDao
import com.scrollstop.app.data.local.entity.DailyStatEntity
import com.scrollstop.app.data.local.entity.MotivationalItemEntity
import com.scrollstop.app.data.local.entity.ReelScrollEventEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DailyStatEntity::class,
        ReelScrollEventEntity::class,
        MotivationalItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ScrollStopDatabase : RoomDatabase() {

    abstract fun dailyStatDao(): DailyStatDao
    abstract fun reelScrollDao(): ReelScrollDao
    abstract fun motivationalItemDao(): MotivationalItemDao

    companion object {
        @Volatile
        private var INSTANCE: ScrollStopDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ScrollStopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScrollStopDatabase::class.java,
                    "scrollstop_database"
                )
                    .addCallback(ScrollStopDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ScrollStopDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDefaultMotivation(database.motivationalItemDao())
                }
            }
        }

        private suspend fun populateDefaultMotivation(dao: MotivationalItemDao) {
            val defaults = listOf(
                MotivationalItemEntity(
                    quote = "STOP SCROLLING. YOU ARE WASTING YOUR POTENTIAL.\nYou have goals. You have responsibilities. You have a future to build.\nIs another reel really worth sacrificing your time?",
                    author = "Warrior Mindset",
                    isUserAdded = false,
                    isActive = true
                ),
                MotivationalItemEntity(
                    quote = "The dopamine you are chasing right now is stealing the life you promised yourself. Put the phone down.",
                    author = "Focus Protocol",
                    isUserAdded = false,
                    isActive = false
                ),
                MotivationalItemEntity(
                    quote = "Discipline is choosing between what you want now and what you want most. Get back to work.",
                    author = "Execution First",
                    isUserAdded = false,
                    isActive = false
                ),
                MotivationalItemEntity(
                    quote = "Nobody is coming to save your dreams. While you scroll, your competition is putting in the reps.",
                    author = "Ruthless Truth",
                    isUserAdded = false,
                    isActive = false
                )
            )
            defaults.forEach { dao.insertItem(it) }
        }
    }
}
