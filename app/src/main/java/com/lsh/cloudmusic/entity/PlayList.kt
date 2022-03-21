package com.lsh.cloudmusic.entity



import com.lsh.cloudmusic.network.baen.PlayListDetail
import com.lsh.cloudmusic.network.baen.Song

/**
 * 歌单 数据类
 */
data class PlayList(
        var playListDetail: PlayListDetail,
        var songs : MutableList<Song>
)
