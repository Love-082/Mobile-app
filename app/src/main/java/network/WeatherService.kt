package network

import data.Weather

object WeatherService {

    fun getWeather(): Weather {
        // Temporary fake data (replace with API later)
        return Weather(
            temperature = "18°C",
            condition = "Sunny"
        )
    }
}