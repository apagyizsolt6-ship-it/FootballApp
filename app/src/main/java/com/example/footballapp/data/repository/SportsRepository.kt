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

    suspend fun getEventsByDay(date: String, leagueId: String? = null): Result<List<Event>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getEventsByDay(date, "Soccer", leagueId)
                Result.success(response.events ?: emptyList())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Naptár: next + past meccseket dátum szerint csoportosít.
     * Emellett megpróbálja a napi endpointot is a következő napokra.
     */
    suspend fun getMatchesGroupedByDate(
        leagueId: String,
        extraDays: Int = 14
    ): Result<Map<String, List<Event>>> = withContext(Dispatchers.IO) {
        try {
            val allEvents = mutableListOf<Event>()

            // 1. Next + Past (ezek megbízhatóbbak a free kulccsal)
            try {
                val next = api.getNextLeagueEvents(leagueId).events ?: emptyList()
                allEvents.addAll(next)
            } catch (_: Exception) {}

            try {
                val past = api.getPastLeagueEvents(leagueId).events ?: emptyList()
                allEvents.addAll(past)
            } catch (_: Exception) {}

            // 2. Extra napok a napi endpointból (ha van adat)
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            // Kezdjünk 5 nappal ezelőtt
            calendar.add(Calendar.DAY_OF_YEAR, -5)

            for (i in 0 until (extraDays + 5)) {
                val date = dateFormat.format(calendar.time)
                try {
                    val dayEvents = api.getEventsByDay(date, "Soccer", leagueId).events ?: emptyList()
                    allEvents.addAll(dayEvents)
                } catch (_: Exception) {}
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            // Egyedi meccsek (idEvent alapján)
            val unique = allEvents
                .filter { !it.idEvent.isNullOrBlank() }
                .distinctBy { it.idEvent }
                .filter { it.idLeague == leagueId || it.idLeague == null }

            // Csoportosítás dátum szerint
            val grouped = unique
                .groupBy { it.dateEvent ?: "Ismeretlen" }
                .mapValues { (_, list) ->
                    list.sortedBy { it.strTime ?: "99:99" }
                }
                .toSortedMap()

            Result.success(grouped)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
