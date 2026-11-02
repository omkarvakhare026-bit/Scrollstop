package com.scrollstop.app.ui.statistics

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrollstop.app.data.local.entity.DailyStatEntity
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
fun StatisticsScreen(
    viewModel: StatisticsViewModel
) {
    val lifetime by viewModel.lifetimeStats.collectAsState()
    val allDays by viewModel.allDaysStats.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header
        Text(
            text = "LIFETIME INTELLIGENCE",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "PERSISTENT HABIT & INTERVENTION METRICS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = StopRed,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lifetime Discipline Hero Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(18.dp)),
            color = SurfaceDark,
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "DISCIPLINE SUCCESS SCORE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${lifetime.disciplineRate}%",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = if (lifetime.disciplineRate >= 50) WorkGreen else StopRed
                    )
                    Text(
                        text = "${lifetime.stops} quit / ${lifetime.continues} continued",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "${lifetime.totalReels}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Total Reels Tracked", fontSize = 11.sp, color = TextMuted)
                    }
                    Column {
                        Text(text = "${lifetime.reminders}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                        Text(text = "Interventions Fired", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "DAILY BREAKDOWN",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = TextMuted,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (allDays.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(text = "No history recorded yet.", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(allDays, key = { it.date }) { day ->
                    DayStatCard(day = day)
                }
            }
        }
    }
}

@Composable
private fun DayStatCard(day: DailyStatEntity) {
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
            Column {
                Text(
                    text = day.date,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "IG: ${day.instagramCount}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = InstagramPink
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "YT: ${day.youtubeCount}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = YouTubeRed
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${day.totalReels} reels",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "${day.stopCount} stops / ${day.reminderCount} alerts",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}
