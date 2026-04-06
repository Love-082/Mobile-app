package screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Int,
    viewModel: ProjectViewModel,
    onEdit: (Int) -> Unit,
    onBack: () -> Unit
) {
    val project = viewModel.getProjectById(projectId) ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Project Overview", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6A5ACD),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onEdit(projectId) },
                containerColor = Color(0xFF6A5ACD),
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                text = { Text("Edit Project") } // This makes it much easier to see
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
            // 1. Professional Header Card
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF8F9FF))
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
                            // Fixed: Modifier.size(16.dp) instead of size(16.dp)
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Due: ${project.dueDate}", color = Color.Gray)
                        }
                    }
                }
            }

            // 2. Description Section
            item {
                Text("About Project", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(project.description, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp))
            }

            // 3. Team Members Row
            item {
                Text("Team", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                // Use FlowRow if you have many members, or a scrollable Row
                Row(
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
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

            // 4. Attractive Task List Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tasks Checklist", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            // Fixed: items() requires the correct import
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
                        Checkbox(checked = task.isDone, onCheckedChange = null)
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
}