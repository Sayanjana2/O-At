package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = DarkNavyBackground,
    primaryContainer = GlassSurfaceElevated,
    onPrimaryContainer = CyanAccent,
    secondary = SkyBlue,
    onSecondary = DarkNavyBackground,
    background = DarkNavyBackground,
    onBackground = TextPrimary,
    surface = DeepGlassSurface,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = TextPrimary,
    primaryContainer = SkyBlue,
    onPrimaryContainer = DarkNavyBackground,
    secondary = CyanAccentVariant,
    onSecondary = DarkNavyBackground,
    background = DarkNavyBackground, // Deskify maintains a sleek dark desktop aesthetic
    onBackground = TextPrimary,
    surface = DeepGlassSurface,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder
)

@Composable
fun DeskifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DarkNavyBackground.toArgb()
                window.navigationBarColor = DarkNavyBackground.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
