package com.example.footballapp.data.model

import com.google.gson.annotations.SerializedName

// ========== Matches ==========
data class MatchesResponse(
    @SerializedName("matches") val matches: List<FdMatch>? = null,
    @SerializedName("resultSet") val resultSet: ResultSet? = null
)

data class ResultSet(
    @SerializedName("count") val count: Int? = null,
    @SerializedName("first") val first: String? = null,
    @SerializedName("last") val last: String? = null
)

data class FdMatch(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("utcDate") val utcDate: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("matchday") val matchday: Int? = null,
    @SerializedName("stage") val stage: String? = null,
    @SerializedName("group") val group: String? = null,
    @SerializedName("lastUpdated") val lastUpdated: String? = null,
    @SerializedName("homeTeam") val homeTeam: FdTeamRef? = null,
    @SerializedName("awayTeam") val awayTeam: FdTeamRef? = null,
    @SerializedName("score") val score: FdScore? = null,
    @SerializedName("competition") val competition: FdCompetition? = null
)

data class FdTeamRef(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("shortName") val shortName: String? = null,
    @SerializedName("tla") val tla: String? = null,
    @SerializedName("crest") val crest: String? = null
)

data class FdScore(
    @SerializedName("winner") val winner: String? = null,
    @SerializedName("duration") val duration: String? = null,
    @SerializedName("fullTime") val fullTime: FdScoreDetail? = null,
    @SerializedName("halfTime") val halfTime: FdScoreDetail? = null
)

data class FdScoreDetail(
    @SerializedName("home") val home: Int? = null,
    @SerializedName("away") val away: Int? = null
)

data class FdCompetition(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("emblem") val emblem: String? = null
)

// ========== Standings ==========
data class StandingsResponse(
    @SerializedName("standings") val standings: List<StandingGroup>? = null,
    @SerializedName("competition") val competition: FdCompetition? = null
)

data class StandingGroup(
    @SerializedName("stage") val stage: String? = null,
    @SerializedName("type") val type: String? = null, // TOTAL, HOME, AWAY
    @SerializedName("group") val group: String? = null, // GROUP_A, GROUP_B stb.
    @SerializedName("table") val table: List<FdTableEntry>? = null
)

data class FdTableEntry(
    @SerializedName("position") val position: Int? = null,
    @SerializedName("team") val team: FdTeamRef? = null,
    @SerializedName("playedGames") val playedGames: Int? = null,
    @SerializedName("form") val form: String? = null,
    @SerializedName("won") val won: Int? = null,
    @SerializedName("draw") val draw: Int? = null,
    @SerializedName("lost") val lost: Int? = null,
    @SerializedName("points") val points: Int? = null,
    @SerializedName("goalsFor") val goalsFor: Int? = null,
    @SerializedName("goalsAgainst") val goalsAgainst: Int? = null,
    @SerializedName("goalDifference") val goalDifference: Int? = null
)

// ========== Teams ==========
data class TeamsResponse(
    @SerializedName("teams") val teams: List<FdTeam>? = null
)

data class FdTeam(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("shortName") val shortName: String? = null,
    @SerializedName("tla") val tla: String? = null,
    @SerializedName("crest") val crest: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("website") val website: String? = null,
    @SerializedName("founded") val founded: Int? = null,
    @SerializedName("clubColors") val clubColors: String? = null,
    @SerializedName("venue") val venue: String? = null
)

// ========== Kompatibilitási réteg a régi UI-hoz ==========
// A MatchCard és a többi UI még a régi Event / TableEntry mezőket várja,
// ezért mapping függvényeket adunk.

data class Event(
    val idEvent: String? = null,
    val strEvent: String? = null,
    val strLeague: String? = null,
    val strHomeTeam: String? = null,
    val strAwayTeam: String? = null,
    val intHomeScore: String? = null,
    val intAwayScore: String? = null,
    val dateEvent: String? = null,
    val strTime: String? = null,
    val strStatus: String? = null,
    val idHomeTeam: String? = null,
    val idAwayTeam: String? = null,
    val strHomeTeamBadge: String? = null,
    val strAwayTeamBadge: String? = null
)

fun FdMatch.toEvent(): Event {
    val dateTime = utcDate ?: ""
    val date = dateTime.take(10)
    val time = if (dateTime.length >= 16) dateTime.substring(11, 16) else null

    val homeScore = score?.fullTime?.home?.toString()
    val awayScore = score?.fullTime?.away?.toString()

    val statusText = when (status) {
        "FINISHED" -> "FT"
        "IN_PLAY", "PAUSED", "LIVE" -> "LIVE"
        "SCHEDULED", "TIMED" -> "NS"
        "POSTPONED" -> "PP"
        "CANCELLED" -> "CANC"
        else -> status
    }

    return Event(
        idEvent = id?.toString(),
        strEvent = "${homeTeam?.name} vs ${awayTeam?.name}",
        strLeague = competition?.name,
        strHomeTeam = homeTeam?.name,
        strAwayTeam = awayTeam?.name,
        intHomeScore = homeScore,
        intAwayScore = awayScore,
        dateEvent = date,
        strTime = time,
        strStatus = statusText,
        idHomeTeam = homeTeam?.id?.toString(),
        idAwayTeam = awayTeam?.id?.toString(),
        strHomeTeamBadge = homeTeam?.crest,
        strAwayTeamBadge = awayTeam?.crest
    )
}

data class TableEntry(
    val intRank: String? = null,
    val strTeam: String? = null,
    val strBadge: String? = null,
    val intPlayed: String? = null,
    val intWin: String? = null,
    val intDraw: String? = null,
    val intLoss: String? = null,
    val intGoalsFor: String? = null,
    val intGoalsAgainst: String? = null,
    val intGoalDifference: String? = null,
    val intPoints: String? = null,
    val strForm: String? = null
)

fun FdTableEntry.toTableEntry(): TableEntry = TableEntry(
    intRank = position?.toString(),
    strTeam = team?.name,
    strBadge = team?.crest,
    intPlayed = playedGames?.toString(),
    intWin = won?.toString(),
    intDraw = draw?.toString(),
    intLoss = lost?.toString(),
    intGoalsFor = goalsFor?.toString(),
    intGoalsAgainst = goalsAgainst?.toString(),
    intGoalDifference = goalDifference?.toString(),
    intPoints = points?.toString(),
    strForm = form
)

// Régi kompatibilitás (keresés / csapat)
data class Team(
    val strLeague: String? = null,
    val idTeam: String? = null,
    val strTeam: String? = null,
    val strTeamBadge: String? = null,
    val strStadium: String? = null,
    val strDescriptionEN: String? = null,
    val strWebsite: String? = null,
    val strCountry: String? = null
)

fun FdTeam.toTeam(): Team = Team(
    idTeam = id?.toString(),
    strTeam = name,
    strTeamBadge = crest,
    strStadium = venue,
    strWebsite = website
)

data class TeamResponse(val teams: List<Team>? = null)
data class EventResponse(val events: List<Event>? = null)
data class TableResponse(val table: List<TableEntry>? = null)
data class PlayerResponse(val player: List<Player>? = null)
data class Player(val idPlayer: String? = null, val strPlayer: String? = null)

// ========== Scorers ==========
data class ScorersResponse(
    @SerializedName("scorers") val scorers: List<FdScorer>? = null,
    @SerializedName("competition") val competition: FdCompetition? = null
)

data class FdScorer(
    @SerializedName("player") val player: FdPlayerRef? = null,
    @SerializedName("team") val team: FdTeamRef? = null,
    @SerializedName("goals") val goals: Int? = null,
    @SerializedName("assists") val assists: Int? = null,
    @SerializedName("penalties") val penalties: Int? = null
)

data class FdPlayerRef(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName") val lastName: String? = null,
    @SerializedName("dateOfBirth") val dateOfBirth: String? = null,
    @SerializedName("nationality") val nationality: String? = null,
    @SerializedName("position") val position: String? = null,
    @SerializedName("shirtNumber") val shirtNumber: Int? = null
)

data class ScorerEntry(
    val rank: Int,
    val playerName: String,
    val teamName: String?,
    val teamBadge: String?,
    val goals: Int,
    val assists: Int?,
    val penalties: Int?
)

fun FdScorer.toScorerEntry(rank: Int): ScorerEntry = ScorerEntry(
    rank = rank,
    playerName = player?.name ?: listOfNotNull(player?.firstName, player?.lastName).joinToString(" ").ifBlank { "Ismeretlen" },
    teamName = team?.name,
    teamBadge = team?.crest,
    goals = goals ?: 0,
    assists = assists,
    penalties = penalties
)

// Group standings support
data class GroupedTable(
    val groupName: String,
    val entries: List<TableEntry>
)

