package com.example.footballapp.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.footballapp.data.repository.SportsRepository
import com.example.footballapp.ui.components.MatchCard
import com.example.footballapp.ui.theme.PrimaryGreen
import com.example.footballapp.ui.theme.TextPrimary
import com.example.footballapp.ui.theme.TextSecondary
import com.example.footballapp.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val leagues = listOf(
        "Premier League" to SportsRepository.PREMIER_LEAGUE,
        "La Liga" to SportsRepository.LA_LIGA,
        "Serie A" to SportsRepository.SERIE_A,
        "Bundesliga" to SportsRepository.BUNDESLIGA,
        "Ligue 1" to SportsRepository.LIGUE_1,
        "Champions League" to SportsRepository.CHAMPIONS_LEAGUE
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Text(
            text = "Mérkőzések",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        // League chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leagues.forEach { (name, id) ->
                FilterChip(
                    selected = uiState.selectedLeagueId == id,
                    onClick = { viewModel.selectLeague(id) },
                    label = { Text(name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }
            uiState.matchesByDate.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.error ?: "Nincs elérhető meccs",
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                        Text(
                            text = "Tipp: cseréld ki az API kulcsot sajátra\naz ApiClient.kt-ben",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // NAPTÁR NÉZET – dátum szerint
                    val sortedDates = uiState.matchesByDate.keys.sorted()

                    sortedDates.forEach { date ->
                        val events = uiState.matchesByDate[date] ?: emptyList()
                        if (events.isNotEmpty()) {
                            item {
                                Text(
                                    text = formatHungarianDate(date),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        top = 20.dp,
                                        bottom = 8.dp
                                    )
                                )
                            }
                            items(events, key = { it.idEvent ?: it.hashCode().toString() }) { event ->
                                MatchCard(event = event)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatHungarianDate(date: String): String {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val output = SimpleDateFormat("yyyy. MMMM d.", Locale("hu", "HU"))
        val parsed = input.parse(date)
        if (parsed != null) output.format(parsed) else date
    } catch (e: Exception) {
        date
    }
}
