package com.mit.ams.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import com.mit.ams.R;

import java.io.File;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);
        //设置全屏
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        createFolder();
        jump();

    }

    /**
     * 创建一个xiucheba的文件夹
     */
    private void createFolder(){
        File file = new File("/storage/emulated/0/ams/");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    //任务执行完延迟500ms进入主界面
    private void jump(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        // Splash界面不允许使用back键
    }
}
