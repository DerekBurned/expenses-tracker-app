package com.example.expenses_tracker_app.data.remote

import com.example.expenses_tracker_app.data.local.entity.SettingsEntity
import com.example.expenses_tracker_app.domain.model.Settings
import io.realm.kotlin.ext.realmDictionaryOf

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

fun SettingsDTO.toEntity() =
    SettingsEntity().apply {
        userId = this@toEntity.userId
        name = this@toEntity.name
        icon = this@toEntity.icon
        email = this@toEntity.email
        darkTheme = this@toEntity.darkTheme
        customCategories = realmDictionaryOf<String>().apply {
            putAll(this@toEntity.customCategories)
        }
    }

