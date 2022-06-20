package com.aweirdtrashcan.musicplayer.di

import android.app.Application
import com.aweirdtrashcan.musicplayer.data.MusicExternalStorage
import com.aweirdtrashcan.musicplayer.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModules {

    @Provides
    @Singleton
    fun providesMusicExternalStorage(application: Application) : MusicExternalStorage {
        return MusicExternalStorage(application)
    }

    @Provides
    @Singleton
    fun provideRepository(musicExternalStorage: MusicExternalStorage) : MainRepository {
        return MainRepository(musicExternalStorage)
    }
}