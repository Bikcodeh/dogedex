package com.bikcodeh.favoritedata.di

import com.bikcodeh.favoritedata.repository.FavoriteRepositoryImpl
import com.bikcodeh.favoritedomain.repository.FavoriteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class FavoriteDataModule {

    @Binds
    @ViewModelScoped
    abstract fun providesFavoriteRepository(favoriteRepositoryImpl: FavoriteRepositoryImpl): FavoriteRepository
}