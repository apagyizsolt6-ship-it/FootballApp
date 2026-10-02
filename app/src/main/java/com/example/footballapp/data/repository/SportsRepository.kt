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
        const val EREDIVISIE = "DED"
        const val PRIMEIRA_LIGA = "PPL"
        const val BRASILEIRAO = "BSA"
        const val SUPERLIGA = "DSU"
        const val EURO = "EC"
        const val WORLD_CUP = "WC"

        val ALL_CODES = listOf(
            PREMIER_LEAGUE, LA_LIGA, SERIE_A, BUNDESLIGA, LIGUE_1,
            CHAMPIONS_LEAGUE, EREDIVISIE, PRIMEIRA_LIGA, BRASILEIRAO, SUPERLIGA, EURO, WORLD_CUP
        )
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
            MatchCache.get(competitionCode, date)?.let {
                return@withContext Result.success(it)
            }
            Result.failure(friendlyError(e))
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
                Result.failure(friendlyError(e))
            }
        }

    /** Csoportos tabella (CL, Euro, WC esetén) */
    suspend fun getGroupedTable(competitionCode: String): Result<List<GroupedTable>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getStandings(competitionCode)
                val groups = response.standings
                    ?.filter { it.type == "TOTAL" || it.type == null }
                    ?.mapNotNull { group ->
                        val name = group.group ?: group.stage ?: "Tabella"
                        val table = group.table?.map { it.toTableEntry() } ?: return@mapNotNull null
                        if (table.isEmpty()) null else GroupedTable(name, table)
                    }
                    ?: emptyList()
                if (groups.isEmpty()) {
                    // fallback: egyetlen TOTAL
                    val single = response.standings?.firstOrNull()?.table?.map { it.toTableEntry() }
                    if (!single.isNullOrEmpty()) {
                        Result.success(listOf(GroupedTable("Tabella", single)))
                    } else {
                        Result.success(emptyList())
                    }
                } else {
                    Result.success(groups)
                }
            } catch (e: Exception) {
                Result.failure(friendlyError(e))
            }
        }

    suspend fun getScorers(competitionCode: String, limit: Int = 20): Result<List<ScorerEntry>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getScorers(competitionCode, limit)
                val list = response.scorers?.mapIndexed { index, s -> s.toScorerEntry(index + 1) } ?: emptyList()
                Result.success(list)
            } catch (e: Exception) {
                Result.failure(friendlyError(e))
            }
        }

    suspend fun getTeamsInLeague(competitionCode: String = PREMIER_LEAGUE): Result<List<Team>> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(api.getTeams(competitionCode).teams?.map { it.toTeam() } ?: emptyList())
            } catch (e: Exception) {
                Result.failure(friendlyError(e))
            }
        }

    suspend fun getTeam(teamId: String): Result<Team?> = withContext(Dispatchers.IO) {
        try {
            val id = teamId.toIntOrNull() ?: return@withContext Result.success(null)
            Result.success(api.getTeam(id).toTeam())
        } catch (e: Exception) {
            Result.failure(friendlyError(e))
        }
    }

    suspend fun getTeamMatches(teamId: String, status: String? = null): Result<List<Event>> =
        withContext(Dispatchers.IO) {
            try {
                val id = teamId.toIntOrNull() ?: return@withContext Result.success(emptyList())
                val response = api.getTeamMatches(id, status = status, limit = 20)
                Result.success(response.matches?.map { it.toEvent() } ?: emptyList())
            } catch (e: Exception) {
                Result.failure(friendlyError(e))
            }
        }

    suspend fun searchTeams(query: String): Result<List<Team>> = withContext(Dispatchers.IO) {
        try {
            val all = mutableListOf<Team>()
            ALL_CODES.forEach { code ->
                try {
                    all.addAll(api.getTeams(code).teams?.map { it.toTeam() } ?: emptyList())
                } catch (_: Exception) {}
            }
            Result.success(
                all.distinctBy { it.idTeam }
                    .filter { it.strTeam?.contains(query, ignoreCase = true) == true }
            )
        } catch (e: Exception) {
            Result.failure(friendlyError(e))
        }
    }

    suspend fun getMatch(matchId: String): Result<Event?> = withContext(Dispatchers.IO) {
        try {
            val id = matchId.toIntOrNull() ?: return@withContext Result.success(null)
            val response = api.getMatch(id)
            Result.success(response.toEvent())
        } catch (e: Exception) {
            Result.failure(friendlyError(e))
        }
    }

    suspend fun getMatchDetail(matchId: String): Result<MatchDetail?> = withContext(Dispatchers.IO) {
        try {
            if (ApiClient.isRateLimited()) {
                return@withContext Result.failure(
                    ApiClient.RateLimitException(ApiClient.rateLimitSecondsLeft())
                )
            }
            val id = matchId.toIntOrNull() ?: return@withContext Result.success(null)
            val response = api.getMatch(id)
            Result.success(response.toMatchDetail())
        } catch (e: Exception) {
            Result.failure(friendlyError(e))
        }
    }

    private fun friendlyError(e: Exception): Exception {
        return when (e) {
            is ApiClient.RateLimitException -> e
            else -> {
                val msg = e.message ?: ""
                when {
                    "429" in msg || "rate" in msg.lowercase() ->
                        Exception("Túl sok kérés. Várj egy percet, majd frissíts.")
                    "Unable to resolve host" in msg || "UnknownHost" in msg ->
                        Exception("Nincs internetkapcsolat")
                    "timeout" in msg.lowercase() ->
                        Exception("Időtúllépés – próbáld újra")
                    else -> e
                }
            }
        }
    }


    suspend fun getHead2Head(matchId: String, limit: Int = 10): Result<List<Event>> =
        withContext(Dispatchers.IO) {
            try {
                if (ApiClient.isRateLimited()) {
                    return@withContext Result.failure(
                        ApiClient.RateLimitException(ApiClient.rateLimitSecondsLeft())
                    )
                }
                val id = matchId.toIntOrNull()
                    ?: return@withContext Result.success(emptyList())
                val response = api.getHead2Head(id, limit)
                Result.success(response.matches?.map { it.toEvent() } ?: emptyList())
            } catch (e: Exception) {
                Result.failure(friendlyError(e))
            }
        }

}
