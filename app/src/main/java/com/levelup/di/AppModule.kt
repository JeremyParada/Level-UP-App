package com.levelup.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.levelup.data.repository.CartRepository
import com.levelup.data.repository.ProductRepository
import com.levelup.data.repository.ProductRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.levelup.data.auth.AuthRepository
import com.levelup.data.auth.AuthRepositoryImpl
import com.levelup.data.session.SessionManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        @ApplicationContext context: Context,
        gson: Gson
    ): ProductRepository {
        return ProductRepositoryImpl(context, gson)
    }

    @Provides
    @Singleton
    fun provideCartRepository(): CartRepository {
        return CartRepository()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }
}