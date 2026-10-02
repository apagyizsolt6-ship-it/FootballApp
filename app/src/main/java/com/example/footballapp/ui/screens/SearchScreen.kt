package com.example.footballapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.footballapp.ui.components.TeamCard
import com.example.footballapp.ui.theme.TextPrimary
import com.example.footballapp.ui.theme.TextSecondary
import com.example.footballapp.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    onTeamClick: (String) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Keresés",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = uiState.query,
            onValueChange = { viewModel.onQueryChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Csapat keresése...") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.search()
                    focusManager.clearFocus()
                }) {
                    Icon(Icons.Default.Search, contentDescription = "Keresés")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.search()
                focusManager.clearFocus()
            })
        )

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.error != null -> {
                Text(
                    text = "Hiba: ${uiState.error}",
                    color = TextSecondary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            uiState.teams.isEmpty() && uiState.query.isNotBlank() -> {
                Text(
                    text = "Nincs találat",
                    color = TextSecondary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.teams, key = { it.idTeam ?: it.hashCode().toString() }) { team ->
                        TeamCard(
                            team = team,
                            onClick = { team.idTeam?.let { onTeamClick(it) } }
                        )
                    }
                }
            }
        }
    }
}
