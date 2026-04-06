package data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Birthday::class, Project::class], version = 2) // Added Project here
@TypeConverters(ProjectConverters::class) // This line is required!
abstract class AppDatabase : RoomDatabase() {
    abstract fun birthdayDao(): BirthdayDao
    abstract fun projectDao(): ProjectDao
}