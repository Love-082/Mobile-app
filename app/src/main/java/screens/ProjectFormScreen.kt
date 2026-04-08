package screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.rememberDatePickerState
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import viewmodel.ProjectViewModel

private const val PROJECT_DUE_DATE_PATTERN = "dd MMM yyyy"

private fun formatProjectDueDate(millis: Long): String {
    val formatter = SimpleDateFormat(PROJECT_DUE_DATE_PATTERN, Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

private fun parseProjectDueDateToMillis(date: String): Long? {
    if (date.isBlank()) return null

    return runCatching {
        val formatter = SimpleDateFormat(PROJECT_DUE_DATE_PATTERN, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        formatter.parse(date)?.time
    }.getOrNull()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectFormScreen(
    projectId: Int?,
    viewModel: ProjectViewModel,
    padding: PaddingValues,
    onBack: () -> Unit
) {
    val isAddMode = projectId == null
    val existingProject = remember(projectId, viewModel.projectList) {
        projectId?.let { viewModel.getProjectById(it) }
    }

    var name by remember { mutableStateOf(existingProject?.name ?: "") }
    var description by remember { mutableStateOf(existingProject?.description ?: "") }
    var dueDate by remember { mutableStateOf(existingProject?.dueDate ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }

    val people = remember {
        mutableStateListOf<String>().apply {
            if (isAddMode) {
                add("me")
            } else {
                addAll(existingProject?.people ?: listOf())
            }
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
                        if (isAddMode) "Create New Assignment" else "Edit Assignment",
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
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            OutlinedTextField(
                                value = dueDate,
                                onValueChange = { },
                                label = { Text("Due Date") },
                                placeholder = { Text("Select due date") },
                                modifier = Modifier.fillMaxSize(),
                                readOnly = true,
                                leadingIcon = {
                                    Icon(Icons.Default.DateRange, contentDescription = "Open calendar")
                                },
                                singleLine = true
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        showDatePicker = true
                                    }
                            )
                        }
                    }
                }
            }

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

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val normalizedPeople = people.filter { it.isNotBlank() }.toMutableList()
                        val normalizedTasks = tasks.toMutableList()
                        val keepCompleted = existingProject?.isCompleted == true && normalizedTasks.all { it.isDone }

                        val finalProject = if (isAddMode) {
                            Project(
                                name = name,
                                description = description,
                                dueDate = dueDate,
                                people = normalizedPeople,
                                tasks = normalizedTasks,
                                isCompleted = false
                            )
                        } else {
                            existingProject?.copy(
                                name = name,
                                description = description,
                                dueDate = dueDate,
                                people = normalizedPeople,
                                tasks = normalizedTasks,
                                isCompleted = keepCompleted
                            )
                        } ?: Project(
                            name = name,
                            description = description,
                            dueDate = dueDate,
                            people = normalizedPeople,
                            tasks = normalizedTasks,
                            isCompleted = false
                        )

                        viewModel.updateProject(finalProject)
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A5ACD)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(if (isAddMode) "Create Assignment" else "Update Assignment", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Cancel", color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = parseProjectDueDateToMillis(dueDate)
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                            dueDate = formatProjectDueDate(selectedMillis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("CANCEL")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
