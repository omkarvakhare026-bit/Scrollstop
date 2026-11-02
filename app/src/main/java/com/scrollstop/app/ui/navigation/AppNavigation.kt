package com.scrollstop.app.ui.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scrollstop.app.ui.dashboard.DashboardScreen
import com.scrollstop.app.ui.dashboard.DashboardViewModel
import com.scrollstop.app.ui.detectionlab.DetectionLabScreen
import com.scrollstop.app.ui.detectionlab.DetectionLabViewModel
import com.scrollstop.app.ui.gallery.MotivationGalleryScreen
import com.scrollstop.app.ui.gallery.MotivationGalleryViewModel
import com.scrollstop.app.ui.permissions.PermissionsScreen
import com.scrollstop.app.ui.statistics.StatisticsScreen
import com.scrollstop.app.ui.statistics.StatisticsViewModel
import com.scrollstop.app.ui.theme.BackgroundDark
import com.scrollstop.app.ui.theme.StopRed
import com.scrollstop.app.ui.theme.SurfaceBorderDark
import com.scrollstop.app.ui.theme.SurfaceDark
import com.scrollstop.app.ui.theme.TextMuted
import com.scrollstop.app.ui.theme.TextPrimary

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screens = listOf(
        Screen.Dashboard,
        Screen.Gallery,
        Screen.Statistics,
        Screen.DetectionLab,
        Screen.Permissions
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = TextPrimary,
                modifier = Modifier.border(width = 1.dp, color = SurfaceBorderDark)
            ) {
                screens.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(imageVector = screen.icon, contentDescription = screen.title)
                        },
                        label = {
                            Text(text = screen.title, fontSize = 10.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextPrimary,
                            selectedTextColor = TextPrimary,
                            indicatorColor = StopRed,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                val vm: DashboardViewModel = viewModel()
                DashboardScreen(
                    viewModel = vm,
                    onNavigateToPermissions = {
                        navController.navigate(Screen.Permissions.route)
                    },
                    onNavigateToDetectionLab = {
                        navController.navigate(Screen.DetectionLab.route)
                    }
                )
            }
            composable(Screen.Gallery.route) {
                val vm: MotivationGalleryViewModel = viewModel()
                MotivationGalleryScreen(viewModel = vm)
            }
            composable(Screen.Statistics.route) {
                val vm: StatisticsViewModel = viewModel()
                StatisticsScreen(viewModel = vm)
            }
            composable(Screen.DetectionLab.route) {
                val vm: DetectionLabViewModel = viewModel()
                DetectionLabScreen(viewModel = vm)
            }
            composable(Screen.Permissions.route) {
                PermissionsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
