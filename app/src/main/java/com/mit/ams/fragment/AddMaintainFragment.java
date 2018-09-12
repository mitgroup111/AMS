package com.mit.ams.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * description: 增加保养记录
 * autour: BlueAmer
 * date: 17.7.20 14:56
 * update: 17.7.20
 * version:
 */
public class AddMaintainFragment extends Fragment {

    private String TAG = AddMaintainFragment.class.getSimpleName();

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
    private EditText maintainDate;
    private Button nextButton;
    private String userId, carBrand, carType, carItem, LFDate, lastMaintDate;
    private int chosePosition, chosenCarId;
    private List<UserCar> carList;

    protected static final int REUEST_CODDE = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_maintain, container, false);
        activity = (AppCompatActivity) this.getActivity();
        carList = new ArrayList<>();
        UserCar userCar = new UserCar();
        userCar.setCar_id(0);
        userCar.setCar_plates("--请选择车辆--");
        carList.add(userCar);
        userId = (String) LifePreferences.getInstance().readSpData("userId", "");
        if (!StringUtils.isEmpty(userId)) {
            progressDialog = ProgressDialog.show(activity, "提示", "加载数据...");
            //如果用户已登录，获取用户车辆列表
            getUserCars();
        }

        initView();
        return view;
    }

    private void initView() {
        //把标题栏改为登陆
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.button_titlebar);
        actionbarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title);
        actionbarTitle.setText("新增保养记录");
        actionBar.setDisplayHomeAsUpEnabled(true);// 显示返回按钮
        setHasOptionsMenu(true);//这个需要，不然onOptionsItemSelected方法不会被调用

        //页面空间初始化
        chooseCarSp = (AppCompatSpinner) view.findViewById(R.id.choose_car);
        maintainDate = (EditText) view.findViewById(R.id.damage_desc);
        nextButton = (Button) view.findViewById(R.id.next_btn);

        //监听
        chooseCarSp.setOnItemSelectedListener(spListener);
        nextButton.setOnClickListener(listener);
        maintainDate.setOnClickListener(listener);

    }

    //监听器
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.next_btn:
                    if (chosePosition == 0) {
                        showToast("请选择车辆");
                        break;
                    }
                    lastMaintDate = maintainDate.getText().toString();
                    if (StringUtils.isEmpty(lastMaintDate)) {
                        showToast("请选择上次保养日期");
                        break;
                    }
                    progressDialog = ProgressDialog.show(activity, "提示", "正在提交...");
                    commitMaintInfo();
                    break;
                case R.id.add_car:
                    Log.d(TAG, "添加车辆跳转");
                    if (StringUtils.isEmpty(userId)) {
                        showToast("对不起，请先登录");
                        break;
                    }
                    AddCarFragment centerItemFragment = new AddCarFragment();
                    Bundle bundle = new Bundle();
                    FragmentManager manager = activity.getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    bundle.putString("center_item_web_url", Constants.CAR_URL.replace("REPLACE_USERID", userId));
                    bundle.putInt("flag", 4);

                    centerItemFragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, centerItemFragment);
                    transaction.commit();
                    break;
                case R.id.damage_desc:
                    DatePickerFragment fragment = new DatePickerFragment();
                    fragment.setTargetFragment(AddMaintainFragment.this, REUEST_CODDE);
                    fragment.show(activity.getSupportFragmentManager(), "datePicker");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REUEST_CODDE)
        {
            String stringExtra = data.getStringExtra("date");
            lastMaintDate = stringExtra;
            maintainDate.setText(stringExtra);
            Log.i(TAG, stringExtra);
        }
    }

    AdapterView.OnItemSelectedListener spListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "选择车辆:" + position + "----" + parent.getSelectedItem().toString());
            UserCar selectCar = (UserCar) parent.getSelectedItem();
            chosenCarId = selectCar.getCar_id();
            carBrand = selectCar.getBrand_name();
            carType = selectCar.getFamily_name();
            carItem = selectCar.getGroup_name();
            chosePosition = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * 获取用户车辆
     */
    private void getUserCars() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                try {
                    final String[] key = {"userId"};
                    final String[] value = {userId};
                    String rs = WSClient.soapGetInfo("getUserCars", key, value);
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
                } catch (Exception e) {
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
     * 获取用户车辆
     */
    private void commitMaintInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    final String[] key = {"userId", "carBrand", "carType", "carItem", "LFDate", "lastMaintDate"};
                    final String[] value = {userId, carBrand, carType, carItem, format.format(new Date()), lastMaintDate};
                    String rs = WSClient.soapGetInfo("submitMRInfo", key, value);
                    if (StringUtils.isEmpty(rs)) {
                        bundle.putString(BUNDLE_LOCAL_ERROR, "提交出错");
                    } else {
                        JSONObject resultObj = JSONObject.parseObject(rs);
                        String status = resultObj.getString("status");
                        if ("1".equals(status)) {
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
                        transaction.replace(R.id.content_fragment, new MaintainFragment());
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
    private void showCars() {
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
                transaction.replace(R.id.content_fragment, new MaintainFragment());
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
