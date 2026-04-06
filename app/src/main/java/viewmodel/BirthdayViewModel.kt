package viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Birthday
import data.BirthdayDao
import kotlinx.coroutines.launch

class BirthdayViewModel(private val dao: BirthdayDao) : ViewModel() {

    // Using mutableStateListOf so Compose detects changes automatically
    private val _birthdays = mutableStateListOf<Birthday>()
    val birthdays: List<Birthday> get() = _birthdays

    init {
        loadBirthdays()
    }

    fun loadBirthdays() {
        viewModelScope.launch {
            val list = dao.getAll()
            _birthdays.clear()
            _birthdays.addAll(list)
        }
    }

    fun addBirthday(name: String, date: String) {
        viewModelScope.launch {
            dao.insert(Birthday(name = name, date = date))
            loadBirthdays()
        }
    }
}