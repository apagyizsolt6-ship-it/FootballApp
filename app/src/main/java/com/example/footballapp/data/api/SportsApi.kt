package com.example.footballapp.data.api

import com.example.footballapp.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SportsApi {

    @GET("competitions/{code}/matches")
    suspend fun getCompetitionMatches(
        @Path("code") competitionCode: String,
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null,
        @Query("status") status: String? = null,
        @Query("matchday") matchday: Int? = null
    ): MatchesResponse

    @GET("competitions/{code}/standings")
    suspend fun getStandings(
        @Path("code") competitionCode: String
    ): StandingsResponse

    @GET("competitions/{code}/teams")
    suspend fun getTeams(
        @Path("code") competitionCode: String
    ): TeamsResponse

    @GET("teams/{id}/matches")
    suspend fun getTeamMatches(
        @Path("id") teamId: Int,
        @Query("status") status: String? = null,
        @Query("limit") limit: Int? = 20
    ): MatchesResponse

    @GET("teams/{id}")
    suspend fun getTeam(
        @Path("id") teamId: Int
    ): FdTeam

    @GET("matches")
    suspend fun getMatches(
        @Query("competitions") competitions: String? = null,
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null,
        @Query("status") status: String? = null
    ): MatchesResponse

    @GET("matches/{id}")
    suspend fun getMatch(
        @Path("id") matchId: Int
    ): FdMatch
}
