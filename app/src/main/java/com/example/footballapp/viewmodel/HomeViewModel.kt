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
    val matchesByDate: Map<String, List<Event>> = emptyMap(),
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
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedLeagueId = leagueId
            )

            // 1. Napi teljes lista (naptár nézet) – ez a fő forrás
            val dailyResult = repository.getEventsForDays(
                days = 21,
                leagueId = leagueId,
                includePastDays = 5
            )

            // 2. Fallback a régi next/past endpointokra
            val nextResult = repository.getNextLeagueEvents(leagueId)
            val pastResult = repository.getPastLeagueEvents(leagueId)

            val matchesByDate = dailyResult.getOrDefault(emptyMap())
            val next = nextResult.getOrDefault(emptyList())
            val past = pastResult.getOrDefault(emptyList())

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                matchesByDate = matchesByDate,
                nextMatches = next,
                recentMatches = past,
                error = if (matchesByDate.isEmpty() && next.isEmpty() && past.isEmpty()) {
                    dailyResult.exceptionOrNull()?.message
                        ?: nextResult.exceptionOrNull()?.message
                        ?: pastResult.exceptionOrNull()?.message
                        ?: "Nincs elérhető meccs (ingyenes API limit)"
                } else null
            )
        }
    }

    fun selectLeague(leagueId: String) {
        loadMatches(leagueId)
    }
}
