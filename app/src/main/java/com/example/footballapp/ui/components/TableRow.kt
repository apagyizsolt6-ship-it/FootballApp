package com.example.footballapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.footballapp.data.model.TableEntry
import com.example.footballapp.ui.theme.*

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#", modifier = Modifier.width(28.dp), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Text("Csapat", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Text("M", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Text("Gy", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Text("D", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Text("V", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Text("Gk", modifier = Modifier.width(36.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Text("P", modifier = Modifier.width(32.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TableRowItem(entry: TableEntry, isEven: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isEven) CardDark else SurfaceDark)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.intRank ?: "-",
            modifier = Modifier.width(28.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!entry.strBadge.isNullOrBlank()) {
                AsyncImage(
                    model = entry.strBadge,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = entry.strTeam ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(entry.intPlayed ?: "-", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(entry.intWin ?: "-", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = WinGreen)
        Text(entry.intDraw ?: "-", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = DrawGray)
        Text(entry.intLoss ?: "-", modifier = Modifier.width(28.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = LossRed)
        Text(entry.intGoalDifference ?: "-", modifier = Modifier.width(36.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(
            text = entry.intPoints ?: "-",
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = PrimaryGreen,
            fontWeight = FontWeight.Bold
        )
    }
}
