package network

data class WeatherResponse(
    val main: Main,
    val weather: List<WeatherInfo>,
    val name: String
)

data class Main(
    val temp: Double
)

data class WeatherInfo(
    val main: String,
    val icon: String
)