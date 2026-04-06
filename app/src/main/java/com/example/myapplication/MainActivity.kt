package com.example.myapplication

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import data.AppDatabase
import viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-database"
        )
            .fallbackToDestructiveMigration()
            .build()

        val birthdayDao = db.birthdayDao()
        val projectDao = db.projectDao()
        val taskDao = db.taskDao()

        setContent {

            val taskViewModel: TaskViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TaskViewModel(taskDao) as T
                    }
                }
            )

            MainScreen(
                taskViewModel = taskViewModel,
                birthdayDao = birthdayDao,
                projectDao = projectDao
            )
        }
    }
}