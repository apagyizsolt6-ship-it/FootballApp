package com.example.footballapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.footballapp.ui.screens.HomeScreen
import com.example.footballapp.ui.screens.MatchDetailScreen
import com.example.footballapp.ui.screens.SearchScreen
import com.example.footballapp.ui.screens.TableScreen
import com.example.footballapp.ui.screens.TeamDetailScreen

@Composable
fun FootballNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute == "home" || currentRoute == "search" || currentRoute == "table") {
                NavigationBar(
                    containerColor = Color(0xFF131722)
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Mérkőzések") },
                        label = { Text("Mérkőzések") },
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00C853),
                            selectedTextColor = Color(0xFF00C853),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFF1E2235)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Keresés") },
                        label = { Text("Keresés") },
                        selected = currentRoute == "search",
                        onClick = { navController.navigate("search") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00C853),
                            selectedTextColor = Color(0xFF00C853),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFF1E2235)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = "Tabella") },
                        label = { Text("Tabella") },
                        selected = currentRoute == "table",
                        onClick = { navController.navigate("table") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00C853),
                            selectedTextColor = Color(0xFF00C853),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFF1E2235)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onMatchClick = { matchId ->
                        navController.navigate("match_detail/$matchId")
                    }
                )
            }
            composable("search") {
                SearchScreen()
            }
            composable("table") {
                TableScreen(
                    onTeamClick = { teamId ->
                        navController.navigate("team_detail/$teamId")
                    }
                )
            }
            composable(
                route = "match_detail/{matchId}",
                arguments = listOf(navArgument("matchId") { type = NavType.StringType })
            ) { backStackEntry ->
                val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
                MatchDetailScreen(
                    matchId = matchId,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(
                route = "team_detail/{teamId}",
                arguments = listOf(navArgument("teamId") { type = NavType.StringType })
            ) { backStackEntry ->
                val teamId = backStackEntry.arguments?.getString("teamId") ?: ""
                TeamDetailScreen(
                    teamId = teamId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
