package com.example.footballapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballapp.data.model.GroupedTable
import com.example.footballapp.data.model.ScorerEntry
import com.example.footballapp.data.model.TableEntry
import com.example.footballapp.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TableUiState(
    val isLoading: Boolean = false,
    val table: List<TableEntry> = emptyList(),
    val groupedTables: List<GroupedTable> = emptyList(),
    val scorers: List<ScorerEntry> = emptyList(),
    val selectedLeagueId: String = SportsRepository.PREMIER_LEAGUE,
    val showScorers: Boolean = false,
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
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedLeagueId = leagueId,
                showScorers = false
            )

            val grouped = repository.getGroupedTable(leagueId)
            val flat = repository.getLeagueTable(leagueId)

            if (grouped.isSuccess && !grouped.getOrNull().isNullOrEmpty()) {
                val groups = grouped.getOrDefault(emptyList())
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    groupedTables = groups,
                    table = if (groups.size == 1) groups.first().entries else emptyList(),
                    error = null
                )
            } else if (flat.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    table = flat.getOrDefault(emptyList()),
                    groupedTables = emptyList(),
                    error = if (flat.getOrDefault(emptyList()).isEmpty()) "Nincs elérhető tabella" else null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    table = emptyList(),
                    groupedTables = emptyList(),
                    error = flat.exceptionOrNull()?.message
                        ?: "Hiba (az ingyenes kulcs korlátozott lehet)"
                )
            }
        }
    }

    fun loadScorers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, showScorers = true, error = null)
            val result = repository.getScorers(_uiState.value.selectedLeagueId)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                scorers = result.getOrDefault(emptyList()),
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun showTable() {
        _uiState.value = _uiState.value.copy(showScorers = false)
    }
}
