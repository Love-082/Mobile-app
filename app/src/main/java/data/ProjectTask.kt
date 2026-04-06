package data

data class ProjectTask(
    val id: Int,
    var title: String,
    var description: String,
    var assignedTo: String,
    var isDone: Boolean = false
)