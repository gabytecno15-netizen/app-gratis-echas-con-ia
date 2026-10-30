package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenMeteoResponse(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "timezone") val timezone: String?,
    @Json(name = "current") val current: CurrentWeatherDto?,
    @Json(name = "hourly") val hourly: HourlyWeatherDto?,
    @Json(name = "daily") val daily: DailyWeatherDto?
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    @Json(name = "time") val time: String,
    @Json(name = "temperature_2m") val temperature: Double,
    @Json(name = "relative_humidity_2m") val humidity: Int,
    @Json(name = "apparent_temperature") val apparentTemperature: Double,
    @Json(name = "is_day") val isDay: Int,
    @Json(name = "precipitation") val precipitation: Double,
    @Json(name = "weather_code") val weatherCode: Int,
    @Json(name = "surface_pressure") val surfacePressure: Double,
    @Json(name = "wind_speed_10m") val windSpeed: Double,
    @Json(name = "wind_direction_10m") val windDirection: Double
)

@JsonClass(generateAdapter = true)
data class HourlyWeatherDto(
    @Json(name = "time") val time: List<String>,
    @Json(name = "temperature_2m") val temperature: List<Double>,
    @Json(name = "relative_humidity_2m") val humidity: List<Int>?,
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int>?,
    @Json(name = "weather_code") val weatherCode: List<Int>,
    @Json(name = "uv_index") val uvIndex: List<Double>?
)

@JsonClass(generateAdapter = true)
data class DailyWeatherDto(
    @Json(name = "time") val time: List<String>,
    @Json(name = "weather_code") val weatherCode: List<Int>,
    @Json(name = "temperature_2m_max") val tempMax: List<Double>,
    @Json(name = "temperature_2m_min") val tempMin: List<Double>,
    @Json(name = "sunrise") val sunrise: List<String>?,
    @Json(name = "sunset") val sunset: List<String>?,
    @Json(name = "uv_index_max") val uvIndexMax: List<Double>?,
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int>?,
    @Json(name = "wind_speed_10m_max") val windSpeedMax: List<Double>?
)

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    @Json(name = "results") val results: List<GeocodingResultDto>?
)

@JsonClass(generateAdapter = true)
data class GeocodingResultDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "country") val country: String?,
    @Json(name = "admin1") val admin1: String?
)

// UI Domain models
data class CityLocation(
    val name: String,
    val country: String,
    val state: String? = null,
    val latitude: Double,
    val longitude: Double,
    val isCurrentLocation: Boolean = false
)

data class HourlyForecast(
    val timeLabel: String,
    val temperatureCelsius: Double,
    val weatherCondition: WeatherCondition,
    val precipitationChance: Int,
    val isNow: Boolean = false
)

data class DailyForecast(
    val dateLabel: String,
    val dayName: String,
    val weatherCondition: WeatherCondition,
    val minTempCelsius: Double,
    val maxTempCelsius: Double,
    val precipitationChance: Int,
    val uvIndex: Double,
    val sunrise: String,
    val sunset: String
)

data class WeatherMetrics(
    val apparentTemperatureCelsius: Double,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val windDirectionDegrees: Double,
    val surfacePressureHpa: Double,
    val precipitationMm: Double,
    val uvIndex: Double,
    val sunriseTime: String,
    val sunsetTime: String
)

data class CurrentWeatherData(
    val location: CityLocation,
    val temperatureCelsius: Double,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val metrics: WeatherMetrics,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val lastUpdated: String
)

enum class WeatherCondition(
    val descriptionEs: String,
    val iconName: String,
    val atmosphericVibe: WeatherVibe
) {
    CLEAR_DAY("Despejado y Soleado", "sunny", WeatherVibe.SUNNY),
    CLEAR_NIGHT("Noche Despejada", "clear_night", WeatherVibe.CLEAR_NIGHT),
    PARTLY_CLOUDY_DAY("Parcialmente Nublado", "partly_cloudy_day", WeatherVibe.PARTLY_CLOUDY),
    PARTLY_CLOUDY_NIGHT("Noche con Nubes", "partly_cloudy_night", WeatherVibe.CLEAR_NIGHT),
    OVERCAST("Cielo Cubierto", "cloudy", WeatherVibe.CLOUDY),
    FOG("Niebla Espesa", "fog", WeatherVibe.FOGGY),
    DRIZZLE("Llovizna Suave", "drizzle", WeatherVibe.RAINY),
    RAIN("Lluvia Constante", "rain", WeatherVibe.RAINY),
    HEAVY_RAIN("Lluvia Torrencial", "heavy_rain", WeatherVibe.STORM),
    SNOW("Nevada", "snow", WeatherVibe.SNOWY),
    THUNDERSTORM("Tormenta Eléctrica", "thunderstorm", WeatherVibe.STORM);

    companion object {
        fun fromWmoCode(code: Int, isDay: Boolean): WeatherCondition {
            return when (code) {
                0 -> if (isDay) CLEAR_DAY else CLEAR_NIGHT
                1, 2 -> if (isDay) PARTLY_CLOUDY_DAY else PARTLY_CLOUDY_NIGHT
                3 -> OVERCAST
                45, 48 -> FOG
                51, 53, 55 -> DRIZZLE
                61, 63 -> RAIN
                65, 80, 81, 82 -> HEAVY_RAIN
                71, 73, 75, 77, 85, 86 -> SNOW
                95, 96, 99 -> THUNDERSTORM
                else -> if (isDay) PARTLY_CLOUDY_DAY else PARTLY_CLOUDY_NIGHT
            }
        }
    }
}

enum class WeatherVibe {
    SUNNY,
    CLEAR_NIGHT,
    PARTLY_CLOUDY,
    CLOUDY,
    RAINY,
    STORM,
    SNOWY,
    FOGGY
}
