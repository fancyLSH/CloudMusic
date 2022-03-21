package com.lsh.cloudmusic.network

import com.lsh.cloudmusic.network.baen.BannerResult
import com.lsh.cloudmusic.network.baen.RecommendPlayListResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * "发现页“相关的网络请求
 */
interface DiscoverService {

    /**
     * 获取轮播图数据
     */
    @GET("/banner?type=1")
    fun getBanner(): Call<BannerResult>

    /**
     * 获取6个推荐歌单
     */
//    @GET("/personalized?limit=6")
    @GET("/personalized")
    fun getRecommendPlayList(@Query("cookie") cookie: String)
            : Call<RecommendPlayListResult>


}