package com.example.footballapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.footballapp.data.api.ApiClient
import com.example.footballapp.data.model.Event
import com.example.footballapp.data.repository.SportsRepository
import com.example.footballapp.ui.theme.LiveRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: String,
    onBack: () -> Unit,
    onTeamClick: (String) -> Unit = {}
) {
    var event by remember { mutableStateOf<Event?>(null) }
    var h2h by remember { mutableStateOf<List<Event>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var refreshing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val repo = remember { SportsRepository() }
    val scope = rememberCoroutineScope()

    suspend fun load(force: Boolean = false) {
        if (force) refreshing = true else loading = true
        error = null
        if (ApiClient.isRateLimited()) {
            error = "Túl sok kérés. Próbáld újra ${ApiClient.rateLimitSecondsLeft()} mp múlva."
            loading = false
            refreshing = false
            return
        }
        val result = repo.getMatch(matchId)
        event = result.getOrNull()
        error = result.exceptionOrNull()?.message
        if (event != null) {
            val h2hResult = repo.getHead2Head(matchId, limit = 10)
            h2h = h2hResult.getOrDefault(emptyList())
                .filter { it.idEvent != matchId }
        }
        loading = false
        refreshing = false
    }

    LaunchedEffect(matchId) { load() }

    // Élő meccs auto-frissítés
    LaunchedEffect(event?.strStatus) {
        while (event?.strStatus == "LIVE") {
            delay(30_000)
            load(force = true)
        }
    }

    val e = event
    val isLive = e?.strStatus == "LIVE"
    val isFinished = e?.strStatus == "FT"
    val hasScore = e?.intHomeScore != null

    // H2H összesítő
    val homeId = e?.idHomeTeam
    val awayId = e?.idAwayTeam
    var homeWins = 0
    var awayWins = 0
    var draws = 0
    h2h.forEach { past ->
        val hs = past.intHomeScore?.toIntOrNull()
        val as_ = past.intAwayScore?.toIntOrNull()
        if (hs == null || as_ == null) return@forEach
        val pastHomeIsCurrentHome = past.idHomeTeam == homeId
        when {
            hs == as_ -> draws++
            pastHomeIsCurrentHome && hs > as_ -> homeWins++
            pastHomeIsCurrentHome && hs < as_ -> awayWins++
            !pastHomeIsCurrentHome && hs > as_ -> awayWins++
            !pastHomeIsCurrentHome && hs < as_ -> homeWins++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meccs részletek") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Vissza")
                    }
                },
            )
        }
    ) { padding ->
        when {
            loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            e == null -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = error ?: "Meccs nem található",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                    TextButton(onClick = { scope.launch { load(force = true) } }) {
                        Text("Újrapróbálás")
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // Liga + státusz
                    item {
                        Text(
                            text = e.strLeague ?: "",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(Modifier.height(4.dp))
                        val meta = buildList {
                            e.matchday?.let { add("$it. forduló") }
                            e.group?.let { add(it.replace("_", " ")) }
                            e.stage?.takeIf { it != "REGULAR_SEASON" }?.let {
                                add(it.replace("_", " "))
                            }
                        }.joinToString(" · ")
                        if (meta.isNotBlank()) {
                            Text(
                                text = meta,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Spacer(Modifier.height(12.dp))

                        StatusBadge(status = e.strStatus)
                        Spacer(Modifier.height(8.dp))

                        if (!isLive) {
                            Text(
                                text = "${e.dateEvent ?: ""}  ${e.strTime?.take(5) ?: ""}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(28.dp))
                    }

                    // Csapatok + eredmény
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TeamColumn(
                                name = e.strHomeTeam ?: "?",
                                badge = e.strHomeTeamBadge,
                                onClick = { e.idHomeTeam?.let(onTeamClick) },
                                modifier = Modifier.weight(1f)
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = if (hasScore)
                                        "${e.intHomeScore} – ${e.intAwayScore}"
                                    else "vs",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isLive -> LiveRed
                                        isFinished -> MaterialTheme.colorScheme.onSurface
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                )
                                if (e.intHomeHtScore != null || e.intAwayHtScore != null) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Félidő: ${e.intHomeHtScore ?: "-"} – ${e.intAwayHtScore ?: "-"}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            TeamColumn(
                                name = e.strAwayTeam ?: "?",
                                badge = e.strAwayTeamBadge,
                                onClick = { e.idAwayTeam?.let(onTeamClick) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        TextButton(
                            onClick = { scope.launch { load(force = true) } },
                            enabled = !refreshing
                        ) {
                            if (refreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(8.dp))
                            } else {
                                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(if (isLive) "Élő frissítés" else "Frissítés")
                        }
                        if (isLive) {
                            Text(
                                text = "Automatikus frissítés 30 mp-enként",
                                style = MaterialTheme.typography.labelSmall,
                                color = LiveRed,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(16.dp))
                    }

                    // H2H összesítő
                    item {
                        Text(
                            text = "Egymás ellen (H2H)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        if (h2h.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                H2HStat(
                                    label = e.strHomeTeam?.take(12) ?: "Hazai",
                                    value = homeWins.toString()
                                )
                                H2HStat(label = "Döntetlen", value = draws.toString())
                                H2HStat(
                                    label = e.strAwayTeam?.take(12) ?: "Vendég",
                                    value = awayWins.toString()
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    if (h2h.isEmpty()) {
                        item {
                            Text(
                                text = "Nincs elérhető korábbi meccs (API korlát).",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        items(h2h) { past ->
                            H2HMatchCard(past)
                        }
                    }

                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String?) {
    val (text, color) = when (status) {
        "LIVE" -> "● ÉLŐ" to LiveRed
        "FT" -> "Vége" to MaterialTheme.colorScheme.primary
        "NS" -> "Még nem kezdődött" to MaterialTheme.colorScheme.onSurfaceVariant
        "PP" -> "Elhalasztva" to MaterialTheme.colorScheme.error
        "CANC" -> "Törölve" to MaterialTheme.colorScheme.error
        else -> (status ?: "") to MaterialTheme.colorScheme.onSurfaceVariant
    }
    if (text.isBlank()) return
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        color = color,
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
private fun TeamColumn(
    name: String,
    badge: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        if (!badge.isNullOrBlank()) {
            AsyncImage(
                model = badge,
                contentDescription = name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun H2HStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun H2HMatchCard(past: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = listOfNotNull(past.dateEvent, past.strLeague).joinToString(" · "),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = past.strHomeTeam ?: "?",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = if (past.intHomeScore != null)
                        "${past.intHomeScore} – ${past.intAwayScore}"
                    else "vs",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    text = past.strAwayTeam ?: "?",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }
    }
}
