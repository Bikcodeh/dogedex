package com.bikcodeh.dogrecognizer.authpresentation.di

import com.bikcodeh.dogrecognizer.authdata.local.preferences.DataStoreOperationsImpl
import com.bikcodeh.dogrecognizer.authdata.repository.AuthRepositoryImpl
import com.bikcodeh.dogrecognizer.authdomain.repository.AuthRepository
import com.bikcodeh.dogrecognizer.authdomain.repository.DataStoreOperations
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AuthModule {

    @Binds
    @ViewModelScoped
    abstract fun providesAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @ViewModelScoped
    abstract fun providesDataStoreOperations(dataStoreOperationsImpl: DataStoreOperationsImpl): DataStoreOperations
}