package com.lsh.cloudmusic.adapter.different

import androidx.recyclerview.widget.DiffUtil
import com.lsh.cloudmusic.network.baen.Song

class SongDiffCallback: DiffUtil.ItemCallback<Song>(){
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.equals(newItem)
    }
}