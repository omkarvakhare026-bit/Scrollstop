package com.scrollstop.app.ui.overlay

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.scrollstop.app.ScrollStopApp
import com.scrollstop.app.data.local.entity.MotivationalItemEntity
import com.scrollstop.app.ui.theme.ScrollStopTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ReminderOverlayManager {

    private var activeOverlayView: ComposeView? = null
    private var windowManager: WindowManager? = null
    private var isOverlayShowing = false

    fun isShowing(): Boolean = isOverlayShowing

    fun showReminder(
        context: Context,
        reelsCount: Int,
        onDismiss: () -> Unit = {}
    ) {
        if (isOverlayShowing) return

        val app = ScrollStopApp.instance
        app.applicationScope.launch {
            val prefs = app.userPreferencesRepository.userPreferencesFlow.first()
            val motivation = app.motivationRepository.getActiveMotivation(prefs.useRandomMotivation)

            withContext(Dispatchers.Main) {
                // Trigger Vibration if enabled
                if (prefs.isVibrationEnabled) {
                    triggerVibration(context)
                }

                // Try WindowManager Overlay if permission is granted
                if (Settings.canDrawOverlays(context)) {
                    val shown = showWindowManagerOverlay(
                        context = context,
                        reelsCount = reelsCount,
                        motivation = motivation,
                        onDismiss = onDismiss
                    )
                    if (shown) return@withContext
                }

                // Fallback to Translucent Activity Intent
                showOverlayActivity(context, reelsCount)
            }
        }
    }

    private fun showWindowManagerOverlay(
        context: Context,
        reelsCount: Int,
        motivation: MotivationalItemEntity?,
        onDismiss: () -> Unit
    ): Boolean {
        return try {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager = wm

            val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.CENTER
            }

            val lifecycleOwner = OverlayLifecycleOwner()
            lifecycleOwner.performRestore(null)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

            val composeView = ComposeView(context).apply {
                setViewTreeLifecycleOwner(lifecycleOwner)
                setViewTreeViewModelStoreOwner(lifecycleOwner)
                setViewTreeSavedStateRegistryOwner(lifecycleOwner)

                setContent {
                    ScrollStopTheme {
                        ReminderOverlayContent(
                            reelsCount = reelsCount,
                            motivation = motivation,
                            onStopWork = {
                                dismissOverlay()
                                handleStopDecision(context)
                                onDismiss()
                            },
                            onContinueScrolling = {
                                dismissOverlay()
                                handleContinueDecision(context)
                                onDismiss()
                            }
                        )
                    }
                }
            }

            wm.addView(composeView, params)
            activeOverlayView = composeView
            isOverlayShowing = true
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun showOverlayActivity(context: Context, reelsCount: Int) {
        val intent = Intent(context, ReminderOverlayActivity::class.java).apply {
            putExtra(ReminderOverlayActivity.EXTRA_REELS_COUNT, reelsCount)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(intent)
        isOverlayShowing = true
    }

    fun dismissOverlay() {
        if (!isOverlayShowing) return
        try {
            activeOverlayView?.let {
                windowManager?.removeViewImmediate(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            activeOverlayView = null
            windowManager = null
            isOverlayShowing = false
        }
    }

    fun handleStopDecision(context: Context) {
        val app = ScrollStopApp.instance
        app.applicationScope.launch {
            app.statsRepository.recordUserDecision(stopped = true)
        }

        // Navigate immediately to device Home Screen to cut dopamine loop
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(homeIntent)
    }

    fun handleContinueDecision(context: Context) {
        val app = ScrollStopApp.instance
        app.applicationScope.launch {
            app.statsRepository.recordUserDecision(stopped = false)
        }
    }

    private fun triggerVibration(context: Context) {
        try {
            val timings = longArrayOf(0, 250, 150, 250, 150, 450)
            val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vm?.vibrate(CombinedVibration.createParallel(effect))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                    v?.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    v?.vibrate(timings, -1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private class OverlayLifecycleOwner :
        androidx.lifecycle.LifecycleOwner,
        androidx.lifecycle.ViewModelStoreOwner,
        SavedStateRegistryOwner {

        private val lifecycleRegistry = androidx.lifecycle.LifecycleRegistry(this)
        private val store = ViewModelStore()
        private val savedStateRegistryController = SavedStateRegistryController.create(this)

        override val lifecycle: Lifecycle get() = lifecycleRegistry
        override val viewModelStore: ViewModelStore get() = store
        override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

        fun performRestore(savedState: android.os.Bundle?) {
            savedStateRegistryController.performAttach()
            savedStateRegistryController.performRestore(savedState)
        }

        fun handleLifecycleEvent(event: Lifecycle.Event) {
            lifecycleRegistry.handleLifecycleEvent(event)
        }
    }
}
