package screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import viewmodel.ProjectViewModel

@Composable
fun ProjectDetailScreen(
    projectId: Int,
    viewModel: ProjectViewModel,
    padding: PaddingValues
) {

    val project = viewModel.getProjectById(projectId)

    Column(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp)
    ) {

        if (project != null) {
            Text("Project: ${project.name}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Description: ${project.description}")
        } else {
            Text("Project not found")
        }
    }
}