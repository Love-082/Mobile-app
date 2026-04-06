package screens

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import network.HolidayResponse
import network.RetrofitInstance
import network.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import viewmodel.BirthdayViewModel
import viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: TaskViewModel, padding: PaddingValues, birthdayViewModel: BirthdayViewModel) {

    val context = LocalContext.current

    var selectedDate by remember {
        mutableStateOf(
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        )
    }

    var weatherText by remember { mutableStateOf("Loading...") }
    var cityName by remember { mutableStateOf("") }
    var weatherIconCode by remember { mutableStateOf("") }
    var holidayText by remember { mutableStateOf("Loading...") }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // ✅ DATE SAFE PARSE
    val parsedDate = remember(selectedDate) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(selectedDate)
    }

    val dayName = parsedDate?.let {
        SimpleDateFormat("EEEE", Locale.getDefault()).format(it)
    } ?: ""

    val formattedDateForApi = parsedDate?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
    } ?: ""

    val todayShort = parsedDate?.let {
        SimpleDateFormat("dd MMM", Locale.getDefault()).format(it)
    } ?: ""

    val birthdays = birthdayViewModel.birthdays

    val todayBirthday = birthdays.find { it.date == todayShort }

    // 🔥 API CALLS
    LaunchedEffect(selectedDate) {

        // WEATHER CITY
        RetrofitInstance.api.getWeather(
            city = "Vancouver",
            apiKey = "31308f16de98c7854c50b532bc1a3302"
        ).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                response.body()?.let {
                    weatherText = "${it.main.temp}°C"
                    cityName = it.name
                    weatherIconCode = it.weather[0].icon
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                weatherText = "Weather Error"
            }
        })

        // HOLIDAY API
        RetrofitInstance.holidayApi.getHolidays(
            apiKey = "XumdX2L9TJNed1FVyed8X2Q6giYDqHO1",
            country = "CA",
            year = Calendar.getInstance().get(Calendar.YEAR)
        ).enqueue(object : Callback<HolidayResponse> {

            override fun onResponse(call: Call<HolidayResponse>, response: Response<HolidayResponse>) {

                val match = response.body()?.response?.holidays?.find {
                    it.date.iso.startsWith(formattedDateForApi)
                }

                holidayText = match?.name ?: "No holiday today"
            }

            override fun onFailure(call: Call<HolidayResponse>, t: Throwable) {
                holidayText = "Holiday Error"
            }
        })

        // LOCATION WEATHER
        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    RetrofitInstance.api.getWeatherByLatLon(
                        lat = it.latitude,
                        lon = it.longitude,
                        apiKey = "31308f16de98c7854c50b532bc1a3302"
                    ).enqueue(object : Callback<WeatherResponse> {

                        override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                            response.body()?.let {
                                weatherText = "${it.main.temp}°C"
                                cityName = it.name
                                weatherIconCode = it.weather[0].icon
                            }
                        }

                        override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                            weatherText = "Location Error"
                        }
                    })
                }
            }
        } else {
            weatherText = "Permission needed"
        }
    }

    val tasks = viewModel.getTasksByDate(selectedDate)

    fun showDatePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            context,
            { _, year, month, day ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, day)

                selectedDate = SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                ).format(newCalendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6A5ACD))
                .statusBarsPadding()
                .padding(bottom = 16.dp)
        ) {

            Text(
                text = "Planner",
                color = Color.White,
                fontSize = 26.sp,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Card(modifier = Modifier.clickable { showDatePicker() }) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(dayName, fontSize = 12.sp)
                        Text(selectedDate, fontSize = 16.sp)
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${weatherIconCode}@2x.png",
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                    Text(weatherText, color = Color.White)
                    Text(cityName, color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // 🎉 PRIORITY DISPLAY
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text =
                    todayBirthday?.let { "🎂 ${it.date}'s Birthday" }
                        ?: holidayText.takeIf { it != "No holiday today" }
                        ?: "No special day today",
                modifier = Modifier.padding(12.dp)
            )
        }

        Text(
            text = "Today's Tasks",
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 16.dp)
        )

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(tasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(task.title)
                        Text(task.description)

                        if (task.reminderEnabled) {
                            Text("⏰ ${task.reminderTime}")
                        }
                    }
                }
            }
        }
    }
}