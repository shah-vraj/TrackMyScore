package com.vraj.trackmyscore.di

import com.vraj.trackmyscore.data.repository.PlayerRepository
import com.vraj.trackmyscore.data.repository.PlayerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindPlayerRepository(playerRepository: PlayerRepositoryImpl): PlayerRepository
}