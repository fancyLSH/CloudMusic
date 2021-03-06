package com.lsh.cloudmusic.cache


import com.lsh.cloudmusic.entity.PlayList
import com.lsh.cloudmusic.util.DiskCacheUtil

class PlayListCache {
    /**
     * 这个类的缓存保存的文件夹
     */
    private val dirName = "/PlayListCache"

    /**
     * 缓存歌单数据
     */
    fun cachePlayList(playList: PlayList){
        DiskCacheUtil.set("$dirName/playlist_${playList.playListDetail.id}",playList)
    }

    /**
     * 获取歌单缓存数据
     */
    fun getPlayListCache(id:Long,callback:(PlayList?)->Unit){
        DiskCacheUtil.get<PlayList>("${dirName}/playlist_$id"){
            callback(it)
        }
    }

}