package data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Birthday::class, Project::class, Task::class], version = 3)
@TypeConverters(ProjectConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun birthdayDao(): BirthdayDao
    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao // Room will now implement this for you
}