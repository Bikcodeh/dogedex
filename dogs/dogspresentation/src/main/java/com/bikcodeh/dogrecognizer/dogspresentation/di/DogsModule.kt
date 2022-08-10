package com.bikcodeh.dogrecognizer.dogspresentation.di

import com.bikcodeh.dogrecognizer.dogsdata.repository.DogRepositoryImpl
import com.bikcodeh.dogrecognizer.dogsdomain.repository.DogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
abstract class DogsModule {

    @Binds
    @ViewModelScoped
    abstract fun providesDogRepository(dogRepositoryImpl: DogRepositoryImpl): DogRepository
}