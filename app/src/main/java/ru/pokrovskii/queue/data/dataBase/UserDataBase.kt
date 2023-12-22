package ru.pokrovskii.queue.data.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.pokrovskii.queue.domain.model.User
import javax.inject.Singleton

@Singleton
@Database(
    entities = [User::class],
    version = 1,
    exportSchema = false
)
abstract class UserDataBase : RoomDatabase() {

    abstract val userDAO: UserDAO

    companion object {
        const val DATABASE_NAME = "user"
    }

}