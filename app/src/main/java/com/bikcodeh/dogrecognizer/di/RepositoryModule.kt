package com.bikcodeh.dogrecognizer.di

import com.bikcodeh.dogrecognizer.data.local.preferences.DataStoreOperationsImpl
import com.bikcodeh.dogrecognizer.data.repository.AuthRepositoryImpl
import com.bikcodeh.dogrecognizer.data.repository.DogRepositoryImpl
import com.bikcodeh.dogrecognizer.domain.repository.AuthRepository
import com.bikcodeh.dogrecognizer.domain.repository.DataStoreOperations
import com.bikcodeh.dogrecognizer.domain.repository.DogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun providesDogRepository(dogRepositoryImpl: DogRepositoryImpl): DogRepository

    @Binds
    @ViewModelScoped
    abstract fun providesAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @ViewModelScoped
    abstract fun providesDataStoreOperations(dataStoreOperationsImpl: DataStoreOperationsImpl): DataStoreOperations
}