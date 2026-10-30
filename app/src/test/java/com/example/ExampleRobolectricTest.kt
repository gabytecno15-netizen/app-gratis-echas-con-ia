package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.WeatherCondition
import com.example.ui.WeatherViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Clima Glass", appName)
  }

  @Test
  fun `weather condition wmo code mapping`() {
    val clearDay = WeatherCondition.fromWmoCode(0, isDay = true)
    assertEquals(WeatherCondition.CLEAR_DAY, clearDay)

    val rain = WeatherCondition.fromWmoCode(61, isDay = true)
    assertEquals(WeatherCondition.RAIN, rain)

    val thunder = WeatherCondition.fromWmoCode(95, isDay = false)
    assertEquals(WeatherCondition.THUNDERSTORM, thunder)
  }

  @Test
  fun `unit toggle changes fahrenheit state`() {
    val viewModel = WeatherViewModel()
    val initialUnit = viewModel.uiState.value.useFahrenheit
    viewModel.toggleUnit()
    assertEquals(!initialUnit, viewModel.uiState.value.useFahrenheit)
  }
}
