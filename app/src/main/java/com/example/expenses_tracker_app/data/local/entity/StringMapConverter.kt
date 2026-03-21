package com.example.expenses_tracker_app.data.local.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringMapConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromMap(map: Map<String, String>?): String {
        return gson.toJson(map ?: emptyMap<String, String>())
    }

    @TypeConverter
    fun toMap(json: String?): Map<String, String> {
        if (json == null) return emptyMap()
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(json, type) ?: emptyMap()
    }
}