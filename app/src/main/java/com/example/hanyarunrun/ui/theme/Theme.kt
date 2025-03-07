package com.example.hanyarunrun.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = OnPrimaryColor,
    secondary = SecondaryColor,
    onSecondary = OnSecondaryColor,
    background = BackgroundColor,
    surface = SurfaceColor,
    onSurface = OnSurfaceColor
)

private val DarkColors = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = OnPrimaryColor,
    secondary = SecondaryColor,
    onSecondary = OnSecondaryColor,
    background = Color(0xFF004D40),
    surface = Color(0xFF00251A),
    onSurface = Color.White
)

@Composable
fun HanyarunrunTheme(
    darkTheme: Boolean = false, // Ubah sesuai preferensi atau logika sistem
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
