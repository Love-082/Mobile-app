package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import data.AppDatabase // Ensure this matches your database class name
import viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize the Database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-database"
        ).build()

        // 2. Get the DAO
        val birthdayDao = db.birthdayDao()

        setContent {
            // TaskViewModel usually doesn't need a DAO in your current setup,
            // but if it does, apply the same Factory logic as BirthdayViewModel.
            val taskViewModel = TaskViewModel()

            // 3. Pass everything to MainScreen
            MainScreen(
                taskViewModel = taskViewModel,
                birthdayDao = birthdayDao
            )
        }
    }
}