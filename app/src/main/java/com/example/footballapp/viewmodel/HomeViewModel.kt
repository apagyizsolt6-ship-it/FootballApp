package com.example.footballapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballapp.data.model.Event
import com.example.footballapp.data.prefs.AppPreferences
import com.example.footballapp.data.repository.SportsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    val error: String? = null,
    val lastRefreshTime: String? = null,
    val showOnlyFavorites: Boolean = false,
    val selectedLeagueCodes: Set<String> = emptySet()
)

class HomeViewModel(
    private val repository: SportsRepository = SportsRepository(),
    private val prefs: AppPreferences? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val allLeagues = AppPreferences.ALL_LEAGUES

    init {
        val dates = buildDateList(pastDays = 3, futureDays = 12)
        val today = dateFormat.format(Calendar.getInstance().time)
        val initial = if (dates.contains(today)) today else dates.firstOrNull() ?: today
        _uiState.value = _uiState.value.copy(availableDates = dates, selectedDate = initial)
        viewModelScope.launch {
            val selected = prefs?.selectedLeagueCodes?.first() ?: AppPreferences.DEFAULT_LEAGUES
            val onlyFav = prefs?.showOnlyFavorites?.first() ?: false
            _uiState.value = _uiState.value.copy(
                selectedLeagueCodes = selected,
                showOnlyFavorites = onlyFav
            )
            loadDay(initial)
        }
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

    fun setShowOnlyFavorites(enabled: Boolean) {
        viewModelScope.launch {
            prefs?.setShowOnlyFavorites(enabled)
            _uiState.value = _uiState.value.copy(showOnlyFavorites = enabled)
            loadDay(_uiState.value.selectedDate)
        }
    }

    fun toggleLeagueFilter(code: String) {
        viewModelScope.launch {
            prefs?.toggleLeague(code)
            val selected = prefs?.selectedLeagueCodes?.first() ?: AppPreferences.DEFAULT_LEAGUES
            _uiState.value = _uiState.value.copy(selectedLeagueCodes = selected)
            loadDay(_uiState.value.selectedDate)
        }
    }

    private fun loadDay(date: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = !forceRefresh && _uiState.value.leaguesForDay.isEmpty(),
                isRefreshing = forceRefresh,
                error = null
            )
            try {
                val selectedCodes = _uiState.value.selectedLeagueCodes.ifEmpty {
                    AppPreferences.DEFAULT_LEAGUES
                }
                val leaguesToLoad = allLeagues.filter { it.first in selectedCodes }

                val favorites = prefs?.favoriteTeamIds?.first() ?: emptySet()
                val onlyFav = _uiState.value.showOnlyFavorites

                val results = leaguesToLoad.map { (code, name) ->
                    async {
                        val result = repository.getMatchesForDate(code, date, forceRefresh)
                        var matches = result.getOrDefault(emptyList())
                        if (onlyFav && favorites.isNotEmpty()) {
                            matches = matches.filter {
                                it.idHomeTeam in favorites || it.idAwayTeam in favorites
                            }
                        }
                        LeagueMatches(code, name, matches)
                    }
                }.awaitAll()

                val withMatches = results.filter { it.matches.isNotEmpty() }
                val now = timeFormat.format(Calendar.getInstance().time)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    leaguesForDay = withMatches,
                    lastRefreshTime = now,
                    error = when {
                        withMatches.isEmpty() && onlyFav -> "Nincs kedvenc csapat meccse ezen a napon"
                        withMatches.isEmpty() -> "Nincs meccs ezen a napon a kiválasztott ligákban"
                        else -> null
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = e.message ?: "Hiba a betöltés során (ellenőrizd a netet)"
                )
            }
        }
    }
}
