package com.scrollstop.app.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.scrollstop.app.data.local.entity.MotivationalItemEntity
import com.scrollstop.app.ui.theme.BackgroundDark
import com.scrollstop.app.ui.theme.StopRed
import com.scrollstop.app.ui.theme.StopRedDark
import com.scrollstop.app.ui.theme.SurfaceBorderDark
import com.scrollstop.app.ui.theme.SurfaceDark
import com.scrollstop.app.ui.theme.SurfaceVariantDark
import com.scrollstop.app.ui.theme.TextMuted
import com.scrollstop.app.ui.theme.TextPrimary
import com.scrollstop.app.ui.theme.TextSecondary
import com.scrollstop.app.ui.theme.WorkGreen
import com.scrollstop.app.ui.theme.WorkGreenDark
import java.io.File

@Composable
fun ReminderOverlayContent(
    reelsCount: Int,
    motivation: MotivationalItemEntity?,
    onStopWork: () -> Unit,
    onContinueScrolling: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(300)
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        brush = Brush.verticalGradient(
                            listOf(StopRed, Color(0xFF3B0707))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(24.dp), ambientColor = StopRed, spotColor = StopRed),
                color = SurfaceDark,
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Warning Icon Badge
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = StopRed.copy(alpha = 0.15f),
                                shape = CircleShape
                            )
                            .border(1.5.dp, StopRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = StopRed,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reel Count Alert Badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = SurfaceVariantDark,
                                shape = RoundedCornerShape(100.dp)
                            )
                            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(100.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "INTERVENTION ALERT — $reelsCount REELS SCROLLED",
                            color = StopRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Main Headline
                    Text(
                        text = "STOP SCROLLING.",
                        color = TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.5).sp
                    )

                    Text(
                        text = "YOU ARE WASTING YOUR POTENTIAL.",
                        color = StopRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Motivational Image if available
                    val localImage = motivation?.imagePath?.let { File(it) }
                    if (localImage != null && localImage.exists()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(16.dp))
                        ) {
                            AsyncImage(
                                model = localImage,
                                contentDescription = "Personal Motivation",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Motivational Reality Check Card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp)),
                        color = SurfaceVariantDark,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = motivation?.quote
                                    ?: "You have goals.\nYou have responsibilities.\nYou have a future to build.\n\nIs another reel really worth sacrificing your time?",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (motivation?.author?.isNotBlank() == true) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "— ${motivation.author}",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Decision Button 1: GET BACK TO WORK
                    Button(
                        onClick = onStopWork,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = WorkGreen),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WorkGreenDark,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "I'M GETTING BACK TO WORK",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Decision Button 2: CONTINUE SCROLLING
                    OutlinedButton(
                        onClick = onContinueScrolling,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark)
                    ) {
                        Text(
                            text = "CONTINUE SCROLLING (NOT RECOMMENDED)",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
