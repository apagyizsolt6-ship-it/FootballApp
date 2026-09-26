package com.example.footballapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballapp.data.model.Event
import com.example.footballapp.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val nextMatches: List<Event> = emptyList(),
    val recentMatches: List<Event> = emptyList(),
    val error: String? = null,
    val selectedLeagueId: String = SportsRepository.PREMIER_LEAGUE
)

class HomeViewModel(
    private val repository: SportsRepository = SportsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMatches()
    }

    fun loadMatches(leagueId: String = _uiState.value.selectedLeagueId) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, selectedLeagueId = leagueId)

            val nextResult = repository.getNextLeagueEvents(leagueId)
            val pastResult = repository.getPastLeagueEvents(leagueId)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                nextMatches = nextResult.getOrDefault(emptyList()),
                recentMatches = pastResult.getOrDefault(emptyList()),
                error = nextResult.exceptionOrNull()?.message ?: pastResult.exceptionOrNull()?.message
            )
        }
    }

    fun selectLeague(leagueId: String) {
        loadMatches(leagueId)
    }
}
