package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CityLocation
import com.example.data.model.WeatherCondition
import com.example.data.model.WeatherVibe
import com.example.ui.components.ConditionIcon
import com.example.ui.components.DailyForecastRow
import com.example.ui.components.HourlyForecastPill
import com.example.ui.components.HumidityDropletVisual
import com.example.ui.components.LiquidAmbientBackground
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.MetricGlassCard
import com.example.ui.components.SunriseSunsetVisual
import com.example.ui.components.WindCompassVisual
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.GlassSurfaceBrush
import com.example.ui.theme.GlassSurfaceCardBrush
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonAqua
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onRequestLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val weather = state.weatherData
    val vibe = weather?.condition?.atmosphericVibe ?: WeatherVibe.CLEAR_NIGHT
    val isDay = weather?.isDay ?: true

    Box(modifier = modifier.fillMaxSize()) {
        // Dynamic Liquid Atmosphere Background
        LiquidAmbientBackground(vibe = vibe, isDay = isDay)

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Liquid Glass Top Navigation Bar
                GlassTopAppBar(
                    locationName = weather?.location?.name ?: state.selectedLocation.name,
                    countryName = weather?.location?.country ?: state.selectedLocation.country,
                    isFavorite = state.favorites.any {
                        it.name == state.selectedLocation.name && it.country == state.selectedLocation.country
                    },
                    useFahrenheit = state.useFahrenheit,
                    onToggleFavorite = { viewModel.toggleFavorite(state.selectedLocation) },
                    onToggleUnit = { viewModel.toggleUnit() },
                    onSearchClick = { viewModel.openSearch() },
                    onGpsClick = onRequestLocation,
                    onRefreshClick = { viewModel.refreshWeather() },
                    isLoading = state.isLoading
                )

                // Quick Favorites Pill Bar
                FavoritesPillRow(
                    favorites = state.favorites,
                    selectedLocation = state.selectedLocation,
                    onSelectCity = { viewModel.selectLocation(it) }
                )

                if (state.isLoading && weather == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = NeonAqua,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Obteniendo clima líquido...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                } else if (weather != null) {
                    // Main Scrollable Weather Content
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            // Hero Glass Card
                            HeroWeatherGlassCard(
                                weather = weather,
                                useFahrenheit = state.useFahrenheit
                            )
                        }

                        item {
                            // Hourly Forecast Section
                            HourlyForecastGlassSection(
                                hourly = weather.hourly,
                                useFahrenheit = state.useFahrenheit
                            )
                        }

                        item {
                            // 7-Day Forecast Section
                            DailyForecastGlassSection(
                                daily = weather.daily,
                                useFahrenheit = state.useFahrenheit
                            )
                        }

                        item {
                            // Atmospheric Metrics Grid
                            AtmosphericMetricsGrid(
                                metrics = weather.metrics,
                                useFahrenheit = state.useFahrenheit
                            )
                        }

                        item {
                            // Footer info
                            Text(
                                text = "Actualizado a las ${weather.lastUpdated} • Datos de Open-Meteo",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Search Bottom Sheet Dialog
        if (state.showSearchDialog) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.closeSearch() },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color(0xFF0F172A).copy(alpha = 0.95f),
                tonalElevation = 16.dp
            ) {
                SearchCityContent(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    results = state.searchResults,
                    isSearching = state.isSearching,
                    onSelectCity = { viewModel.selectLocation(it) },
                    onClose = { viewModel.closeSearch() }
                )
            }
        }
    }
}

/**
 * Top App Bar with glass capsule controls.
 */
@Composable
fun GlassTopAppBar(
    locationName: String,
    countryName: String,
    isFavorite: Boolean,
    useFahrenheit: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleUnit: () -> Unit,
    onSearchClick: () -> Unit,
    onGpsClick: () -> Unit,
    onRefreshClick: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Location text
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .clickable { onSearchClick() }
                .padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Ubicación",
                tint = NeonAqua,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (countryName.isNotBlank()) {
                    Text(
                        text = countryName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Action Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Unit Toggle Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, GlassBorderBrush, RoundedCornerShape(16.dp))
                    .clickable { onToggleUnit() }
                    .testTag("temp_unit_toggle")
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (useFahrenheit) "°F" else "°C",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonAqua
                )
            }

            // Favorite button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, GlassBorderBrush, CircleShape)
                    .testTag("favorite_button")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) NeonPink else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // GPS button
            IconButton(
                onClick = onGpsClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, GlassBorderBrush, CircleShape)
                    .testTag("gps_location_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Usar mi ubicación",
                    tint = NeonAqua,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Search button
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, GlassBorderBrush, CircleShape)
                    .testTag("search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar ciudad",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Refresh button
            IconButton(
                onClick = onRefreshClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, GlassBorderBrush, CircleShape)
                    .testTag("refresh_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = NeonAqua
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Horizontal favorite cities pill selector.
 */
@Composable
fun FavoritesPillRow(
    favorites: List<CityLocation>,
    selectedLocation: CityLocation,
    onSelectCity: (CityLocation) -> Unit
) {
    if (favorites.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        favorites.forEach { city ->
            val isSelected = (city.name == selectedLocation.name && city.country == selectedLocation.country)
            val bgBrush = if (isSelected) {
                Brush.horizontalGradient(listOf(NeonAqua.copy(alpha = 0.35f), NeonCyan.copy(alpha = 0.2f)))
            } else {
                Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.10f), Color.White.copy(alpha = 0.05f)))
            }
            val border = if (isSelected) NeonAqua else Color.White.copy(alpha = 0.2f)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(bgBrush)
                    .border(1.dp, border, RoundedCornerShape(18.dp))
                    .clickable { onSelectCity(city) }
                    .testTag("city_chip_${city.name}")
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Hero weather display with large temperature and liquid glass refractions.
 */
@Composable
fun HeroWeatherGlassCard(
    weather: com.example.data.model.CurrentWeatherData,
    useFahrenheit: Boolean
) {
    val displayTemp = if (useFahrenheit) {
        (weather.temperatureCelsius * 9.0 / 5.0 + 32).toInt()
    } else {
        weather.temperatureCelsius.toInt()
    }

    val feelsLike = if (useFahrenheit) {
        (weather.metrics.apparentTemperatureCelsius * 9.0 / 5.0 + 32).toInt()
    } else {
        weather.metrics.apparentTemperatureCelsius.toInt()
    }

    val minToday = weather.daily.firstOrNull()?.minTempCelsius ?: (weather.temperatureCelsius - 3)
    val maxToday = weather.daily.firstOrNull()?.maxTempCelsius ?: (weather.temperatureCelsius + 4)

    val minDisplay = if (useFahrenheit) (minToday * 9.0 / 5.0 + 32).toInt() else minToday.toInt()
    val maxDisplay = if (useFahrenheit) (maxToday * 9.0 / 5.0 + 32).toInt() else maxToday.toInt()

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp),
        contentPadding = 24.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Weather icon with halo
            ConditionIcon(
                condition = weather.condition,
                isDay = weather.isDay,
                size = 76.dp
            )

            // Huge temperature
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$displayTemp",
                    fontSize = 88.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-2).sp
                )
                Text(
                    text = "°",
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Light,
                    color = NeonAqua
                )
            }

            // Condition title
            Text(
                text = weather.condition.descriptionEs,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            // High / Low and Feels Like Pill
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Máx: $maxDisplay°   Mín: $minDisplay°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(Color.White.copy(alpha = 0.4f), CircleShape)
                )

                Text(
                    text = "Sensación: $feelsLike°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonAqua
                )
            }
        }
    }
}

/**
 * Hourly weather glass section.
 */
@Composable
fun HourlyForecastGlassSection(
    hourly: List<com.example.data.model.HourlyForecast>,
    useFahrenheit: Boolean
) {
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp),
        contentPadding = 16.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = NeonAqua,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "PRONÓSTICO POR HORA",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(hourly) { item ->
                    HourlyForecastPill(item = item, useFahrenheit = useFahrenheit)
                }
            }
        }
    }
}

/**
 * 7-Day weather glass section.
 */
@Composable
fun DailyForecastGlassSection(
    daily: List<com.example.data.model.DailyForecast>,
    useFahrenheit: Boolean
) {
    val minWeek = daily.minOfOrNull { it.minTempCelsius } ?: 10.0
    val maxWeek = daily.maxOfOrNull { it.maxTempCelsius } ?: 30.0

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp),
        contentPadding = 18.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = NeonAqua,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "PRONÓSTICO DE 7 DÍAS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            daily.forEach { dayItem ->
                DailyForecastRow(
                    item = dayItem,
                    useFahrenheit = useFahrenheit,
                    minWeekTemp = minWeek,
                    maxWeekTemp = maxWeek
                )
            }
        }
    }
}

/**
 * Atmospheric metrics 2x2 grid.
 */
@Composable
fun AtmosphericMetricsGrid(
    metrics: com.example.data.model.WeatherMetrics,
    useFahrenheit: Boolean
) {
    val feelsLike = if (useFahrenheit) {
        (metrics.apparentTemperatureCelsius * 9.0 / 5.0 + 32).toInt()
    } else {
        metrics.apparentTemperatureCelsius.toInt()
    }

    val windSpeedLabel = if (useFahrenheit) {
        "${(metrics.windSpeedKmh * 0.621371).toInt()} mph"
    } else {
        "${metrics.windSpeedKmh.toInt()} km/h"
    }

    val uvStatus = when {
        metrics.uvIndex < 3.0 -> "Bajo"
        metrics.uvIndex < 6.0 -> "Moderado"
        metrics.uvIndex < 8.0 -> "Alto"
        metrics.uvIndex < 11.0 -> "Muy Alto"
        else -> "Extremo"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: Viento & Humedad
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricGlassCard(
                title = "Viento",
                value = windSpeedLabel,
                subtitle = "Ráfagas leves del NE",
                icon = Icons.Default.Air,
                iconTint = NeonCyan,
                modifier = Modifier.weight(1f),
                visual = {
                    WindCompassVisual(degrees = metrics.windDirectionDegrees)
                }
            )

            MetricGlassCard(
                title = "Humedad",
                value = "${metrics.humidityPercent}%",
                subtitle = if (metrics.humidityPercent > 70) "Ambiente húmedo" else "Humedad agradable",
                icon = Icons.Default.WaterDrop,
                iconTint = NeonAqua,
                modifier = Modifier.weight(1f),
                visual = {
                    HumidityDropletVisual(percentage = metrics.humidityPercent)
                }
            )
        }

        // Row 2: Índice UV & Presión
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricGlassCard(
                title = "Índice UV",
                value = String.format("%.1f", metrics.uvIndex),
                subtitle = "$uvStatus durante el mediodía",
                icon = Icons.Default.WbSunny,
                iconTint = NeonAmber,
                modifier = Modifier.weight(1f)
            )

            MetricGlassCard(
                title = "Presión",
                value = "${metrics.surfacePressureHpa.toInt()} hPa",
                subtitle = "Estable y normal",
                icon = Icons.Default.Compress,
                iconTint = NeonAqua,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: Amanecer / Atardecer
        MetricGlassCard(
            title = "Luz Solar",
            value = "${metrics.sunriseTime} - ${metrics.sunsetTime}",
            subtitle = "Salida y puesta del sol local",
            icon = Icons.Default.WbTwilight,
            iconTint = NeonAmber,
            modifier = Modifier.fillMaxWidth(),
            visual = {
                SunriseSunsetVisual(
                    sunrise = metrics.sunriseTime,
                    sunset = metrics.sunsetTime,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        )
    }
}

/**
 * Search city bottom sheet content.
 */
@Composable
fun SearchCityContent(
    query: String,
    onQueryChange: (String) -> Unit,
    results: List<CityLocation>,
    isSearching: Boolean,
    onSelectCity: (CityLocation) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Buscar Ciudad",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White
                )
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("city_search_input"),
            placeholder = { Text("Ej: Madrid, Buenos Aires, Tokio...", color = Color.White.copy(alpha = 0.5f)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NeonAqua)
            },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = NeonAqua
                    )
                } else if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Limpiar", tint = Color.White)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonAqua,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = NeonAqua
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Text(
            text = if (query.isEmpty()) "CIUDADES SUGERIDAS" else "RESULTADOS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(results) { city ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable { onSelectCity(city) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = city.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        val subtitle = listOfNotNull(city.state, city.country).joinToString(", ")
                        if (subtitle.isNotEmpty()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = NeonAqua.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (results.isEmpty() && !isSearching) {
                item {
                    Text(
                        text = "No se encontraron ciudades",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}
