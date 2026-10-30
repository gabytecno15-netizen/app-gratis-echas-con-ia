package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Liquid Glass Palette
val GlassWhiteHigh = Color(0x33FFFFFF)
val GlassWhiteMedium = Color(0x1FFFFFFF)
val GlassWhiteLow = Color(0x0FFFFFFF)
val GlassWhiteBorder = Color(0x55FFFFFF)

val GlassBorderBrush = Brush.linearGradient(
    colors = listOf(
        Color(0x99FFFFFF),
        Color(0x33FFFFFF),
        Color(0x10FFFFFF)
    )
)

val GlassSurfaceBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0x3DFFFFFF),
        Color(0x14FFFFFF)
    )
)

val GlassSurfaceCardBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0x2EFFFFFF),
        Color(0x0AFFFFFF)
    )
)

val GlassHighlightBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0x40FFFFFF),
        Color(0x00FFFFFF)
    )
)

// Vibrant Glow Accents
val NeonAqua = Color(0xFF00F5D4)
val NeonCyan = Color(0xFF00BBF9)
val NeonAmber = Color(0xFFFFB703)
val NeonCoral = Color(0xFFFB8500)
val NeonViolet = Color(0xFF7209B7)
val NeonPink = Color(0xFFF72585)
val IceBlue = Color(0xFFA2D2FF)
val DeepNavy = Color(0xFF0A1128)

// Weather Atmosphere Palettes
object WeatherAtmospheres {
    val SunnyGradients = listOf(
        Color(0xFF0077B6),
        Color(0xFF0096C7),
        Color(0xFF00B4D8),
        Color(0xFF48CAE4),
        Color(0xFFFFB703)
    )

    val NightGradients = listOf(
        Color(0xFF050814),
        Color(0xFF0B132B),
        Color(0xFF1C2541),
        Color(0xFF3A0CA3),
        Color(0xFF4CC9F0)
    )

    val RainyGradients = listOf(
        Color(0xFF0D1B2A),
        Color(0xFF1B263B),
        Color(0xFF415A77),
        Color(0xFF1D3557),
        Color(0xFF457B9D)
    )

    val StormGradients = listOf(
        Color(0xFF0F0A1C),
        Color(0xFF240046),
        Color(0xFF3C096C),
        Color(0xFF5A189A),
        Color(0xFF7B2CBF)
    )

    val SnowyGradients = listOf(
        Color(0xFF1C2541),
        Color(0xFF2B3A67),
        Color(0xFF496F9D),
        Color(0xFF8ECAE6),
        Color(0xFFE2EAFC)
    )

    val CloudyGradients = listOf(
        Color(0xFF1E293B),
        Color(0xFF334155),
        Color(0xFF475569),
        Color(0xFF64748B),
        Color(0xFF38BDF8)
    )
}
