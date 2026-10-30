package com.example.data.repository

import com.example.data.api.NetworkClient
import com.example.data.api.WeatherApiService
import com.example.data.model.CityLocation
import com.example.data.model.CurrentWeatherData
import com.example.data.model.DailyForecast
import com.example.data.model.HourlyForecast
import com.example.data.model.WeatherCondition
import com.example.data.model.WeatherMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeatherRepository(
    private val apiService: WeatherApiService = NetworkClient.apiService
) {

    val popularCities = listOf(
        CityLocation("Madrid", "España", "Comunidad de Madrid", 40.4168, -3.7038),
        CityLocation("Ciudad de México", "México", "CDMX", 19.4326, -99.1332),
        CityLocation("Buenos Aires", "Argentina", "CABA", -34.6037, -58.3816),
        CityLocation("Bogotá", "Colombia", "Cundinamarca", 4.7110, -74.0721),
        CityLocation("Barcelona", "España", "Cataluña", 41.3879, 2.1699),
        CityLocation("Santiago", "Chile", "Región Metropolitana", -33.4489, -70.6693),
        CityLocation("Miami", "Estados Unidos", "Florida", 25.7617, -80.1918)
    )

    suspend fun getWeatherData(location: CityLocation): Result<CurrentWeatherData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getForecast(location.latitude, location.longitude)
            val current = response.current ?: throw IllegalStateException("No current weather data")
            val isDay = current.isDay == 1
            val condition = WeatherCondition.fromWmoCode(current.weatherCode, isDay)

            // Format Hourly (next 24 hours starting around current hour)
            val hourlyList = mutableListOf<HourlyForecast>()
            response.hourly?.let { hourlyDto ->
                val timeStrings = hourlyDto.time
                val temps = hourlyDto.temperature
                val codes = hourlyDto.weatherCode
                val precipProbs = hourlyDto.precipitationProbability

                val currentTimeIso = current.time.substringBefore(":")
                var startIndex = timeStrings.indexOfFirst { it.startsWith(currentTimeIso) }
                if (startIndex == -1) startIndex = 0

                val endIndex = minOf(startIndex + 24, timeStrings.size)
                for (i in startIndex until endIndex) {
                    val rawTime = timeStrings[i]
                    val hourPart = parseHourLabel(rawTime)
                    val isFirst = (i == startIndex)
                    val hCode = codes.getOrElse(i) { current.weatherCode }
                    val hIsDay = isDayTimeFromHour(rawTime)
                    val hCondition = WeatherCondition.fromWmoCode(hCode, hIsDay)
                    val hTemp = temps.getOrElse(i) { current.temperature }
                    val hPrecip = precipProbs?.getOrNull(i) ?: 0

                    hourlyList.add(
                        HourlyForecast(
                            timeLabel = if (isFirst) "Ahora" else hourPart,
                            temperatureCelsius = hTemp,
                            weatherCondition = hCondition,
                            precipitationChance = hPrecip,
                            isNow = isFirst
                        )
                    )
                }
            }

            // Format Daily (7 days)
            val dailyList = mutableListOf<DailyForecast>()
            response.daily?.let { dailyDto ->
                val times = dailyDto.time
                val maxTemps = dailyDto.tempMax
                val minTemps = dailyDto.tempMin
                val codes = dailyDto.weatherCode
                val precips = dailyDto.precipitationProbabilityMax
                val uvList = dailyDto.uvIndexMax
                val sunrises = dailyDto.sunrise
                val sunsets = dailyDto.sunset

                for (i in times.indices) {
                    val rawDate = times[i]
                    val dayName = formatDayName(rawDate, i)
                    val dayCondition = WeatherCondition.fromWmoCode(codes.getOrElse(i) { current.weatherCode }, true)
                    dailyList.add(
                        DailyForecast(
                            dateLabel = rawDate,
                            dayName = dayName,
                            weatherCondition = dayCondition,
                            minTempCelsius = minTemps.getOrElse(i) { current.temperature - 3.0 },
                            maxTempCelsius = maxTemps.getOrElse(i) { current.temperature + 4.0 },
                            precipitationChance = precips?.getOrNull(i) ?: 0,
                            uvIndex = uvList?.getOrNull(i) ?: 4.0,
                            sunrise = sunrises?.getOrNull(i)?.substringAfter("T")?.take(5) ?: "06:45",
                            sunset = sunsets?.getOrNull(i)?.substringAfter("T")?.take(5) ?: "19:30"
                        )
                    )
                }
            }

            val todaySunrise = response.daily?.sunrise?.firstOrNull()?.substringAfter("T")?.take(5) ?: "07:15"
            val todaySunset = response.daily?.sunset?.firstOrNull()?.substringAfter("T")?.take(5) ?: "19:45"
            val todayUv = response.daily?.uvIndexMax?.firstOrNull() ?: 5.2

            val weatherData = CurrentWeatherData(
                location = location,
                temperatureCelsius = current.temperature,
                condition = condition,
                isDay = isDay,
                metrics = WeatherMetrics(
                    apparentTemperatureCelsius = current.apparentTemperature,
                    humidityPercent = current.humidity,
                    windSpeedKmh = current.windSpeed,
                    windDirectionDegrees = current.windDirection,
                    surfacePressureHpa = current.surfacePressure,
                    precipitationMm = current.precipitation,
                    uvIndex = todayUv,
                    sunriseTime = todaySunrise,
                    sunsetTime = todaySunset
                ),
                hourly = hourlyList,
                daily = dailyList,
                lastUpdated = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            )
            Result.success(weatherData)
        } catch (e: Exception) {
            e.printStackTrace()
            // Provide sensible fallback based on popular cities if offline or on timeout
            Result.failure(e)
        }
    }

    suspend fun searchCities(query: String): List<CityLocation> = withContext(Dispatchers.IO) {
        if (query.trim().length < 2) return@withContext emptyList()
        try {
            val response = apiService.searchLocations(name = query.trim())
            response.results?.map {
                CityLocation(
                    name = it.name,
                    country = it.country ?: "",
                    state = it.admin1,
                    latitude = it.latitude,
                    longitude = it.longitude
                )
            } ?: emptyList()
        } catch (e: Exception) {
            popularCities.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.country.contains(query, ignoreCase = true)
            }
        }
    }

    private fun parseHourLabel(isoString: String): String {
        return try {
            val hourStr = isoString.substringAfter("T").take(2)
            "$hourStr:00"
        } catch (_: Exception) {
            "12:00"
        }
    }

    private fun isDayTimeFromHour(isoString: String): Boolean {
        return try {
            val hour = isoString.substringAfter("T").take(2).toInt()
            hour in 7..19
        } catch (_: Exception) {
            true
        }
    }

    private fun formatDayName(isoDate: String, index: Int): String {
        if (index == 0) return "Hoy"
        if (index == 1) return "Mañana"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(isoDate) ?: return "Día"
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            when (dayOfWeek) {
                Calendar.MONDAY -> "Lunes"
                Calendar.TUESDAY -> "Martes"
                Calendar.WEDNESDAY -> "Miércoles"
                Calendar.THURSDAY -> "Jueves"
                Calendar.FRIDAY -> "Viernes"
                Calendar.SATURDAY -> "Sábado"
                Calendar.SUNDAY -> "Domingo"
                else -> "Día"
            }
        } catch (_: Exception) {
            "Día $index"
        }
    }
}
