package com.lsh.cloudmusic.util;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.lsh.cloudmusic.entity.PlayerSong;
import com.lsh.cloudmusic.service.MusicService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SongPlayer {

    private static final String TAG = "SongPlayer";

    private static MediaPlayer mediaPlayer = new MediaPlayer();

    /**
     * 当前播放的歌曲列表
     */
    private static List<PlayerSong> songList = new LinkedList<>();

    /**
     * 当前播放的歌曲在列表中的索引
     */
    private static int curPlaySongIndex = -1;

    /**
     * 上一首播放的歌曲在列表中的索引
     */
    private static int lastPlaySongIndex = -1;

    /**
     * 下一首播放的歌曲在列表中的索引
     */
    private static int nextPlaySongIndex = -1;

    /**
     * 播放状态
     */
    private static int state = State.OVER;

    /**
     * 是否正在播放
     */
    private static boolean isPlaying = false;

    /**
     * 歌曲资源是否准备就绪（如网络资源是否已正确加载）
     */
    private static boolean isPrepared = false;

    /**
     * 循环类型
     */
    private static int turnType = TurnType.SEQUENCE;

    /**
     * 播放监听接口
     */
    private static List<OnPlayListener> playListenerList = new LinkedList<>();

    /**
     * 是否是第一次播放
     */
    private static boolean isFirstPlay = true;

    private static Context context;

    private static Handler handler;

    /**
     * 初始化，需要在使用前调用一次
     *
     * @param context
     */
    public static void init(Context context) {
        SongPlayer.context = context;
        handler = new Handler(Looper.myLooper());

        new Thread(() -> {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (state == State.PLAYING) {
                        handler.post(() -> onPlaying());
                    }
                }
            }, 0, 1000);
        }).start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e(TAG, "onCompletion: 触发歌曲结束");
                SongPlayer.onCompletion();
            }
        });

    }

    // 播放相关函数

    /**
     * 播放网络歌曲
     *
     * @param url
     */
    private static void play(String url) {
        Log.e(TAG, "play: 播放网络歌曲："+url);
        isPrepared = false;
        if(isFirstPlay){
            onFirstPlay();
            isFirstPlay =false;
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.e(TAG, "prepare 完成");
                isPrepared = true;
                mediaPlayer.start();
                // 生命周期
                onStart();
                state = State.PLAYING;
            });
        } catch (IOException e) {
            Log.e(TAG, "异常");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e(TAG, "非法状态异常");
            e.printStackTrace();
        }

    }

    /**
     * 播放网络歌曲
     *
     * @param id
     */
    private static void play(Long id) {
        getUrl(id, new OnGetUrlListener() {
            @Override
            public void onGetUrl(String url) {
                if (url != null) {
                    play(url);
                } else {
                    // TODO 异常
                }
            }
        });
    }

    /**
     * 播放本地歌曲
     *
     * @param uri
     */
    private static void play(Uri uri) {
        isPrepared = false;
        if(isFirstPlay){
            onFirstPlay();
            isFirstPlay =false;
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                mediaPlayer.start();
                onStart();
                state = State.PLAYING;
            }
        });
    }

    /**
     * 给播放器设置歌单和当前歌曲
     * @param songs
     * @param curIndex
     */
    public static void setPlayList(List<PlayerSong> songs,int curIndex){
        songList = songs;
        curPlaySongIndex = curIndex;
    }

    /**
     * 播放歌曲
     *
     * @param song
     */
    private static void playPlayerSong(PlayerSong song) {
        if (song.getType() == 2) {
            // 网络资源
            play(song.getId());
        } else {
            // 本地资源
            play(song.getUri());
        }
    }

    /**
     * 播放歌曲，如果该歌曲不在播放列表中，则自动添加
     * @param song 歌曲
     * @param addToPlayList 是否加入播放列表，如果不在，也会自动加入
     */
    public static void play(PlayerSong song, Boolean addToPlayList) {
        // 把歌曲添加到当前播放列表
        if (addToPlayList) {
            songList.add(song);
            curPlaySongIndex = songList.size() - 1;
        } else {
            curPlaySongIndex = songList.indexOf(song);
            if(curPlaySongIndex == -1){
                play(song,true);
            }
        }
        specialUpdateLastSong();
        specialUpdateNextSong();
        // 播放歌曲
        playPlayerSong(song);
    }

    /**
     * 播放歌曲，如果该歌曲不在播放列表中，则自动加入播放列表
     * @param song 歌曲
     */
    public static void play(PlayerSong song) {
        play(song, false);
    }

    /**
     * 播放歌曲并修改播放列表
     * @param song
     * @param songs
     */
    public static void play(PlayerSong song, List<PlayerSong> songs) {
        // 修改播放列表
        songList.clear();
        songList.addAll(songs);
        curPlaySongIndex = songList.indexOf(song);

        specialUpdateLastSong();
        specialUpdateNextSong();
        // 播放歌曲
        playPlayerSong(song);
    }

    /**
     * 暂停播放
     */
    public static void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            onStop();
        }
    }

    /**
     * 继续播放
     */
    public static void goon() {
        Log.e(TAG, "goon: ");
        if(!isPrepared){
            Log.e(TAG, "goon: 资源未准备，调用play方法");
            play(getCurSong());
        }else{
            mediaPlayer.start();
            state = State.PLAYING;
            onGoon();
            onPlay();
        }
//        onPlaying();

    }

    /**
     * @param mesc
     */
    public static void seekTo(int mesc) {
        mediaPlayer.seekTo(mesc);
    }

    /**
     * 播放下一首
     */
    public static void playNext() {
        if(nextPlaySongIndex == -1){
            nextPlaySongIndex = getNextSongIndex();
        }
        lastPlaySongIndex = curPlaySongIndex;
        curPlaySongIndex = nextPlaySongIndex;
        nextPlaySongIndex = getNextSongIndex();
        Log.e(TAG, "播放下一首" + curPlaySongIndex);
        playPlayerSong(songList.get(curPlaySongIndex));
        onNext();
    }

    /**
     * 播放上一首
     */
    public static void playLast() {
        if(lastPlaySongIndex == -1){
            lastPlaySongIndex = getLastSongIndex();
        }
        nextPlaySongIndex = curPlaySongIndex;
        curPlaySongIndex = lastPlaySongIndex;
        lastPlaySongIndex = getLastSongIndex();
        Log.e(TAG, "播放上一首" + curPlaySongIndex);
        playPlayerSong(songList.get(curPlaySongIndex));
        onLast();
    }

    /**
     * 设置播放顺序，必须为 TurnType类 的变量
     * @param playTurn
     */
    public static void setPlayTurn(int playTurn){
        turnType = playTurn;
        specialUpdateNextSong();
        specialUpdateLastSong();
    }

    /**
     * 修改下一首播放
     * @param song
     */
    public static void setNextPlay(PlayerSong song){
        int nextIndex = songList.indexOf(song);
        if(nextIndex == -1){
            songList.add(song);
            nextIndex = songList.size()-1;
        }
        nextPlaySongIndex = nextIndex;
        onUpdateNextSong();

    }

    // 生命周期相关函数

    /**
     * 首次用播放器播放歌曲时调用此方法
     */
    private static void onFirstPlay(){
        Intent intent = new Intent(context, MusicService.class);
        context.startService(intent);
    }

    /**
     * 歌曲开始播放时回调此方法
     */
    private static void onStart() {
        Log.e(TAG, "onStart");
        for (OnPlayListener listener : playListenerList) {
            listener.onStart();
        }
        onPlay();
    }

    /**
     * 当歌曲从未播放状态进入播放状态时会回调一次此方法
     */
    private static void onPlay(){
        Log.e(TAG, "onPlay");
        if(!isPlaying){
            isPlaying = true;
            for (OnPlayListener listener : playListenerList) {
                listener.onPlay();
            }
        }
    }

    /**
     * 歌曲正在播放时每隔1s回调一次此方法
     */
    private static void onPlaying() {
        Log.d(TAG, "onPlaying");
        for (OnPlayListener listener : playListenerList) {
            listener.onPlaying(getCurrentPosition());
        }
    }

    /**
     * 歌曲播放结束时回调此方法
     */
    private static void onCompletion() {

        Log.e(TAG, "onOver");
        for (OnPlayListener listener : playListenerList) {
            listener.onOver();
        }
        // 修改生命周期状态
        state = State.OVER;

        // 如果是单曲循环则重复播放
        if(turnType == TurnType.REPEAT){
            seekTo(0);
            goon();
        }else{
            // 否则播放下一首
            playNext();
        }
    }

    /**
     * 歌曲暂停时回调此方法
     */
    private static void onStop() {
        Log.e(TAG, "onStop");
        isPlaying = false;
        for (OnPlayListener listener : playListenerList) {
            listener.onStop();
        }
        state = State.STOP;
    }

    /**
     * 歌曲播放下一首时回调此方法
     */
    private static void onNext(){
        Log.e(TAG, "onNext");
        for (OnPlayListener listener : playListenerList) {
            listener.onNext();
        }
    }

    /**
     * 歌曲播放上一首时回调此方法
     */
    private static void onLast(){
        Log.e(TAG, "onLast");
        for (OnPlayListener listener : playListenerList) {
            listener.onLast();
        }
    }

    /**
     * 歌曲继续播放时回调此方法
     */
    private static void onGoon(){
        Log.e(TAG, "onGoon");
        for (OnPlayListener listener : playListenerList) {
            listener.onGoon();
        }
    }

    /**
     * 此方法会在下一首歌曲已确定的情况下异常更换时调用
     * 例如，切换列表循环方式导致的下一首更换
     */
    private static void onUpdateNextSong(){
        for (OnPlayListener listener : playListenerList) {
            listener.onSpecialUpdateNextSong();
        }

    }

    /**
     * 此方法会在上一首歌曲已确定的情况下异常更换时调用
     * 例如，切换列表循环方式导致的上一首更换
     */
    private static void onUpdateLastSong(){
        for (OnPlayListener listener : playListenerList) {
            listener.onSpecialUpdateLastSong();
        }
    }

    /**
     * 设置播放监听接口
     *
     * @param onPlayListener
     */
    public static void addOnPlayListener(OnPlayListener onPlayListener) {
        playListenerList.add(onPlayListener);
    }

    /**
     * 移除播放监听接口
     *
     * @param onPlayListener
     */
    public static void removeOnPlayListener(OnPlayListener onPlayListener) {
        playListenerList.remove(onPlayListener);
    }

    // 状态相关函数

    /**
     * 获取当前播放的歌曲
     *
     * @return
     */
    public static PlayerSong getCurSong() {
        if(curPlaySongIndex == -1){
            return null;
        }
        return songList.get(curPlaySongIndex);
    }

    /**
     * 获取上一首播放的歌曲
     * @return
     */
    public static PlayerSong getLastSong(){
        if(lastPlaySongIndex == -1){
            lastPlaySongIndex = getLastSongIndex();
        }
        return songList.get(lastPlaySongIndex);
    }

    /**
     * 获取下一首播放的歌曲
     * @return
     */
    public static PlayerSong getNextSong(){
        if(nextPlaySongIndex == -1){
            nextPlaySongIndex = getNextSongIndex();
        }
        return songList.get(nextPlaySongIndex);
    }

    /**
     * 获取当前播放列表
     * @return
     */
    public static List<PlayerSong> getPlayList(){
        return songList;
    }

    /**
     * 获取当前播放歌曲在列表中的索引
     * @return
     */
    public static int getCurSongIndex(){return curPlaySongIndex;}

    /**
     * 获取当前播放位置，单位为毫秒
     *
     * @return
     */
    public static int getCurrentPosition() {

        if(!isPrepared){
            return 0;
        }
//        Log.e(TAG, "getCurrentPosition: getDuration = "+mediaPlayer.getDuration());
        return Math.max(mediaPlayer.getCurrentPosition(), 0);
    }

    /**
     * 获取歌曲总长度，单位为毫秒
     *
     * @return
     */
    public static int getDuration() {
        return songList.get(curPlaySongIndex).getDuration();
    }

    /**
     * 是否正在播放
     * @return
     */
    public static boolean isPlaying(){
        return isPlaying;
    }

    /**
     * 获取播放状态
     *
     * @return
     */
    public static int getPlayState() {
        return state;
    }

    /**
     * 获取播放顺序
     * @return
     */
    public static int getPlayTurn(){
        return turnType;
    }

    // 工具函数

    /**
     * 获取下一首歌曲的索引
     * @return
     */
    private static int getNextSongIndex() {
        switch (turnType) {
            // 顺序播放
            // 单曲循环
            case TurnType.SEQUENCE:
            case TurnType.REPEAT:
                if (curPlaySongIndex + 1 >= songList.size()) {
                    return 0;
                } else {
                    return curPlaySongIndex + 1;
                }
            // 随机播放
            case TurnType.RANDOM:
                return new Random().nextInt(songList.size());
        }
        return -1;
    }

    /**
     * 获取上一首歌曲的索引
     *
     * @return
     */
    private static int getLastSongIndex() {
        switch (turnType) {
            // 顺序播放与单曲循环
            case TurnType.SEQUENCE:
            case TurnType.REPEAT:
                if (curPlaySongIndex - 1 >= 0) {
                    return curPlaySongIndex - 1;
                } else {
                    return songList.size() - 1;
                }
                // 随机播放
            case TurnType.RANDOM:
                return new Random().nextInt(songList.size());
        }
        return -1;
    }

    /**
     * 更新下一首歌曲
     */
    private static void specialUpdateNextSong(){
        nextPlaySongIndex = getNextSongIndex();
        onUpdateNextSong();
    }

    /**
     * 更新上一首歌曲
     */
    private static void specialUpdateLastSong(){
        lastPlaySongIndex = getLastSongIndex();
        onUpdateLastSong();
    }

    /**
     * 根据歌曲id发送网络请求获取url
     *
     * @param id               歌曲id
     * @param onGetUrlListener 获取到url的监听
     */
    private static void getUrl(Long id, OnGetUrlListener onGetUrlListener) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://music.inaction.fun/song/url?id=" + id+"&cookie"+UserBaseDataUtil.getCookie())
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                onGetUrlListener.onGetUrl(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();
                String url = null;
                try {
                    url = new JSONObject(body).getJSONArray("data").getJSONObject(0).getString("url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onGetUrlListener.onGetUrl(url);

            }
        });
    }

    /**
     * 获取url的监听接口
     */
    interface OnGetUrlListener {
        void onGetUrl(String url);
    }

    /**
     * 播放状态监听接口
     */
    public static interface OnPlayListener {

        /**
         * 开始播放
         */
        void onStart();

        /**
         * 当从未播放状态切换到播放状态会调用此方法
         */
        void onPlay();

        /**
         * 正在播放
         *
         * @param mesc 当前播放到第几毫秒
         */
        void onPlaying(int mesc);

        /**
         * 停止播放
         */
        void onStop();

        /**
         * 播放下一首
         */
        void onNext();

        /**
         * 播放上一首
         */
        void onLast();

        /**
         * 继续播放
         */
        void onGoon();

        /**
         * 播放结束
         */
        void onOver();

        /**
         * 更新下一首歌曲
         *  此方法不会在每次播放下一首或上一首时调用
         *  而是在下一首已确定的情况下更新时调用，例如更改循环方式导致的下一首变化
         */
        void onSpecialUpdateNextSong();

        /**
         * 更新上一首歌曲，类似于 onUpdateNextSong()
         */
        void onSpecialUpdateLastSong();

    }

    /**
     * 播放状态监听接口的空实现
     */
    public static abstract class SimpleOnPlayListener implements OnPlayListener{
        @Override
        public void onStart() {

        }

        @Override
        public void onPlay() {

        }

        @Override
        public void onPlaying(int mesc) {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onNext() {

        }

        @Override
        public void onLast() {

        }

        @Override
        public void onGoon() {

        }

        @Override
        public void onOver() {

        }

        @Override
        public void onSpecialUpdateNextSong() {

        }

        @Override
        public void onSpecialUpdateLastSong() {

        }
    }

    /**
     * 播放状态类
     */
    public static class State {

        /**
         * 正在播放
         */
        public static final int PLAYING = 0;
        /**
         * 暂停播放
         */
        public static final int STOP = 1;
        /**
         * 完成播放
         */
        public static final int OVER = 2;
    }

    /**
     * 播放顺序类
     */
    public static class TurnType {
        /**
         * 随机播放
         */
        public static final int RANDOM = 0;

        /**
         * 单曲循环
         */
        public static final int REPEAT = 1;

        /**
         * 列表循环
         */
        public static final int SEQUENCE = 2;

    }

}
