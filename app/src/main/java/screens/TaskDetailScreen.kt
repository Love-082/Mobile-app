package screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    viewModel: TaskViewModel,
    onEdit: (Int) -> Unit,
    onBack: () -> Unit
) {
    val tasks by viewModel.allTasks.observeAsState(emptyList())
    val task = tasks.find { it.id == taskId }

    if (task == null) {
        onBack()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(task.id) }) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                    IconButton(onClick = {
                        viewModel.deleteTask(task)
                        onBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Title: ${task.title}")
            Spacer(Modifier.height(8.dp))

            Text("Description: ${task.description}")
            Spacer(Modifier.height(8.dp))

            Text("Date: ${task.dueDate}")
            Spacer(Modifier.height(8.dp))

            Text("Time: ${task.dueTime}")
        }
    }
}