package data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProjectConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromPeopleList(value: MutableList<String>): String = gson.toJson(value)

    @TypeConverter
    fun toPeopleList(value: String): MutableList<String> {
        val listType = object : TypeToken<MutableList<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromTaskList(value: MutableList<ProjectTask>): String = gson.toJson(value)

    @TypeConverter
    fun toTaskList(value: String): MutableList<ProjectTask> {
        val listType = object : TypeToken<MutableList<ProjectTask>>() {}.type
        return gson.fromJson(value, listType)
    }
}