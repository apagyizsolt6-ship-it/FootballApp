package com.example.footballapp.data.model

data class Match(
    val id: String,
    val league: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val date: String, // "YYYY-MM-DD"
    val time: String, // "HH:mm"
    val status: MatchStatus, // LIVE, FINISHED, UPCOMING
    val isFavorite: Boolean = false,
    val homePossession: Int = 50,
    val awayPossession: Int = 50,
    val shotsOnTargetHome: Int = 0,
    val shotsOnTargetAway: Int = 0,
    val venue: String = "Stadion"
)

enum class MatchStatus {
    LIVE, FINISHED, UPCOMING
}

enum class MatchFilter {
    ALL, LIVE, FINISHED, UPCOMING
}

data class Team(
    val id: String,
    val name: String,
    val league: String,
    val matchesPlayed: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val points: Int,
    val logoUrl: String = ""
)
