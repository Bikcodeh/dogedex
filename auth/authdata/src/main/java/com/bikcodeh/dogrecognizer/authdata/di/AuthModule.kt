package com.bikcodeh.dogrecognizer.authdata.di

import com.bikcodeh.dogrecognizer.authdata.repository.AuthRepositoryImpl
import com.bikcodeh.dogrecognizer.authdomain.repository.AuthRepository
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
}