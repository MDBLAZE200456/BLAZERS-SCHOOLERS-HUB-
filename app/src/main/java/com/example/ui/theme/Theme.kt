package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ui.AppThemeMode

// 1. Black Theme (Immersive dark study space)
val BlackColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),       // Neon Cyan Blue
    secondary = Color(0xFF818CF8),     // Indigo Accent
    tertiary = Color(0xFF10B981),      // Active Mint Green
    background = Color(0xFF030712),    // Midnight dark black
    surface = Color(0xFF111827),       // Deep slate card surface
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1F2937), // Border accent grey
    onSurfaceVariant = Color(0xFF9CA3AF),
    error = Color(0xFFEF4444),
    onError = Color.White
)

// 2. White Theme (Crisp daylight light mode)
val WhiteColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),       // Study Royal Blue
    secondary = Color(0xFF3B82F6),     // Smart Active Blue
    tertiary = Color(0xFF059669),      // Clean Success Mint-Green
    background = Color(0xFFF8FAFC),    // Crisp daylight white slate background
    surface = Color(0xFFFFFFFF),       // Clear immaculate white card surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0), // Border/Outline clean light grey
    onSurfaceVariant = Color(0xFF64748B),
    error = Color(0xFFDC2626),
    onError = Color.White
)

// 3. Rainbow Theme (Highly vibrant cyberpunk gamer theme)
val RainbowColorScheme = darkColorScheme(
    primary = Color(0xFFF43F5E),       // Cyber Pink Red
    secondary = Color(0xFFA855F7),     // Glowing Purple
    tertiary = Color(0xFF22D3EE),      // Electric Cyan
    background = Color(0xFF090117),    // Neon Deep Void Dark
    surface = Color(0xFF1A0B2E),       // Cyber Violet Card Surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF3B0764), // Glowing neon border magenta-purple
    onSurfaceVariant = Color(0xFFD8B4FE),
    error = Color(0xFFE11D48),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.BLACK,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.BLACK -> BlackColorScheme
        AppThemeMode.WHITE -> WhiteColorScheme
        AppThemeMode.RAINBOW -> RainbowColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
