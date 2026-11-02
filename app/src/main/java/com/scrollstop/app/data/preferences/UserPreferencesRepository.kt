package com.scrollstop.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scrollstop_preferences")

data class UserPreferences(
    val reminderInterval: Int = 5,
    val dailyLimit: Int = 30,
    val isVibrationEnabled: Boolean = true,
    val isStrictOverlayEnabled: Boolean = true,
    val useRandomMotivation: Boolean = false,
    val hasCompletedOnboarding: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val REMINDER_INTERVAL = intPreferencesKey("reminder_interval")
        val DAILY_LIMIT = intPreferencesKey("daily_limit")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val STRICT_OVERLAY_ENABLED = booleanPreferencesKey("strict_overlay_enabled")
        val RANDOM_MOTIVATION = booleanPreferencesKey("random_motivation")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                reminderInterval = preferences[PreferencesKeys.REMINDER_INTERVAL] ?: 5,
                dailyLimit = preferences[PreferencesKeys.DAILY_LIMIT] ?: 30,
                isVibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
                isStrictOverlayEnabled = preferences[PreferencesKeys.STRICT_OVERLAY_ENABLED] ?: true,
                useRandomMotivation = preferences[PreferencesKeys.RANDOM_MOTIVATION] ?: false,
                hasCompletedOnboarding = preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
            )
        }

    suspend fun updateReminderInterval(interval: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_INTERVAL] = interval.coerceAtLeast(1)
        }
    }

    suspend fun updateDailyLimit(limit: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_LIMIT] = limit.coerceAtLeast(1)
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun setStrictOverlayEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.STRICT_OVERLAY_ENABLED] = enabled
        }
    }

    suspend fun setUseRandomMotivation(random: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.RANDOM_MOTIVATION] = random
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }
}
