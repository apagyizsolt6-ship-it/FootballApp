package com.example.footballapp.data.repository

import com.example.footballapp.data.model.Match
import com.example.footballapp.data.model.MatchStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class SportsRepository {

    private val today = LocalDate.now().toString()
    private val yesterday = LocalDate.now().minusDays(1).toString()
    private val tomorrow = LocalDate.now().plusDays(1).toString()

    private val _matches = MutableStateFlow(
        listOf(
            Match(
                id = "1",
                league = "Premier League",
                homeTeam = "Arsenal FC",
                awayTeam = "Chelsea FC",
                homeScore = 2,
                awayScore = 1,
                date = today,
                time = "15:00",
                status = MatchStatus.FINISHED,
                isFavorite = true,
                homePossession = 54,
                awayPossession = 46,
                shotsOnTargetHome = 7,
                shotsOnTargetAway = 4,
                venue = "Emirates Stadium"
            ),
            Match(
                id = "2",
                league = "Premier League",
                homeTeam = "Manchester City FC",
                awayTeam = "Liverpool FC",
                homeScore = null,
                awayScore = null,
                date = today,
                time = "17:30",
                status = MatchStatus.LIVE,
                isFavorite = false,
                homePossession = 60,
                awayPossession = 40,
                shotsOnTargetHome = 5,
                shotsOnTargetAway = 3,
                venue = "Etihad Stadium"
            ),
            Match(
                id = "3",
                league = "Premier League",
                homeTeam = "Manchester United FC",
                awayTeam = "Tottenham Hotspur",
                homeScore = null,
                awayScore = null,
                date = tomorrow,
                time = "20:00",
                status = MatchStatus.UPCOMING,
                isFavorite = false,
                homePossession = 50,
                awayPossession = 50,
                shotsOnTargetHome = 0,
                shotsOnTargetAway = 0,
                venue = "Old Trafford"
            ),
            Match(
                id = "4",
                league = "La Liga",
                homeTeam = "Real Madrid",
                awayTeam = "FC Barcelona",
                homeScore = 3,
                awayScore = 2,
                date = yesterday,
                time = "21:00",
                status = MatchStatus.FINISHED,
                isFavorite = true,
                homePossession = 48,
                awayPossession = 52,
                shotsOnTargetHome = 8,
                shotsOnTargetAway = 7,
                venue = "Santiago Bernabéu"
            ),
            Match(
                id = "5",
                league = "Serie A",
                homeTeam = "Inter Milan",
                awayTeam = "AC Milan",
                homeScore = 1,
                awayScore = 1,
                date = today,
                time = "18:00",
                status = MatchStatus.UPCOMING,
                isFavorite = false,
                venue = "San Siro"
            )
        )
    )
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    fun toggleFavorite(matchId: String) {
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) match.copy(isFavorite = !match.isFavorite) else match
        }
    }

    fun getMatchById(matchId: String): Match? {
        return _matches.value.find { it.id == matchId }
    }
}
