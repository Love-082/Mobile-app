package data

data class Project(
    val id: Int,
    var name: String,
    var description: String,
    var dueDate: String,
    var people: MutableList<String>,
    var tasks: MutableList<ProjectTask>
)