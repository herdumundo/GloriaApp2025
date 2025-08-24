package com.gloria.ui.theme

import android.app.Activity
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
    primary = InventoryPrimaryDark,
    primaryContainer = InventoryPrimaryContainerDark,
    secondary = InventorySecondaryDark,
    secondaryContainer = InventoryPrimaryContainerDark,
    tertiary = InventorySecondaryDark,
    background = InventoryBackgroundDark,
    surface = InventorySurfaceDark,
    surfaceVariant = Color(0xFF3D3D3D),
    onPrimary = Color.White,
    onPrimaryContainer = Color.White,
    onSecondary = Color.White,
    onSecondaryContainer = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFE0E0E0),
    error = InventoryError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = InventoryPrimary,
    primaryContainer = InventoryPrimaryContainer,
    secondary = InventorySecondary,
    secondaryContainer = InventoryPrimaryContainer,
    tertiary = InventorySecondary,
    background = InventoryBackground,
    surface = InventorySurface,
    surfaceVariant = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onPrimaryContainer = Color.White,
    onSecondary = Color.White,
    onSecondaryContainer = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFF666666),
    error = InventoryError,
    onError = Color.White
)

@Composable
fun AppinventarioTheme(
    darkTheme: Boolean = ThemeManager.isDarkTheme,
    // Dynamic color is disabled to use our custom red theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}