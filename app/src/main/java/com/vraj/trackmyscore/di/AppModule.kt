package com.vraj.trackmyscore.di

import android.content.Context
import com.vraj.trackmyscore.data.AppDatabase
import com.vraj.trackmyscore.data.dao.PlayerDao
import com.vraj.trackmyscore.util.AppSharedPreferences
import com.vraj.trackmyscore.util.AppSharedPreferencesImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Defines all the classes that need to be provided in the scope of the app.
 * If they are singleton mark them as '@Singleton'.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.buildDatabase(context)

    @Singleton
    @Provides
    fun providePlayerDao(appDatabase: AppDatabase): PlayerDao =
        appDatabase.getPlayerDao()

    @Singleton
    @Provides
    fun provideAppSharedPreferences(@ApplicationContext context: Context): AppSharedPreferences =
        AppSharedPreferencesImpl(context)
}