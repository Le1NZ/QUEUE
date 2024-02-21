package ru.pokrovskii.queue.data.dataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ru.pokrovskii.queue.domain.model.User

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Query("DELETE FROM users WHERE username = :userName")
    suspend fun deleteUser(userName: String)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users ORDER BY username DESC LIMIT 1")
    suspend fun getCurrUser(): User?

}