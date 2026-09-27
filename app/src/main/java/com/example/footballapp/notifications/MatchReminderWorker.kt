package com.example.footballapp.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.footballapp.data.prefs.AppPreferences
import com.example.footballapp.data.repository.SportsRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MatchReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = AppPreferences(applicationContext)
            if (!prefs.notificationsEnabled.first()) return Result.success()

            val favorites = prefs.favoriteTeamIds.first()
            if (favorites.isEmpty()) return Result.success()

            val repo = SportsRepository()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(Calendar.getInstance().time)

            var notifId = 1000
            listOf(
                SportsRepository.PREMIER_LEAGUE,
                SportsRepository.LA_LIGA,
                SportsRepository.SERIE_A,
                SportsRepository.BUNDESLIGA,
                SportsRepository.LIGUE_1
            ).forEach { code ->
                val matches = repo.getMatchesForDate(code, today).getOrNull() ?: return@forEach
                matches.forEach { event ->
                    val homeId = event.idHomeTeam
                    val awayId = event.idAwayTeam
                    if (homeId in favorites || awayId in favorites) {
                        MatchNotificationHelper.showMatchReminder(
                            applicationContext,
                            notifId++,
                            "Ma meccs: ${event.strHomeTeam} vs ${event.strAwayTeam}",
                            "${event.strTime?.take(5) ?: ""} • ${event.strLeague ?: ""}"
                        )
                    }
                }
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<MatchReminderWorker>(12, TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "match_reminders",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
