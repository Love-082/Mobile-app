package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import data.Task
import data.TaskDao
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks().asLiveData()

    fun addTask(task: Task) {
        viewModelScope.launch { taskDao.insertTask(task) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { taskDao.deleteTask(task) }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { taskDao.updateTask(task) }
    }

    fun getTasksByDate(date: String): List<Task> {
        return allTasks.value?.filter { it.dueDate == date } ?: emptyList()
    }
}