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
    val selectedLeagueCodes: Set<String> = AppPreferences.DEFAULT_LEAGUES,
    val favoriteTeamIds: Set<String> = emptySet(),
    val hasLiveMatches: Boolean = false,
    val isOffline: Boolean = false
)

class HomeViewModel(
    private val repository: SportsRepository = SportsRepository()
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
        loadDay(initial)
    }

    private fun buildDateList(pastDays: Int, futureDays: Int): List<String> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -pastDays)
        return List(pastDays + futureDays) {
            dateFormat.format(cal.time).also { cal.add(Calendar.DAY_OF_YEAR, 1) }
        }
    }

    /** Hívandó a Composable-ből, amikor a prefs értékek megérkeznek */
    fun syncFromPrefs(
        selectedLeagues: Set<String>,
        showOnlyFavorites: Boolean,
        favoriteIds: Set<String>
    ) {
        val changed = selectedLeagues != _uiState.value.selectedLeagueCodes ||
                showOnlyFavorites != _uiState.value.showOnlyFavorites ||
                favoriteIds != _uiState.value.favoriteTeamIds
        _uiState.value = _uiState.value.copy(
            selectedLeagueCodes = selectedLeagues.ifEmpty { AppPreferences.DEFAULT_LEAGUES },
            showOnlyFavorites = showOnlyFavorites,
            favoriteTeamIds = favoriteIds
        )
        if (changed) {
            loadDay(_uiState.value.selectedDate)
        }
    }

    fun selectDate(date: String) {
        if (date == _uiState.value.selectedDate) return
        _uiState.value = _uiState.value.copy(selectedDate = date)
        loadDay(date)
    }

    fun goToToday() {
        val today = dateFormat.format(Calendar.getInstance().time)
        if (today !in _uiState.value.availableDates) {
            val dates = buildDateList(pastDays = 3, futureDays = 12)
            _uiState.value = _uiState.value.copy(availableDates = dates)
        }
        selectDate(today)
    }

    fun setOffline(offline: Boolean) {
        _uiState.value = _uiState.value.copy(isOffline = offline)
    }

    fun refresh() {
        loadDay(_uiState.value.selectedDate, forceRefresh = true)
    }

    fun setShowOnlyFavoritesLocal(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(showOnlyFavorites = enabled)
        loadDay(_uiState.value.selectedDate)
    }

    fun toggleLeagueFilterLocal(code: String) {
        val current = _uiState.value.selectedLeagueCodes.toMutableSet()
        if (current.contains(code)) {
            if (current.size > 1) current.remove(code)
        } else {
            current.add(code)
        }
        _uiState.value = _uiState.value.copy(selectedLeagueCodes = current)
        loadDay(_uiState.value.selectedDate)
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
                val favorites = _uiState.value.favoriteTeamIds
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
                val live = withMatches.any { block ->
                    block.matches.any { it.strStatus == "LIVE" }
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    leaguesForDay = withMatches,
                    lastRefreshTime = now,
                    hasLiveMatches = live,
                    isOffline = false,
                    error = when {
                        withMatches.isEmpty() && onlyFav -> "Nincs kedvenc csapat meccse ezen a napon"
                        withMatches.isEmpty() -> "Nincs meccs ezen a napon a kiválasztott ligákban"
                        else -> null
                    }
                )
            } catch (e: Exception) {
                val msg = e.message ?: "Hiba a betöltés során (ellenőrizd a netet)"
                val offline = "internet" in msg.lowercase() || "Unable to resolve" in msg || "UnknownHost" in msg
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isOffline = offline,
                    error = msg
                )
            }
        }
    }
}
