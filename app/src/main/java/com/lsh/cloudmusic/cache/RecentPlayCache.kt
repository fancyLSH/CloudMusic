package com.lsh.cloudmusic.cache


import com.lsh.cloudmusic.entity.PlayerSong
import com.lsh.cloudmusic.util.DiskCacheUtil

/**
 * 缓存最后一次播放的歌曲和列表
 */
class RecentPlayCache {

    private val dirName = "/RecentPlayCache"

    fun cachePlayList(playList:List<PlayerSong>){
        DiskCacheUtil.set("$dirName/playlist",playList)
    }

    fun getPlayListCache(callback:(List<PlayerSong>?)->Unit){
        DiskCacheUtil.get<List<PlayerSong>>("$dirName/playlist"){
            callback(it)
        }
    }

    fun cacheLastSongIndex(index:Int){
        DiskCacheUtil.set("$dirName/index",index)
    }

    fun getIndexCache(callback:(Int?)->Unit){
        DiskCacheUtil.get<Int>("$dirName/index"){
            callback(it)
        }
    }

}