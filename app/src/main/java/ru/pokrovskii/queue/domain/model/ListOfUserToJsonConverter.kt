package ru.pokrovskii.queue.domain.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.pokrovskii.queue.domain.DTO.UserDTO


class ListOfUserToJsonConverter {
    @TypeConverter
    fun fromJson(json: String): MutableList<UserDTO> {
        val listType = object : TypeToken<MutableList<UserDTO>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @TypeConverter
    fun toJson(list: MutableList<UserDTO>): String {
        return Gson().toJson(list)
    }
}