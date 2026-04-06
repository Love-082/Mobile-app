package screens

// CRITICAL IMPORTS FOR DATE LOGIC
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

    Scaffold(
        modifier = Modifier.padding(padding),

    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            // Requirement 1: Add task button at top
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

            Spacer(Modifier.height(16.dp))

            // Requirement 1: List of all tasks with delete
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(sortedTasks) { task ->
                    TaskItemCard(
                        task = task,
                        onDelete = { viewModel.deleteTask(task) },
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItemCard(task: Task, onDelete: () -> Unit, onClick: () -> Unit) {
    val isOverdue = checkIfOverdue(task.dueDate)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() },
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isOverdue) Color(0xFFFFEBEE) else Color.White
        )
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(task.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                // Using reminderTime as per your Task.kt
                Text("${task.dueDate} at ${task.reminderTime}", fontSize = 12.sp, color = Color.Gray)

                if (isOverdue) {
                    Text("OVERDUE", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
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