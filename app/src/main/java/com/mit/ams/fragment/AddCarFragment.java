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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.mit.ams.R;
import com.mit.ams.common.Constants;

/**
 * Created by 刘鹏飞 on 17.7.6.
 */

public class AddCarFragment extends Fragment {

    private WebView webView;
    private String center_item_web_url = Constants.ARS_WEB_URL;
    private AppCompatActivity activity;
    private View view;
    private ActionBar actionBar;
    private TextView actionbarTitle;

    private int titleFlag;

    private static final String APP_CACAHE_DIRNAME = "/webcache";

    @Nullable
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
        actionBar.setDisplayHomeAsUpEnabled(true);// 显示返回按钮
        setHasOptionsMenu(true);//这个需要，不然onOptionsItemSelected方法不会被调用

        //获取网页标题
        WebChromeClient wcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.d("--------", title);
                actionbarTitle.setText(title);
            }
        };
        webView.setWebChromeClient(wcc);

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
        return view;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    webViewGoBack();
                }
                break;
            }
        }
    };

    private void webViewGoBack() {
        webView.goBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt("flag", titleFlag);
                AuthOneFragment authOneFragment = new AuthOneFragment();
                authOneFragment.setArguments(bundle);
                transaction.replace(R.id.content_fragment, authOneFragment);
                transaction.commit();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

