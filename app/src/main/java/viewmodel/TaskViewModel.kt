package viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import data.Task

class TaskViewModel : ViewModel() {

    var taskList = mutableStateListOf<Task>()

    fun addTask(task: Task) {
        taskList.add(task)
    }

    fun getTasksByDate(date: String): List<Task> {
        return taskList.filter { it.dueDate == date }
    }
}