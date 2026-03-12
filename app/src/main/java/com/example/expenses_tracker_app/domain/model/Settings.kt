package com.example.expenses_tracker_app.domain.model

import com.example.expenses_tracker_app.data.local.entity.SettingsEntity
import com.example.expenses_tracker_app.data.remote.SettingsDTO

data class Settings(
    val userId: String,
    val name: String,
    val icon: String,
    val email: String,
    val darkTheme: Boolean?,
    val customCategories: Map<String, String>
)

fun Settings.toEntity() = SettingsEntity(
    userId = userId,
    name = name,
    icon = icon,
    email = email,
    darkTheme = darkTheme,
    customCategories = customCategories
)

fun Settings.toDTO() = SettingsDTO(
    userId = userId,
    name = name,
    icon = icon,
    email = email,
    darkTheme = darkTheme,
    customCategories = customCategories
)