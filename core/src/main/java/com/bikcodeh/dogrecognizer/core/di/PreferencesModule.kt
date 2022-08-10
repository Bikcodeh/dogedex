package com.bikcodeh.dogrecognizer.core.di

import android.content.Context
import com.bikcodeh.dogrecognizer.core.data.repository.DataStoreOperationsImpl
import com.bikcodeh.dogrecognizer.core.domain.repository.DataStoreOperations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun providesPreferences(@ApplicationContext context: Context): DataStoreOperations =
        DataStoreOperationsImpl(context)
}