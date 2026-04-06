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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    // Sort projects by date
    val sortedProjects = viewModel.projectList.sortedBy {
        try {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(it.dueDate)
        } catch (e: Exception) { Date() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFF5F5F5))
    ) {
        // --- Top Bar Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Projects",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A5ACD))
            ) {
                Text("+ New Project")
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sortedProjects) { project ->
                // Calculate Days Left
                val daysLeft = try {
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val due = sdf.parse(project.dueDate)
                    val diff = (due?.time ?: 0) - Date().time
                    (diff / (1000 * 60 * 60 * 24)).toInt()
                } catch (e: Exception) { 0 }

                ElevatedCard(
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProjectClick(project.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Side Status Strip
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(60.dp)
                                .background(if (daysLeft < 3) Color.Red else Color(0xFF4CAF50))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Project Details
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = project.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
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
                                        text = if (daysLeft < 0) "Overdue" else "$daysLeft days left",
                                        fontSize = 13.sp,
                                        color = if (daysLeft < 0) Color.Red else Color.Black
                                    )
                                }
                            }
                        }

                        // Right Corner Delete
                        IconButton(onClick = { viewModel.deleteProject(project) }) {
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
    }
}