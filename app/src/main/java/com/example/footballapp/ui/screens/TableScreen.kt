package com.example.footballapp.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.footballapp.ui.components.TableHeader
import com.example.footballapp.ui.components.TableRowItem
import com.example.footballapp.ui.theme.PrimaryGreen
import com.example.footballapp.ui.theme.TextPrimary
import com.example.footballapp.ui.theme.TextSecondary
import com.example.footballapp.viewmodel.TableViewModel

@Composable
fun TableScreen(
    viewModel: TableViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val leagues = listOf(
        "Premier League" to SportsRepository.PREMIER_LEAGUE,
        "La Liga" to SportsRepository.LA_LIGA,
        "Serie A" to SportsRepository.SERIE_A,
        "Bundesliga" to SportsRepository.BUNDESLIGA,
        "Ligue 1" to SportsRepository.LIGUE_1
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Tabella",
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
                    onClick = { viewModel.loadTable(id) },
                    label = { Text(name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Hiba: ${uiState.error}\n(Az ingyenes kulcs korlátozott a tabellákra)",
                    color = TextSecondary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (uiState.table.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nincs elérhető tabella", color = TextSecondary)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { TableHeader() }
                itemsIndexed(uiState.table) { index, entry ->
                    TableRowItem(entry = entry, isEven = index % 2 == 0)
                }
            }
        }
    }
}
