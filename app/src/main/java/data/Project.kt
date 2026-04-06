package data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var description: String,
    var dueDate: String,
    @TypeConverters(ProjectConverters::class)
    var people: MutableList<String>,
    @TypeConverters(ProjectConverters::class)
    var tasks: MutableList<ProjectTask>
)