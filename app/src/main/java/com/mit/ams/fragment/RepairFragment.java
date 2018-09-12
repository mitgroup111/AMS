package com.mit.ams.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.mit.ams.R;
import com.mit.ams.bean.CityLocation;
import com.mit.ams.bean.RepairFactory;
import com.mit.ams.common.StringUtils;
import com.mit.ams.utils.DownloadImageTask;
import com.mit.ams.utils.LifePreferences;
import com.mit.ams.utils.WSClient;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 17.7.5.
 */

public class RepairFragment extends Fragment implements LocationSource, AMapLocationListener, AMap.OnMapClickListener, AMap.OnMarkerClickListener {

    private String TAG = RepairFragment.class.getSimpleName();

    private static final int MSG_GET_FACTORIES = 1;
    private static final String BUNDLE_LOCAL_ERROR = "local_error";
    private static final String BUNDLE_FACTORIES_JUMP = "login_jump";
    private static final String BUNDLE_FACTORIES_FAIL = "login_fail";

    private AppCompatActivity activity;
    private View view;

    private AppCompatSpinner changeCitySpinner;

    private TextureMapView textureMapView;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    private OnLocationChangedListener mListener;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption clientOption;

    protected static CameraPosition cameraPosition;

    private List<CityLocation> locations;
    private double defaultLat = 37.548309;
    private double defaultLng = 121.382846;

    private String city;
    private List<RepairFactory> repairFactories;
    private PopupWindow popupWindow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_repair, container, false);
        activity = (AppCompatActivity) this.getActivity();
        //请求定位权限
        requestLocationPermission();
        initView(savedInstanceState, view);
        return view;
    }

    /**
     *
     * 初始化地图
     *
     * @param savedInstanceState
     * @param view
     */
    private void initView(Bundle savedInstanceState, View view) {
        String cityLocations = (String) LifePreferences.getInstance().initSP(activity).readSpData("cityLocations", "");
        if(null != cityLocations && !"".equals(cityLocations)){
            locations = JSON.parseArray(cityLocations, CityLocation.class);
        }
        //把标题栏改为地区选择
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setCustomView(R.layout.titlebar);
        actionBar.setDisplayHomeAsUpEnabled(false);// 显示返回按钮

        changeCitySpinner = (AppCompatSpinner) actionBar.getCustomView().findViewById(R.id.change_city);
        changeCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = changeCitySpinner.getSelectedItem().toString();
                getRepairFactories();
                if(null != locations && locations.size() > 0){
                    for (CityLocation location : locations) {
                        if(changeCitySpinner.getSelectedItem().equals(location.getCity())){
                            defaultLat = Double.valueOf(location.getLat());
                            defaultLng = Double.valueOf(location.getLng());
                            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(defaultLat, defaultLng), 15, 0, 0)));
                        }
                    }
                }
                //showToast(position + ":" + changeCitySpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //地图初始化
        textureMapView = (TextureMapView) view.findViewById(R.id.map);
        if (textureMapView != null) {
            textureMapView.onCreate(savedInstanceState);
            aMap = textureMapView.getMap();
        }
        if (aMap == null) {
            aMap = textureMapView.getMap();
        }
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
        LatLng defaultCity = new LatLng(defaultLat, defaultLng);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultCity, 10));

        //图标点击响应
        aMap.setOnMarkerClickListener(this);
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (locationClient == null) {
            locationClient = new AMapLocationClient(activity);
            clientOption = new AMapLocationClientOption();
            locationClient.setLocationListener(this);
            clientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度定位
            clientOption.setOnceLocationLatest(true);//设置单次精确定位
            locationClient.setLocationOption(clientOption);
            locationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient = null;
    }

    private double lat;
    private double lon;

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                amapLocation.getAoiName();//获取当前定位点的AOI信息
                lat = amapLocation.getLatitude();
                lon = amapLocation.getLongitude();
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                if (getCameraPosition() == null) {
                    LifePreferences.getInstance().initSP(activity).writeSpData("lat", lat);
                    LifePreferences.getInstance().initSP(activity).writeSpData("lon", lon);
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lat, lon), 15, 0, 0)));
                }else {
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(getCameraPosition()));
                }
            } else {
                Toast.makeText(activity, "定位失败，请确定GPS权限已经开启", Toast.LENGTH_LONG).show();
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e(TAG, errText);
            }
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_FACTORIES:
                    String key = msg.getData().keySet().iterator().next();
                    if (BUNDLE_FACTORIES_FAIL.equals(key)) {
                        showToast(msg.getData().getString(BUNDLE_FACTORIES_FAIL));
                    } else if (BUNDLE_LOCAL_ERROR.equals(key)) {
                        showToast(msg.getData().getString(BUNDLE_LOCAL_ERROR));
                    } else if (msg.getData().keySet().contains(BUNDLE_FACTORIES_JUMP)) {
                        //地图上添加Markers
                        for (RepairFactory factory : repairFactories) {
                            LatLng repairLatLng = new LatLng(Double.valueOf(factory.getLat()), Double.valueOf(factory.getLon()));
                            Bitmap markerIcon = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), R.drawable.location_marker);
                            Marker marker = aMap.addMarker(new MarkerOptions().position(repairLatLng).icon(BitmapDescriptorFactory.fromBitmap(markerIcon)));
                            marker.setObject(factory);
                            Log.d(TAG, factory.getRepair_fty_name());
                        }
                    }
                    break;

                default:
                    break;
            }
            removeMessages(msg.what);
        }
    };

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        if(null != textureMapView){
            textureMapView.onResume();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        if(null != textureMapView) {
            textureMapView.onPause();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(null != textureMapView){
            textureMapView.onSaveInstanceState(outState);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        if(null != aMap){
            setCameraPosition(aMap.getCameraPosition());
        }
        super.onDestroy();
        if(null != textureMapView){
            textureMapView.onDestroy();
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    CameraPosition getCameraPosition() {
        return cameraPosition;
    }

    void setCameraPosition(CameraPosition cameraPosition) {
        RepairFragment.cameraPosition = cameraPosition;
    }

    /**
     * 从服务器请求获取修理厂
     */
    private void getRepairFactories(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                try {
                    final String[] key = {"city"};
                    final String[] value = {city};
                    //String rs = "{\"content\":[{\"address\":\"烟台市开发区五指山路1号麦特集团\",\"city\":\"烟台市\",\"id\":\"232\",\"lat\":\"37.537647\",\"lon\":\"121.277833\",\"mobile\":\"1322222222\",\"repair_fty_name\":\"我是一个修理厂\"}],\"status\":1}";
                    String rs = WSClient.soapGetInfo("getRepairFactories", key, value);
                    if (StringUtils.isEmpty(rs)) {
                        bundle.putString(BUNDLE_LOCAL_ERROR, "sorry,服务器罢工了T_T");
                    } else {
                        JSONObject resultObj = JSONObject.parseObject(rs);
                        String status = resultObj.getString("status");
                        if ("1".equals(status)) {
                            JSONArray factories = resultObj.getJSONArray("content");
                            bundle.putString(BUNDLE_FACTORIES_JUMP, "获取城市成功");
                            repairFactories = JSON.parseArray(factories.toJSONString(), RepairFactory.class);
                        } else {
                            String content = resultObj.getString("content");
                            bundle.putString(BUNDLE_FACTORIES_FAIL, content);
                        }
                    }
                } catch (Exception e) {
                    bundle.putString(BUNDLE_LOCAL_ERROR, "sorry,程序罢工了T_T");
                }
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_GET_FACTORIES;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //点击地图上没marker 的地方，隐藏popupWindow
        if(popupWindow != null){
            popupWindow.dismiss();
        }
    }

    ImageView factImg;
    RepairFactory factory;

    @Override
    public boolean onMarkerClick(Marker marker) {
        factory = (RepairFactory)marker.getObject();
        // 一个自定义的布局，显示修理厂信息
        View contentView = LayoutInflater.from(activity).inflate(R.layout.infowindow, null);
        TextView facName = (TextView)contentView.findViewById(R.id.fac_name);
        facName.setText(factory.getRepair_fty_name());
        TextView facTel = (TextView)contentView.findViewById(R.id.fac_tel);
        facTel.setText(factory.getMobile());
        TextView facAddr = (TextView)contentView.findViewById(R.id.fac_addr);
        facAddr.setText(factory.getAddress());
        Button yuyueButton = (Button) contentView.findViewById(R.id.yuyue_btn);
        yuyueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "跳转预约");
                popupWindow.dismiss();
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("FACTORY_ID", factory.getId());
                YuyueOneFragment oneFragment= new YuyueOneFragment();
                oneFragment.setArguments(bundle);
                transaction.replace(R.id.content_fragment, oneFragment);
                transaction.commit();
            }
        });
        factImg = (ImageView) contentView.findViewById(R.id.fac_img);
        if(!StringUtils.isEmpty(factory.getFty_photo())){
            new DownloadImageTask(factImg).execute("http://www.51ars.cn/dsWebService/uploadPhoto/" + factory.getFty_photo());
        }

        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //实例化一个ColorDrawable颜色为半透明，已达到变暗的效果
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(dw);
        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        return false;
    }

    /**
     * 向用户请求定位权限
     */
    private void requestLocationPermission() {
        //Android 6.0判断用户是否授予定位权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(activity, "自Android 6.0开始需要打开位置权限", Toast.LENGTH_SHORT).show();
                }
                //请求权限
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }

}
