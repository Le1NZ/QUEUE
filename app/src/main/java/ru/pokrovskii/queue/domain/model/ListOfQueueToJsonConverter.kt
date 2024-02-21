package ru.pokrovskii.queue.domain.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.pokrovskii.queue.domain.DTO.QueueDTO


class ListOfQueueToJsonConverter {
    @TypeConverter
    fun fromJson(json: String): MutableList<QueueDTO> {
        val listType = object : TypeToken<MutableList<QueueDTO>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @TypeConverter
    fun toJson(list: MutableList<QueueDTO>): String {
        return Gson().toJson(list)
    }
}