package screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.Task
import viewmodel.TaskViewModel

@Composable
fun TaskFormScreen(
    taskId: Int?,
    viewModel: TaskViewModel,
    padding: PaddingValues,
    onBack: () -> Unit
) {

    var isEditing by remember { mutableStateOf(taskId == null) }

    val existingTask = taskId?.let {
        viewModel.taskList.find { t -> t.id == it }
    }

    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var dueDate by remember { mutableStateOf(existingTask?.dueDate ?: "") }

    var reminderEnabled by remember { mutableStateOf(existingTask?.reminderEnabled ?: false) }
    var reminderTime by remember { mutableStateOf(existingTask?.reminderTime ?: "") }

    Column(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp)
    ) {

        LazyColumn {

            item {

                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Name") },
                    enabled = isEditing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!isEditing) isEditing = true
                        }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    enabled = isEditing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!isEditing) isEditing = true
                        }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date") },
                    enabled = isEditing,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Checkbox(
                        checked = reminderEnabled,
                        onCheckedChange = {
                            if (isEditing) reminderEnabled = it
                        }
                    )
                    Text(if (reminderEnabled) "Reminder ON" else "Add Reminder")
                }

                if (reminderEnabled && isEditing) {
                    TextField(
                        value = reminderTime,
                        onValueChange = { reminderTime = it },
                        label = { Text("Reminder Time") },
                        modifier = Modifier.fillMaxWidth()
                    )
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

                    if (taskId == null) {
                        viewModel.addTask(
                            Task(
                                id = viewModel.taskList.size + 1,
                                title = title,
                                description = description,
                                dueDate = dueDate,
                                reminderEnabled = reminderEnabled,
                                reminderTime = reminderTime
                            )
                        )
                    } else {
                        existingTask?.apply {
                            this.title = title
                            this.description = description
                            this.dueDate = dueDate
                            this.reminderEnabled = reminderEnabled
                            this.reminderTime = reminderTime
                        }
                    }

                    onBack()

                }) {
                    Text(if (taskId == null) "Add" else "Update")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = { onBack() }) {
                    Text("Cancel")
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (taskId != null && !isEditing) {
                Button(onClick = {
                    viewModel.taskList.remove(existingTask)
                    onBack()
                }) {
                    Text("Delete")
                }
            }
        }
    }
}