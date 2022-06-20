package com.aweirdtrashcan.musicplayer.views.activities

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aweirdtrashcan.musicplayer.databinding.ActivityMainBinding
import com.aweirdtrashcan.musicplayer.views.recyclerview.RecyclerViewItemDecoration
import com.aweirdtrashcan.musicplayer.views.recyclerview.SongAdapter
import com.aweirdtrashcan.musicplayer.models.Songs
import com.aweirdtrashcan.musicplayer.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.NullPointerException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private var allSongs = mutableListOf<Songs>()

    private lateinit var permissions : ActivityResultLauncher<String>

    private var hasReadPermission = false

    private lateinit var adapter : SongAdapter

    private lateinit var mediaPlayer: MediaPlayer

    private var isPlaying = false

    private val RecyclerViewItemDecoration = RecyclerViewItemDecoration(10)

    private val viewModel : MainViewModel by viewModels<MainViewModel>()

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
            allSongs = viewModel.getSongs().toMutableList()
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
}