package com.example.footballapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.footballapp.data.model.Match
import com.example.footballapp.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: SportsRepository = SportsRepository()
) : ViewModel() {

    private val matchId: String = savedStateHandle.get<String>("matchId") ?: ""

    private val _match = MutableStateFlow<Match?>(repository.getMatchById(matchId))
    val match: StateFlow<Match?> = _match.asStateFlow()

    fun toggleFavorite() {
        repository.toggleFavorite(matchId)
        _match.value = repository.getMatchById(matchId)
    }
}
