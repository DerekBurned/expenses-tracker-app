package com.example.expenses_tracker_app.di

import com.example.expenses_tracker_app.utils.ConnectivityNetworkObserver
import com.example.expenses_tracker_app.utils.internetConnectionObserver.NetworkObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ObserverModule {

    @Binds
    @Singleton
    abstract fun bindNetworkObserver(
        impl: ConnectivityNetworkObserver
    ): NetworkObserver
}