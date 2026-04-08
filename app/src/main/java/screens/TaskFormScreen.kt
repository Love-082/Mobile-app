package screens

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import viewmodel.TaskViewModel

private const val TASK_DUE_DATE_PATTERN = "dd MMM yyyy"

private fun formatTaskDueDate(millis: Long): String {
    val formatter = SimpleDateFormat(TASK_DUE_DATE_PATTERN, Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

private fun parseTaskDueDateToMillis(date: String): Long? {
    if (date.isBlank()) return null

    return runCatching {
        val formatter = SimpleDateFormat(TASK_DUE_DATE_PATTERN, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        formatter.parse(date)?.time
    }.getOrNull()
}

private fun formatTaskDueTime(hour: Int, minute: Int, is24HourFormat: Boolean): String {
    return if (is24HourFormat) {
        String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    } else {
        val period = if (hour >= 12) "PM" else "AM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, period)
    }
}

private fun parseTaskDueTime(time: String): Pair<Int, Int>? {
    if (time.isBlank()) return null

    val patterns = listOf("HH:mm", "H:mm", "hh:mm a", "h:mm a")

    for (pattern in patterns) {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        formatter.isLenient = false

        val parsedDate = runCatching { formatter.parse(time) }.getOrNull() ?: continue
        val calendar = Calendar.getInstance().apply { timeInMillis = parsedDate.time }
        return calendar.get(Calendar.HOUR_OF_DAY) to calendar.get(Calendar.MINUTE)
    }

    return null
}

@Composable
private fun SystemTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    is24HourFormat: Boolean,
    onDismiss: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit
) {
    val context = LocalContext.current

    DisposableEffect(context, initialHour, initialMinute, is24HourFormat) {
        val dialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                onTimeSelected(selectedHour, selectedMinute)
                onDismiss()
            },
            initialHour,
            initialMinute,
            is24HourFormat
        )

        dialog.setOnDismissListener {
            onDismiss()
        }

        dialog.show()

        onDispose {
            dialog.dismiss()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    taskId: Int?,
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val tasks by viewModel.allTasks.observeAsState(emptyList())
    val context = LocalContext.current
    val is24HourFormat = remember(context) {
        android.text.format.DateFormat.is24HourFormat(context)
    }

    val existingTask = remember(taskId, tasks) {
        tasks.find { it.id == taskId }
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var dueTime by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var attemptedSubmit by remember { mutableStateOf(false) }

    LaunchedEffect(existingTask?.id, taskId) {
        if (taskId == null) {
            title = ""
            description = ""
            dueDate = ""
            dueTime = ""
            isCompleted = false
        } else {
            existingTask?.let {
                title = it.title
                description = it.description
                dueDate = it.dueDate
                dueTime = it.dueTime
                isCompleted = it.isCompleted
            }
        }
    }

    val initialSelectedDateMillis = remember(dueDate) {
        parseTaskDueDateToMillis(dueDate)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis
    )

    val initialTime = remember(dueTime) {
        parseTaskDueTime(dueTime)
    }
    val currentTime = remember {
        Calendar.getInstance()
    }

    val initialHour = initialTime?.first ?: currentTime.get(Calendar.HOUR_OF_DAY)
    val initialMinute = initialTime?.second ?: currentTime.get(Calendar.MINUTE)

    val titleError = attemptedSubmit && title.isBlank()
    val dueDateError = attemptedSubmit && dueDate.isBlank()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                            dueDate = formatTaskDueDate(selectedMillis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        SystemTimePickerDialog(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24HourFormat = is24HourFormat,
            onDismiss = { showTimePicker = false },
            onTimeSelected = { selectedHour, selectedMinute ->
                dueTime = formatTaskDueTime(
                    hour = selectedHour,
                    minute = selectedMinute,
                    is24HourFormat = is24HourFormat
                )
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (taskId == null) "New Task" else "Edit Task",
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = titleError,
                supportingText = {
                    if (titleError) {
                        Text("Task name is required")
                    }
                }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        OutlinedTextField(
                            value = dueDate,
                            onValueChange = { },
                            label = { Text("Date") },
                            placeholder = { Text("06 Apr 2026") },
                            modifier = Modifier.fillMaxSize(),
                            readOnly = true,
                            leadingIcon = {
                                Icon(Icons.Default.DateRange, contentDescription = "Open calendar")
                            },
                            singleLine = true,
                            isError = dueDateError
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

                    if (dueDateError) {
                        Text(
                            text = "Date is required",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    OutlinedTextField(
                        value = dueTime,
                        onValueChange = { },
                        label = { Text("Time") },
                        placeholder = { Text(if (is24HourFormat) "14:00" else "2:00 PM") },
                        modifier = Modifier.fillMaxSize(),
                        readOnly = true,
                        leadingIcon = {
                            Icon(Icons.Default.AccessTime, contentDescription = "Open time picker")
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
                                showTimePicker = true
                            }
                    )
                }
            }

            if (taskId != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Task Status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isCompleted) "Completed" else "Incomplete",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Switch(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        attemptedSubmit = true

                        if (title.isBlank() || dueDate.isBlank()) {
                            return@Button
                        }

                        val trimmedTitle = title.trim()

                        if (taskId == null) {
                            viewModel.addTask(
                                Task(
                                    title = trimmedTitle,
                                    description = description,
                                    dueDate = dueDate,
                                    dueTime = dueTime
                                )
                            )
                        } else {
                            existingTask?.let {
                                viewModel.updateTask(
                                    it.copy(
                                        title = trimmedTitle,
                                        description = description,
                                        dueDate = dueDate,
                                        dueTime = dueTime,
                                        isCompleted = isCompleted
                                    )
                                )
                            }
                        }
                        onBack()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A5ACD)
                    )
                ) {
                    Text(if (taskId == null) "Create" else "Update")
                }
            }
        }
    }
}
