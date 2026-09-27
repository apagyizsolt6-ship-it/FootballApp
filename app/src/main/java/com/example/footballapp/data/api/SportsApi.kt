package com.example.footballapp.data.api

import com.example.footballapp.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SportsApi {

    // Meccsek egy ligában (dátum szűréssel)
    @GET("competitions/{code}/matches")
    suspend fun getCompetitionMatches(
        @Path("code") competitionCode: String,
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null,
        @Query("status") status: String? = null,
        @Query("matchday") matchday: Int? = null
    ): MatchesResponse

    // Tabella
    @GET("competitions/{code}/standings")
    suspend fun getStandings(
        @Path("code") competitionCode: String
    ): StandingsResponse

    // Csapatok egy ligában
    @GET("competitions/{code}/teams")
    suspend fun getTeams(
        @Path("code") competitionCode: String
    ): TeamsResponse

    // Egy csapat meccsei
    @GET("teams/{id}/matches")
    suspend fun getTeamMatches(
        @Path("id") teamId: Int,
        @Query("status") status: String? = null,
        @Query("limit") limit: Int? = 20
    ): MatchesResponse

    // Csapat keresés / részletek
    @GET("teams/{id}")
    suspend fun getTeam(
        @Path("id") teamId: Int
    ): FdTeam

    // Mai / dátum szerinti meccsek (több liga)
    @GET("matches")
    suspend fun getMatches(
        @Query("competitions") competitions: String? = null,
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null,
        @Query("status") status: String? = null
    ): MatchesResponse
}
