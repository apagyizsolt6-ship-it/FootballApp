package com.example.footballapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.footballapp.ui.components.MatchCard
import com.example.footballapp.ui.theme.PrimaryGreen
import com.example.footballapp.ui.theme.TextPrimary
import com.example.footballapp.ui.theme.TextSecondary
import com.example.footballapp.viewmodel.TeamDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    teamId: String,
    onBack: () -> Unit,
    viewModel: TeamDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(teamId) {
        viewModel.loadTeam(teamId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.team?.strTeam ?: "Csapat") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Vissza")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else if (uiState.team == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Csapat nem található", color = TextSecondary)
            }
        } else {
            val team = uiState.team!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Header with badge
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = team.strTeamBadge,
                            contentDescription = team.strTeam,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = team.strTeam ?: "",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${team.strLeague ?: ""} • ${team.strCountry ?: ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        if (!team.strStadium.isNullOrBlank()) {
                            Text(
                                text = "Stadion: ${team.strStadium}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Description
                if (!team.strDescriptionEN.isNullOrBlank()) {
                    item {
                        Text(
                            text = "Leírás",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        Text(
                            text = team.strDescriptionEN.take(400) + if ((team.strDescriptionEN?.length ?: 0) > 400) "..." else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // Next matches
                item {
                    Text(
                        text = "Következő mérkőzések",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                    )
                }
                if (uiState.nextEvents.isEmpty()) {
                    item {
                        Text(
                            text = "Nincs közelgő mérkőzés",
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(uiState.nextEvents) { event ->
                        MatchCard(event = event)
                    }
                }

                // Last matches
                item {
                    Text(
                        text = "Legutóbbi mérkőzések",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                    )
                }
                if (uiState.lastEvents.isEmpty()) {
                    item {
                        Text(
                            text = "Nincs friss mérkőzés",
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(uiState.lastEvents) { event ->
                        MatchCard(event = event)
                    }
                }
            }
        }
    }
}
