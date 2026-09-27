package com.example.footballapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.footballapp.data.model.MatchStatus
import com.example.footballapp.viewmodel.MatchDetailViewModel

@Composable
fun MatchDetailScreen(
    matchId: String,
    onBackClick: () -> Unit,
    viewModel: MatchDetailViewModel = viewModel()
) {
    val match by viewModel.match.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1222))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Vissza", tint = Color.White)
            }
            Text(
                text = "Mérkőzés részletei",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.toggleFavorite() }) {
                Icon(
                    imageVector = if (match?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Kedvenc",
                    tint = if (match?.isFavorite == true) Color.Red else Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        match?.let { m ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2235))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = m.league, color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${m.date} • ${m.time}", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = m.homeTeam,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = if (m.status == MatchStatus.UPCOMING) "VS" else "${m.homeScore ?: 0} - ${m.awayScore ?: 0}",
                            color = Color(0xFF00C853),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Text(
                            text = m.awayTeam,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Helyszín: ${m.venue}", color = Color.LightGray, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Statisztikák",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            StatBar(
                label = "Labdabirtoklás",
                homeValue = "${m.homePossession}%",
                awayValue = "${m.awayPossession}%",
                progress = m.homePossession / 100f
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatBar(
                label = "Kapura lövés",
                homeValue = "${m.shotsOnTargetHome}",
                awayValue = "${m.shotsOnTargetAway}",
                progress = if (m.shotsOnTargetHome + m.shotsOnTargetAway > 0)
                    m.shotsOnTargetHome.toFloat() / (m.shotsOnTargetHome + m.shotsOnTargetAway).toFloat()
                else 0.5f
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* Értesítés / Emlékeztető logika */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Értesítés küldése a meccsről", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatBar(label: String, homeValue: String, awayValue: String, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2235))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = homeValue, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = label, color = Color.Gray, fontSize = 14.sp)
                Text(text = awayValue, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFF00C853),
                trackColor = Color(0xFF2E3B55)
            )
        }
    }
}
