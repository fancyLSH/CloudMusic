package com.lsh.cloudmusic;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.annotation.Target;


public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.INSTANCE.addActivity(this);
//        swipeStatusBar(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.INSTANCE.removeActivity(this);
    }
}
