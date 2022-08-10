package com.bikcodeh.dogrecognizer.scandogdata.di

import com.bikcodeh.dogrecognizer.scandogdata.repository.ScanDogRepositoryImpl
import com.bikcodeh.dogrecognizer.scandogdomain.repository.ScanDogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class ScanDogDataModule {

    @Binds
    @ViewModelScoped
    abstract fun providesScanDogRepository(scanDogRepositoryImpl: ScanDogRepositoryImpl): ScanDogRepository
}