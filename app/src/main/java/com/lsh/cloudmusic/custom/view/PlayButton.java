package com.lsh.cloudmusic.custom.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.lsh.cloudmusic.R;

public class PlayButton extends androidx.appcompat.widget.AppCompatImageView{

    private boolean isPlay = true;

    public PlayButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        showPlay();
    }


    public void showPlay(){
        Glide.with(this)
                .load(R.drawable.ic_play_play_activity)
                .into(this);
        isPlay = true;
    }

    public void showStop(){
        Glide.with(this)
                .load(R.drawable.ic_stop_play_activity)
                .into(this);
        isPlay = false;
    }



}
