package com.aweirdtrashcan.musicplayer.repository

import com.aweirdtrashcan.musicplayer.data.MusicExternalStorage
import com.aweirdtrashcan.musicplayer.models.Songs
import javax.inject.Inject

class MainRepository @Inject constructor(
    val musicExternalStorage: MusicExternalStorage
) {
    suspend fun getSongs() : List<Songs> {
        return musicExternalStorage.getSongs()
    }
}