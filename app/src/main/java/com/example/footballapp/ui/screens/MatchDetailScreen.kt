package com.example.footballapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.footballapp.data.model.Event
import com.example.footballapp.data.repository.SportsRepository
import com.example.footballapp.ui.theme.LiveRed
import com.example.footballapp.ui.theme.PrimaryGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: String,
    onBack: () -> Unit
) {
    var event by remember { mutableStateOf<Event?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val repo = remember { SportsRepository() }

    LaunchedEffect(matchId) {
        loading = true
        val result = repo.getMatch(matchId)
        event = result.getOrNull()
        error = result.exceptionOrNull()?.message
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
            loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
            event == null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(error ?: "Meccs nem található", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> {
                val e = event!!
                val isLive = e.strStatus == "LIVE"
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                    Spacer(Modifier.height(32.dp))
                    Text(
                        text = "Góllövők és felállás: a free API tier korlátozottan adja.\nPrémium tokennel több adat érhető el.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
