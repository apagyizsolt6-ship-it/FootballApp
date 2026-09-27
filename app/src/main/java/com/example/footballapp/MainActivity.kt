package com.example.footballapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.footballapp.data.prefs.AppPreferences
import com.example.footballapp.notifications.MatchNotificationHelper
import com.example.footballapp.notifications.MatchReminderWorker
import com.example.footballapp.ui.navigation.FootballNavGraph
import com.example.footballapp.ui.theme.FootballAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or not */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        MatchNotificationHelper.createChannel(this)
        MatchReminderWorker.schedule(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val prefs = AppPreferences(applicationContext)

        setContent {
            val isDark by prefs.isDarkMode.collectAsState(initial = true)
            val scope = rememberCoroutineScope()

            FootballAppTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FootballNavGraph(
                        isDarkTheme = isDark,
                        onToggleTheme = {
                            scope.launch { prefs.setDarkMode(!isDark) }
                        },
                        prefs = prefs
                    )
                }
            }
        }
    }
}
