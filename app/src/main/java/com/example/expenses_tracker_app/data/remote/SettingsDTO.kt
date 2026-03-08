package com.example.expenses_tracker_app.data.remote

import com.example.expenses_tracker_app.data.local.entity.SettingsEntity
import com.example.expenses_tracker_app.domain.model.Settings
import com.google.gson.annotations.SerializedName
import io.realm.kotlin.ext.realmDictionaryOf

data class SettingsDTO(
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("email") val email: String,
    @SerializedName("darkTheme") val darkTheme: Boolean?,
    @SerializedName("customCategories") val customCategories: Map<String, String>
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

