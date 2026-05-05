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
    // Acciones principales y enlaces
    primary = BrandBlue,
    onPrimary = TextWhite,
    primaryContainer = BrandBlueContainer,
    onPrimaryContainer = TextWhite,

    // Acciones secundarias
    secondary = BrandBlueHover,
    onSecondary = TextWhite,
    secondaryContainer = BrandBlueContainer,
    onSecondaryContainer = TextWhite,

    // Verde fosforescente (usado con moderación: focus, resaltados sutiles)
    tertiary = BrandGreen,
    onTertiary = BrandBlack,
    tertiaryContainer = BrandGreenContainer,
    onTertiaryContainer = TextWhite,

    // Color primario: Negro (fondos principales y estructurales)
    background = BackgroundBlack,
    onBackground = TextWhite,
    surface = SurfaceBlack,
    onSurface = TextWhite,
    surfaceVariant = SurfaceVariantBlack,
    onSurfaceVariant = TextGray,

    // Detalles de borde y estados
    outline = OutlineDark,
    outlineVariant = BrandGreenMuted // Usado para focus y bordes sutiles
)

private val LightColorScheme = lightColorScheme(
    // Acciones principales y enlaces
    primary = BrandBlue,
    onPrimary = TextWhite,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF00337A),

    // Acciones secundarias
    secondary = BrandBlue,
    onSecondary = TextWhite,
    secondaryContainer = Color(0xFFD6E4FF),
    onSecondaryContainer = Color(0xFF00337A),

    // Verde muted adaptado para tema claro (contraste accesible)
    tertiary = BrandGreenMuted,
    onTertiary = TextWhite,
    tertiaryContainer = Color(0xFFB8F5D0),
    onTertiaryContainer = Color(0xFF003918),

    // Fondos y superficies claras
    background = BackgroundLight,
    onBackground = TextBlack,
    surface = SurfaceLight,
    onSurface = TextBlack,
    surfaceVariant = Color(0xFFEAEAF0),
    onSurfaceVariant = TextDarkGray,

    outline = Color(0xFFB0B0BA),
    outlineVariant = Color(0xFFD4D4DC),
    error = Color(0xFFBA1A1A),
    onError = TextWhite,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun MapexTheme(
    // Sigue la preferencia del sistema (dark/light)
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to enforce the brand identity
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