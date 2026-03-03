package com.coreo.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// MARK: - Brand Colors (mirrors CoreoTheme.swift exactly)

object CoreoColors {
    val Primary        = Color(0xFF1E4D3B) // Forest Green
    val Accent         = Color(0xFFE8913A) // Amber
    val Success        = Color(0xFFA8C26E) // Lime Green
    val Background     = Color(0xFFF5F0E8) // Creamy White
    val Text           = Color(0xFF3D3D3D) // Soft Charcoal
    val TextOnDark     = Color.White

    val PrimaryLight   = Primary.copy(alpha = 0.1f)
    val TextSecondary  = Text.copy(alpha = 0.6f)
    val Error          = Text.copy(alpha = 0.8f)
}

// MARK: - Spacing

object CoreoSpacing {
    val XS:  Dp = 4.dp
    val S:   Dp = 8.dp
    val M:   Dp = 16.dp
    val L:   Dp = 24.dp
    val XL:  Dp = 32.dp
    val XXL: Dp = 40.dp
}

// MARK: - Corner Radius

object CoreoRadius {
    val S:  Dp = 8.dp
    val M:  Dp = 12.dp
    val L:  Dp = 16.dp
    val XL: Dp = 20.dp
}

// MARK: - Typography

object CoreoType {
    val Headline = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Medium)
    val H2       = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium)
    val H3       = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium)
    val Body     = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
    val Caption  = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)
    val Small    = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
}

// MARK: - App Constants

object CoreoConstants {
    const val AppName = "Coreo"
    const val Tagline = "Strength from the inside out"
}

// MARK: - Material3 Color Scheme (maps Coreo brand to Material slots)

private val CoreoColorScheme = lightColorScheme(
    primary          = CoreoColors.Primary,
    secondary        = CoreoColors.Accent,
    tertiary         = CoreoColors.Success,
    background       = CoreoColors.Background,
    surface          = CoreoColors.Background,
    onPrimary        = CoreoColors.TextOnDark,
    onSecondary      = CoreoColors.TextOnDark,
    onBackground     = CoreoColors.Text,
    onSurface        = CoreoColors.Text,
    error            = CoreoColors.Error
)

private val CoreoTypography = Typography(
    headlineLarge  = CoreoType.Headline,
    headlineMedium = CoreoType.H2,
    headlineSmall  = CoreoType.H3,
    bodyLarge      = CoreoType.Body,
    bodyMedium     = CoreoType.Caption,
    bodySmall      = CoreoType.Small
)

// MARK: - Theme Composable

@Composable
fun CoreoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CoreoColorScheme,
        typography  = CoreoTypography,
        content     = content
    )
}