package com.mit.ams.application;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.mit.ams.common.OkHttpUtils.OkHttpUtils;
import com.squareup.leakcanary.LeakCanary;

import org.xutils.x;


/**
 * Created by Administrator on 17.7.5.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
//        OkHttpUtils.initOkHttp(this);
//        SDKInitializer.initialize(this);
        Utils.init(this);
        x.Ext.init(this);
        x.Ext.setDebug(true); //是否输出debug日志，开启debug会影响性能。

    }
}
