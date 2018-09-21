package com.mit.ams.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
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
import com.mit.ams.common.Constants;
import com.mit.ams.common.FileUtil;
import com.mit.ams.common.RecognizeService;

/**
 * Created by 刘鹏飞 on 17.7.6.
 */

public class AddCarFragment extends Fragment {
    private String TAG = AddCarFragment.class.getSimpleName();

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

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (AppCompatActivity) this.getActivity();

        Bundle bundle = getArguments();
        titleFlag = bundle.getInt("flag");

        view = inflater.inflate(R.layout.fragment_maintain, container, false);
        webView = (WebView) view.findViewById(R.id.ars_webview);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);  //支持放大缩小
        settings.setBuiltInZoomControls(true);
        center_item_web_url = getArguments().getString("center_item_web_url");
        webView.loadUrl(center_item_web_url);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSaveFormData(true);// 保存表单数据
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        String cacheDirPath = getActivity().getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME; //缓存路径
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);  //缓存模式
        settings.setAppCachePath(cacheDirPath); //设置缓存路径
        settings.setAppCacheEnabled(false); //不开启缓存功能

        //把标题栏改为登陆
        actionBar = activity.getSupportActionBar();
        actionBar.setCustomView(R.layout.text_titlebar);
        actionbarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title);
        actionbarTitle.setText(Constants.title);
        actionBar.setDisplayHomeAsUpEnabled(false);// 显示返回按钮
        setHasOptionsMenu(true);//这个需要，不然onOptionsItemSelected方法不会被调用
        webView.addJavascriptInterface(new JsInterface(this.getActivity()), "AndroidWebView");

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
        verifyStoragePermissions(activity);
        return view;
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
                case 1: {
                    webViewGoBack();
                    break;
                }
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
        String path =  FileUtil.getSaveFile(activity).getAbsolutePath();
        Intent intent = new Intent(activity, CameraActivity.class);
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
        String path =  FileUtil.getSaveFile(activity ).getAbsolutePath();
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                path);
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL);
        startActivityForResult(intent, REQUEST_CODE_GENERAL_BASIC);
    }

    private void webViewGoBack() {
        webView.goBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

    public static void verifyStoragePermissions(Activity activity) {
        try {
            Log.e("SSSSSS", "---请求权限成功==");
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            Log.e("SSSSSS", "---请求权限失败==" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initAccessToken() {
        OCR.getInstance(this.activity).initAccessToken(new OnResultListener<AccessToken>() {
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
        }, activity.getApplicationContext());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            Log.e(TAG, "---请求权限成功==");
        } else {
            Log.e(TAG, "---请求权限失败==");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                RecognizeService.recVehicleLicense(this.getContext(),FileUtil.getSaveFile(activity).getAbsolutePath(),
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
                RecognizeService.recGeneralBasic(this.getContext(), FileUtil.getSaveFile(activity).getAbsolutePath(),
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
                RecognizeService.recLicensePlate(this.getContext(), FileUtil.getSaveFile(activity).getAbsolutePath(),
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
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }
}

