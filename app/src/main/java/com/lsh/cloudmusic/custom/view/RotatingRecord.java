package com.lsh.cloudmusic.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lsh.cloudmusic.R;
import com.lsh.cloudmusic.entity.PlayerSong;


import java.util.List;

/**
 * 旋转的唱片
 * 自定义view，组合控件
 */
public class RotatingRecord extends FrameLayout {

    private static final String TAG = "RotatingRecord";


    /**
     * 针压图片视图
     */
    private ImageView pin;

    /**
     * 唱片视图
     */
    private GoonViewPager<RecordView> recordPager;

    /**
     * 唱片GoonViewPager的adapter
     */
    private GoonViewPager.Adapter<RecordView> adapter;



    private OnPageChangeListener onPageChangeListener;

    /**
     * 针压动画的时长
     */
    private final long pinDuration = 400;

    private boolean isPinDown;

    private boolean isScrollProgram = false;

    private boolean isPlaying;


    public RotatingRecord(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.custom_view_rotating_record,this);
        pin = view.findViewById(R.id.pin);
        recordPager = view.findViewById(R.id.recordPager);

        //让针压起始装态是远离唱片的
        pin.setPivotX(dp2px(context,18));
        pin.setPivotY(dp2px(context,16));
        pin.setRotation(-20);

    }


    /**
     * 此初始化函数必须被调用
     * @param lastSong 上一首歌
     * @param curSong 当前播放歌曲
     * @param nextSong 下一首歌
     * @param isPlaying 是否正在播放
     */
    public void init(PlayerSong lastSong, PlayerSong curSong, PlayerSong nextSong
            , boolean isPlaying, GetSongInterface getSongInterface){
        this.isPlaying = isPlaying;
        adapter = new GoonViewPager.Adapter<RecordView>() {

            @Override
            public RecordView createView() {
                return new RecordView(getContext());
            }

            @Override
            public void initData(List<RecordView> viewList) {
                RecordView view1 = viewList.get(0);
                RecordView view2 = viewList.get(1);
                RecordView view3 = viewList.get(2);
                view1.setImage(lastSong.getAlbums().getPicUrl());
                view2.setImage(curSong.getAlbums().getPicUrl());
                view3.setImage(nextSong.getAlbums().getPicUrl());
            }

            @Override
            public void setNextData(RecordView view) {
                PlayerSong song = getSongInterface.getNextSong();
                view.setImage(song.getAlbums().getPicUrl());
            }

            @Override
            public void setLastData(RecordView view) {
                PlayerSong song = getSongInterface.getLastSong();
                view.setImage(song.getAlbums().getPicUrl());
            }
        };
        recordPager.setAdapter(adapter);

        recordPager.setOnScrollListener(new GoonViewPager.OnScrollListener() {
            @Override
            public void startScroll() {
                if(isPinDown){
                    setPinUp(true);
                }
                recordPager.getCurView().stopAnimation();
            }

            @Override
            public void endScroll(int oldPosition,int newPosition) {
                Log.e(TAG, "endScroll: ");
                if(oldPosition == newPosition && RotatingRecord.this.isPlaying){
                    setPinDown(true);
                    recordPager.getCurView().startAnimation();
                }else if(oldPosition != newPosition){
                    setPinDown(true);
                    recordPager.getCurView().startAnimation();
                    recordPager.getLastView().resetAnimation();
                    recordPager.getNextView().resetAnimation();
                }


                if(onPageChangeListener != null && !isScrollProgram){
                    if(newPosition > oldPosition){
                        onPageChangeListener.onToNextPage();
                    }else if(newPosition < oldPosition){
                        onPageChangeListener.onToLastPage();
                    }
                }
                isScrollProgram = false;

            }
        });

        // 如果正在播放
        if(isPlaying){
            setPinDown(false);
            recordPager.getCurView().startAnimation();
        }


    }


    public void setOnPageChangeListener(OnPageChangeListener pageChangeListener){
        this.onPageChangeListener = pageChangeListener;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        recordPager.setOnClickListener(l);
    }

    public void updateNextSong(){
        Log.e(TAG, "updateNextSong: ");
        recordPager.updateNextSong();
    }

    public void updateLastSong(){
        Log.e(TAG, "updateLastSong: " );
        recordPager.updateLastSong();
    }

    // 给外部调用的生命周期函数
    public void onPlay(){
        Log.e(TAG, "onPlay" );
        isPlaying = true;
        setPinDown(true);
        recordPager.getCurView().startAnimation();
    }

    public void onStop(){
        isPlaying = false;
        Log.e(TAG, "onStop" );
        setPinUp(true);
        recordPager.getCurView().stopAnimation();
    }

    public void onNext(){
        Log.e(TAG, "onNext: " );
        isScrollProgram = true;
        recordPager.scrollToNext();
    }

    public void onLast(){
        isScrollProgram = true;
        recordPager.scrollToLast();
    }



    /**
     * 开始播放的ui
     * @param hasAnimation
     */
    private void setPlayUi(boolean hasAnimation){
        setPinDown(hasAnimation);
//        adapter.startRecordAnimation(recordPager.getCurrentItem());
//        adapter.resumeRecordAnimation(recordPager.getCurrentItem());
    }

    /**
     * 停止播放的ui
     * @param hasAnimation
     */
    private void setStopUi(boolean hasAnimation){
        setPinUp(hasAnimation);
//        adapter.stopRecordAnimation();
    }

    /**
     * 针压下来
     * @param hasAnimation 是否有动画
     */
    private void setPinDown(boolean hasAnimation){
        RotateAnimation animation = new RotateAnimation(0,20,dp2px(getContext(),18)
                ,dp2px(getContext(),16));
        animation.setFillAfter(true);
        if(hasAnimation){
            animation.setDuration(pinDuration);
        }else{
            animation.setDuration(0);
        }
        pin.startAnimation(animation);
        isPinDown = true;
    }

    /**
     * 针抬起来
     * @param hasAnimation 是否有动画
     */
    private void setPinUp(boolean hasAnimation){
        RotateAnimation animation = new RotateAnimation(20,0,dp2px(getContext(),18)
                ,dp2px(getContext(),16));
        animation.setFillAfter(true);
        if(hasAnimation){
            animation.setDuration(pinDuration);
        }else{
            animation.setDuration(0);
        }
        pin.startAnimation(animation);
        isPinDown = false;
    }

    public interface GetSongInterface{
        PlayerSong getNextSong();

        PlayerSong getLastSong();
    }

    public interface OnPageChangeListener{
        void onToNextPage();

        void onToLastPage();
    }

    /**
     * px转dp静态方法
     * @param context
     * @param pxValue
     * @return
     */
    private int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转px静态方法
     * @param context
     * @param dpValue
     * @return
     */
    private int dp2px(Context context, float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }


}
