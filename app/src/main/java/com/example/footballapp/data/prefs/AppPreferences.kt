package com.example.footballapp.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "football_prefs")

class AppPreferences(private val context: Context) {

    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val favoritesKey = stringSetPreferencesKey("favorite_teams")
    private val notificationsKey = booleanPreferencesKey("notifications_enabled")

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[darkModeKey] ?: true }
    val favoriteTeamIds: Flow<Set<String>> = context.dataStore.data.map { it[favoritesKey] ?: emptySet() }
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[notificationsKey] ?: true }

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
}
