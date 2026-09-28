package com.example.footballapp.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.footballapp.data.repository.SportsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "football_prefs")

class AppPreferences(private val context: Context) {

    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val favoritesKey = stringSetPreferencesKey("favorite_teams")
    private val notificationsKey = booleanPreferencesKey("notifications_enabled")
    private val selectedLeaguesKey = stringSetPreferencesKey("selected_leagues")
    private val showOnlyFavoritesKey = booleanPreferencesKey("show_only_favorites")
    private val accentColorKey = stringPreferencesKey("accent_color")

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[darkModeKey] ?: true }
    val favoriteTeamIds: Flow<Set<String>> = context.dataStore.data.map { it[favoritesKey] ?: emptySet() }
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[notificationsKey] ?: true }
    val showOnlyFavorites: Flow<Boolean> = context.dataStore.data.map { it[showOnlyFavoritesKey] ?: false }
    val accentColor: Flow<String> = context.dataStore.data.map { it[accentColorKey] ?: ACCENT_GREEN }

    val selectedLeagueCodes: Flow<Set<String>> = context.dataStore.data.map {
        it[selectedLeaguesKey] ?: DEFAULT_LEAGUES
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[darkModeKey] = enabled }
    }

    suspend fun toggleFavorite(teamId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[favoritesKey]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(teamId)) current.remove(teamId) else current.add(teamId)
            prefs[favoritesKey] = current
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[notificationsKey] = enabled }
    }

    suspend fun setShowOnlyFavorites(enabled: Boolean) {
        context.dataStore.edit { it[showOnlyFavoritesKey] = enabled }
    }

    suspend fun setSelectedLeagues(codes: Set<String>) {
        context.dataStore.edit { it[selectedLeaguesKey] = codes }
    }

    suspend fun toggleLeague(code: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[selectedLeaguesKey]?.toMutableSet() ?: DEFAULT_LEAGUES.toMutableSet()
            if (current.contains(code)) {
                if (current.size > 1) current.remove(code) // legalább 1 maradjon
            } else {
                current.add(code)
            }
            prefs[selectedLeaguesKey] = current
        }
    }

    suspend fun setAccentColor(colorKey: String) {
        context.dataStore.edit { it[accentColorKey] = colorKey }
    }

    companion object {
        const val ACCENT_GREEN = "green"
        const val ACCENT_BLUE = "blue"
        const val ACCENT_ORANGE = "orange"
        const val ACCENT_PURPLE = "purple"

        val DEFAULT_LEAGUES = setOf(
            SportsRepository.PREMIER_LEAGUE,
            SportsRepository.LA_LIGA,
            SportsRepository.SERIE_A,
            SportsRepository.BUNDESLIGA,
            SportsRepository.LIGUE_1,
            SportsRepository.CHAMPIONS_LEAGUE
        )

        val ALL_LEAGUES = listOf(
            SportsRepository.PREMIER_LEAGUE to "Premier League",
            SportsRepository.LA_LIGA to "La Liga",
            SportsRepository.SERIE_A to "Serie A",
            SportsRepository.BUNDESLIGA to "Bundesliga",
            SportsRepository.LIGUE_1 to "Ligue 1",
            SportsRepository.CHAMPIONS_LEAGUE to "Bajnokok Ligája",
            SportsRepository.EREDIVISIE to "Eredivisie",
            SportsRepository.PRIMEIRA_LIGA to "Primeira Liga",
            SportsRepository.BRASILEIRAO to "Brasileirão",
            SportsRepository.SUPERLIGA to "Superliga",
            SportsRepository.EURO to "Európa-bajnokság",
            SportsRepository.WORLD_CUP to "Világkupa"
        )
    }
}
