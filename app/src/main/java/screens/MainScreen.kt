package com.example.myapplication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import screens.DashboardScreen
import screens.ProjectFormScreen
import screens.ProjectsScreen
import screens.TaskFormScreen
import screens.TasksScreen
import viewmodel.ProjectViewModel
import viewmodel.TaskViewModel

@Composable
fun MainScreen(viewModel: TaskViewModel) {

    var selectedTab by remember { mutableStateOf(0) }

    val projectViewModel = ProjectViewModel()
    var selectedProjectId by remember { mutableStateOf<Int?>(null) }
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
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Folder, contentDescription = "Projects") },
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
            0 -> DashboardScreen(viewModel, innerPadding)

            1 -> {
                when {
                    isAddingProject -> {
                        ProjectFormScreen(
                            projectId = null,
                            viewModel = projectViewModel,
                            padding = innerPadding,
                            onBack = {
                                isAddingProject = false
                            }
                        )
                    }

                    currentProjectId != null -> {
                        ProjectFormScreen(
                            projectId = currentProjectId,
                            viewModel = projectViewModel,
                            padding = innerPadding,
                            onBack = {
                                currentProjectId = null
                            }
                        )
                    }

                    else -> {
                        ProjectsScreen(
                            viewModel = projectViewModel,
                            padding = innerPadding,
                            onAddClick = {
                                isAddingProject = true
                            },
                            onProjectClick = { id ->
                                currentProjectId = id
                            }
                        )
                    }
                }
            }



            2 -> {
                when {
                    isAddingTask -> {
                        TaskFormScreen(
                            taskId = null,
                            viewModel = viewModel,
                            padding = innerPadding,
                            onBack = { isAddingTask = false }
                        )
                    }

                    currentTaskId != null -> {
                        TaskFormScreen(
                            taskId = currentTaskId,
                            viewModel = viewModel,
                            padding = innerPadding,
                            onBack = { currentTaskId = null }
                        )
                    }

                    else -> {
                        TasksScreen(
                            viewModel = viewModel,
                            padding = innerPadding,
                            onAddClick = { isAddingTask = true },
                            onTaskClick = { id -> currentTaskId = id }
                        )
                    }
                }
            }
        }
    }
}