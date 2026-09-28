package com.example.footballapp.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.footballapp.data.prefs.AppPreferences
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
    val leagues = AppPreferences.ALL_LEAGUES

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
            leagues.forEach { (id, name) ->
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

        // Tabella / Gólkirályok váltó
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !uiState.showScorers,
                onClick = { viewModel.showTable() },
                label = { Text("Tabella") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryGreen,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            FilterChip(
                selected = uiState.showScorers,
                onClick = { viewModel.loadScorers() },
                label = { Text("Gólkirályok") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryGreen,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }
            uiState.error != null && !uiState.showScorers && uiState.table.isEmpty() && uiState.groupedTables.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Hiba: ${uiState.error}\n(Az ingyenes kulcs korlátozott lehet)",
                        color = TextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            uiState.showScorers -> {
                if (uiState.scorers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.error ?: "Nincs elérhető gólkirály lista",
                            color = TextSecondary
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("#", Modifier.width(32.dp), color = TextSecondary, fontWeight = FontWeight.Bold)
                                Text("Játékos", Modifier.weight(1f), color = TextSecondary, fontWeight = FontWeight.Bold)
                                Text("Gól", Modifier.width(40.dp), color = TextSecondary, fontWeight = FontWeight.Bold)
                                Text("Gólpassz", Modifier.width(56.dp), color = TextSecondary, fontWeight = FontWeight.Bold)
                            }
                        }
                        items(uiState.scorers) { s ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${s.rank}", Modifier.width(32.dp), color = TextPrimary)
                                Column(Modifier.weight(1f)) {
                                    Text(s.playerName, color = TextPrimary, fontWeight = FontWeight.Medium)
                                    if (s.teamName != null) {
                                        Text(s.teamName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                }
                                Text("${s.goals}", Modifier.width(40.dp), color = PrimaryGreen, fontWeight = FontWeight.Bold)
                                Text("${s.assists ?: "-"}", Modifier.width(56.dp), color = TextSecondary)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        }
                    }
                }
            }
            uiState.groupedTables.size > 1 -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    uiState.groupedTables.forEach { group ->
                        item {
                            Text(
                                text = group.groupName.replace("_", " "),
                                style = MaterialTheme.typography.titleMedium,
                                color = PrimaryGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                            )
                        }
                        item { TableHeader() }
                        itemsIndexed(group.entries) { index, entry ->
                            TableRowItem(entry = entry, isEven = index % 2 == 0)
                        }
                    }
                }
            }
            uiState.table.isEmpty() && uiState.groupedTables.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nincs elérhető tabella", color = TextSecondary)
                }
            }
            else -> {
                val entries = if (uiState.groupedTables.isNotEmpty()) {
                    uiState.groupedTables.first().entries
                } else {
                    uiState.table
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { TableHeader() }
                    itemsIndexed(entries) { index, entry ->
                        TableRowItem(entry = entry, isEven = index % 2 == 0)
                    }
                }
            }
        }
    }
}
