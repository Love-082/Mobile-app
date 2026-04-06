package com.example.myapplication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import data.BirthdayDao
import screens.DashboardScreen
import screens.ProjectFormScreen
import screens.ProjectsScreen
import screens.TaskFormScreen
import screens.TasksScreen
import viewmodel.BirthdayViewModel
import viewmodel.ProjectViewModel
import viewmodel.TaskViewModel

@Composable
fun MainScreen(taskViewModel: TaskViewModel, birthdayDao: BirthdayDao) {

    var selectedTab by remember { mutableIntStateOf(0) }

    // Factory to handle the BirthdayDao dependency
    val birthdayViewModel: BirthdayViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BirthdayViewModel(birthdayDao) as T
            }
        }
    )

    val projectViewModel: ProjectViewModel = viewModel()

    // Navigation states
    var currentProjectId by remember { mutableStateOf<Int?>(null) }
    var isAddingProject by remember { mutableStateOf(false) }
    var currentTaskId by remember { mutableStateOf<Int?>(null) }
    var isAddingTask by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Projects") },
                    label = { Text("Projects") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Tasks") },
                    label = { Text("Tasks") }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> DashboardScreen(
                viewModel = taskViewModel,
                padding = innerPadding,
                birthdayViewModel = birthdayViewModel
            )

            1 -> {
                when {
                    isAddingProject -> ProjectFormScreen(null, projectViewModel, innerPadding) { isAddingProject = false }
                    currentProjectId != null -> ProjectFormScreen(currentProjectId, projectViewModel, innerPadding) { currentProjectId = null }
                    else -> ProjectsScreen(projectViewModel, innerPadding, { isAddingProject = true }, { currentProjectId = it })
                }
            }

            2 -> {
                when {
                    isAddingTask -> TaskFormScreen(null, taskViewModel, innerPadding) { isAddingTask = false }
                    currentTaskId != null -> TaskFormScreen(currentTaskId, taskViewModel, innerPadding) { currentTaskId = null }
                    else -> TasksScreen(taskViewModel, innerPadding, { isAddingTask = true }, { currentTaskId = it })
                }
            }
        }
    }
}