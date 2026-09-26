package com.example.footballapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballapp.data.model.Event
import com.example.footballapp.data.model.Team
import com.example.footballapp.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TeamDetailUiState(
    val isLoading: Boolean = false,
    val team: Team? = null,
    val nextEvents: List<Event> = emptyList(),
    val lastEvents: List<Event> = emptyList(),
    val error: String? = null
)

class TeamDetailViewModel(
    private val repository: SportsRepository = SportsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamDetailUiState())
    val uiState: StateFlow<TeamDetailUiState> = _uiState.asStateFlow()

    fun loadTeam(teamId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val teamResult = repository.getTeam(teamId)
            val nextResult = repository.getNextEvents(teamId)
            val lastResult = repository.getLastEvents(teamId)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                team = teamResult.getOrNull(),
                nextEvents = nextResult.getOrDefault(emptyList()),
                lastEvents = lastResult.getOrDefault(emptyList()),
                error = teamResult.exceptionOrNull()?.message
            )
        }
    }
}
