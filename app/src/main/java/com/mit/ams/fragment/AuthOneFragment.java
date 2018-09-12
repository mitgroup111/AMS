package com.mit.ams.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mit.ams.R;
import com.mit.ams.bean.UserCar;
import com.mit.ams.common.Constants;
import com.mit.ams.common.StringUtils;
import com.mit.ams.utils.LifePreferences;
import com.mit.ams.utils.WSClient;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 定损第一步
 * autour: BlueAmer
 * date: 17.7.20 14:56
 * update: 17.7.20
 * version:
*/
public class AuthOneFragment extends Fragment {

    private String TAG = AuthOneFragment.class.getSimpleName();

    private static final int MSG_GET_CARS = 1;
    private static final int MSG_COMMIT_INFOS = 2;

    private static final String BUNDLE_LOCAL_ERROR = "local_error";
    private static final String BUNDLE_CARS_JUMP = "jump";
    private static final String BUNDLE_CARS_FAIL = "fail";

    private ProgressDialog progressDialog;

    private AppCompatActivity activity;
    private View view;
    private ActionBar actionBar;
    private TextView actionbarTitle;

    private AppCompatSpinner chooseCarSp;
    private EditText damageDescEt;
    private Button nextButton;
    private TextView addCarBtn;
    private String userId, damageDesc, carBrand, carType, carItem, demageInfo, damageDes, lon, lat, flag, vehicleCode, vehicleName, damageId;
    private int chosePosition, chosenCarId, titleFlag;
    private List<UserCar> carList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_auth_one, container, false);
        activity = (AppCompatActivity) this.getActivity();

        carList = new ArrayList<>();
        UserCar userCar = new UserCar();
        userCar.setCar_id(0);
        userCar.setCar_plates("--请选择车辆--");
        carList.add(userCar);
        userId = (String) LifePreferences.getInstance().readSpData("userId", "");
        if(!StringUtils.isEmpty(userId)){
            progressDialog = ProgressDialog.show(activity, "提示", "加载数据...");
            //如果用户已登录，获取用户车辆列表
            getUserCars();
        }
        initView();
        showCars();
        return view;
    }

    private void initView(){
        //把标题栏改为登陆
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.button_titlebar);
        actionbarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title);
        Bundle bundle = getArguments();
        titleFlag = bundle.getInt("flag");
        if(titleFlag == 1){
            actionbarTitle.setText("维修定损");
        } else {
            actionbarTitle.setText("事故定损");
        }
        actionBar.setDisplayHomeAsUpEnabled(true);// 显示返回按钮
        addCarBtn = (TextView) actionBar.getCustomView().findViewById(R.id.add_car);
        setHasOptionsMenu(true);//这个需要，不然onOptionsItemSelected方法不会被调用

        //页面空间初始化
        chooseCarSp = (AppCompatSpinner) view.findViewById(R.id.choose_car);
        damageDescEt = (EditText) view.findViewById(R.id.damage_desc);
        nextButton = (Button) view.findViewById(R.id.next_btn);

        //监听
        chooseCarSp.setOnItemSelectedListener(spListener);
        nextButton.setOnClickListener(listener);
        addCarBtn.setOnClickListener(listener);

    }

    //监听器
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.next_btn:
                    if(chosePosition == 0){
                        showToast("请选择您要定损的车辆");
                        break;
                    }
                    damageDesc = damageDescEt.getText().toString();
                    if(StringUtils.isEmpty(damageDesc)){
                        showToast("请认真填写故障描述，以便我们进行评估");
                        break;
                    }
                    progressDialog = ProgressDialog.show(activity, "提示", "正在提交...");
                    commitDamageInfo();
                    break;
                case R.id.add_car:
                    Log.d(TAG, "添加车辆跳转");
                    if(StringUtils.isEmpty(userId)){
                        showToast("对不起，请先登录");
                        break;
                    }
                    AddCarFragment centerItemFragment =  new AddCarFragment();
                    Bundle bundle = new Bundle();
                    FragmentManager manager = activity.getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    bundle.putString("center_item_web_url", Constants.CAR_URL.replace("REPLACE_USERID", userId));
                    bundle.putInt("flag", titleFlag);
                    centerItemFragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, centerItemFragment);
                    transaction.commit();
                    break;
                default:
                    break;
            }
        }
    };

    AdapterView.OnItemSelectedListener spListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "选择车辆:" + position + "----" + parent.getSelectedItem().toString());
            UserCar selectCar = (UserCar)parent.getSelectedItem();
            chosenCarId = selectCar.getCar_id();
            carBrand = selectCar.getBrand_name();
            carType = selectCar.getFamily_name();
            carItem = selectCar.getGroup_name();
            vehicleCode = selectCar.getVehicle_code();
            vehicleName = selectCar.getVehicle_name();
            flag = "2";
            chosePosition = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * 获取用户车辆
     */
    private void getUserCars(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                try{
                    final String[] key = {"userId"};
                    final String[] value = {userId};
                    String rs = WSClient.soapGetInfo("getUserCars", key, value);
                    //String rs = "{\"content\":[{\"add_time\":\"2015-06-26 16:30:04\",\"brand_name\":\"\",\"car_brand\":\"绅宝\",\"car_id\":101,\"car_item\":\"\",\"car_plates\":\"23333ddff\",\"car_type\":\"D系列\",\"car_vin\":\"123df3333\",\"cfg_level\":\"\",\"family_name\":\"\",\"group_name\":\"\",\"user_id\":\"101\",\"vehicle_code\":\"\",\"vehicle_name\":\"\",\"year_pattern\":\"\"},{\"add_time\":\"2017-06-13 16:23:50\",\"brand_name\":\"标致\",\"car_brand\":\"\",\"car_id\":510,\"car_item\":\"\",\"car_plates\":\"木JJH765\",\"car_type\":\"\",\"car_vin\":\"LFV2A11JX73018512\",\"cfg_level\":\"时尚版\",\"family_name\":\"标致3008\",\"group_name\":\"标致3008(10/12-)\",\"user_id\":\"101\",\"vehicle_code\":\"402880882ce35a1f012d0157217b0ce0\",\"vehicle_name\":\"标致PEUGEOT 3008越野车\",\"year_pattern\":\"2011\"},{\"add_time\":\"2017-06-13 16:24:19\",\"brand_name\":\"长安\",\"car_brand\":\"\",\"car_id\":511,\"car_item\":\"\",\"car_plates\":\"木JJH765\",\"car_type\":\"\",\"car_vin\":\"LFV2A11JX73018512\",\"cfg_level\":\"\",\"family_name\":\"星光\",\"group_name\":\"长安星光SC6335\",\"user_id\":\"101\",\"vehicle_code\":\"af96f4e93fa8002d000000006bcc3c8b\",\"vehicle_name\":\"长安SC6335A客车\",\"year_pattern\":\"\"},{\"add_time\":\"2017-06-13 16:24:37\",\"brand_name\":\"长安\",\"car_brand\":\"\",\"car_id\":512,\"car_item\":\"\",\"car_plates\":\"鲁JKI876\",\"car_type\":\"\",\"car_vin\":\"LFV2A11JX73018512\",\"cfg_level\":\"\",\"family_name\":\"星光\",\"group_name\":\"长安星光SC6335\",\"user_id\":\"101\",\"vehicle_code\":\"af96f4e93fa8002d000000006bcc3c8b\",\"vehicle_name\":\"长安SC6335A客车\",\"year_pattern\":\"\"},{\"add_time\":\"2017-06-13 16:48:35\",\"brand_name\":\"上汽通用雪佛兰\",\"car_brand\":\"\",\"car_id\":514,\"car_item\":\"\",\"car_plates\":\"鲁JH76T5\",\"car_type\":\"\",\"car_vin\":\"LFV2A11JX73018512\",\"cfg_level\":\"先锋版\",\"family_name\":\"科鲁兹\",\"group_name\":\"科鲁兹(16/07-)\",\"user_id\":\"101\",\"vehicle_code\":\"4028b2b655edd02e015607a25fa92715\",\"vehicle_name\":\"雪佛兰SGM7146DAA1轿车\",\"year_pattern\":\"2017\"},{\"add_time\":\"2017-06-13 16:49:33\",\"brand_name\":\"一汽大众\",\"car_brand\":\"\",\"car_id\":515,\"car_item\":\"\",\"car_plates\":\"木KJHG76\",\"car_type\":\"\",\"car_vin\":\"LFV2A11JX73018512\",\"cfg_level\":\"时尚型\",\"family_name\":\"宝来\",\"group_name\":\"宝来GP 三厢(06/07-08/09)\",\"user_id\":\"101\",\"vehicle_code\":\"I0000000000000000250000000000040\",\"vehicle_name\":\"宝来FV7162F轿车\",\"year_pattern\":\"2006\"}],\"status\":1}";
                    if (StringUtils.isEmpty(rs)) {
                        bundle.putString(BUNDLE_LOCAL_ERROR, "获取车辆信息出错");
                    } else {
                        JSONObject resultObj = JSONObject.parseObject(rs);
                        String status = resultObj.getString("status");
                        if ("1".equals(status)) {
                            JSONArray cars = resultObj.getJSONArray("content");
                            carList.addAll(JSON.parseArray(cars.toJSONString(), UserCar.class));
                            bundle.putString(BUNDLE_CARS_JUMP, "获取车辆信息成功");
                        } else {
                            String content = resultObj.getString("content");
                            bundle.putString(BUNDLE_CARS_FAIL, content);
                        }
                    }
                } catch (Exception e){
                    Log.e(TAG, "获取用户车辆失败" + e.toString());
                    bundle.putString(BUNDLE_LOCAL_ERROR, "获取车辆失败");
                }
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_GET_CARS;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 提交损伤信息
     */
    private void commitDamageInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                try {
                    final String[] key = {"userId", "carBrand", "carType", "carItem", "demageInfo", "damageDes", "lon", "lat", "flag", "vehicleCode", "vehicleName", "carId"};
                    final String[] value = {userId, carBrand, carType, carItem, demageInfo, damageDesc, lon, lat, flag, vehicleCode, vehicleName, String.valueOf(chosenCarId)};
                    String rs = WSClient.soapGetInfo("uploadDamageInfo", key, value);
                    //String rs = "{\"status\":\"1\",\"content\":\"2290\"}";
                    if (StringUtils.isEmpty(rs)) {
                        bundle.putString(BUNDLE_LOCAL_ERROR, "提交出错");
                    } else {
                        JSONObject resultObj = JSONObject.parseObject(rs);
                        String status = resultObj.getString("status");
                        if ("1".equals(status)) {
                            damageId = resultObj.getString("content");
                            bundle.putString(BUNDLE_CARS_JUMP, "提交成功");
                        } else {
                            String content = resultObj.getString("content");
                            bundle.putString(BUNDLE_CARS_FAIL, content);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "提交失败" + e.toString());
                    bundle.putString(BUNDLE_LOCAL_ERROR, "提交失败");
                }
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_COMMIT_INFOS;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_CARS:
                    String key = msg.getData().keySet().iterator().next();
                    if (BUNDLE_CARS_FAIL.equals(key)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        showToast(msg.getData().getString(BUNDLE_CARS_FAIL));
                    } else if (BUNDLE_LOCAL_ERROR.equals(key)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        showToast(msg.getData().getString(BUNDLE_LOCAL_ERROR));
                    } else if (msg.getData().keySet().contains(BUNDLE_CARS_JUMP)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        showCars();
                    }
                    break;
                case MSG_COMMIT_INFOS:
                    String key1 = msg.getData().keySet().iterator().next();
                    if (BUNDLE_CARS_FAIL.equals(key1)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        showToast(msg.getData().getString(BUNDLE_CARS_FAIL));
                    } else if (BUNDLE_LOCAL_ERROR.equals(key1)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        showToast(msg.getData().getString(BUNDLE_LOCAL_ERROR));
                    } else if (msg.getData().keySet().contains(BUNDLE_CARS_JUMP)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        showToast(msg.getData().getString(BUNDLE_CARS_JUMP));
                        //跳转第二步
                        FragmentManager manager = activity.getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        AuthTwoFragment authTwoFragment = new AuthTwoFragment();
                        Bundle passBundle = new Bundle();
                        passBundle.putString("damageId", damageId);
                        authTwoFragment.setArguments(passBundle);
                        transaction.replace(R.id.content_fragment, authTwoFragment);
                        transaction.commit();
                    }
                    break;
                default:
                    break;
            }
            removeMessages(msg.what);
        }
    };

    /**
     * 加载用户车辆列表
     */
    private void showCars(){
        ArrayAdapter<UserCar> adapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item, carList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        chooseCarSp.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content_fragment, new DamageFragment());
                transaction.commit();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }

}
