package com.example.footballapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun FootballAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accentKey: String = "green",
    content: @Composable () -> Unit
) {
    val primary = accentPrimary(accentKey)
    val secondary = accentSecondary(accentKey)

    val colors = if (darkTheme) {
        darkColorScheme(
            primary = primary,
            onPrimary = Color.White,
            secondary = secondary,
            background = BackgroundDark,
            surface = SurfaceDark,
            onBackground = TextPrimaryDark,
            onSurface = TextPrimaryDark,
            surfaceVariant = CardDark,
            onSurfaceVariant = TextSecondaryDark
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            secondary = secondary,
            background = BackgroundLight,
            surface = SurfaceLight,
            onBackground = TextPrimaryLight,
            onSurface = TextPrimaryLight,
            surfaceVariant = CardLight,
            onSurfaceVariant = TextSecondaryLight
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
