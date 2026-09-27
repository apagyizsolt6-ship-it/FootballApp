package com.example.footballapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.footballapp.data.model.Event
import com.example.footballapp.ui.theme.LiveRed
import com.example.footballapp.ui.theme.PrimaryGreen

@Composable
fun MatchCard(
    event: Event,
    onClick: () -> Unit = {}
) {
    val isLive = event.strStatus == "LIVE"
    val hasScore = event.intHomeScore != null && event.intAwayScore != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
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
                    text = event.strLeague ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (isLive) {
                    Text(
                        text = "● LIVE",
                        color = LiveRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                } else {
                    Text(
                        text = buildString {
                            append(event.dateEvent ?: "")
                            if (!event.strTime.isNullOrBlank()) {
                                append(" • ")
                                append(event.strTime?.take(5) ?: "")
                            }
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.strHomeTeam ?: "Home",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .background(
                            color = when {
                                isLive -> LiveRed.copy(alpha = 0.15f)
                                hasScore -> PrimaryGreen.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surface
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasScore) {
                        Text(
                            text = "${event.intHomeScore} - ${event.intAwayScore}",
                            fontWeight = FontWeight.Bold,
                            color = if (isLive) LiveRed else PrimaryGreen,
                            fontSize = 16.sp
                        )
                    } else {
                        Text(
                            text = "vs",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = event.strAwayTeam ?: "Away",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.strStatus ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = if (isLive) LiveRed else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isLive) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
