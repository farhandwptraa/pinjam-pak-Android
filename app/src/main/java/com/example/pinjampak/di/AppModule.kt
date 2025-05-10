package com.example.pinjampak.di

import android.content.Context
import com.example.pinjampak.data.local.dao.ProfileDao
import com.example.pinjampak.data.remote.api.ApiService
import com.example.pinjampak.data.remote.api.AuthApi
import com.example.pinjampak.data.repository.AuthRepositoryImpl
import com.example.pinjampak.data.repository.ProfileRepositoryImpl
import com.example.pinjampak.domain.repository.AuthRepository
import com.example.pinjampak.domain.repository.ProfileRepository
import com.example.pinjampak.utils.Constants.BASE_URL
import com.example.pinjampak.utils.SharedPrefHelper
import com.example.pinjampak.utils.SharedPrefManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Provides
    @Singleton
    fun provideSharedPrefManager(sharedPrefHelper: SharedPrefHelper): SharedPrefManager {
        return SharedPrefManager(sharedPrefHelper)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        apiService: ApiService,
        profileDao: ProfileDao,
        sharedPrefManager: SharedPrefManager
    ): ProfileRepository {
        return ProfileRepositoryImpl(apiService, profileDao, sharedPrefManager)
    }

    // Menambahkan penyediaan ApiService
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // Ganti dengan URL API Anda
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
