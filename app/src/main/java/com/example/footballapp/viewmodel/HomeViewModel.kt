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

            val result = repository.getMatchesGroupedByDate(
                competitionCode = leagueId,
                pastDays = 7,
                futureDays = 21
            )

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                matchesByDate = result.getOrDefault(emptyMap()),
                error = if (result.isFailure || result.getOrDefault(emptyMap()).isEmpty()) {
                    result.exceptionOrNull()?.message
                        ?: "Nincs meccs. Ellenőrizd az API tokent az ApiClient.kt-ben!"
                } else null
            )
        }
    }

    fun selectLeague(leagueId: String) {
        loadMatches(leagueId)
    }
}
