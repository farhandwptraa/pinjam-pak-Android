package com.example.pinjampak.di

import android.content.Context
import android.util.Log
import com.example.pinjampak.R
import com.example.pinjampak.data.local.dao.ProfileDao
import com.example.pinjampak.data.remote.api.ApiService
import com.example.pinjampak.data.remote.api.AuthApi
import com.example.pinjampak.data.repository.AuthRepositoryImpl
import com.example.pinjampak.data.repository.ProfileRepositoryImpl
import com.example.pinjampak.data.repository.RegisterCustomerRepositoryImpl
import com.example.pinjampak.domain.repository.AuthRepository
import com.example.pinjampak.domain.repository.ProfileRepository
import com.example.pinjampak.domain.repository.RegisterCustomerRepository
import com.example.pinjampak.utils.Constants.BASE_URL
import com.example.pinjampak.utils.SharedPrefHelper
import com.example.pinjampak.utils.SharedPrefManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
    fun provideAuthRepository(api: AuthApi, apiService: ApiService, sharedPrefManager: SharedPrefManager): AuthRepository = AuthRepositoryImpl(api, apiService, sharedPrefManager)

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

    // Menambahkan penyediaan RegisterCustomerRepository
    @Provides
    @Singleton
    fun provideRegisterCustomerRepository(
        apiService: ApiService,
        sharedPrefManager: SharedPrefManager
    ): RegisterCustomerRepository {
        return RegisterCustomerRepositoryImpl(apiService, sharedPrefManager)
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(
        @ApplicationContext ctx: Context
    ): GoogleSignInClient {
        val clientId = ctx.getString(R.string.server_client_id)
        Log.d("AppModule", "Google Sign-In Client ID: $clientId")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(ctx, gso)
    }
}
