package com.scrollstop.app.ui.detectionlab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrollstop.app.service.AccessibilityInspectionLog
import com.scrollstop.app.ui.theme.AccentGold
import com.scrollstop.app.ui.theme.BackgroundDark
import com.scrollstop.app.ui.theme.InstagramPink
import com.scrollstop.app.ui.theme.StopRed
import com.scrollstop.app.ui.theme.SurfaceBorderDark
import com.scrollstop.app.ui.theme.SurfaceDark
import com.scrollstop.app.ui.theme.SurfaceVariantDark
import com.scrollstop.app.ui.theme.TextMuted
import com.scrollstop.app.ui.theme.TextPrimary
import com.scrollstop.app.ui.theme.TextSecondary
import com.scrollstop.app.ui.theme.WorkGreen
import com.scrollstop.app.ui.theme.YouTubeRed

@Composable
fun DetectionLabScreen(
    viewModel: DetectionLabViewModel
) {
    val logs by viewModel.logs.collectAsState(initial = emptyList())
    val isServiceActive by viewModel.isServiceActive.collectAsState()
    val sessionCount by viewModel.sessionReelsCount.collectAsState()
    val filterReelsOnly by viewModel.filterReelsOnly.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DETECTION LAB",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "LIVE ACCESSIBILITY EVENT VERIFIER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StopRed,
                    letterSpacing = 1.sp
                )
            }

            IconButton(onClick = { viewModel.clearLogs() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear Logs",
                    tint = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Diagnostic Summary Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp)),
            color = SurfaceDark,
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(if (isServiceActive) WorkGreen else StopRed, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isServiceActive) "SERVICE LISTENING" else "SERVICE OFFLINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isServiceActive) WorkGreen else StopRed
                    )
                }

                Text(
                    text = "Session Reels: $sessionCount",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips & Instructions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = filterReelsOnly,
                onClick = { viewModel.toggleFilter() },
                label = {
                    Text(
                        text = if (filterReelsOnly) "Showing: Reels Only" else "Showing: All Events",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StopRed.copy(alpha = 0.2f),
                    selectedLabelColor = StopRed,
                    containerColor = SurfaceDark,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (filterReelsOnly) StopRed else SurfaceBorderDark,
                    selectedBorderColor = StopRed
                )
            )

            Text(
                text = "${logs.size} events captured",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No events captured yet.",
                        color = TextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Enable Accessibility in Settings\n2. Open Instagram or YouTube\n3. Scroll through Reels / Shorts\n4. Return here to inspect exact detection logs",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(logs, key = { it.id }) { log ->
                    LogCard(log = log)
                }
            }
        }
    }
}

@Composable
private fun LogCard(log: AccessibilityInspectionLog) {
    val isInstagram = log.packageName.contains("Instagram", ignoreCase = true)
    val verdictColor = when {
        log.verdict.startsWith("CONFIRMED") -> WorkGreen
        log.verdict.startsWith("INFERRED") -> AccentGold
        else -> TextMuted
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (log.isReelAction) verdictColor.copy(alpha = 0.4f) else SurfaceBorderDark,
                RoundedCornerShape(12.dp)
            ),
        color = SurfaceDark,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Package + Timestamp + Verdict Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = log.packageName,
                        color = if (isInstagram) InstagramPink else YouTubeRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = log.timestamp,
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(verdictColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (log.verdict.startsWith("CONFIRMED")) "CONFIRMED" else if (log.verdict.startsWith("INFERRED")) "INFERRED" else "IGNORED",
                        color = verdictColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Verdict Detail
            Text(
                text = log.verdict,
                color = if (log.isReelAction) TextPrimary else TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (log.isReelAction) FontWeight.Bold else FontWeight.Normal
            )

            // Event & Class
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${log.eventType.replace("TYPE_", "")} • ${log.className.substringAfterLast('.')}",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )

            // Detected View IDs if any
            if (log.detectedIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Matched IDs: [${log.detectedIds.joinToString(", ")}]",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
