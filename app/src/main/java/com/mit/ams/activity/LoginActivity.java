package com.mit.ams.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allenliu.versionchecklib.core.AllenChecker;
import com.allenliu.versionchecklib.core.VersionParams;
import com.blankj.utilcode.util.EncryptUtils;
import com.mit.ams.R;
import com.mit.ams.common.Constants;
import com.mit.ams.common.Novate.MyAPI;
import com.mit.ams.common.Novate.MyBaseSubscriber;
import com.mit.ams.common.StringUtils;
import com.mit.ams.service.VersionCheckService;
import com.mit.ams.utils.LifePreferences;
import com.tamic.novate.Novate;
import com.tamic.novate.cache.CookieCacheImpl;
import com.tamic.novate.cookie.NovateCookieManager;
import com.tamic.novate.cookie.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity  {
    private String TAG = "LoginActivity";
    private Button loginBtn;
    private EditText etUserName;
    private EditText etUserPassword;
    private String userName;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //设置全屏
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //页面布局初始化
        loginBtn = (Button) findViewById(R.id.button);
        etUserName = (EditText) findViewById(R.id.userName);
        etUserPassword = (EditText) findViewById(R.id.userPassword);
        //添加监听
        loginBtn.setOnClickListener(listener);
        //检查更新
        checkVersion();
    }

    private void checkVersion(){
        VersionParams.Builder builder = new VersionParams.Builder()
                .setRequestUrl(Constants.WEB_DOMAIN + "/upload/version_new.txt")
                .setService(VersionCheckService.class);
        stopService(new Intent(this, VersionCheckService.class));
        builder.setPauseRequestTime(10L);
        builder.setDownloadAPKPath("/storage/emulated/0/ams/");
        //更新界面选择
        CustomVersionDialogActivity.customVersionDialogIndex = 2;
        //更改下载界面
        CustomVersionDialogActivity.isCustomDownloading = true;
        //强制更新
        CustomVersionDialogActivity.isForceUpdate = true;
        builder.setCustomDownloadActivityClass(CustomVersionDialogActivity.class);
        //强制重新下载
        builder.setForceRedownload(true);
        AllenChecker.startVersionCheck(this, builder.build());
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
                    String userId = (String) LifePreferences.getInstance().initSP(LoginActivity.this).readSpData("userId", "");
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
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Intent intent=new Intent(LoginActivity.this, MainActivity.class); startActivity(intent);

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
