package com.lsh.cloudmusic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.lsh.cloudmusic.databinding.ActivityMainBinding
import com.lsh.cloudmusic.util.SongBarHelper
import com.shuyu.gsyvideoplayer.GSYVideoManager
import kotlin.reflect.KParameter
import android.os.Build




class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val songBarHelper: SongBarHelper = SongBarHelper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 跳转到主页面后，销毁其他页面
        ActivityCollector.finishOthers(this)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_find, R.id.navigation_podcast, R.id.navigation_my, R.id.navigation_follow, R.id.navigation_cloudgroup))
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // 设置底部导航栏同时显示图标和颜色
        navView.labelVisibilityMode= LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        // 设置底部导航栏显示图标原始颜色
        navView.itemIconTintList = null


        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = this.resources.getColor(R.color.cloudred);

//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏

//        // 设置页面顶部导航栏（时间、电量）区域透明
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        setStatusBar(R.color.cloudred)
    }


    //设置状态栏背景颜色
    fun setStatusBar(colorID: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = resources.getColor(colorID) //设置状态栏颜色
        }
    }


    override fun onResume() {
        super.onResume()
        songBarHelper.setSongBar(this, binding.songBar)
    }

    override fun onStop() {
        super.onStop()
        songBarHelper.release()
    }

    override fun onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }


}