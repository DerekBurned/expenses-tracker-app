package com.example.expenses_tracker_app.di

import com.example.expenses_tracker_app.data.local.config.RealmConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRealm(): Realm {
        return Realm.open(RealmConfig.configuration)
    }


}