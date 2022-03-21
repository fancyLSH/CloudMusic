package com.lsh.cloudmusic.ui.find

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lsh.cloudmusic.network.DiscoverService
import com.lsh.cloudmusic.network.OkCallback
import com.lsh.cloudmusic.network.ServiceCreator
import com.lsh.cloudmusic.network.baen.Banner
import com.lsh.cloudmusic.network.baen.BannerResult
import com.lsh.cloudmusic.network.baen.RecommendPlayList
import com.lsh.cloudmusic.network.baen.RecommendPlayListResult
import com.lsh.cloudmusic.util.UserBaseDataUtil

class FindViewModel : ViewModel() {

    private val discoverService = ServiceCreator.create<DiscoverService>()

    /**
     * 轮播图数据
     */
    val bannerData  = MutableLiveData<MutableList<Banner>>()

    /**
     * 推荐歌单数据
     */
    var recommendPlayListData = MutableLiveData<MutableList<RecommendPlayList>>()

    init {
        requestBannerData()
        requestRecommendPlayListData()
    }

    /**
     * 请求轮播图数据
     */
    fun requestBannerData(){
        discoverService.getBanner().enqueue(object : OkCallback<BannerResult>(){

            override fun onSuccess(result: BannerResult) {
                bannerData.value = result.banners
                // TODO 缓存
            }

        })
    }

    /**
     * 请求推荐歌单数据
     */
    fun requestRecommendPlayListData(){
        discoverService.getRecommendPlayList(UserBaseDataUtil.getCookie())
            .enqueue(object :OkCallback<RecommendPlayListResult>(){
                override fun onSuccess(result: RecommendPlayListResult) {
                    recommendPlayListData.value = result.result
                    // TODO 缓存
                }
            })
    }


}