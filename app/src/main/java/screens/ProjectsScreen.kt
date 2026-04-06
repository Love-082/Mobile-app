package screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    val sortedProjects = viewModel.projectList.sortedBy {
        try {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(it.dueDate)
        } catch (e: Exception) {
            Date()
        }
    }

    Column(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp)
    ) {

        Button(onClick = { onAddClick() }) {
            Text("Add New Project")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(sortedProjects) { project ->

                val daysLeft = try {
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val due = sdf.parse(project.dueDate)
                    val diff = due.time - Date().time
                    (diff / (1000 * 60 * 60 * 24)).toInt()
                } catch (e: Exception) {
                    0
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProjectClick(project.id) }
                        .padding(12.dp)
                ) {

                    Text(text = project.name)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = project.description)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = "Due: ${project.dueDate}")

                    Text(text = "Days left: $daysLeft")

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.projectList.remove(project)
                        }
                    ) {
                        Text("Delete")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}