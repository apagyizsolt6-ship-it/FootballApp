package com.example.footballapp.data.repository

import com.example.footballapp.data.api.ApiClient
import com.example.footballapp.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SportsRepository {

    private val api = ApiClient.api

    companion object {
        // football-data.org competition codes
        const val PREMIER_LEAGUE = "PL"
        const val LA_LIGA = "PD"
        const val SERIE_A = "SA"
        const val BUNDESLIGA = "BL1"
        const val LIGUE_1 = "FL1"
        const val CHAMPIONS_LEAGUE = "CL"
    }

    /**
     * Naptár nézet: meccsek dátum szerint csoportosítva.
     * dateFrom = ma - 7 nap, dateTo = ma + 21 nap
     */
    suspend fun getMatchesGroupedByDate(
        competitionCode: String,
        pastDays: Int = 7,
        futureDays: Int = 21
    ): Result<Map<String, List<Event>>> = withContext(Dispatchers.IO) {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val calendar = Calendar.getInstance()

            calendar.add(Calendar.DAY_OF_YEAR, -pastDays)
            val dateFrom = dateFormat.format(calendar.time)

            calendar.add(Calendar.DAY_OF_YEAR, pastDays + futureDays)
            val dateTo = dateFormat.format(calendar.time)

            val response = api.getCompetitionMatches(
                competitionCode = competitionCode,
                dateFrom = dateFrom,
                dateTo = dateTo
            )

            val events = response.matches
                ?.map { it.toEvent() }
                ?: emptyList()

            val grouped = events
                .filter { !it.dateEvent.isNullOrBlank() }
                .groupBy { it.dateEvent!! }
                .mapValues { (_, list) ->
                    list.sortedBy { it.strTime ?: "99:99" }
                }
                .toSortedMap()

            Result.success(grouped)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLeagueTable(competitionCode: String = PREMIER_LEAGUE): Result<List<TableEntry>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getStandings(competitionCode)
                val totalTable = response.standings
                    ?.firstOrNull { it.type == "TOTAL" }
                    ?.table
                    ?: response.standings?.firstOrNull()?.table
                    ?: emptyList()

                Result.success(totalTable.map { it.toTableEntry() })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getTeamsInLeague(competitionCode: String = PREMIER_LEAGUE): Result<List<Team>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getTeams(competitionCode)
                Result.success(response.teams?.map { it.toTeam() } ?: emptyList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getTeam(teamId: String): Result<Team?> = withContext(Dispatchers.IO) {
        try {
            val id = teamId.toIntOrNull() ?: return@withContext Result.success(null)
            val team = api.getTeam(id)
            Result.success(team.toTeam())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTeamMatches(teamId: String, status: String? = null): Result<List<Event>> =
        withContext(Dispatchers.IO) {
            try {
                val id = teamId.toIntOrNull() ?: return@withContext Result.success(emptyList())
                val response = api.getTeamMatches(id, status = status, limit = 20)
                Result.success(response.matches?.map { it.toEvent() } ?: emptyList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Keresés – football-data.org-on nincs szabad szöveges keresés,
    // ezért a liga csapataiból szűrünk
    suspend fun searchTeams(query: String): Result<List<Team>> = withContext(Dispatchers.IO) {
        try {
            val all = mutableListOf<Team>()
            listOf(PREMIER_LEAGUE, LA_LIGA, SERIE_A, BUNDESLIGA, LIGUE_1).forEach { code ->
                try {
                    val teams = api.getTeams(code).teams?.map { it.toTeam() } ?: emptyList()
                    all.addAll(teams)
                } catch (_: Exception) {}
            }
            val filtered = all
                .distinctBy { it.idTeam }
                .filter { it.strTeam?.contains(query, ignoreCase = true) == true }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Régi kompatibilitás (ha valahol még hívják)
    suspend fun getNextLeagueEvents(leagueId: String): Result<List<Event>> =
        getMatchesGroupedByDate(leagueId, pastDays = 0, futureDays = 14)
            .map { it.values.flatten() }

    suspend fun getPastLeagueEvents(leagueId: String): Result<List<Event>> =
        getMatchesGroupedByDate(leagueId, pastDays = 14, futureDays = 0)
            .map { it.values.flatten() }
}
