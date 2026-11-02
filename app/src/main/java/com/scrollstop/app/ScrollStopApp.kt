package com.scrollstop.app

import android.app.Application
import com.scrollstop.app.data.local.ScrollStopDatabase
import com.scrollstop.app.data.preferences.UserPreferencesRepository
import com.scrollstop.app.data.repository.MotivationRepository
import com.scrollstop.app.data.repository.StatsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ScrollStopApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { ScrollStopDatabase.getDatabase(this, applicationScope) }
    val userPreferencesRepository by lazy { UserPreferencesRepository(this) }
    val statsRepository by lazy { StatsRepository(database.dailyStatDao(), database.reelScrollDao()) }
    val motivationRepository by lazy { MotivationRepository(this, database.motivationalItemDao()) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ScrollStopApp
            private set
    }
}
