package com.example.footballapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.footballapp.data.prefs.AppPreferences
import com.example.footballapp.ui.screens.HomeScreen
import com.example.footballapp.ui.screens.MatchDetailScreen
import com.example.footballapp.ui.screens.SearchScreen
import com.example.footballapp.ui.screens.SettingsScreen
import com.example.footballapp.ui.screens.TableScreen
import com.example.footballapp.ui.screens.TeamDetailScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Meccsek", Icons.Default.Home)
    data object Search : Screen("search", "Keresés", Icons.Default.Search)
    data object Table : Screen("table", "Tabella", Icons.Default.TableChart)
}

@Composable
fun FootballNavGraph(
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    prefs: AppPreferences? = null
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomScreens = listOf(Screen.Home, Screen.Search, Screen.Table)
    val showBottomBar = bottomScreens.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomScreens.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
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
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    onMatchClick = { matchId ->
                        navController.navigate("match/$matchId")
                    },
                    onSettingsClick = {
                        navController.navigate("settings")
                    },
                    prefs = prefs
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onTeamClick = { teamId ->
                        navController.navigate("team/$teamId")
                    }
                )
            }
            composable(Screen.Table.route) {
                TableScreen()
            }
            composable("settings") {
                if (prefs != null) {
                    SettingsScreen(
                        prefs = prefs,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = onToggleTheme,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            composable(
                route = "team/{teamId}",
                arguments = listOf(navArgument("teamId") { type = NavType.StringType })
            ) { backStackEntry ->
                val teamId = backStackEntry.arguments?.getString("teamId") ?: return@composable
                TeamDetailScreen(
                    teamId = teamId,
                    onBack = { navController.popBackStack() },
                    prefs = prefs
                )
            }
            composable(
                route = "match/{matchId}",
                arguments = listOf(navArgument("matchId") { type = NavType.StringType })
            ) { backStackEntry ->
                val matchId = backStackEntry.arguments?.getString("matchId") ?: return@composable
                MatchDetailScreen(
                    matchId = matchId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
