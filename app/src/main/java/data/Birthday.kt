package data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Birthday(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val date: String
)