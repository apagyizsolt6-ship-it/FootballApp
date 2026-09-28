package com.example.footballapp.ui.theme

import androidx.compose.ui.graphics.Color

val PrimaryGreen = Color(0xFF00C853)
val PrimaryGreenDark = Color(0xFF00A844)

val AccentBlue = Color(0xFF2196F3)
val AccentBlueDark = Color(0xFF1976D2)
val AccentOrange = Color(0xFFFF9800)
val AccentOrangeDark = Color(0xFFF57C00)
val AccentPurple = Color(0xFFAB47BC)
val AccentPurpleDark = Color(0xFF8E24AA)

val BackgroundDark = Color(0xFF0D1117)
val SurfaceDark = Color(0xFF161B22)
val CardDark = Color(0xFF1C2333)

val BackgroundLight = Color(0xFFF5F7FA)
val SurfaceLight = Color(0xFFFFFFFF)
val CardLight = Color(0xFFFFFFFF)

val TextPrimaryDark = Color(0xFFE6EDF3)
val TextSecondaryDark = Color(0xFF8B949E)
val TextPrimaryLight = Color(0xFF1F2328)
val TextSecondaryLight = Color(0xFF656D76)

// Backward compat names used in UI
val TextPrimary = TextPrimaryDark
val TextSecondary = TextSecondaryDark

val LiveRed = Color(0xFFFF1744)
val FormWin = Color(0xFF00C853)
val FormDraw = Color(0xFFFFAB00)
val FormLoss = Color(0xFFFF5252)

fun accentPrimary(key: String): Color = when (key) {
    "blue" -> AccentBlue
    "orange" -> AccentOrange
    "purple" -> AccentPurple
    else -> PrimaryGreen
}

fun accentSecondary(key: String): Color = when (key) {
    "blue" -> AccentBlueDark
    "orange" -> AccentOrangeDark
    "purple" -> AccentPurpleDark
    else -> PrimaryGreenDark
}
