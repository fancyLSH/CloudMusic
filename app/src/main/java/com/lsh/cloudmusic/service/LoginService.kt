package com.lsh.cloudmusic.service

import com.lsh.cloudmusic.network.baen.CheckPhoneResult
import com.lsh.cloudmusic.network.baen.LoginResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 登录相关的网络请求
 */
interface LoginService {

    /**
     * 检查手机号是否存在
     */
    @GET("/cellphone/existence/check")
    fun checkPhoneExist(@Query("phone") phone: String): Call<CheckPhoneResult>

    /**
     * 用手机号和密码登录
     */
    @GET("/login/cellphone")
    fun login(@Query("phone") phone: String,
              @Query("password") password: String): Call<LoginResult>


}