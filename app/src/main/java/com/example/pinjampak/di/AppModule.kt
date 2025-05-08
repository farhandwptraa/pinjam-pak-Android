package com.example.pinjampak.di

import android.content.Context
import com.example.pinjampak.data.remote.api.AuthApi
import com.example.pinjampak.data.repository.AuthRepositoryImpl
import com.example.pinjampak.domain.repository.AuthRepository
import com.example.pinjampak.utils.SharedPrefHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi): AuthRepository = AuthRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPrefHelper {
        return SharedPrefHelper(context)
    }
}