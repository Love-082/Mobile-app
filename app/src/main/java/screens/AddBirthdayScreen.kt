package screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import viewmodel.BirthdayViewModel
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
fun AddBirthdayScreen(viewModel: BirthdayViewModel, onBack: () -> Unit) {

    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })

        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = date, onValueChange = { date = it }, label = { Text("Date (dd MMM)") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.addBirthday(name, date)
            onBack()
        }) {
            Text("Save Birthday")
        }
    }
}