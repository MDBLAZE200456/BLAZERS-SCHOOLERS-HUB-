package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Basic utility colors
val SuccessGreen = Color(0xFF2D6A4F)
val SuccessGreenLight = Color(0xFF52B788)
val WarningAmber = Color(0xFFD08310)
val ErrorRed = Color(0xFFC0392B)

// Light variant placeholders for safety
val LightBg = Color(0xFFF2F8F5)
val LightSurface = Color(0xFFFFFFFF)
val MutedSlate = Color(0xFF52B788)

// Dynamically reference active MaterialTheme colors via composable getters
val DeepSlateBg: Color
    @Composable
    get() = MaterialTheme.colorScheme.background

val DeepSlateSurface: Color
    @Composable
    get() = MaterialTheme.colorScheme.surface

val PrimaryBlue: Color
    @Composable
    get() = MaterialTheme.colorScheme.primary

val SecondaryBlue: Color
    @Composable
    get() = MaterialTheme.colorScheme.secondary

val AccentSlate: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

