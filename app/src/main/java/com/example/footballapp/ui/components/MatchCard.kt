package com.example.footballapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.footballapp.data.model.Match
import com.example.footballapp.data.model.MatchStatus

@Composable
fun MatchCard(
    match: Match,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2235))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.league,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${match.date} • ${match.time}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onFavoriteClick, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = if (match.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Kedvenc",
                            tint = if (match.isFavorite) Color.Red else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.homeTeam,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = when (match.status) {
                                MatchStatus.LIVE -> Color(0xFF00C853)
                                MatchStatus.FINISHED -> Color(0xFF2E3B55)
                                MatchStatus.UPCOMING -> Color(0xFF37474F)
                            },
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val scoreText = if (match.status == MatchStatus.UPCOMING) {
                        "VS"
                    } else {
                        "${match.homeScore ?: 0} - ${match.awayScore ?: 0}"
                    }
                    Text(
                        text = scoreText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = match.awayTeam,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val statusLabel = when (match.status) {
                    MatchStatus.LIVE -> "● ÉLŐ"
                    MatchStatus.FINISHED -> "LEJÁRT (FT)"
                    MatchStatus.UPCOMING -> "HAMAROSAN"
                }
                Text(
                    text = statusLabel,
                    color = when (match.status) {
                        MatchStatus.LIVE -> Color(0xFF00C853)
                        MatchStatus.FINISHED -> Color.Gray
                        MatchStatus.UPCOMING -> Color.Yellow
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
