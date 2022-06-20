package com.aweirdtrashcan.musicplayer.viewmodels

import androidx.lifecycle.ViewModel
import com.aweirdtrashcan.musicplayer.data.MusicExternalStorage
import com.aweirdtrashcan.musicplayer.models.Songs
import com.aweirdtrashcan.musicplayer.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: MainRepository
) : ViewModel() {

    suspend fun getSongs() : List<Songs> {
        return repository.getSongs()
    }

}