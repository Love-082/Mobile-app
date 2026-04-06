package data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BirthdayDao {

    @Insert
    suspend fun insert(birthday: Birthday)

    @Query("SELECT * FROM Birthday")
    suspend fun getAll(): List<Birthday>
}