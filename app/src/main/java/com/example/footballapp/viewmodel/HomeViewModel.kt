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
    val selectedDate: String = "",
    val availableDates: List<String> = emptyList(),
    /** Kiválasztott nap meccsei ligánként, egymás alatt */
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
        val dates = buildDateList(pastDays = 3, futureDays = 12) // összesen 15 nap
        val today = dateFormat.format(Calendar.getInstance().time)
        val initial = if (dates.contains(today)) today else dates.firstOrNull() ?: today
        _uiState.value = _uiState.value.copy(
            availableDates = dates,
            selectedDate = initial
        )
        loadDay(initial)
    }

    private fun buildDateList(pastDays: Int, futureDays: Int): List<String> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -pastDays)
        val list = mutableListOf<String>()
        repeat(pastDays + futureDays) {
            list.add(dateFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }

    fun selectDate(date: String) {
        if (date == _uiState.value.selectedDate) return
        _uiState.value = _uiState.value.copy(selectedDate = date)
        loadDay(date)
    }

    private fun loadDay(date: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Párhuzamosan lekérjük mind az 5 ligát erre a napra
                val results = leagues.map { (code, name) ->
                    async {
                        val result = repository.getMatchesForDate(code, date)
                        LeagueMatches(
                            leagueCode = code,
                            leagueName = name,
                            matches = result.getOrDefault(emptyList())
                        )
                    }
                }.awaitAll()

                // Csak azokat a ligákat mutatjuk, ahol van meccs
                val withMatches = results.filter { it.matches.isNotEmpty() }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    leaguesForDay = withMatches,
                    error = if (withMatches.isEmpty()) "Nincs meccs ezen a napon" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    leaguesForDay = emptyList(),
                    error = e.message ?: "Hiba a betöltéskor"
                )
            }
        }
    }
}
