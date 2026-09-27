package com.example.footballapp.data.repository

import com.example.footballapp.data.api.ApiClient
import com.example.footballapp.data.cache.MatchCache
import com.example.footballapp.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SportsRepository {

    private val api = ApiClient.api

    companion object {
        const val PREMIER_LEAGUE = "PL"
        const val LA_LIGA = "PD"
        const val SERIE_A = "SA"
        const val BUNDESLIGA = "BL1"
        const val LIGUE_1 = "FL1"
        const val CHAMPIONS_LEAGUE = "CL"
    }

    suspend fun getMatchesForDate(
        competitionCode: String,
        date: String,
        forceRefresh: Boolean = false
    ): Result<List<Event>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            MatchCache.get(competitionCode, date)?.let {
                return@withContext Result.success(it)
            }
        }
        try {
            val response = api.getCompetitionMatches(
                competitionCode = competitionCode,
                dateFrom = date,
                dateTo = date
            )
            val events = response.matches
                ?.map { it.toEvent() }
                ?.sortedBy { it.strTime ?: "99:99" }
                ?: emptyList()
            MatchCache.put(competitionCode, date, events)
            Result.success(events)
        } catch (e: Exception) {
            // Offline fallback
            MatchCache.get(competitionCode, date)?.let {
                return@withContext Result.success(it)
            }
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
                Result.success(api.getTeams(competitionCode).teams?.map { it.toTeam() } ?: emptyList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getTeam(teamId: String): Result<Team?> = withContext(Dispatchers.IO) {
        try {
            val id = teamId.toIntOrNull() ?: return@withContext Result.success(null)
            Result.success(api.getTeam(id).toTeam())
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

    suspend fun searchTeams(query: String): Result<List<Team>> = withContext(Dispatchers.IO) {
        try {
            val all = mutableListOf<Team>()
            listOf(PREMIER_LEAGUE, LA_LIGA, SERIE_A, BUNDESLIGA, LIGUE_1).forEach { code ->
                try {
                    all.addAll(api.getTeams(code).teams?.map { it.toTeam() } ?: emptyList())
                } catch (_: Exception) {}
            }
            Result.success(
                all.distinctBy { it.idTeam }
                    .filter { it.strTeam?.contains(query, ignoreCase = true) == true }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMatch(matchId: String): Result<Event?> = withContext(Dispatchers.IO) {
        try {
            // football-data.org: /matches/{id}
            val id = matchId.toIntOrNull() ?: return@withContext Result.success(null)
            val response = api.getMatch(id)
            Result.success(response.toEvent())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
