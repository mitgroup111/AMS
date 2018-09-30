package com.mit.ams.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.mit.ams.common.MessageEvent;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mit.ams.common.StringUtils;
import com.mit.ams.utils.LifePreferences;

import org.greenrobot.eventbus.EventBus;

public class SplashLoadDataTask extends AsyncTask<Void, Void, Integer> {

    private String TAG = SplashLoadDataTask.class.getSimpleName();


    private static final String BUNDLE_LOCAL_ERROR = "local_error";
    private static final String BUNDLE_GET_CITY_LOCATION_JUMP = "get_city_location_jump";

//    private LoadDataCallback callback;

    private Context mContext;

//    public SplashLoadDataTask(LoadDataCallback callback, AppCompatActivity context) {
//        mContext = context;
//        this.callback = callback;
//    }

    public SplashLoadDataTask( AppCompatActivity context) {
        mContext = context;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int status = 0;
        // 在此执行耗时任务，可根据任务（数据加载）执行状态给status赋不同的值。
        try{
            getCityLocations();
        } catch (Exception e){
            status = 1;
            Log.e(TAG, e.toString());
        }
        return status;
    }

    @Override
    protected void onPostExecute(Integer status) {
        super.onPostExecute(status);
        EventBus.getDefault().post(new MessageEvent(String.valueOf(status)));
//        if (status == 0) {
//            callback.loaded();
//        } else if (status == 1) {
//            callback.loadError();
//        }
    }

    /**
     * 用户登陆
     */
    private void getCityLocations() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String userId = (String) LifePreferences.getInstance().initSP(mContext).readSpData("userId", "");
                    final String[] key = {"userId"};
                    final String[] value = {userId};
                    //String rs = "{\"content\":[{\"address\":\"烟台市开发区五指山路1号麦特集团\",\"city\":\"烟台市\",\"id\":\"232\",\"lat\":\"37.537647\",\"lng\":\"121.277833\",\"mobile\":\"1322222222\",\"repair_fty_name\":\"我是一个修理厂\"}],\"status\":1}";

                    String rs = "";
                    if (StringUtils.isEmpty(rs)) {
                        Log.e(TAG, "获取城市经纬度数据出错,无数据返回");
                    } else {
                        JSONObject resultObj = JSONObject.parseObject(rs);
                        String status = resultObj.getString("status");
                        if ("1".equals(status)) {
                            JSONArray cityLocations = resultObj.getJSONArray("content");
                            LifePreferences.getInstance().writeSpData("cityLocations", cityLocations.toJSONString());
                            Log.d(TAG, "获取城市经纬度数据成功");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "获取城市经纬度数据出错" + e.getMessage());
                }
            }
        }).start();
    }

//    /**
//     * 加载数据回调
//     */
//    public interface LoadDataCallback {
//        /**
//         * 数据加载完毕
//         */
//        void loaded();
//
//        /**
//         * 数据加载出错
//         */
//        void loadError();
//    }

}