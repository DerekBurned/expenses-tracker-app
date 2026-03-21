package com.example.expenses_tracker_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expenses_tracker_app.data.remote.SettingsDTO
import com.example.expenses_tracker_app.domain.model.Settings

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val userId: String = "",
    val name: String = "",
    val icon: String = "",
    val email: String = "",
    val darkTheme: Boolean? = null,
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