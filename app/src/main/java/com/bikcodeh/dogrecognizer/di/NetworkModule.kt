package com.bikcodeh.dogrecognizer.di

import com.bikcodeh.dogrecognizer.data.remote.DogApiService
import com.bikcodeh.dogrecognizer.presentation.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesDogService(retrofit: Retrofit): DogApiService {
        return retrofit.create(DogApiService::class.java)
    }
}