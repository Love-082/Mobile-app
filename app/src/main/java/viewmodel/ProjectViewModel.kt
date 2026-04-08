package viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Project
import data.ProjectDao
import kotlinx.coroutines.launch

class ProjectViewModel(private val dao: ProjectDao) : ViewModel() {

    private val _projectList = mutableStateListOf<Project>()
    val projectList: List<Project> get() = _projectList

    init {
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            val list = dao.getAllProjects()
            _projectList.clear()
            _projectList.addAll(list)
        }
    }

    fun addProject(project: Project) {
        viewModelScope.launch {
            dao.insertProject(project)
            loadProjects()
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch {
            dao.insertProject(project)
            loadProjects()
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            dao.deleteProject(project)
            loadProjects()
        }
    }

    fun getProjectById(id: Int): Project? {
        return _projectList.find { it.id == id }
    }

    fun areAllTasksCompleted(project: Project): Boolean {
        return project.tasks.all { it.isDone }
    }
}
