package com.bikcodeh.dogrecognizer.di

import com.bikcodeh.dogrecognizer.data.repository.DogRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun providesDogRepository(): DogRepositoryImpl = DogRepositoryImpl()
}