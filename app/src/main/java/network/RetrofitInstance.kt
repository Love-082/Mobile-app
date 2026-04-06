package network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    val holidayApi: HolidayApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://calendarific.com/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HolidayApi::class.java)
    }
}