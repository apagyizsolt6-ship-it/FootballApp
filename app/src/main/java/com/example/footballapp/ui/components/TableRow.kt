package com.example.footballapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.footballapp.data.model.TableEntry
import com.example.footballapp.ui.theme.FormDraw
import com.example.footballapp.ui.theme.FormLoss
import com.example.footballapp.ui.theme.FormWin
import com.example.footballapp.ui.theme.PrimaryGreen

@Composable
fun TableHeader() {
    TableRowItem(
        entry = TableEntry(
            intRank = "#",
            strTeam = "Csapat",
            intPlayed = "P",
            intWin = "W",
            intDraw = "D",
            intLoss = "L",
            intGoalDifference = "GD",
            intPoints = "Pts"
        ),
        isHeader = true
    )
}

@Composable
fun TableRowItem(entry: TableEntry, isHeader: Boolean = false, isEven: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (!isHeader && isEven)
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                else
                    androidx.compose.ui.graphics.Color.Transparent
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.intRank ?: "",
            modifier = Modifier.width(28.dp),
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.SemiBold,
            color = when {
                isHeader -> MaterialTheme.colorScheme.onSurfaceVariant
                (entry.intRank?.toIntOrNull() ?: 99) <= 4 -> PrimaryGreen
                else -> MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center,
            fontSize = 13.sp
        )

        if (!isHeader && !entry.strBadge.isNullOrBlank()) {
            AsyncImage(
                model = entry.strBadge,
                contentDescription = null,
                modifier = Modifier.size(24.dp).clip(CircleShape),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (!isHeader) {
            Spacer(modifier = Modifier.width(32.dp))
        }

        Text(
            text = entry.strTeam ?: "",
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp
        )

        StatCell(entry.intPlayed ?: "P", isHeader)
        StatCell(entry.intWin ?: "W", isHeader)
        StatCell(entry.intDraw ?: "D", isHeader)
        StatCell(entry.intLoss ?: "L", isHeader)
        StatCell(entry.intGoalDifference ?: "GD", isHeader)
        StatCell(entry.intPoints ?: "Pts", isHeader, bold = true)

        if (!isHeader && !entry.strForm.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(6.dp))
            FormIndicator(entry.strForm!!)
        } else if (isHeader) {
            Spacer(modifier = Modifier.width(40.dp))
        }
    }
}

@Composable
private fun StatCell(text: String, isHeader: Boolean, bold: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.width(28.dp),
        textAlign = TextAlign.Center,
        fontSize = 12.sp,
        fontWeight = if (bold || isHeader) FontWeight.Bold else FontWeight.Normal,
        color = if (isHeader) MaterialTheme.colorScheme.onSurfaceVariant
        else MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun FormIndicator(form: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        form.takeLast(5).forEach { c ->
            val color = when (c.uppercaseChar()) {
                'W' -> FormWin
                'D' -> FormDraw
                'L' -> FormLoss
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = c.toString(),
                    fontSize = 8.sp,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
