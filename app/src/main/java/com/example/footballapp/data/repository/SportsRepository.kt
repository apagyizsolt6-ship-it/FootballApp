package com.example.footballapp.data.repository

import com.example.footballapp.data.api.ApiClient
import com.example.footballapp.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SportsRepository {

    private val api = ApiClient.api

    // Popular league IDs
    companion object {
        const val PREMIER_LEAGUE = "4328"
        const val LA_LIGA = "4335"
        const val SERIE_A = "4332"
        const val BUNDESLIGA = "4331"
        const val LIGUE_1 = "4334"
        const val CHAMPIONS_LEAGUE = "4480"
    }

    suspend fun searchTeams(query: String): Result<List<Team>> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchTeams(query)
            Result.success(response.teams ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTeam(teamId: String): Result<Team?> = withContext(Dispatchers.IO) {
        try {
            val response = api.lookupTeam(teamId)
            Result.success(response.teams?.firstOrNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNextEvents(teamId: String): Result<List<Event>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getNextEvents(teamId)
            Result.success(response.events ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLastEvents(teamId: String): Result<List<Event>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getLastEvents(teamId)
            Result.success(response.events ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNextLeagueEvents(leagueId: String = PREMIER_LEAGUE): Result<List<Event>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getNextLeagueEvents(leagueId)
                Result.success(response.events ?: emptyList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getPastLeagueEvents(leagueId: String = PREMIER_LEAGUE): Result<List<Event>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getPastLeagueEvents(leagueId)
                Result.success(response.events ?: emptyList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getLeagueTable(leagueId: String = PREMIER_LEAGUE, season: String? = null): Result<List<TableEntry>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getLeagueTable(leagueId, season)
                Result.success(response.table ?: emptyList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun searchPlayers(query: String): Result<List<Player>> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchPlayers(query)
            Result.success(response.player ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlayer(playerId: String): Result<Player?> = withContext(Dispatchers.IO) {
        try {
            val response = api.lookupPlayer(playerId)
            Result.success(response.player?.firstOrNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTeamsInLeague(leagueName: String = "English_Premier_League"): Result<List<Team>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getTeamsInLeague(leagueName)
                Result.success(response.teams ?: emptyList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getEventsByDay(date: String): Result<List<Event>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getEventsByDay(date, "Soccer")
            Result.success(response.events ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
