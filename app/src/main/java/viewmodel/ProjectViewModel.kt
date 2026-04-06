package viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import data.Project

class ProjectViewModel : ViewModel() {

    var projectList = mutableStateListOf<Project>()

    fun addProject(project: Project) {
        projectList.add(project)
    }

    fun getProjectById(id: Int): Project? {
        return projectList.find { it.id == id }
    }
}