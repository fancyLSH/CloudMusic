package com.lsh.cloudmusic.login;

import android.content.Intent;
import android.os.Bundle;


import com.lsh.cloudmusic.BaseActivity;
import com.lsh.cloudmusic.databinding.ActivityEntryBinding;

public class EntryActivity extends BaseActivity {

    private ActivityEntryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEntryBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        // 点击“手机号登录“按钮
        binding.loginButton.setOnClickListener(v->{
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        });

    }
}