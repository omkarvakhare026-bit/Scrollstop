package com.scrollstop.app.ui.overlay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.scrollstop.app.ScrollStopApp
import com.scrollstop.app.data.local.entity.MotivationalItemEntity
import com.scrollstop.app.ui.theme.ScrollStopTheme

class ReminderOverlayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val reelsCount = intent.getIntExtra(EXTRA_REELS_COUNT, 5)
        val app = ScrollStopApp.instance

        setContent {
            ScrollStopTheme {
                val prefs by app.userPreferencesRepository.userPreferencesFlow.collectAsState(
                    initial = com.scrollstop.app.data.preferences.UserPreferences()
                )

                val motivation by produceState<MotivationalItemEntity?>(initialValue = null, key1 = prefs) {
                    value = app.motivationRepository.getActiveMotivation(prefs.useRandomMotivation)
                }

                ReminderOverlayContent(
                    reelsCount = reelsCount,
                    motivation = motivation,
                    onStopWork = {
                        ReminderOverlayManager.dismissOverlay()
                        ReminderOverlayManager.handleStopDecision(this@ReminderOverlayActivity)
                        finish()
                    },
                    onContinueScrolling = {
                        ReminderOverlayManager.dismissOverlay()
                        ReminderOverlayManager.handleContinueDecision(this@ReminderOverlayActivity)
                        finish()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ReminderOverlayManager.dismissOverlay()
    }

    companion object {
        const val EXTRA_REELS_COUNT = "extra_reels_count"
    }
}
