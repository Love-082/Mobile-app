package screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import data.ProjectTask
import kotlinx.coroutines.delay
import viewmodel.ProjectViewModel
import com.example.myapplication.R

private enum class AssignmentStatusAction {
    COMPLETE,
    INCOMPLETE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Int,
    viewModel: ProjectViewModel,
    onEdit: (Int) -> Unit,
    onBack: () -> Unit
) {
    val project = viewModel.getProjectById(projectId) ?: return
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var taskToComplete by remember { mutableStateOf<ProjectTask?>(null) }
    var pendingAssignmentAction by remember { mutableStateOf<AssignmentStatusAction?>(null) }
    var showIncompleteTasksPopup by remember { mutableStateOf(false) }
    var showCompletionAnimation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete assignment?") },
            text = { Text("Are you sure you want to delete this assignment? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        viewModel.deleteProject(project)
                        onBack()
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    taskToComplete?.let { selectedTask ->
        AlertDialog(
            onDismissRequest = { taskToComplete = null },
            title = { Text("Complete task?") },
            text = { Text("Mark \"${selectedTask.title}\" as completed?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updatedTasks = project.tasks.map { task ->
                            if (task.id == selectedTask.id) task.copy(isDone = true) else task
                        }.toMutableList()

                        val shouldStayCompleted = project.isCompleted && updatedTasks.all { it.isDone }
                        val updatedProject = project.copy(
                            tasks = updatedTasks,
                            isCompleted = shouldStayCompleted
                        )
                        viewModel.updateProject(updatedProject)
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

    pendingAssignmentAction?.let { action ->
        val isCompleting = action == AssignmentStatusAction.COMPLETE
        AlertDialog(
            onDismissRequest = { pendingAssignmentAction = null },
            title = { Text(if (isCompleting) "Complete assignment?" else "Mark assignment as incomplete?") },
            text = {
                Text(
                    if (isCompleting) {
                        "All tasks are completed. Do you want to mark this assignment as completed?"
                    } else {
                        "Do you want to change this assignment back to incomplete?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updatedProject = project.copy(isCompleted = isCompleting)
                        viewModel.updateProject(updatedProject)
                        pendingAssignmentAction = null
                        if (isCompleting) {
                            showCompletionAnimation = true
                        }
                    }
                ) {
                    Text(if (isCompleting) "Complete" else "Mark Incomplete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAssignmentAction = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showIncompleteTasksPopup) {
        AlertDialog(
            onDismissRequest = { showIncompleteTasksPopup = false },
            title = { Text("Cannot complete assignment") },
            text = { Text("There are still incomplete tasks in this assignment. Please complete all tasks first.") },
            confirmButton = {
                TextButton(onClick = { showIncompleteTasksPopup = false }) {
                    Text("OK")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Assignment Overview", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEdit(projectId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit assignment")
                        }
                        IconButton(
                            onClick = {
                                if (project.isCompleted) {
                                    pendingAssignmentAction = AssignmentStatusAction.INCOMPLETE
                                } else if (viewModel.areAllTasksCompleted(project)) {
                                    pendingAssignmentAction = AssignmentStatusAction.COMPLETE
                                } else {
                                    showIncompleteTasksPopup = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (project.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (project.isCompleted) "Mark assignment as incomplete" else "Mark assignment as completed",
                                tint = if (project.isCompleted) Color(0xFFB9F6CA) else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(
                            onClick = { showDeleteConfirmation = true },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete assignment",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6A5ACD),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (project.isCompleted) Color(0xFFE8F5E9) else Color(0xFFF8F9FF)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = project.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color(0xFF6A5ACD),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Due: ${project.dueDate}", color = Color.Gray)
                            }
                            if (project.isCompleted) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Completed",
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(project.description, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp))
                }

                item {
                    Text("Team", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        project.people.filter { it.isNotBlank() }.forEach { person ->
                            SuggestionChip(
                                onClick = { },
                                label = { Text(person) }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tasks Checklist", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                items(project.tasks) { task ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (task.isDone) Color(0xFFE8F5E9) else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (!task.isDone) {
                                        taskToComplete = task
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (task.isDone) {
                                        Icons.Default.CheckCircle
                                    } else {
                                        Icons.Default.RadioButtonUnchecked
                                    },
                                    contentDescription = if (task.isDone) "Completed task" else "Mark task as completed",
                                    tint = if (task.isDone) Color(0xFF4CAF50) else Color.Gray,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontWeight = FontWeight.Medium,
                                    style = if (task.isDone) {
                                        MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough)
                                    } else {
                                        MaterialTheme.typography.bodyLarge
                                    }
                                )
                                Text("Assigned to: ${task.assignedTo}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        if (showCompletionAnimation) {
            AssignmentCompletionOverlay(onDismiss = { showCompletionAnimation = false })
        }
    }
}

@Composable
private fun AssignmentCompletionOverlay(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    LaunchedEffect(Unit) {
        delay(2200)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66FFFFFF)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = R.raw.assignment_complete,
            imageLoader = imageLoader,
            contentDescription = "Assignment completed animation",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth(0.9f)
        )
    }
}
