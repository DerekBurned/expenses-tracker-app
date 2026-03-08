package com.example.expenses_tracker_app.data.local.entity

import com.example.expenses_tracker_app.data.remote.SettingsDTO
import com.example.expenses_tracker_app.domain.model.Settings
import io.realm.kotlin.ext.realmDictionaryOf
import io.realm.kotlin.types.RealmDictionary
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class SettingsEntity : RealmObject {
    @PrimaryKey
    var userId: String = ""
    var name: String = ""
    var icon: String = ""
    var email: String = ""
    var darkTheme: Boolean? = null
    var customCategories: RealmDictionary<String> = realmDictionaryOf()
}
// Entity → Domain
fun SettingsEntity.toDomain() = Settings(
    userId = userId,
    name = name,
    icon = icon,
    email = email,
    darkTheme = darkTheme,
    customCategories = customCategories.toMap()
)
fun SettingsEntity.toDTO() = SettingsDTO(
    userId = userId,
    name = name,
    icon = icon,
    email = email,
    darkTheme = darkTheme,
    customCategories = customCategories.toMap()
)

