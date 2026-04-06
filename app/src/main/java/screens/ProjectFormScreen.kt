package screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.Project
import data.ProjectTask
import viewmodel.ProjectViewModel

@Composable
fun ProjectFormScreen(
    projectId: Int?,
    viewModel: ProjectViewModel,
    padding: PaddingValues,
    onBack: () -> Unit
) {

    var isEditing by remember { mutableStateOf(projectId == null) }

    val existingProject = projectId?.let { viewModel.getProjectById(it) }

    var name by remember { mutableStateOf(existingProject?.name ?: "") }
    var description by remember { mutableStateOf(existingProject?.description ?: "") }
    var dueDate by remember { mutableStateOf(existingProject?.dueDate ?: "") }

    var people = remember {
        mutableStateListOf<String>().apply {
            addAll(existingProject?.people ?: listOf())
        }
    }

    var tasks = remember {
        mutableStateListOf<ProjectTask>().apply {
            addAll(existingProject?.tasks ?: listOf())
        }
    }

    Column(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp)
    ) {

        LazyColumn {

            item {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project Name") },
                    enabled = isEditing,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    enabled = isEditing,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date") },
                    enabled = isEditing,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("People")

                people.forEachIndexed { index, person ->
                    Row {
                        TextField(
                            value = person,
                            onValueChange = { people[index] = it },
                            enabled = isEditing,
                            modifier = Modifier.weight(1f)
                        )

                        if (isEditing) {
                            Button(onClick = { people.removeAt(index) }) {
                                Text("X")
                            }
                        }
                    }
                }

                if (isEditing) {
                    Button(onClick = { people.add("") }) {
                        Text("Add Person")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Tasks")
            }

            items(tasks, key = { it.id }) { task ->

                Column {

                    TextField(
                        value = task.title,
                        onValueChange = { task.title = it },
                        label = { Text("Task Title") },
                        enabled = isEditing,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = task.description,
                        onValueChange = { task.description = it },
                        label = { Text("Task Description") },
                        enabled = isEditing,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isEditing) {
                        TextField(
                            value = task.assignedTo,
                            onValueChange = { task.assignedTo = it },
                            label = { Text("Assign To") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("Assigned: ${task.assignedTo}")
                    }

                    Row {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = {
                                if (isEditing) task.isDone = it
                            }
                        )
                        Text("Done")
                    }

                    if (isEditing) {
                        Button(onClick = { tasks.removeAll { it.id == task.id } }) {
                            Text("Delete Task")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (isEditing) {
                item {
                    Button(onClick = {
                        tasks.add(
                            ProjectTask(
                                id = tasks.size + 1,
                                title = "",
                                description = "",
                                assignedTo = ""
                            )
                        )
                    }) {
                        Text("Add Task")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {

            if (!isEditing) {
                Button(onClick = { isEditing = true }) {
                    Text("Edit")
                }
            } else {
                Button(onClick = {
                    if (projectId == null) {
                        viewModel.addProject(
                            Project(
                                id = viewModel.projectList.size + 1,
                                name = name,
                                description = description,
                                dueDate = dueDate,
                                people = people,
                                tasks = tasks
                            )
                        )
                    } else {
                        existingProject?.apply {
                            this.name = name
                            this.description = description
                            this.dueDate = dueDate
                            this.people = people.toMutableList()
                            this.tasks = tasks.toMutableList()
                        }
                    }
                    onBack()
                }) {
                    Text(if (projectId == null) "Add" else "Update")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = { onBack() }) {
                    Text("Cancel")
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (projectId != null && !isEditing) {
                Button(onClick = {
                    viewModel.projectList.remove(existingProject)
                    onBack()
                }) {
                    Text("Delete")
                }
            }
        }
    }
}