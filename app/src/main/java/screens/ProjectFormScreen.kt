package screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Project
import data.ProjectTask
import viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectFormScreen(
    projectId: Int?,
    viewModel: ProjectViewModel,
    padding: PaddingValues,
    onBack: () -> Unit
) {
    val isAddMode = projectId == null
    val existingProject = remember(projectId) {
        projectId?.let { viewModel.getProjectById(it) }
    }

    // Form States
    var name by remember { mutableStateOf(existingProject?.name ?: "") }
    var description by remember { mutableStateOf(existingProject?.description ?: "") }
    var dueDate by remember { mutableStateOf(existingProject?.dueDate ?: "") }

    val people = remember {
        mutableStateListOf<String>().apply {
            addAll(existingProject?.people ?: listOf())
        }
    }
    val tasks = remember {
        mutableStateListOf<ProjectTask>().apply {
            addAll(existingProject?.tasks ?: listOf())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isAddMode) "Create New Project" else "Edit Project",
                        fontWeight = FontWeight.Bold
                    )
                },
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
        }
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // --- Section 1: Basic Details Card ---
            item {
                Text("General Info", fontWeight = FontWeight.Bold, color = Color(0xFF6A5ACD))
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF8F9FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Project Title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Project Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        OutlinedTextField(
                            value = dueDate,
                            onValueChange = { dueDate = it },
                            label = { Text("Due Date (e.g., Dec 25, 2026)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                        )
                    }
                }
            }

            // --- Section 2: Team Members ---
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Team Members", fontWeight = FontWeight.Bold, color = Color(0xFF6A5ACD), modifier = Modifier.weight(1f))
                    TextButton(onClick = { people.add("") }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Add Member")
                    }
                }
            }

            itemsIndexed(people) { index, person ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = person,
                        onValueChange = { people[index] = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Enter name...") },
                        trailingIcon = {
                            IconButton(onClick = { people.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(20.dp))
                            }
                        }
                    )
                }
            }

            // --- Section 3: Tasks ---
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Tasks Checklist", fontWeight = FontWeight.Bold, color = Color(0xFF6A5ACD), modifier = Modifier.weight(1f))
                    TextButton(onClick = { tasks.add(ProjectTask(tasks.size + 1, "", "", "")) }) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("New Task")
                    }
                }
            }

            itemsIndexed(tasks) { index, task ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        // Checkbox row for task status
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = task.isDone,
                                onCheckedChange = { isChecked ->
                                    tasks[index] = task.copy(isDone = isChecked)
                                }
                            )
                            Text("Mark as Completed", fontSize = 14.sp)
                        }

                        OutlinedTextField(
                            value = task.title,
                            onValueChange = { newValue -> tasks[index] = task.copy(title = newValue) },
                            label = { Text("Task Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Dropdown for Assignment
                        var expanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = task.assignedTo,
                                onValueChange = { },
                                label = { Text("Assign To") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { expanded = !expanded }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth(0.7f)
                            ) {
                                people.filter { it.isNotBlank() }.forEach { person ->
                                    DropdownMenuItem(
                                        text = { Text(person) },
                                        onClick = {
                                            tasks[index] = task.copy(assignedTo = person)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { tasks.removeAt(index) }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                                Text("Remove Task", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // --- Section 4: Actions ---
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val finalProject = if (isAddMode) {
                            Project(name = name, description = description, dueDate = dueDate, people = people.toMutableList(), tasks = tasks.toMutableList())
                        } else {
                            existingProject?.apply {
                                this.name = name
                                this.description = description
                                this.dueDate = dueDate
                                this.people = people.toMutableList()
                                this.tasks = tasks.toMutableList()
                            }!!
                        }
                        viewModel.addProject(finalProject)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A5ACD)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(if (isAddMode) "Create Project" else "Update Project", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Cancel", color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}