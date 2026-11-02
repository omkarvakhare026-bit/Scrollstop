package com.scrollstop.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Gallery : Screen("gallery", "Motivation", Icons.Default.PhotoLibrary)
    object Statistics : Screen("statistics", "Statistics", Icons.Default.BarChart)
    object DetectionLab : Screen("detection_lab", "Detection Lab", Icons.Default.BugReport)
    object Permissions : Screen("permissions", "Permissions", Icons.Default.Security)
}
