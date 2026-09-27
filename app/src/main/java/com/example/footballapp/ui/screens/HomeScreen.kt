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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.footballapp.ui.components.MatchCard
import com.example.footballapp.ui.theme.PrimaryGreen
import com.example.footballapp.ui.theme.TextPrimary
import com.example.footballapp.ui.theme.TextSecondary
import com.example.footballapp.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Cím
        Text(
            text = "Mérkőzések",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        // ===== VÍZSZINTES 15 NAPOS NAPTÁR =====
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

        // ===== TARTALOM =====
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }
            uiState.leaguesForDay.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Nincs meccs ezen a napon",
                        color = TextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Bajnokságok egymás alatt
                    uiState.leaguesForDay.forEach { leagueBlock ->
                        item {
                            Text(
                                text = leagueBlock.leagueName,
                                style = MaterialTheme.typography.titleMedium,
                                color = PrimaryGreen,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    bottom = 8.dp
                                )
                            )
                        }
                        items(
                            items = leagueBlock.matches,
                            key = { it.idEvent ?: it.hashCode().toString() }
                        ) { event ->
                            MatchCard(event = event)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayChip(
    date: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val (dayName, dayNum, month) = formatDayChip(date)
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .width(56.dp)
            .clip(shape)
            .background(
                if (selected) PrimaryGreen.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .then(
                if (selected) Modifier.border(2.dp, PrimaryGreen, shape)
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayName,
            fontSize = 11.sp,
            color = if (selected) PrimaryGreen else TextSecondary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = dayNum,
            fontSize = 18.sp,
            color = if (selected) PrimaryGreen else TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = month,
            fontSize = 10.sp,
            color = if (selected) PrimaryGreen else TextSecondary
        )
    }
}

/** yyyy-MM-dd → (Hé, 27, szept.) */
private fun formatDayChip(date: String): Triple<String, String, String> {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val parsed = input.parse(date) ?: return Triple("", date, "")
        val cal = Calendar.getInstance().apply { time = parsed }
        val dayNames = arrayOf("Va", "Hé", "Ke", "Sze", "Csü", "Pé", "Szo")
        val dayName = dayNames[cal.get(Calendar.DAY_OF_WEEK) - 1]
        val dayNum = cal.get(Calendar.DAY_OF_MONTH).toString()
        val monthFormat = SimpleDateFormat("MMM", Locale("hu", "HU"))
        val month = monthFormat.format(parsed).replace(".", "")
        Triple(dayName, dayNum, month)
    } catch (e: Exception) {
        Triple("", date.takeLast(2), "")
    }
}
