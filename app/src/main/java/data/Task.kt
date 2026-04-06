package data

data class Task(
    val id: Int,
    var title: String,
    var description: String,
    var dueDate: String = "",
    var reminderEnabled: Boolean = false,
    var reminderTime: String = ""
)