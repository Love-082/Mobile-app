package screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Task
import viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TasksScreen(
    viewModel: TaskViewModel,
    padding: PaddingValues,
    onAddClick: () -> Unit,
    onTaskClick: (Int) -> Unit
) {
    val tasks by viewModel.allTasks.observeAsState(emptyList())
    val sortedTasks = tasks.sortedBy { it.dueDate }
    var taskToComplete by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    taskToComplete?.let { selectedTask ->
        AlertDialog(
            onDismissRequest = { taskToComplete = null },
            title = { Text("Complete task?") },
            text = { Text("Mark \"${selectedTask.title}\" as completed?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateTask(selectedTask.copy(isCompleted = true))
                        taskToComplete = null
                    }
                ) {
                    Text("Complete")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToComplete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    taskToDelete?.let { selectedTask ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete task?") },
            text = { Text("Are you sure you want to delete \"${selectedTask.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTask(selectedTask)
                        taskToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.padding(padding)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "My Tasks",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A5ACD))
                ) {
                    Text("+ Add Task")
                }
            }

            Spacer(Modifier.padding(top = 8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(sortedTasks, key = { it.id }) { task ->
                    TaskItemCard(
                        task = task,
                        onDelete = { taskToDelete = task },
                        onClick = { onTaskClick(task.id) },
                        onCompleteClick = {
                            if (!task.isCompleted) {
                                taskToComplete = task
                            }
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItemCard(
    task: Task,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    val isOverdue = !task.isCompleted && checkIfOverdue(task.dueDate)
    val displayTime = task.dueTime.ifBlank { task.reminderTime }
    val subtitle = if (displayTime.isBlank()) {
        task.dueDate
    } else {
        "${task.dueDate} at $displayTime"
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.elevatedCardColors(
            containerColor = when {
                task.isCompleted -> Color(0xFFE8F5E9)
                isOverdue -> Color(0xFFFFEBEE)
                else -> Color.White
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCompleteClick) {
                Icon(
                    imageVector = if (task.isCompleted) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = if (task.isCompleted) "Completed task" else "Mark task as completed",
                    tint = if (task.isCompleted) Color(0xFF4CAF50) else Color.Gray,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    style = if (task.isCompleted) {
                        MaterialTheme.typography.titleMedium.copy(
                            textDecoration = TextDecoration.LineThrough
                        )
                    } else {
                        MaterialTheme.typography.titleMedium
                    }
                )

                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                if (isOverdue) {
                    Text(
                        "OVERDUE",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun checkIfOverdue(dateString: String): Boolean {
    if (dateString.isBlank()) return false
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
        val taskDate = LocalDate.parse(dateString, formatter)
        taskDate.isBefore(LocalDate.now())
    } catch (e: Exception) {
        false
    }
}

