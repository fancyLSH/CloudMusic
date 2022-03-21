package com.lsh.cloudmusic;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import com.lsh.cloudmusic.util.SongPlayer;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import org.litepal.LitePal;

public class MyApplication extends Application {

    private static Context context;

    private static Handler handler;

    static {
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
        
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SongPlayer.init(context);
        handler = new Handler(Looper.myLooper());
        LitePal.initialize(this);
    }

    public static Context getContext(){
        return context;
    }

    public static void post(Runnable r){
        handler.post(r);
    }

}
