package com.musicpro.player.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = MusicProColors.Primary,
    onPrimary = Color.White,
    secondary = MusicProColors.Accent,
    tertiary = MusicProColors.PrimaryLight,
    background = MusicProColors.Background,
    surface = MusicProColors.Surface,
    surfaceVariant = MusicProColors.SurfaceVariant,
    onBackground = MusicProColors.TextPrimary,
    onSurface = MusicProColors.TextPrimary,
    onSurfaceVariant = MusicProColors.TextSecondary
)

@Composable
fun MusicProTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MusicProTypography,
        content = content
    )
}

val MusicProTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold, fontSize = 32.sp, letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold, fontSize = 28.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold, fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold, fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 10.sp, letterSpacing = 0.5.sp
    )
)
