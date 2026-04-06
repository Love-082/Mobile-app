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
import viewmodel.TaskViewModel

@Composable
fun TasksScreen(
    viewModel: TaskViewModel,
    padding: PaddingValues,
    onAddClick: () -> Unit,
    onTaskClick: (Int) -> Unit
) {

    Column(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp)
    ) {

        Button(onClick = { onAddClick() }) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(viewModel.taskList) { task ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTaskClick(task.id) }
                        .padding(12.dp)
                ) {

                    Text(task.title)
                    Text(task.description)
                    Text("Due: ${task.dueDate}")

                    Text(
                        if (task.reminderEnabled)
                            "⏰ Reminder ON"
                        else
                            "No Reminder"
                    )

                    Button(
                        onClick = {
                            viewModel.taskList.remove(task)
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