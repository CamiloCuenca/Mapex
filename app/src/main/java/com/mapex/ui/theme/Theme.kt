package com.mapex.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = TextPrimaryDark,
    primaryContainer = Color(0xFF1D4ED8),
    onPrimaryContainer = TextPrimaryDark,
    secondary = GreenSecondaryDark,
    onSecondary = TextPrimaryDark,
    secondaryContainer = Color(0xFF065F46),
    onSecondaryContainer = TextPrimaryDark,
    tertiary = BluePrimaryDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF374151),
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF4B5563)
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueLight,
    onPrimaryContainer = TextPrimary,
    secondary = GreenSecondary,
    onSecondary = Color.White,
    secondaryContainer = GreenLight,
    onSecondaryContainer = TextPrimary,
    tertiary = MarkerRed,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFE5E7EB),
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFFD1D5DB)
)

@Composable
fun MapexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}