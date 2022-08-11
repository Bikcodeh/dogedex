package com.bikcodeh.dogrecognizer.core_common.di

import com.bikcodeh.dogrecognizer.core_common.interceptor.ApiServiceInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceInterceptorModule {
    @Provides
    @Singleton
    fun providesApiServiceInterceptor(): ApiServiceInterceptor = ApiServiceInterceptor
}