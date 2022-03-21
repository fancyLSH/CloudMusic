package com.lsh.cloudmusic.me.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlayListDetailViewModelFactory(private val playListId:Long):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayListDetailViewModel(playListId) as T
    }
}