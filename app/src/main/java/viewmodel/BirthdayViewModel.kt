package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Birthday
import data.BirthdayDao
import kotlinx.coroutines.launch

class BirthdayViewModel(private val dao: BirthdayDao) : ViewModel() {

    var birthdays = mutableListOf<Birthday>()

    fun loadBirthdays() {
        viewModelScope.launch {
            birthdays = dao.getAll().toMutableList()
        }
    }

    fun addBirthday(name: String, date: String) {
        viewModelScope.launch {
            dao.insert(Birthday(name = name, date = date))
            loadBirthdays()
        }
    }
}