package com.example.footballapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.footballapp.data.prefs.AppPreferences
import com.example.footballapp.ui.theme.PrimaryGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    prefs: AppPreferences,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val notificationsEnabled by prefs.notificationsEnabled.collectAsState(initial = true)
    val showOnlyFavorites by prefs.showOnlyFavorites.collectAsState(initial = false)
    val selectedLeagues by prefs.selectedLeagueCodes.collectAsState(initial = AppPreferences.DEFAULT_LEAGUES)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beállítások", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Vissza")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Megjelenés",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sötét téma")
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { onToggleTheme() },
                    colors = SwitchDefaults.colors(checkedTrackColor = PrimaryGreen)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Értesítések",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Meccs emlékeztetők")
                    Text(
                        text = "Kedvenc csapatok mai meccseiről",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { enabled ->
                        scope.launch { prefs.setNotificationsEnabled(enabled) }
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = PrimaryGreen)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Meccsek",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Csak kedvencek")
                    Text(
                        text = "Csak a kedvenc csapatok meccseit mutassa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = showOnlyFavorites,
                    onCheckedChange = { enabled ->
                        scope.launch { prefs.setShowOnlyFavorites(enabled) }
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = PrimaryGreen)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Aktív ligák",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryGreen
            )
            Text(
                text = "Legalább egy liga legyen bekapcsolva",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            AppPreferences.ALL_LEAGUES.forEach { (code, name) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(name)
                    Switch(
                        checked = code in selectedLeagues,
                        onCheckedChange = {
                            scope.launch { prefs.toggleLeague(code) }
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = PrimaryGreen)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Football App v2.0\nAdatok: football-data.org",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
