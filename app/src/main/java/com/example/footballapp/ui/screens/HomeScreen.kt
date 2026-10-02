package com.example.footballapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.footballapp.data.prefs.AppPreferences
import com.example.footballapp.ui.components.MatchCard
import com.example.footballapp.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onMatchClick: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    prefs: AppPreferences? = null,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // Prefs szinkronizálása a ViewModel-lel
    if (prefs != null) {
        val selectedLeagues by prefs.selectedLeagueCodes.collectAsState(initial = AppPreferences.DEFAULT_LEAGUES)
        val showOnlyFav by prefs.showOnlyFavorites.collectAsState(initial = false)
        val favoriteIds by prefs.favoriteTeamIds.collectAsState(initial = emptySet())

        LaunchedEffect(selectedLeagues, showOnlyFav, favoriteIds) {
            viewModel.syncFromPrefs(selectedLeagues, showOnlyFav, favoriteIds)
        }
    }


    // Élő meccsek auto-frissítés 45 mp-enként
    LaunchedEffect(uiState.hasLiveMatches) {
        if (!uiState.hasLiveMatches) return@LaunchedEffect
        while (true) {
            delay(45_000)
            if (!viewModel.uiState.value.hasLiveMatches) break
            viewModel.refresh()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mérkőzések",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                val newVal = !uiState.showOnlyFavorites
                viewModel.setShowOnlyFavoritesLocal(newVal)
                if (prefs != null) {
                    scope.launch { prefs.setShowOnlyFavorites(newVal) }
                }
            }) {
                Icon(
                    imageVector = if (uiState.showOnlyFavorites) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Csak kedvencek",
                    tint = if (uiState.showOnlyFavorites) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { viewModel.refresh() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Frissítés", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Beállítások", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Téma",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (uiState.isOffline) {
            Text(
                text = "⚠ Nincs internetkapcsolat – cache / offline adatok",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        if (uiState.lastRefreshTime != null) {
            Text(
                text = if (uiState.hasLiveMatches)
                    "● Élő meccsek – frissítve: ${uiState.lastRefreshTime} (auto 45 mp)"
                else
                    "Frissítve: ${uiState.lastRefreshTime}",
                style = MaterialTheme.typography.labelSmall,
                color = if (uiState.hasLiveMatches)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
            )
        }

        // Dátumválasztó + Ma gomb
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = false,
                onClick = { viewModel.goToToday() },
                label = { Text("Ma", fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    labelColor = MaterialTheme.colorScheme.primary
                )
            )
            uiState.availableDates.forEach { date ->
                DayChip(
                    date = date,
                    selected = date == uiState.selectedDate,
                    onClick = { viewModel.selectDate(date) }
                )
            }
        }

        // Liga szűrő
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AppPreferences.ALL_LEAGUES.forEach { (code, name) ->
                val selected = code in uiState.selectedLeagueCodes
                FilterChip(
                    selected = selected,
                    onClick = {
                        viewModel.toggleLeagueFilterLocal(code)
                        if (prefs != null) {
                            scope.launch { prefs.toggleLeague(code) }
                        }
                    },
                    label = { Text(name, style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        when {
            uiState.isLoading || uiState.isRefreshing -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.leaguesForDay.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.error ?: "Nincs meccs ezen a napon",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                        if (uiState.showOnlyFavorites) {
                            TextButton(onClick = {
                                viewModel.setShowOnlyFavoritesLocal(false)
                                if (prefs != null) {
                                    scope.launch { prefs.setShowOnlyFavorites(false) }
                                }
                            }) {
                                Text("Összes meccs mutatása")
                            }
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    uiState.leaguesForDay.forEach { leagueBlock ->
                        item {
                            Text(
                                text = leagueBlock.leagueName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(
                            items = leagueBlock.matches,
                            key = { it.idEvent ?: it.hashCode().toString() }
                        ) { event ->
                            MatchCard(
                                event = event,
                                onClick = { event.idEvent?.let { onMatchClick(it) } }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayChip(
    date: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val cal = Calendar.getInstance()
    try {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)?.let { cal.time = it }
    } catch (_: Exception) {}
    val dayName = SimpleDateFormat("EEE", Locale("hu")).format(cal.time)
    val dayNum = SimpleDateFormat("d", Locale.US).format(cal.time)
    val month = SimpleDateFormat("MMM", Locale("hu")).format(cal.time)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayName.replaceFirstChar { it.uppercase() },
            fontSize = 11.sp,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = dayNum,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = month,
            fontSize = 10.sp,
            color = if (selected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
