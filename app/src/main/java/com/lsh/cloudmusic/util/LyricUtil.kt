package com.lsh.cloudmusic.util


import com.lsh.cloudmusic.MyApplication
import com.lsh.cloudmusic.entity.Lyric
import com.lsh.cloudmusic.network.OkCallback
import com.lsh.cloudmusic.network.ServiceCreator
import com.lsh.cloudmusic.network.baen.LyricResult
import com.lsh.cloudmusic.service.SongService
import org.litepal.LitePal
import org.litepal.extension.findFirst
import kotlin.concurrent.thread

object LyricUtil {

    private val songService = ServiceCreator.create<SongService>()

    @JvmStatic
    public fun getLyric(id:Long,callback: (String?) -> Unit){
        thread{
            // 先从本地查找
            val result = getLyricFromLocal(id)
            // 如果本地没有，发送网络请求
            if(result == null){
                queryLyric(id){
                    MyApplication.post{
                        callback(it)
                    }
                    // 缓存数据
                    if(it != null){
                        saveLyric(id,it)
                    }
                }
            }else{
                MyApplication.post{
                    callback(result)
                }
            }
        }

    }

    @JvmStatic
    public fun getLyric(id:Long,callback:Callback){
        getLyric(id){
            callback.call(it)
        }
    }

    /**
     * 发送网络请求获取歌词
     */
    private fun queryLyric(id:Long,callback:(String?)->Unit){
        songService.getLyric(id).enqueue(object : OkCallback<LyricResult>(){
            override fun onSuccess(result: LyricResult) {
                super.onSuccess(result)
                if(result.nolyric){
                    callback(null)
                }else{
                    callback(result.lrc.lyric)
                }
            }

            override fun onFailureFinally() {
                super.onFailureFinally()
                callback(null)
            }
        })
    }

    /**
     * 从数据库获取歌词
     */
    private fun getLyricFromLocal(id:Long):String?{
        val lyric = LitePal.where("id = ?","$id").findFirst<Lyric>()
        return lyric?.lyric
    }

    /**
     * 保存歌词到数据库
     */
    private fun saveLyric(id:Long,lyricStr:String){
        val lyric = Lyric(id, lyricStr)
        lyric.save()
    }

    public interface Callback{
        fun call(lyric:String?)
    }

}