package com.scrollstop.app.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.scrollstop.app.ui.theme.AccentGold
import com.scrollstop.app.ui.theme.BackgroundDark
import com.scrollstop.app.ui.theme.InstagramPink
import com.scrollstop.app.ui.theme.StopRed
import com.scrollstop.app.ui.theme.StopRedDark
import com.scrollstop.app.ui.theme.SurfaceBorderDark
import com.scrollstop.app.ui.theme.SurfaceDark
import com.scrollstop.app.ui.theme.SurfaceVariantDark
import com.scrollstop.app.ui.theme.TextMuted
import com.scrollstop.app.ui.theme.TextPrimary
import com.scrollstop.app.ui.theme.TextSecondary
import com.scrollstop.app.ui.theme.WorkGreen
import com.scrollstop.app.ui.theme.YouTubeRed

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToPermissions: () -> Unit,
    onNavigateToDetectionLab: () -> Unit
) {
    val stats by viewModel.todayStats.collectAsState()
    val prefs by viewModel.userPreferences.collectAsState()
    val status by viewModel.status.collectAsState()

    // Refresh permission status on resume
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val isGuardFullyActive = status.isAccessibilityEnabled && status.isOverlayEnabled
    val dailyProgress = if (prefs.dailyLimit > 0) {
        (stats.totalReels.toFloat() / prefs.dailyLimit.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val animatedProgress by animateFloatAsState(targetValue = dailyProgress, label = "progress")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertAlignment
        ) {
            Column {
                Text(
                    text = "SCROLLSTOP",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "WAR ON MINDLESS DOPAMINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StopRed,
                    letterSpacing = 1.sp
                )
            }

            // Status Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(if (isGuardFullyActive) WorkGreen.copy(alpha = 0.15f) else StopRed.copy(alpha = 0.15f))
                    .border(
                        1.dp,
                        if (isGuardFullyActive) WorkGreen else StopRed,
                        RoundedCornerShape(100.dp)
                    )
                    .clickable { onNavigateToPermissions() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (isGuardFullyActive) WorkGreen else StopRed, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGuardFullyActive) "GUARD ACTIVE" else "ACTION REQUIRED",
                        color = if (isGuardFullyActive) WorkGreen else StopRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Warning banner if permissions are missing
        if (!isGuardFullyActive) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToPermissions() }
                    .border(1.dp, StopRed, RoundedCornerShape(16.dp)),
                color = SurfaceVariantDark,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = StopRed,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Monitoring Inactive",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (!status.isAccessibilityEnabled) "Grant Accessibility Service" else "Grant Display Over Other Apps",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Setup",
                        tint = StopRed
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Hero Today's Reels Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (stats.totalReels >= prefs.dailyLimit) StopRed else SurfaceBorderDark,
                    RoundedCornerShape(20.dp)
                )
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black),
            color = SurfaceDark,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TODAY'S CONSUMED REELS",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${stats.totalReels}",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    color = if (stats.totalReels >= prefs.dailyLimit) StopRed else TextPrimary,
                    letterSpacing = (-2).sp
                )

                Text(
                    text = "Intervention triggers every ${prefs.reminderInterval} reels",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Daily Limit Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Daily Limit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Text(
                        text = "${stats.totalReels} / ${prefs.dailyLimit} Reels",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (stats.totalReels >= prefs.dailyLimit) StopRed else TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(100.dp)),
                    color = if (stats.totalReels >= prefs.dailyLimit) StopRed else AccentGold,
                    trackColor = SurfaceVariantDark
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Split Counters: Instagram vs YouTube Shorts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Instagram Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, SurfaceBorderDark, RoundedCornerShape(16.dp)),
                color = SurfaceDark,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "INSTAGRAM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = InstagramPink,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${stats.instagramCount}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Reels Detected",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            // YouTube Shorts Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, SurfaceBorderDark, RoundedCornerShape(16.dp)),
                color = SurfaceDark,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "YOUTUBE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = YouTubeRed,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${stats.youtubeCount}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Shorts Detected",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Discipline & Decisions Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(16.dp)),
            color = SurfaceDark,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "DISCIPLINE & INTERVENTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "${stats.reminderCount}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Alerts Fired",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Column {
                        Text(
                            text = "${stats.stopCount}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = WorkGreen
                        )
                        Text(
                            text = "Quit & Worked",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Column {
                        Text(
                            text = "${stats.continueCount}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = StopRed
                        )
                        Text(
                            text = "Continued",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons: Test Intervention & Open Detection Lab
        Button(
            onClick = { viewModel.triggerTestIntervention() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StopRedDark,
                contentColor = TextPrimary
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "TEST STRICT INTERVENTION OVERLAY",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onNavigateToDetectionLab,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextSecondary
            ),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark)
        ) {
            Text(
                text = "OPEN DETECTION LAB (LIVE EVENT INSPECTOR)",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
