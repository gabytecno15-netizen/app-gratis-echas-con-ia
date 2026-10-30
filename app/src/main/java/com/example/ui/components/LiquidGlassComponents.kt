package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyForecast
import com.example.data.model.HourlyForecast
import com.example.data.model.WeatherCondition
import com.example.data.model.WeatherVibe
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.GlassHighlightBrush
import com.example.ui.theme.GlassSurfaceBrush
import com.example.ui.theme.GlassSurfaceCardBrush
import com.example.ui.theme.IceBlue
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonAqua
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.WeatherAtmospheres
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Liquid Glass container with glossy translucent gradient, specular border highlight,
 * and top light sheen.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    borderBrush: Brush = GlassBorderBrush,
    backgroundBrush: Brush = GlassSurfaceCardBrush,
    contentPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    val tagModifier = if (testTag != null) Modifier.testTag(testTag) else Modifier

    Box(
        modifier = modifier
            .then(tagModifier)
            .clip(shape)
            .background(backgroundBrush)
            .border(width = 1.dp, brush = borderBrush, shape = shape)
            .then(clickableModifier)
            .padding(contentPadding)
    ) {
        // Specular top light streak inside glass
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(GlassHighlightBrush)
                .align(Alignment.TopCenter)
        )
        content()
    }
}

/**
 * Animated dynamic liquid backdrop with floating fluid spheres that morph and drift,
 * matching the current weather condition vibes.
 */
@Composable
fun LiquidAmbientBackground(
    vibe: WeatherVibe,
    isDay: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = when (vibe) {
        WeatherVibe.SUNNY -> WeatherAtmospheres.SunnyGradients
        WeatherVibe.CLEAR_NIGHT -> WeatherAtmospheres.NightGradients
        WeatherVibe.RAINY -> WeatherAtmospheres.RainyGradients
        WeatherVibe.STORM -> WeatherAtmospheres.StormGradients
        WeatherVibe.SNOWY -> WeatherAtmospheres.SnowyGradients
        WeatherVibe.CLOUDY, WeatherVibe.FOGGY -> WeatherAtmospheres.CloudyGradients
        WeatherVibe.PARTLY_CLOUDY -> if (isDay) WeatherAtmospheres.SunnyGradients else WeatherAtmospheres.NightGradients
    }

    val transition = rememberInfiniteTransition(label = "liquid_orbs")
    val animOffset1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb1"
    )

    val animOffset2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(19000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb2"
    )

    val animPulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Background vertical base gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(colors.first(), colors.getOrElse(1) { colors.first() }, colors.last())
            )
        )

        // Animated fluid orbs
        val orb1X = width * 0.3f + cos(animOffset1) * (width * 0.22f)
        val orb1Y = height * 0.22f + sin(animOffset1) * (height * 0.12f)
        val orb1Radius = width * 0.48f * animPulse

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.getOrElse(2) { Color.Cyan }.copy(alpha = 0.55f),
                    colors.getOrElse(3) { Color.Blue }.copy(alpha = 0.2f),
                    Color.Transparent
                ),
                center = Offset(orb1X, orb1Y),
                radius = orb1Radius
            ),
            center = Offset(orb1X, orb1Y),
            radius = orb1Radius
        )

        val orb2X = width * 0.72f + cos(animOffset2) * (width * 0.25f)
        val orb2Y = height * 0.58f + sin(animOffset2) * (height * 0.16f)
        val orb2Radius = width * 0.55f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.getOrElse(3) { Color.Magenta }.copy(alpha = 0.45f),
                    colors.getOrElse(4) { Color.Cyan }.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(orb2X, orb2Y),
                radius = orb2Radius
            ),
            center = Offset(orb2X, orb2Y),
            radius = orb2Radius
        )

        // Lower ambient liquid glow
        val orb3X = width * 0.45f
        val orb3Y = height * 0.88f
        val orb3Radius = width * 0.6f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.getOrElse(2) { Color.Cyan }.copy(alpha = 0.35f),
                    Color.Transparent
                ),
                center = Offset(orb3X, orb3Y),
                radius = orb3Radius
            ),
            center = Offset(orb3X, orb3Y),
            radius = orb3Radius
        )

        // Weather effects (Rain streaks or Sun aura)
        if (vibe == WeatherVibe.RAINY || vibe == WeatherVibe.STORM) {
            val rainAlpha = 0.22f
            for (i in 0..18) {
                val rx = (width * ((i * 19) % 100) / 100f) + (cos(animOffset1 + i) * 15f)
                val ry = (height * ((i * 23 + (animOffset1 * 60).toInt()) % 100) / 100f)
                drawLine(
                    color = Color.White.copy(alpha = rainAlpha),
                    start = Offset(rx, ry),
                    end = Offset(rx - 8f, ry + 24f),
                    strokeWidth = 2.5f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * High quality icon for weather condition with luminous colors.
 */
@Composable
fun ConditionIcon(
    condition: WeatherCondition,
    isDay: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    val (icon, tint) = when (condition) {
        WeatherCondition.CLEAR_DAY -> Icons.Filled.WbSunny to NeonAmber
        WeatherCondition.CLEAR_NIGHT -> Icons.Filled.NightlightRound to IceBlue
        WeatherCondition.PARTLY_CLOUDY_DAY -> Icons.Rounded.Cloud to NeonAmber.copy(alpha = 0.9f)
        WeatherCondition.PARTLY_CLOUDY_NIGHT -> Icons.Rounded.Cloud to IceBlue.copy(alpha = 0.9f)
        WeatherCondition.OVERCAST, WeatherCondition.FOG -> Icons.Rounded.Cloud to Color.White.copy(alpha = 0.85f)
        WeatherCondition.DRIZZLE, WeatherCondition.RAIN -> Icons.Rounded.Grain to NeonAqua
        WeatherCondition.HEAVY_RAIN -> Icons.Rounded.WaterDrop to NeonCyan
        WeatherCondition.SNOW -> Icons.Rounded.AcUnit to Color.White
        WeatherCondition.THUNDERSTORM -> Icons.Rounded.FlashOn to NeonViolet
    }

    Box(
        modifier = modifier
            .size(size)
            .background(tint.copy(alpha = 0.15f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = condition.descriptionEs,
            tint = tint,
            modifier = Modifier.size(size * 0.65f)
        )
    }
}

/**
 * Capsule pill for hourly forecast item in horizontal scroll.
 */
@Composable
fun HourlyForecastPill(
    item: HourlyForecast,
    useFahrenheit: Boolean,
    modifier: Modifier = Modifier
) {
    val displayTemp = if (useFahrenheit) {
        (item.temperatureCelsius * 9.0 / 5.0 + 32).toInt()
    } else {
        item.temperatureCelsius.toInt()
    }

    val backgroundBrush = if (item.isNow) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x5500F5D4),
                Color(0x2000BBF9)
            )
        )
    } else {
        GlassSurfaceBrush
    }

    val borderBrush = if (item.isNow) {
        Brush.linearGradient(
            colors = listOf(NeonAqua, NeonCyan.copy(alpha = 0.5f))
        )
    } else {
        GlassBorderBrush
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundBrush)
            .border(1.dp, borderBrush, RoundedCornerShape(22.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = item.timeLabel,
            style = MaterialTheme.typography.labelMedium,
            color = if (item.isNow) NeonAqua else Color.White.copy(alpha = 0.8f),
            fontWeight = if (item.isNow) FontWeight.Bold else FontWeight.Medium
        )

        ConditionIcon(
            condition = item.weatherCondition,
            isDay = true,
            size = 36.dp
        )

        if (item.precipitationChance > 10) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Opacity,
                    contentDescription = null,
                    tint = NeonAqua,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = "${item.precipitationChance}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonAqua,
                    fontSize = 10.sp
                )
            }
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }

        Text(
            text = "$displayTemp°",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/**
 * 7-Day forecast row with day name, weather icon, precipitation pill, and temperature gradient bar.
 */
@Composable
fun DailyForecastRow(
    item: DailyForecast,
    useFahrenheit: Boolean,
    minWeekTemp: Double,
    maxWeekTemp: Double,
    modifier: Modifier = Modifier
) {
    val minTemp = if (useFahrenheit) {
        (item.minTempCelsius * 9.0 / 5.0 + 32).toInt()
    } else {
        item.minTempCelsius.toInt()
    }

    val maxTemp = if (useFahrenheit) {
        (item.maxTempCelsius * 9.0 / 5.0 + 32).toInt()
    } else {
        item.maxTempCelsius.toInt()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day label
        Text(
            text = item.dayName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.width(80.dp)
        )

        // Weather icon & precip chance
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(72.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ConditionIcon(
                condition = item.weatherCondition,
                isDay = true,
                size = 28.dp
            )
            if (item.precipitationChance > 15) {
                Text(
                    text = "${item.precipitationChance}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonAqua,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Min temp
        Text(
            text = "$minTemp°",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End
        )

        // Temperature range capsule bar
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.15f))
        ) {
            val totalSpan = (maxWeekTemp - minWeekTemp).coerceAtLeast(1.0)
            val startRatio = ((item.minTempCelsius - minWeekTemp) / totalSpan).coerceIn(0.0, 0.8).toFloat()
            val endRatio = ((item.maxTempCelsius - minWeekTemp) / totalSpan).coerceIn(0.2, 1.0).toFloat()

            Canvas(modifier = Modifier.fillMaxSize()) {
                val startX = size.width * startRatio
                val endX = size.width * endRatio
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(NeonAqua, NeonAmber, NeonCoral)
                    ),
                    start = Offset(startX, size.height / 2),
                    end = Offset(endX, size.height / 2),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round
                )
            }
        }

        // Max temp
        Text(
            text = "$maxTemp°",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Start
        )
    }
}

/**
 * Metric card with title, primary value, subtitle, and an aesthetic glass graphic visual.
 */
@Composable
fun MetricGlassCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = NeonAqua,
    modifier: Modifier = Modifier,
    visual: (@Composable () -> Unit)? = null
) {
    LiquidGlassCard(
        modifier = modifier,
        contentPadding = 14.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            visual?.invoke()

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.75f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Stylized Wind compass graphic.
 */
@Composable
fun WindCompassVisual(
    degrees: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, GlassBorderBrush, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.North,
            contentDescription = null,
            tint = NeonAqua,
            modifier = Modifier
                .size(24.dp)
                .rotate(degrees.toFloat())
        )
        Text(
            text = "N",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 9.sp,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 2.dp)
        )
    }
}

/**
 * Stylized Humidity water droplet fill graphic.
 */
@Composable
fun HumidityDropletVisual(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White.copy(alpha = 0.15f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = (percentage / 100f).coerceIn(0f, 1f))
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(NeonAqua, NeonCyan)
                    )
                )
        )
    }
}

/**
 * Sunrise and Sunset Daylight Arc.
 */
@Composable
fun SunriseSunsetVisual(
    sunrise: String,
    sunset: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
        ) {
            val width = size.width
            val height = size.height

            val path = Path().apply {
                moveTo(8f, height)
                quadraticTo(width / 2f, 4f, width - 8f, height)
            }

            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.25f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Sun position along arc
            val sunX = width * 0.42f
            val sunY = 10f
            drawCircle(
                color = NeonAmber,
                radius = 5.dp.toPx(),
                center = Offset(sunX, sunY)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WbTwilight,
                    contentDescription = null,
                    tint = NeonAmber,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = sunrise,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NightlightRound,
                    contentDescription = null,
                    tint = IceBlue,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = sunset,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
