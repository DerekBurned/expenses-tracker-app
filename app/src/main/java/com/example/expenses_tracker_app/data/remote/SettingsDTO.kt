package com.example.expenses_tracker_app.data.remote

import com.example.expenses_tracker_app.data.local.entity.SettingsEntity
import com.example.expenses_tracker_app.domain.model.Settings

data class SettingsDTO(
    val userId: String,
    val name: String,
    val icon: String,
    val email: String,
    val darkTheme: Boolean?,
    val customCategories: Map<String, String>
)

fun SettingsDTO.toDomain() = Settings(
    userId = userId,
    name = name,
    icon = icon,
    email = email,
    darkTheme = darkTheme,
    customCategories = customCategories
)

fun SettingsDTO.toEntity() = SettingsEntity(
    userId = userId,
    name = name,
    icon = icon,
    email = email,
    darkTheme = darkTheme,
    customCategories = customCategories
)