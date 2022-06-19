package com.aweirdtrashcan.musicplayer.recyclerview

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aweirdtrashcan.musicplayer.databinding.ActivityMainBinding
import com.aweirdtrashcan.musicplayer.databinding.SongsAdapterListBinding
import com.aweirdtrashcan.musicplayer.repository.Songs

class SongAdapter(
    val onClick : (uri : Uri) -> Unit
) : ListAdapter<Songs, SongAdapter.SongViewHolder>(SongDiffUtils()) {

    class SongViewHolder(binding : SongsAdapterListBinding) : RecyclerView.ViewHolder(binding.root) {
        val bind = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentItem = getItem(position)
        val bind = holder.bind

//        bind.tvSongDuration.text = currentItem.duration.toString()
        bind.tvSongTitle.text = currentItem.displayName
        bind.tvAlbum.text = currentItem.album
        bind.cvSongs.setOnClickListener {
            onClick(currentItem.uri)
        }

    }

    companion object {
        fun from(parent : ViewGroup) : SongViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = SongsAdapterListBinding.inflate(layoutInflater)
            return SongViewHolder(binding)
        }
    }
}

class SongDiffUtils : DiffUtil.ItemCallback<Songs>() {
    override fun areItemsTheSame(oldItem: Songs, newItem: Songs): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Songs, newItem: Songs): Boolean {
        return oldItem == newItem
    }
}