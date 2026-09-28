package com.example.footballapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.footballapp.data.api.ApiClient
import com.example.footballapp.data.model.Event
import com.example.footballapp.data.repository.SportsRepository
import com.example.footballapp.ui.theme.LiveRed
import com.example.footballapp.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: String,
    onBack: () -> Unit
) {
    var event by remember { mutableStateOf<Event?>(null) }
    var h2h by remember { mutableStateOf<List<Event>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val repo = remember { SportsRepository() }

    LaunchedEffect(matchId) {
        loading = true
        error = null
        if (ApiClient.isRateLimited()) {
            error = "Túl sok kérés. Próbáld újra ${ApiClient.rateLimitSecondsLeft()} mp múlva."
            loading = false
            return@LaunchedEffect
        }
        val result = repo.getMatch(matchId)
        event = result.getOrNull()
        error = result.exceptionOrNull()?.message
        if (event != null) {
            val h2hResult = repo.getHead2Head(matchId, limit = 8)
            h2h = h2hResult.getOrDefault(emptyList())
                .filter { it.idEvent != matchId }
        }
        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meccs részletek") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Vissza")
                    }
                }
            )
        }
    ) { padding ->
        when {
            loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
            event == null -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error ?: "Meccs nem található",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                val e = event!!
                val isLive = e.strStatus == "LIVE"
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        Text(
                            text = e.strLeague ?: "",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        if (isLive) {
                            Text("● LIVE", color = LiveRed, fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                "${e.dateEvent ?: ""}  ${e.strTime?.take(5) ?: ""}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(32.dp))
                        Text(
                            e.strHomeTeam ?: "",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = if (e.intHomeScore != null)
                                "${e.intHomeScore}  -  ${e.intAwayScore}"
                            else "vs",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLive) LiveRed else PrimaryGreen
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            e.strAwayTeam ?: "",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = e.strStatus ?: "",
                            color = if (isLive) LiveRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(24.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Korábbi találkozók (H2H)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    if (h2h.isEmpty()) {
                        item {
                            Text(
                                text = "Nincs elérhető korábbi meccs adat (free API korlát).",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        items(h2h) { past ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${past.dateEvent ?: ""} · ${past.strLeague ?: ""}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "${past.strHomeTeam ?: "?"}  ${past.intHomeScore ?: "-"} : ${past.intAwayScore ?: "-"}  ${past.strAwayTeam ?: "?"}",
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
