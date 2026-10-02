package com.example.footballapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.footballapp.data.prefs.AppPreferences
import com.example.footballapp.ui.components.MatchCard
import com.example.footballapp.viewmodel.TeamDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    teamId: String,
    onBack: () -> Unit,
    prefs: AppPreferences? = null,
    viewModel: TeamDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val favorites by (prefs?.favoriteTeamIds?.collectAsState(initial = emptySet())
        ?: remember { mutableStateOf(emptySet()) })
    val isFavorite = teamId in favorites
    val scope = rememberCoroutineScope()

    LaunchedEffect(teamId) {
        viewModel.loadTeam(teamId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.team?.strTeam ?: "Csapat") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Vissza")
                    }
                },
                actions = {
                    if (prefs != null) {
                        IconButton(onClick = {
                            scope.launch { prefs.toggleFavorite(teamId) }
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite
                                else Icons.Default.FavoriteBorder,
                                contentDescription = "Kedvenc",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.team == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Csapat nem található", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val team = uiState.team!!
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = team.strTeamBadge,
                            contentDescription = team.strTeam,
                            modifier = Modifier.size(100.dp).clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = team.strTeam ?: "",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!team.strStadium.isNullOrBlank()) {
                            Text(
                                text = "Stadion: ${team.strStadium}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                item {
                    Text(
                        "Következő mérkőzések",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }
                if (uiState.nextEvents.isEmpty()) {
                    item {
                        Text(
                            "Nincs közelgő mérkőzés",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(uiState.nextEvents) { MatchCard(event = it) }
                }
                item {
                    Text(
                        "Legutóbbi mérkőzések",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                    )
                }
                if (uiState.lastEvents.isEmpty()) {
                    item {
                        Text(
                            "Nincs friss mérkőzés",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(uiState.lastEvents) { MatchCard(event = it) }
                }
            }
        }
    }
}
