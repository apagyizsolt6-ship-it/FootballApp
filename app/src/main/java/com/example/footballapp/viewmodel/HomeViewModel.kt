package com.example.footballapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballapp.data.model.MatchFilter
import com.example.footballapp.data.model.MatchStatus
import com.example.footballapp.data.repository.SportsRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class HomeViewModel(
    private val repository: SportsRepository = SportsRepository()
) : ViewModel() {

    private val _selectedLeague = MutableStateFlow("Premier League")
    val selectedLeague = _selectedLeague.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now().toString())
    val selectedDate = _selectedDate.asStateFlow()

    private val _selectedFilter = MutableStateFlow(MatchFilter.ALL)
    val selectedFilter = _selectedFilter.asStateFlow()

    val matches = combine(
        repository.matches,
        _selectedLeague,
        _selectedDate,
        _selectedFilter
    ) { allMatches, league, date, filter ->
        allMatches.filter { match ->
            val matchesLeague = match.league.equals(league, ignoreCase = true)
            val matchesDate = match.date == date
            val matchesFilter = when (filter) {
                MatchFilter.ALL -> true
                MatchFilter.LIVE -> match.status == MatchStatus.LIVE
                MatchFilter.FINISHED -> match.status == MatchStatus.FINISHED
                MatchFilter.UPCOMING -> match.status == MatchStatus.UPCOMING
            }
            matchesLeague && matchesDate && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectLeague(league: String) {
        _selectedLeague.value = league
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun setFilter(filter: MatchFilter) {
        _selectedFilter.value = filter
    }

    fun toggleFavorite(matchId: String) {
        repository.toggleFavorite(matchId)
    }
}
