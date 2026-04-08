package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import data.Project
import viewmodel.ProjectViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectsScreen(
    viewModel: ProjectViewModel,
    padding: PaddingValues,
    onAddClick: () -> Unit,
    onProjectClick: (Int) -> Unit
) {
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    val sortedProjects = viewModel.projectList.sortedBy {
        try {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(it.dueDate)
        } catch (e: Exception) {
            Date()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Assignments",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A5ACD))
            ) {
                Text("New Assignment")
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sortedProjects) { project ->
                val daysLeft = try {
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val due = sdf.parse(project.dueDate)
                    val diff = (due?.time ?: 0) - Date().time
                    (diff / (1000 * 60 * 60 * 24)).toInt()
                } catch (e: Exception) {
                    0
                }

                val cardColor = if (project.isCompleted) Color(0xFFE8F5E9) else Color.White
                val statusText = when {
                    project.isCompleted -> "Completed"
                    daysLeft < 0 -> "Overdue"
                    else -> "$daysLeft days left"
                }
                val statusColor = when {
                    project.isCompleted -> Color(0xFF2E7D32)
                    daysLeft < 0 -> Color.Red
                    else -> Color.Black
                }

                ElevatedCard(
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProjectClick(project.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(60.dp)
                                .background(
                                    when {
                                        project.isCompleted -> Color(0xFF4CAF50)
                                        daysLeft < 3 -> Color.Red
                                        else -> Color(0xFF4CAF50)
                                    }
                                )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = project.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textDecoration = if (project.isCompleted) TextDecoration.LineThrough else null
                            )
                            Text(
                                text = project.description,
                                color = Color.Gray,
                                fontSize = 14.sp,
                                maxLines = 1
                            )

                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Due Date", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        text = project.dueDate,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Status", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        text = statusText,
                                        fontSize = 13.sp,
                                        color = statusColor,
                                        fontWeight = if (project.isCompleted) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { projectToDelete = project }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        projectToDelete?.let { project ->
            AlertDialog(
                onDismissRequest = { projectToDelete = null },
                title = { Text("Delete Assignment?") },
                text = {
                    Text("Are you sure you want to delete \"${project.name}\"? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteProject(project)
                            projectToDelete = null
                        }
                    ) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { projectToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
