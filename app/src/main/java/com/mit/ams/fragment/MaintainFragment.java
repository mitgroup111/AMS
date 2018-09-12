package com.mit.ams.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.mit.ams.R;
import com.mit.ams.common.Constants;

/**
 * Created by Administrator on 17.7.6.
 */

public class MaintainFragment extends Fragment {

    private WebView webView;
    private AppCompatActivity activity;
    private View view;
    private ActionBar actionBar;
    private TextView actionbarTitle;

    private static final String APP_CACAHE_DIRNAME = "/webcache";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maintain, container, false);

        activity = (AppCompatActivity) this.getActivity();
        //把标题栏改为登陆
        actionBar = activity.getSupportActionBar();
        actionBar.setCustomView(R.layout.maintain_titlebar);
        actionbarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title);
        actionbarTitle.setText("上门保养");
        actionBar.setDisplayHomeAsUpEnabled(false);// 显示返回按钮

        TextView addMaint = (TextView) actionBar.getCustomView().findViewById(R.id.add_maint);
        addMaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content_fragment, new AddMaintainFragment());
                transaction.commit();
            }
        });

        webView = (WebView) view.findViewById(R.id.ars_webview);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);  //支持放大缩小
        settings.setBuiltInZoomControls(true);
        webView.loadUrl(Constants.ARS_WEB_URL);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSaveFormData(true);// 保存表单数据
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        String cacheDirPath = getActivity().getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME; //缓存路径
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //缓存模式
        settings.setAppCachePath(cacheDirPath); //设置缓存路径
        settings.setAppCacheEnabled(true); //开启缓存功能

        webView.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    handler.sendEmptyMessage(1);
                    return true;
                }
                return false;
            }

        });
        return view;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1 : {
                    webViewGoBack();
                }
                break;
            }
        }
    };

    private void webViewGoBack() {
        webView.goBack();
    }

    }
