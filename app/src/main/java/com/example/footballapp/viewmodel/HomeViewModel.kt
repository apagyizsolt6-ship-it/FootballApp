package com.example.footballapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballapp.data.model.Event
import com.example.footballapp.data.repository.SportsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class LeagueMatches(
    val leagueCode: String,
    val leagueName: String,
    val matches: List<Event>
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedDate: String = "",
    val availableDates: List<String> = emptyList(),
    val leaguesForDay: List<LeagueMatches> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    private val repository: SportsRepository = SportsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val leagues = listOf(
        SportsRepository.PREMIER_LEAGUE to "Premier League",
        SportsRepository.LA_LIGA to "La Liga",
        SportsRepository.SERIE_A to "Serie A",
        SportsRepository.BUNDESLIGA to "Bundesliga",
        SportsRepository.LIGUE_1 to "Ligue 1"
    )

    init {
        val dates = buildDateList(pastDays = 3, futureDays = 12)
        val today = dateFormat.format(Calendar.getInstance().time)
        val initial = if (dates.contains(today)) today else dates.firstOrNull() ?: today
        _uiState.value = _uiState.value.copy(availableDates = dates, selectedDate = initial)
        loadDay(initial)
    }

    private fun buildDateList(pastDays: Int, futureDays: Int): List<String> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -pastDays)
        return List(pastDays + futureDays) {
            dateFormat.format(cal.time).also { cal.add(Calendar.DAY_OF_YEAR, 1) }
        }
    }

    fun selectDate(date: String) {
        if (date == _uiState.value.selectedDate) return
        _uiState.value = _uiState.value.copy(selectedDate = date)
        loadDay(date)
    }

    fun refresh() {
        loadDay(_uiState.value.selectedDate, forceRefresh = true)
    }

    private fun loadDay(date: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = !forceRefresh && _uiState.value.leaguesForDay.isEmpty(),
                isRefreshing = forceRefresh,
                error = null
            )
            try {
                val results = leagues.map { (code, name) ->
                    async {
                        val result = repository.getMatchesForDate(code, date, forceRefresh)
                        LeagueMatches(code, name, result.getOrDefault(emptyList()))
                    }
                }.awaitAll()
                val withMatches = results.filter { it.matches.isNotEmpty() }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    leaguesForDay = withMatches,
                    error = if (withMatches.isEmpty()) "Nincs meccs ezen a napon" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = e.message ?: "Hiba"
                )
            }
        }
    }
}
