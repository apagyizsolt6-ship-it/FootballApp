package com.example.footballapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.footballapp.ui.components.MatchCard
import com.example.footballapp.ui.theme.PrimaryGreen
import com.example.footballapp.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onMatchClick: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mérkőzések",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Téma",
                    tint = PrimaryGreen
                )
            }
        }

        // Vízszintes 15 napos naptár
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.availableDates.forEach { date ->
                DayChip(
                    date = date,
                    selected = date == uiState.selectedDate,
                    onClick = { viewModel.selectDate(date) }
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryGreen)
                    }
                }
                uiState.leaguesForDay.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.error ?: "Nincs meccs ezen a napon",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                                    color = PrimaryGreen,
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
}

@Composable
private fun DayChip(date: String, selected: Boolean, onClick: () -> Unit) {
    val (dayName, dayNum, month) = formatDayChip(date)
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .width(56.dp)
            .clip(shape)
            .background(
                if (selected) PrimaryGreen.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .then(if (selected) Modifier.border(2.dp, PrimaryGreen, shape) else Modifier)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayName,
            fontSize = 11.sp,
            color = if (selected) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = dayNum,
            fontSize = 18.sp,
            color = if (selected) PrimaryGreen else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = month,
            fontSize = 10.sp,
            color = if (selected) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDayChip(date: String): Triple<String, String, String> {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val parsed = input.parse(date) ?: return Triple("", date, "")
        val cal = Calendar.getInstance().apply { time = parsed }
        val dayNames = arrayOf("Va", "Hé", "Ke", "Sze", "Csü", "Pé", "Szo")
        val dayName = dayNames[cal.get(Calendar.DAY_OF_WEEK) - 1]
        val dayNum = cal.get(Calendar.DAY_OF_MONTH).toString()
        val month = SimpleDateFormat("MMM", Locale("hu", "HU")).format(parsed).replace(".", "")
        Triple(dayName, dayNum, month)
    } catch (_: Exception) {
        Triple("", date.takeLast(2), "")
    }
}
