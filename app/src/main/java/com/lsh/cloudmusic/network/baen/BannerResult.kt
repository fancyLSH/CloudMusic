package com.lsh.cloudmusic.network.baen


data class BannerResult(
        var banners:MutableList<Banner>
):Result()

data class Banner(
        /**
         * 图片
         */
        var pic: String,
        /**
         * 类型
         */
        var typeTitle: String,
        /**
         * 链接
         */
        var url: String,
)
