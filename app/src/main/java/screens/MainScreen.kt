package com.example.myapplication

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import data.BirthdayDao
import data.ProjectDao
import screens.DashboardScreen
import screens.ProjectDetailScreen
import screens.ProjectFormScreen
import screens.ProjectsScreen
import screens.TaskDetailScreen
import screens.TaskFormScreen
import screens.TasksScreen
import viewmodel.BirthdayViewModel
import viewmodel.ProjectViewModel
import viewmodel.TaskViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    taskViewModel: TaskViewModel,
    birthdayDao: BirthdayDao,
    projectDao: ProjectDao // ADD THIS LINE
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Update ProjectViewModel to use the DAO factory
    val projectViewModel: ProjectViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProjectViewModel(projectDao) as T
            }
        }
    )

    // Keep your existing BirthdayViewModel factory below this...
    val birthdayViewModel: BirthdayViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BirthdayViewModel(birthdayDao) as T
            }
        }
    )

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
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Assignments") },
                    label = { Text("Assignments") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Tasks") },
                    label = { Text("Tasks") }
                )
            }
        }
    ) { innerPadding -> // This is the padding provided by Scaffold
        when (selectedTab) {
            0 -> DashboardScreen(
                viewModel = taskViewModel,
                padding = innerPadding, // Correctly passed here
                birthdayViewModel = birthdayViewModel
            )

            1 -> {
                Box(modifier = Modifier.padding(innerPadding)) {
                    when {
                        // 1. Check for FORM MODE first
                        isAddingProject -> {
                            ProjectFormScreen(
                                projectId = currentProjectId, // null for 'Add', ID for 'Edit'
                                viewModel = projectViewModel,
                                padding = innerPadding,
                                onBack = { isAddingProject = false }
                            )
                        }

                        // 2. Check for DETAIL MODE second
                        currentProjectId != null -> {
                            ProjectDetailScreen(
                                projectId = currentProjectId!!,
                                viewModel = projectViewModel,
                                onEdit = { id -> isAddingProject = true },
                                onBack = { currentProjectId = null }
                            )
                        }

                        // 3. Default to the LIST
                        else -> {
                            ProjectsScreen(
                                viewModel = projectViewModel,
                                padding = innerPadding,
                                onProjectClick = { id ->
                                    currentProjectId = id
                                    isAddingProject = false
                                },
                                onAddClick = {
                                    currentProjectId = null
                                    isAddingProject = true
                                }
                            )
                        }
                    }
                }
            }

            // ... inside MainScreen.kt ...
            2 -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        // FORM SCREEN (Add / Edit)
                        isAddingTask -> {
                            TaskFormScreen(
                                taskId = currentTaskId,
                                viewModel = taskViewModel,
                                onBack = {
                                    isAddingTask = false
                                    currentTaskId = null
                                }
                            )
                        }

                        // DETAIL SCREEN
                        currentTaskId != null -> {
                            TaskDetailScreen(
                                taskId = currentTaskId!!,
                                viewModel = taskViewModel,
                                onEdit = { id ->
                                    currentTaskId = id
                                    isAddingTask = true
                                },
                                onBack = { currentTaskId = null }
                            )
                        }

                        // LIST SCREEN
                        else -> {
                            TasksScreen(
                                viewModel = taskViewModel,
                                padding = innerPadding,
                                onAddClick = {
                                    currentTaskId = null
                                    isAddingTask = true
                                },
                                onTaskClick = { id ->
                                    currentTaskId = id
                                    isAddingTask = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}