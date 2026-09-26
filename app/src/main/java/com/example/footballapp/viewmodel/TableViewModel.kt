package com.example.footballapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballapp.data.model.TableEntry
import com.example.footballapp.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TableUiState(
    val isLoading: Boolean = false,
    val table: List<TableEntry> = emptyList(),
    val selectedLeagueId: String = SportsRepository.PREMIER_LEAGUE,
    val error: String? = null
)

class TableViewModel(
    private val repository: SportsRepository = SportsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TableUiState())
    val uiState: StateFlow<TableUiState> = _uiState.asStateFlow()

    init {
        loadTable()
    }

    fun loadTable(leagueId: String = _uiState.value.selectedLeagueId) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, selectedLeagueId = leagueId)

            val result = repository.getLeagueTable(leagueId)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                table = result.getOrDefault(emptyList()),
                error = result.exceptionOrNull()?.message
            )
        }
    }
}
