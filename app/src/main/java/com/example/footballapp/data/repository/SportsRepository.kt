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

    /**
     * Naptár nézethez: lekéri a következő [days] nap meccseit,
     * és dátum szerint csoportosítja.
     * Ha leagueId meg van adva, csak az adott ligát szűri.
     */
    suspend fun getEventsForDays(
        days: Int = 21,
        leagueId: String? = null,
        includePastDays: Int = 3
    ): Result<Map<String, List<Event>>> = withContext(Dispatchers.IO) {
        try {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val resultMap = linkedMapOf<String, List<Event>>()

            // Először a múltbeli napok (legutóbbi eredmények)
            calendar.add(Calendar.DAY_OF_YEAR, -includePastDays)
            for (i in 0 until (days + includePastDays)) {
                val date = dateFormat.format(calendar.time)
                try {
                    val response = api.getEventsByDay(date, "Soccer")
                    var events = response.events ?: emptyList()

                    if (leagueId != null) {
                        events = events.filter { it.idLeague == leagueId }
                    }

                    // Csak a releváns meccseket tartjuk meg
                    events = events.filter {
                        it.strSport == "Soccer" || it.strSport == null
                    }

                    if (events.isNotEmpty()) {
                        resultMap[date] = events.sortedBy { it.strTime ?: "99:99" }
                    }
                } catch (_: Exception) {
                    // Egy nap hibája ne törje el az egészet
                }
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            Result.success(resultMap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
