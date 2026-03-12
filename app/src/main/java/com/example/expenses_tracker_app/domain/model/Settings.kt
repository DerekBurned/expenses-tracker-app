package com.example.expenses_tracker_app.domain.model

import com.example.expenses_tracker_app.data.local.entity.SettingsEntity
import com.example.expenses_tracker_app.data.remote.SettingsDTO
import io.realm.kotlin.ext.realmDictionaryOf
import io.realm.kotlin.ext.toRealmDictionary
import kotlin.apply

data class Settings (
    val userId: String,
    val name: String,
    val icon: String,
    val email: String,
    val darkTheme: Boolean?,
    val customCategories: Map<String, String>
)
fun Settings.toEntity() = SettingsEntity().apply {
    userId = this@toEntity.userId
    name = this@toEntity.name
    icon = this@toEntity.icon
    email = this@toEntity.email
    darkTheme = this@toEntity.darkTheme
    customCategories = realmDictionaryOf<String>().apply {
        putAll(this@toEntity.customCategories)
    }
}
fun  Settings.toDTO() = SettingsDTO(
    userId = userId,
    name = name,
    icon = icon,
    email = email,
    darkTheme = darkTheme,
    customCategories = customCategories
)

