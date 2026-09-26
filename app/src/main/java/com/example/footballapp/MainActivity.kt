package com.example.footballapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.footballapp.ui.navigation.FootballNavGraph
import com.example.footballapp.ui.theme.FootballAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FootballAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FootballNavGraph()
                }
            }
        }
    }
}
