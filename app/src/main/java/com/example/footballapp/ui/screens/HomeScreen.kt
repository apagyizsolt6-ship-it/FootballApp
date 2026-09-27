package com.example.footballapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.footballapp.data.model.MatchFilter
import com.example.footballapp.ui.components.MatchCard
import com.example.footballapp.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onMatchClick: (String) -> Unit
) {
    val selectedLeague by viewModel.selectedLeague.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val matches by viewModel.matches.collectAsState()

    val leagues = listOf("Premier League", "La Liga", "Serie A", "Bundesliga", "Ligue 1")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1222))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Mérkőzések",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Bajnokság választó sáv
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leagues.forEach { league ->
                val isSelected = league == selectedLeague
                Button(
                    onClick = { viewModel.selectLeague(league) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF00C853) else Color(0xFF1E2235)
                    )
                ) {
                    Text(
                        text = league,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Vízszintes naptár sáv
        HorizontalDateStrip(
            selectedDate = selectedDate,
            onDateSelected = { viewModel.selectDate(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Szűrő chip-ek
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MatchFilter.values().forEach { filter ->
                val isSelected = filter == selectedFilter
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setFilter(filter) },
                    label = {
                        Text(
                            text = when (filter) {
                                MatchFilter.ALL -> "Összes"
                                MatchFilter.LIVE -> "Élő"
                                MatchFilter.FINISHED -> "Lejárt"
                                MatchFilter.UPCOMING -> "Közelgő"
                            }
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF00C853),
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0xFF1E2235),
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (matches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nincs mérkőzés ezen a napon.",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(matches) { match ->
                    MatchCard(
                        match = match,
                        onClick = { onMatchClick(match.id) },
                        onFavoriteClick = { viewModel.toggleFavorite(match.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalDateStrip(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val today = LocalDate.now()
    val dates = (-3..3).map { today.plusDays(it.toLong()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        dates.forEach { date ->
            val dateString = date.toString()
            val isSelected = dateString == selectedDate
            val dayName = date.format(DateTimeFormatter.ofPattern("EEE"))
            val dayNum = date.format(DateTimeFormatter.ofPattern("MM.dd."))

            Card(
                onClick = { onDateSelected(dateString) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF00C853) else Color(0xFF1E2235)
                ),
                modifier = Modifier.width(75.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayName.uppercase(),
                        color = if (isSelected) Color.Black else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dayNum,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
