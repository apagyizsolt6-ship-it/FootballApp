package com.example.footballapp.data.cache

import com.example.footballapp.data.model.Event
import java.util.concurrent.ConcurrentHashMap

object MatchCache {
    private data class Entry(val data: List<Event>, val timestamp: Long)
    private val map = ConcurrentHashMap<String, Entry>()
    private const val TTL_MS = 5 * 60 * 1000L

    fun get(leagueCode: String, date: String): List<Event>? {
        val entry = map["$leagueCode|$date"] ?: return null
        if (System.currentTimeMillis() - entry.timestamp > TTL_MS) {
            map.remove("$leagueCode|$date")
            return null
        }
        return entry.data
    }

    fun put(leagueCode: String, date: String, data: List<Event>) {
        map["$leagueCode|$date"] = Entry(data, System.currentTimeMillis())
    }

    fun clear() = map.clear()
}
