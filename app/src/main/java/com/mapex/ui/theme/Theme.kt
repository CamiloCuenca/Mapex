package com.mapex.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
    primaryContainer = BrandBlueHover,
    onPrimaryContainer = TextWhite,

    // Acciones secundarias
    secondary = BrandBlue,
    onSecondary = TextWhite,
    secondaryContainer = BrandBlueContainer,
    onSecondaryContainer = TextWhite,

    // Verde fosforescente adaptado para contraste en tema claro
    tertiary = BrandGreenMuted, 
    onTertiary = TextWhite,
    tertiaryContainer = BrandGreenContainer,
    onTertiaryContainer = TextWhite,

    // Estructurales en tema claro
    background = BackgroundLight,
    onBackground = TextBlack,
    surface = SurfaceLight,
    onSurface = TextBlack,
    surfaceVariant = OutlineLight,
    onSurfaceVariant = TextDarkGray,

    outline = OutlineLight,
    outlineVariant = BrandGreenMuted
)

@Composable
fun MapexTheme(
    // Forzamos el tema oscuro por defecto para garantizar que el "Color primario: negro"
    // se aplique siempre a los fondos principales, tal como dicta la identidad visual.
    darkTheme: Boolean = true,
    // Dynamic color is disabled by default to enforce the brand identity
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