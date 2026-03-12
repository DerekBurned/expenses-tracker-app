package com.example.expenses_tracker_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.expenses_tracker_app.data.remote.SettingsDTO
import com.example.expenses_tracker_app.domain.model.Settings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// ── Type converter ─────────────────────────────────────────────────────────────
// Room cannot store Map<String,String> natively; we serialise it to JSON.
class StringMapConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromMap(map: Map<String, String>): String = gson.toJson(map)

    @TypeConverter
    fun toMap(json: String): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(json, type) ?: emptyMap()
    }
}

// ── Entity ─────────────────────────────────────────────────────────────────────
@Entity(tableName = "settings")
@TypeConverters(StringMapConverter::class)
data class SettingsEntity(
    @PrimaryKey
    val userId: String = "",
    val name: String = "",
    val icon: String = "",
    val email: String = "",
    val darkTheme: Boolean? = null,
    // Stored as JSON string via StringMapConverter
    val customCategories: Map<String, String> = emptyMap()
)

// Entity → Domain
fun SettingsEntity.toDomain() = Settings(
    userId = userId,
    name = name,
    icon = icon,
    email = email,
    darkTheme = darkTheme,
    customCategories = customCategories
)

// Entity → DTO
fun SettingsEntity.toDTO() = SettingsDTO(
    userId = userId,
    name = name,
    icon = icon,
    email = email,
    darkTheme = darkTheme,
    customCategories = customCategories
)