package com.example.footballapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballapp.data.model.Player
import com.example.footballapp.data.model.Team
import com.example.footballapp.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val isLoading: Boolean = false,
    val teams: List<Team> = emptyList(),
    val players: List<Player> = emptyList(),
    val query: String = "",
    val searchType: SearchType = SearchType.TEAMS,
    val error: String? = null
)

enum class SearchType { TEAMS, PLAYERS }

class SearchViewModel(
    private val repository: SportsRepository = SportsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun setSearchType(type: SearchType) {
        _uiState.value = _uiState.value.copy(searchType = type, teams = emptyList(), players = emptyList())
    }

    fun search() {
        val query = _uiState.value.query.trim()
        if (query.length < 2) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (_uiState.value.searchType) {
                SearchType.TEAMS -> {
                    val result = repository.searchTeams(query)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        teams = result.getOrDefault(emptyList()),
                        error = result.exceptionOrNull()?.message
                    )
                }
                SearchType.PLAYERS -> {
                    val result = repository.searchPlayers(query)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        players = result.getOrDefault(emptyList()),
                        error = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }
}
