package com.example.footballapp.data.model

import com.google.gson.annotations.SerializedName

// Bajnokság konstansok a TableScreen számára
object Leagues {
    const val PREMIER_LEAGUE = "English Premier League"
    const val LA_LIGA = "Spanish La Liga"
    const val SERIE_A = "Italian Serie A"
    const val BUNDESLIGA = "German Bundesliga"
    const val LIGUE_1 = "French Ligue 1"
}

// Eredeti API Válasz modellek a SportsApi-hoz
data class MatchesResponse(
    @SerializedName("matches") val matches: List<ApiMatch>? = null,
    @SerializedName("events") val events: List<ApiMatch>? = null
)

data class StandingsResponse(
    @SerializedName("standings") val standings: List<TableEntry>? = null,
    @SerializedName("table") val table: List<TableEntry>? = null
)

data class TeamsResponse(
    @SerializedName("teams") val teams: List<Team>? = null
)

data class FdTeam(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("crest") val crest: String? = null
)

data class TableEntry(
    @SerializedName("position") val position: Int? = null,
    @SerializedName("team") val team: FdTeam? = null,
    @SerializedName("playedGames") val playedGames: Int? = null,
    @SerializedName("won") val won: Int? = null,
    @SerializedName("draw") val draw: Int? = null,
    @SerializedName("lost") val lost: Int? = null,
    @SerializedName("goalsFor") val goalsFor: Int? = null,
    @SerializedName("goalsAgainst") val goalsAgainst: Int? = null,
    @SerializedName("goalDifference") val goalDifference: Int? = null,
    @SerializedName("points") val points: Int? = null,
    @SerializedName("intRank") val intRank: String? = null,
    @SerializedName("strTeam") val strTeam: String? = null,
    @SerializedName("strTeamBadge") val strTeamBadge: String? = null,
    @SerializedName("strBadge") val strBadge: String? = null,
    @SerializedName("played") val played: String? = null,
    @SerializedName("win") val win: String? = null,
    @SerializedName("draws") val draws: String? = null,
    @SerializedName("loss") val loss: String? = null,
    @SerializedName("goalsfor") val goalsfor: String? = null,
    @SerializedName("goalsagainst") val goalsagainst: String? = null,
    @SerializedName("total") val total: String? = null
)

data class Team(
    @SerializedName("idTeam") val idTeam: String? = null,
    @SerializedName("strTeam") val strTeam: String? = null,
    @SerializedName("strTeamBadge") val strTeamBadge: String? = null,
    @SerializedName("strCountry") val strCountry: String? = null,
    @SerializedName("strDescriptionEN") val strDescriptionEN: String? = null
)

data class ApiMatch(
    @SerializedName("id") val id: Any? = null,
    @SerializedName("utcDate") val utcDate: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("homeTeam") val homeTeam: FdTeam? = null,
    @SerializedName("awayTeam") val awayTeam: FdTeam? = null,
    @SerializedName("score") val score: ApiScore? = null,
    @SerializedName("idEvent") val idEvent: String? = null,
    @SerializedName("strEvent") val strEvent: String? = null,
    @SerializedName("dateEvent") val dateEvent: String? = null,
    @SerializedName("strTime") val strTime: String? = null,
    @SerializedName("strHomeTeam") val strHomeTeam: String? = null,
    @SerializedName("strAwayTeam") val strAwayTeam: String? = null,
    @SerializedName("intHomeScore") val intHomeScore: String? = null,
    @SerializedName("intAwayScore") val intAwayScore: String? = null
)

data class ApiScore(
    @SerializedName("fullTime") val fullTime: ApiScoreDetail? = null
)

data class ApiScoreDetail(
    @SerializedName("home") val home: Int? = null,
    @SerializedName("away") val away: Int? = null
)

// UI modellek a naptárhoz és részletekhez
data class Match(
    val id: String,
    val league: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val date: String,
    val time: String,
    val status: MatchStatus,
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
