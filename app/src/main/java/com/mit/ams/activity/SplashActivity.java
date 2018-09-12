package com.mit.ams.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.Utils;
import com.mit.ams.R;
import com.mit.ams.common.Constants;
import com.mit.ams.common.MessageEvent;
import com.mit.ams.common.Novate.MyAPI;
import com.mit.ams.common.Novate.MyBaseSubscriber;
import com.mit.ams.common.StringUtils;
import com.mit.ams.utils.LifePreferences;
import com.tamic.novate.Novate;
import com.tamic.novate.cache.CookieCacheImpl;
import com.tamic.novate.cookie.NovateCookieManager;
import com.tamic.novate.cookie.SharedPrefsCookiePersistor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.ResponseBody;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity  {
    private String TAG = "SplashActivity";
    private Button loginBtn;
    private EditText etUserName;
    private EditText etUserPassword;
    private String userName;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);
        //设置全屏
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //页面布局初始化
        loginBtn = (Button) findViewById(R.id.button);
        etUserName = (EditText) findViewById(R.id.userName);
        etUserPassword = (EditText) findViewById(R.id.userPassword);
        //添加监听
        loginBtn.setOnClickListener(listener);

        createFolder();
    }

    /**
     * 点击事件的响应s
     */
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button:
                    //判断用户是否登陆，如果已登陆，不做操作
                    String userId = (String) LifePreferences.getInstance().initSP(SplashActivity.this).readSpData("userId", "");
                    if(!StringUtils.isEmpty(userId)){
                        Log.d(TAG, "用户已登录，不做操作……");
                        break;
                    }
                    //获取输入框的用户名 和密码
                    userName = etUserName.getText().toString().trim();
                    userPassword = etUserPassword.getText().toString().trim();
                    if (StringUtils.isEmpty(userName)) {
                        showToast("用户名不能为空");
                        break;
                    }
                    if (StringUtils.isEmpty(userPassword)) {
                        showToast("密码不能为空");
                        break;
                    }

                    login();
                    break;
            }
        }
    };

    /**
     * 创建一个ams的文件夹
     */
    private void createFolder(){
        File file = new File("/storage/emulated/0/ams/");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * 用户登陆
     */
    private void login() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", userName.toString());
        //设置md5加密
        String test = EncryptUtils.encryptMD5ToString(userName.toString()+ "#" + userPassword.toString()).toLowerCase();
        parameters.put("password", test);

        final SharedPrefsCookiePersistor spp =  new SharedPrefsCookiePersistor(this);
        Novate novate = new Novate.Builder(this)
                .connectTimeout(8)
                .baseUrl(Constants.ARS_WEB_URL_1)
                //.addApiManager(ApiManager.class)
                .cookieManager(new NovateCookieManager(new CookieCacheImpl(),spp))
                .addLog(true)
                .build();

        MyAPI myAPI = novate.create(MyAPI.class);
        novate.call(myAPI.getLogin(Constants.ARS_LOGIN_URL, parameters),
                new MyBaseSubscriber<ResponseBody>(this) {
                    @Override
                    public void onError(com.tamic.novate.Throwable e) {
                        Log.e("OkHttp", e.getMessage());
                        Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String json = new String(responseBody.bytes());
                            JSONObject jsonObject = JSON.parseObject(json);
                            if (jsonObject.getString("status").equals("200")){
                                showToast(jsonObject.getString("message"));
                            } else {
                                showToast(jsonObject.getString("message"));
                            }

                            //跳转到 mainActivity
                            Intent intent=new Intent(SplashActivity.this, MainActivity.class); startActivity(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        // Splash界面不允许使用back键
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
