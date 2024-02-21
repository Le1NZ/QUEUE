package ru.pokrovskii.queue.data.dataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.pokrovskii.queue.domain.model.Queue

@Dao
interface QueueDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addQueue(queue: Queue)

    @Query("SELECT * FROM queues")
    suspend fun getAllQueues(): List<Queue>

    @Query("DELETE FROM queues WHERE id = :id")
    suspend fun deleteQueue(id: String)

}