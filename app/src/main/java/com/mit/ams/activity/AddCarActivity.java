package com.mit.ams.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.mit.ams.R;
import com.mit.ams.application.MyApplication;
import com.mit.ams.common.Constants;
import com.mit.ams.common.FileUtil;
import com.mit.ams.common.RecognizeService;

import java.io.File;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AddCarActivity extends AppCompatActivity  {

    private String TAG = AddCarActivity.class.getSimpleName();

    private WebView webView;
    private String center_item_web_url = Constants.ARS_WEB_URL;
    private AppCompatActivity activity;
    private View view;
    private ActionBar actionBar;
    private TextView actionbarTitle;
    private PopupWindow popupWindow;

    private static final int MSG_HYXY = 21;
    private static final int MSG_VIN = 22;

    private static final int REQUEST_CODE_VEHICLE_LICENSE = 120;
    private static final int REQUEST_CODE_GENERAL_BASIC = 106;
    private static final int REQUEST_CODE_LICENSE_PLATE = 122;

    private ProgressDialog progressDialog;

    private int titleFlag;

    private static final String APP_CACAHE_DIRNAME = "/webcache";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maintain);
        webView = (WebView) findViewById(R.id.ars_webview);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);  //支持放大缩小
        settings.setBuiltInZoomControls(true);
        center_item_web_url =Constants.ARS_WEB_URL_1 + Constants.ARS_CAR_ADD_URL;
        webView.loadUrl(center_item_web_url);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSaveFormData(true);// 保存表单数据
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        String cacheDirPath = this.getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME; //缓存路径
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);  //缓存模式
        settings.setAppCachePath(cacheDirPath); //设置缓存路径
        settings.setAppCacheEnabled(false); //不开启缓存功能

        webView.addJavascriptInterface(new AddCarActivity.JsInterface(this), "AndroidWebView");

        // 创建WebViewClient对象
        WebViewClient wvc = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                webView.loadUrl(url);
                // 消耗掉这个事件。Android中返回True的即到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
                return true;
            }
        };
        webView.setWebViewClient(wvc);

        webView.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    handler.sendEmptyMessage(1);
                    return true;
                }
                return false;
            }

        });
        //注册百度OCR
        initAccessToken();
        //initAccessTokenWithAkSk();
    }

    private class JsInterface {
        private Context mContext;

        public JsInterface(Context context) {
            this.mContext = context;
        }

        //在js中调用window.AndroidWebView.showInfoFromJs(name)，便会触发此方法。
        @JavascriptInterface
        public void getJsPlate(String name) {
            if(name.equals("1")){
                Log.e(TAG, "android js调用拍摄车牌号主线程" );
                Message msg = new Message();
                msg.what = MSG_HYXY;
                handler.sendMessage(msg);
            }
            if(name.equals("2")){
                Log.e(TAG, "android js调用拍摄VIN主线程" );
                Message msg = new Message();
                msg.what = MSG_VIN;
                handler.sendMessage(msg);
            }

        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HYXY: {
                    //调用获取车牌号
                    Log.e(TAG, "主线程 调用获取车牌号百度" );
                    getPlate();
                    break;
                }
                case MSG_VIN: {
                    //调用获取vin码
                    Log.e(TAG, "主线程 调用获取VIN百度" );
                    getVin();
                    break;
                }
            }
        }
    };

    private void getPlate(){
        if (!checkTokenStatus()) {
            return;
        }
        Log.e(TAG, "getPlate 调用获取车牌号百度" );
        String path =  FileUtil.getSaveFile().getAbsolutePath();
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                path);
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL);
        startActivityForResult(intent, REQUEST_CODE_LICENSE_PLATE);

    }

    private void getVin(){
        if (!checkTokenStatus()) {
            return;
        }
        Log.e(TAG, "getVin 调用获取VIN百度" );
        String path =  FileUtil.getSaveFile().getAbsolutePath();
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                path);
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL);
        startActivityForResult(intent, REQUEST_CODE_GENERAL_BASIC);
    }


    private boolean hasGotToken = false;

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            //Toast.makeText(activity.getApplicationContext(), "相机初始化中，请稍候重试", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.CAMERA"};

    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Log.e(TAG, "licence方式获取token失败" + error.getMessage());
            }
        }, this.getApplicationContext());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initAccessToken();
        } else {
            Toast.makeText(getApplicationContext(), "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        }
        try{
//            // 识别成功回调，通用文字识别（含位置信息）
//            if (requestCode == REQUEST_CODE_GENERAL && resultCode == Activity.RESULT_OK) {
//                RecognizeService.recGeneral(this, FileUtil.getSaveFile("vin").getAbsolutePath(),
//                        new RecognizeService.ServiceListener() {
//                            @Override
//                            public void onResult(String result) {
//                                showToast(result);
//                            }
//                        });
//            }
            // 识别成功回调，行驶证识别
            if (requestCode == REQUEST_CODE_VEHICLE_LICENSE && resultCode == Activity.RESULT_OK) {
                Log.e(TAG, "识别成功回调，行驶证识别" );
                RecognizeService.recVehicleLicense(this,FileUtil.getSaveFile().getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(String result) {
                                showToast(result);
                            }
                        });
            }

            // 识别成功回调，通用文字识别
            if (requestCode == REQUEST_CODE_GENERAL_BASIC && resultCode == Activity.RESULT_OK) {
                Log.e(TAG, "识别成功回调，通用文字识别" );
                RecognizeService.recGeneralBasic(this, FileUtil.getSaveFile().getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(String result) {
                                try {
                                    JSONObject obj = JSONObject.parseObject(result);
                                    JSONArray infoObj = obj.getJSONArray("words_result");
                                    JSONObject word = (JSONObject)JSONObject.toJSON(infoObj.get(0));
                                    final String wordStr = word.getString("words");
                                    Log.d(TAG, "识别VIN码为:" + wordStr);
                                    showToast("识别VIN码为:" + wordStr);
                                    webView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            webView.loadUrl("javascript:androidSetVin2web('" + wordStr + "')");
                                        }
                                    });

                                }catch (Exception e){
                                    Log.d(TAG, "识别失败" );
                                    showToast("识别失败");
                                }
                            }
                        });
            }

            // 识别成功回调，车牌识别
            if (requestCode == REQUEST_CODE_LICENSE_PLATE && resultCode == Activity.RESULT_OK) {
                Log.e(TAG, "识别成功回调，车牌识别" );
                RecognizeService.recLicensePlate(this, FileUtil.getSaveFile().getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(String result) {
                                try {
                                    JSONObject obj = JSONObject.parseObject(result);
                                    JSONObject infoObj = obj.getJSONObject("words_result");
                                    String numObj = infoObj.getString("number");
                                    final String num = numObj.toString();
                                    Log.d(TAG, "识别车牌号为:" + num);
                                    showToast("识别车牌号为:" + num);
                                    webView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            webView.loadUrl("javascript:androidSetPlate2web('" + num + "')");
                                        }
                                    });

                                } catch (Exception e) {
                                    Log.d(TAG, "识别失败");
                                    showToast("识别失败");
                                }
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "选取图片出错" + e.getMessage());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    /**
     * 用明文ak，sk初始化
     */
    private void initAccessTokenWithAkSk() {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                Log.e("AK，SK方式获取token失败",error.getMessage());
            }
        }, this.getApplicationContext(),  "LZDOtMZGHZCTyApG2ywX7HlG", "jbqwZLcPyNbfr6yoXSgfBFIOEloe3e5D");
    }
}
