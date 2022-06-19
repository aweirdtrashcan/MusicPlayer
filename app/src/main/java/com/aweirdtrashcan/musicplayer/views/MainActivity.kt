package com.aweirdtrashcan.musicplayer.views

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aweirdtrashcan.musicplayer.R
import com.aweirdtrashcan.musicplayer.databinding.ActivityMainBinding
import com.aweirdtrashcan.musicplayer.recyclerview.RecyclerViewItemDecoration
import com.aweirdtrashcan.musicplayer.recyclerview.SongAdapter
import com.aweirdtrashcan.musicplayer.repository.Songs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.NullPointerException

open class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private var allSongs = mutableListOf<Songs>()

    private lateinit var permissions : ActivityResultLauncher<String>

    private var hasReadPermission = false

    private lateinit var adapter : SongAdapter

    private lateinit var mediaPlayer: MediaPlayer

    private var isPlaying = false

    private val RecyclerViewItemDecoration = RecyclerViewItemDecoration(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = SongAdapter {
            try {
                if (!isPlaying){
                    startMusic(it)
                } else {
                    mediaPlayer.stop()
                    startMusic(it)
                }
            } catch (e : NullPointerException) {
                errorDialog(e.toString())
            }
        }

        permissions = registerForActivityResult(ActivityResultContracts.RequestPermission()){}

        askPermission()

        lifecycleScope.launch{
            accessExternalMusic()
            allSongs = accessExternalMusic().toMutableList()
            setupRecyclerView()
        }

        binding.btnStop.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.stop()
            }
        }

    }

    private fun errorDialog(e : String) {
        AlertDialog.Builder(this@MainActivity).apply {
            setTitle("Erro")
            setMessage(e)
            setNeutralButton("Fechar") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            show()
        }
    }

    private fun startMusic(uri: Uri) {
        mediaPlayer = MediaPlayer.create(this@MainActivity, uri)
        mediaPlayer.start()
        isPlaying = mediaPlayer.isPlaying
        mediaPlayer.setOnCompletionListener { isPlaying = false }
    }

    private fun setupRecyclerView(){
        adapter.submitList(allSongs)
        binding.rvMain.adapter = adapter
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.addItemDecoration(RecyclerViewItemDecoration)
    }

    private fun askPermission() {
        val permissionToAsk = Manifest.permission.READ_EXTERNAL_STORAGE

        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            permissionToAsk
        ) == PackageManager.PERMISSION_GRANTED

        hasReadPermission = hasPermission

        if (!hasPermission) {
            permissions.launch(permissionToAsk)
        }

    }

    private suspend fun accessExternalMusic() : List <Songs> {
        return withContext(Dispatchers.IO) {
            try {
                val query = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                val projection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION
                )

                val songs = mutableListOf<Songs>()
                contentResolver.query(
                    query,
                    projection,
                    null,
                    null,
                    null
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                    val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val displayName = cursor.getString(displayNameColumn)
                        val album = cursor.getString(albumColumn)
                        val duration = cursor.getLong(durationColumn)

                        val uriContent = ContentUris.withAppendedId(
                            query,
                            id
                        )

                        songs.add(Songs(id, displayName, album, duration, uriContent))

                    }
                    Log.d("Songs", songs.toString())
                    songs.toList()
                } ?: listOf()
            } catch (e : Exception) {
                e.printStackTrace()
                Log.d("Error", e.toString())
                listOf()
            }
        }
    }
}