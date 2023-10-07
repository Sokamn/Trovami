package com.sokamn.trovami.di

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {/*
    @Singleton
    @Provides
    fun providesDatastore(
        @ApplicationContext context: Context
    ): DataStore = DataStoreImpl(context)*/
}