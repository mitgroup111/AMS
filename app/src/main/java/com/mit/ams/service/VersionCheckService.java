package com.mit.ams.service;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.allenliu.versionchecklib.core.AVersionService;
import com.mit.ams.common.Constants;
import com.mit.ams.common.StringUtils;

/**
 * description: $todo$
 * autour: BlueAmer
 * date: $date$ $time$
 * update: $date$
 * version: $version$
 */

public class VersionCheckService extends AVersionService {

    public VersionCheckService(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onResponses(AVersionService service, String response) {
        Log.e("VersionCheckService", "更新文本-----" + response);
        int currentVersion = 0;
        int remoteVertion = 0;
        String remoteDesc = "";
        try {
            PackageManager packageManager = getPackageManager();
            //getPackageName()是你当前程序的包名
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            currentVersion = packInfo.versionCode;
            Log.d("VersionCheckService", "当前版本号-----" + currentVersion);

            //解析更新文本
            if(!StringUtils.isEmpty(response)){
                JSONObject remoteObj = JSONObject.parseObject(response);
                remoteVertion = remoteObj.getInteger("VERSION_NUMBER");
                remoteDesc = remoteObj.getString("VERSION_DESC");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("VersionCheckService", e.getMessage());
        }
        if(remoteVertion > currentVersion){
            showVersionDialog(Constants.APK_VERSION_ADDRESS, "检测到新版本", remoteDesc);
        }
    }
}
