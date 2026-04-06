package network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HolidayApi {

    @GET("holidays")
    fun getHolidays(
        @Query("api_key") apiKey: String,
        @Query("country") country: String,
        @Query("year") year: Int
    ): Call<HolidayResponse>
}