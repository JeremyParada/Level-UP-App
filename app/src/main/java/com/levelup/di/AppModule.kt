package com.levelup.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.levelup.data.repository.CartRepository
import com.levelup.data.repository.ProductRepository
import com.levelup.data.repository.ProductRepositoryImpl
import com.levelup.data.AppDatabase
import com.levelup.data.UserDao
import com.levelup.data.repository.UserRepository
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }
}