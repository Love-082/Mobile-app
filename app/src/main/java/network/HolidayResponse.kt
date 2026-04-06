package network

data class HolidayResponse(
    val response: HolidayData
)

data class HolidayData(
    val holidays: List<Holiday>
)

data class Holiday(
    val name: String,
    val date: HolidayDate
)

data class HolidayDate(
    val iso: String
)