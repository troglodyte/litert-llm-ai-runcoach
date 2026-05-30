package com.litert.coach.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.litert.coach.ai.ModelVariant
import com.litert.coach.ui.chat.ChatScreen
import com.litert.coach.ui.debug.DebugScreen
import com.litert.coach.ui.history.HistoryScreen
import com.litert.coach.ui.plan.PlanScreen
import com.litert.coach.ui.profile.ProfileScreen

private data class TabItem(
    val screen: Screen.Tab,
    val label: String,
    val icon: @Composable () -> Unit
)

private val tabs = listOf(
    TabItem(Screen.Tab.Plan, "Plan") { Icon(Icons.Filled.DateRange, contentDescription = "Plan") },
    TabItem(Screen.Tab.History, "History") { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "History") },
    TabItem(Screen.Tab.Profile, "Profile") { Icon(Icons.Filled.Person, contentDescription = "Profile") }
)

@Composable
fun MainScaffold(onNavigateToDownload: (ModelVariant) -> Unit = {}) {
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == tab.screen.route } == true,
                        onClick = {
                            tabNavController.navigate(tab.screen.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = tab.icon,
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = Screen.Tab.Plan.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Tab.Plan.route) {
                PlanScreen(onNavigateToChat = {
                    tabNavController.navigate(Screen.Chat.route)
                })
            }
            composable(Screen.Chat.route) {
                ChatScreen(onNavigateToDebug = {
                    tabNavController.navigate(Screen.PromptDebug.route)
                })
            }
            composable(Screen.PromptDebug.route) {
                DebugScreen(onBack = { tabNavController.popBackStack() })
            }
            composable(Screen.Tab.History.route) {
                HistoryScreen()
            }
            composable(Screen.Tab.Profile.route) {
                ProfileScreen(onNavigateToDownload = onNavigateToDownload)
            }
        }
    }
}
