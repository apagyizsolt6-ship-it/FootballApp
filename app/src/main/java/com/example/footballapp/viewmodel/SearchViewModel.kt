package com.example.footballapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballapp.data.model.Team
import com.example.footballapp.data.repository.SportsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val isLoading: Boolean = false,
    val teams: List<Team> = emptyList(),
    val favoriteTeams: List<Team> = emptyList(),
    val query: String = "",
    val error: String? = null
)

class SearchViewModel(
    private val repository: SportsRepository = SportsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(teams = emptyList(), error = null)
        }
    }

    fun search() {
        val query = _uiState.value.query.trim()
        if (query.length < 2) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.searchTeams(query)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                teams = result.getOrDefault(emptyList()),
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun loadFavorites(favoriteIds: Set<String>) {
        if (favoriteIds.isEmpty()) {
            _uiState.value = _uiState.value.copy(favoriteTeams = emptyList())
            return
        }
        viewModelScope.launch {
            val teams = favoriteIds.map { id ->
                async {
                    repository.getTeam(id).getOrNull()
                }
            }.awaitAll().filterNotNull()
            _uiState.value = _uiState.value.copy(favoriteTeams = teams)
        }
    }
}
