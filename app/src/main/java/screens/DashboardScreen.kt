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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
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
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var selectedDate by remember {
        mutableStateOf(SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()))
    }

    var weatherText by remember { mutableStateOf("Loading...") }
    var cityName by remember { mutableStateOf("Vancouver") }
    var weatherIconCode by remember { mutableStateOf("") }
    var holidayText by remember { mutableStateOf("Checking holidays...") }

    // Logic for parsing dates
    val parsedDate = remember(selectedDate) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(selectedDate) ?: Date()
    }
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(parsedDate)
    val formattedDateForApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsedDate)
    val todayShort = SimpleDateFormat("dd MMM", Locale.getDefault()).format(parsedDate)

    val birthdays = birthdayViewModel.birthdays
    val todayBirthday = birthdays.find { it.date == todayShort }

    LaunchedEffect(selectedDate) {
        // 1. Fetch Holiday Data
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
                holidayText = "Holiday service unavailable"
            }
        })

        // 2. Fetch Location-Based Weather
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { loc ->
                    RetrofitInstance.api.getWeatherByLatLon(
                        lat = loc.latitude,
                        lon = loc.longitude,
                        apiKey = "31308f16de98c7854c50b532bc1a3302"
                    ).enqueue(object : Callback<WeatherResponse> {
                        override fun onResponse(call: Call<WeatherResponse>, resp: Response<WeatherResponse>) {
                            resp.body()?.let { data ->
                                weatherText = "${data.main.temp.toInt()}°C"
                                cityName = data.name
                                weatherIconCode = data.weather.firstOrNull()?.icon ?: ""
                            }
                        }
                        override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                            weatherText = "Weather Error"
                        }
                    })
                }
            }
        } else {
            // Default to City Weather if no permission
            RetrofitInstance.api.getWeather("Vancouver", "31308f16de98c7854c50b532bc1a3302")
                .enqueue(object : Callback<WeatherResponse> {
                    override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                        response.body()?.let {
                            weatherText = "${it.main.temp.toInt()}°C"
                            cityName = it.name
                            weatherIconCode = it.weather.firstOrNull()?.icon ?: ""
                        }
                    }
                    override fun onFailure(p0: Call<WeatherResponse>, p1: Throwable) { weatherText = "N/A" }
                })
        }
    }

    val tasks = viewModel.getTasksByDate(selectedDate)

    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        // --- PROFESSIONAL HEADER ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6A5ACD))
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Text("My Planner", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date Picker Card
                Card(modifier = Modifier.clickable {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(context, { _, y, m, d ->
                        val newCal = Calendar.getInstance().apply { set(y, m, d) }
                        selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(newCal.time)
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                }) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(dayName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(selectedDate, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }

                // Weather Display
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(weatherText, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(cityName, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                    if (weatherIconCode.isNotEmpty()) {
                        AsyncImage(
                            model = "https://openweathermap.org/img/wn/${weatherIconCode}@2x.png",
                            contentDescription = null,
                            modifier = Modifier.size(45.dp)
                        )
                    }
                }
            }
        }

        // --- SPECIAL EVENT SECTION ---
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            val displayMessage = when {
                todayBirthday != null -> "🎂 It's ${todayBirthday.name}'s Birthday!"
                holidayText != "No holiday today" -> "🎉 $holidayText"
                else -> "✨ You have ${tasks.size} tasks for today"
            }
            Text(displayMessage, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
        }

        Text("Today's Schedule", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))

        // --- TASK LIST ---
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(tasks) { task ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(task.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(task.description, color = Color.Gray, fontSize = 14.sp)
                        if (task.reminderEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("⏰ ${task.reminderTime}", fontSize = 12.sp, color = Color(0xFF6A5ACD))
                        }
                    }
                }
            }
        }
    }
}