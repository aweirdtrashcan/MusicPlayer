package com.aweirdtrashcan.musicplayer.data

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import com.aweirdtrashcan.musicplayer.models.Songs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicExternalStorage @Inject constructor(
    private val application : Application
) {
    suspend fun getSongs() : List<Songs> {
        return withContext(Dispatchers.IO){
            val contentResolver = application.contentResolver

            val queryUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION
            )

            val songs = mutableListOf<Songs>()
            contentResolver.query(
                queryUri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val albumColum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val album = cursor.getString(albumColum)
                    val duration = cursor.getLong(durationColumn)

                    val contentUris = ContentUris.withAppendedId(
                        queryUri,
                        id
                    )

                    songs.add(Songs(id, displayName, album, duration, contentUris))

                }
                songs.toList()
            } ?: listOf()
        }
    }
}