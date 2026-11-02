package com.scrollstop.app.ui.permissions

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
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhoneAndroid
import com.scrollstop.app.util.RedmiMiuiHelper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.scrollstop.app.service.ScrollStopAccessibilityService
import com.scrollstop.app.ui.theme.AccentGold
import com.scrollstop.app.ui.theme.BackgroundDark
import com.scrollstop.app.ui.theme.StopRed
import com.scrollstop.app.ui.theme.SurfaceBorderDark
import com.scrollstop.app.ui.theme.SurfaceDark
import com.scrollstop.app.ui.theme.SurfaceVariantDark
import com.scrollstop.app.ui.theme.TextMuted
import com.scrollstop.app.ui.theme.TextPrimary
import com.scrollstop.app.ui.theme.TextSecondary
import com.scrollstop.app.ui.theme.WorkGreen

@Composable
fun PermissionsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var isAccessibilityGranted by remember {
        mutableStateOf(com.scrollstop.app.util.RedmiMiuiHelper.isAccessibilityEnabled(context, ScrollStopAccessibilityService::class.java))
    }
    var isOverlayGranted by remember {
        mutableStateOf(com.scrollstop.app.util.RedmiMiuiHelper.canDrawOverlays(context))
    }
    val isRedmiMiui = remember { com.scrollstop.app.util.RedmiMiuiHelper.isXiaomiOrRedmi() }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAccessibilityGranted = com.scrollstop.app.util.RedmiMiuiHelper.isAccessibilityEnabled(context, ScrollStopAccessibilityService::class.java)
                isOverlayGranted = com.scrollstop.app.util.RedmiMiuiHelper.canDrawOverlays(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Text(
            text = "SYSTEM PERMISSIONS",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "ESSENTIAL SHIELDS FOR REEL DETECTION & INTERVENTION",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = StopRed,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Card 1: Accessibility Service
        PermissionCard(
            title = "1. Accessibility Service",
            description = "Required to inspect reel transitions in Instagram and YouTube Shorts. Never reads private messages or passwords.",
            isGranted = isAccessibilityGranted,
            buttonText = if (isAccessibilityGranted) "ENABLED" else "GRANT ACCESSIBILITY",
            onAction = { com.scrollstop.app.util.RedmiMiuiHelper.openAccessibilitySettings(context) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Card 2: Draw Over Other Apps
        PermissionCard(
            title = "2. Display Over Other Apps",
            description = "Allows ScrollStop to launch the strict motivational reminder over Instagram and YouTube Shorts after 5 reels.",
            isGranted = isOverlayGranted,
            buttonText = if (isOverlayGranted) "ENABLED" else "GRANT OVERLAY PERMISSION",
            onAction = { com.scrollstop.app.util.RedmiMiuiHelper.openOverlaySettings(context) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Card 3: Redmi 12 5G (MIUI / HyperOS Special Section)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (isRedmiMiui) AccentGold else SurfaceBorderDark, RoundedCornerShape(16.dp)),
            color = SurfaceDark,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REDMI 12 5G / MIUI / HYPEROS SETUP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentGold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Xiaomi's MIUI / HyperOS aggressively stops background services unless explicitly configured. Perform these 3 quick adjustments to guarantee 100% reliability:",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // MIUI Setting 1: Autostart
                OutlinedButton(
                    onClick = { com.scrollstop.app.util.RedmiMiuiHelper.openAutostartSettings(context) },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("1. Enable Autostart in Security", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // MIUI Setting 2: Pop-up windows in background
                OutlinedButton(
                    onClick = { com.scrollstop.app.util.RedmiMiuiHelper.openMiuiPopupPermissions(context) },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("2. Allow Pop-up Windows in Background", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // MIUI Setting 3: Battery Saver No Restrictions
                OutlinedButton(
                    onClick = { com.scrollstop.app.util.RedmiMiuiHelper.openBatteryOptimizationSettings(context) },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("3. Battery Saver: Set to 'No Restrictions'", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StopRed),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(text = "BACK TO DASHBOARD", fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    isGranted: Boolean,
    buttonText: String,
    onAction: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isGranted) WorkGreen.copy(alpha = 0.5f) else StopRed.copy(alpha = 0.5f),
                RoundedCornerShape(16.dp)
            ),
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isGranted) WorkGreen.copy(alpha = 0.15f) else StopRed.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isGranted) "GRANTED" else "MISSING",
                        color = if (isGranted) WorkGreen else StopRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onAction,
                modifier = Modifier.fillMaxWidth().height(42.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isGranted) SurfaceVariantDark else StopRed,
                    contentColor = if (isGranted) WorkGreen else TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = buttonText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
