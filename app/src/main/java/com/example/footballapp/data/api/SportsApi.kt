package com.example.footballapp.data.api

import com.example.footballapp.data.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface SportsApi {

    // Search teams by name
    @GET("searchteams.php")
    suspend fun searchTeams(
        @Query("t") teamName: String
    ): TeamResponse

    // Lookup team by ID
    @GET("lookupteam.php")
    suspend fun lookupTeam(
        @Query("id") teamId: String
    ): TeamResponse

    // Next events for a team
    @GET("eventsnext.php")
    suspend fun getNextEvents(
        @Query("id") teamId: String
    ): EventResponse

    // Last events for a team
    @GET("eventslast.php")
    suspend fun getLastEvents(
        @Query("id") teamId: String
    ): EventResponse

    // Next events for a league
    @GET("eventsnextleague.php")
    suspend fun getNextLeagueEvents(
        @Query("id") leagueId: String
    ): EventResponse

    // Past events for a league
    @GET("eventspastleague.php")
    suspend fun getPastLeagueEvents(
        @Query("id") leagueId: String
    ): EventResponse

    // League table
    @GET("lookuptable.php")
    suspend fun getLeagueTable(
        @Query("l") leagueId: String,
        @Query("s") season: String? = null
    ): TableResponse

    // Search players
    @GET("searchplayers.php")
    suspend fun searchPlayers(
        @Query("p") playerName: String
    ): PlayerResponse

    // Lookup player
    @GET("lookupplayer.php")
    suspend fun lookupPlayer(
        @Query("id") playerId: String
    ): PlayerResponse

    // All teams in a league
    @GET("search_all_teams.php")
    suspend fun getTeamsInLeague(
        @Query("l") leagueName: String
    ): TeamResponse

    // Events by day
    @GET("eventsday.php")
    suspend fun getEventsByDay(
        @Query("d") date: String,
        @Query("s") sport: String? = "Soccer"
    ): EventResponse

    // Lookup event
    @GET("lookupevent.php")
    suspend fun lookupEvent(
        @Query("id") eventId: String
    ): EventResponse
}
