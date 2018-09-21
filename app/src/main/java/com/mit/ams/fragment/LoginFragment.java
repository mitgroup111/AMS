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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import com.mit.ams.R;
import com.mit.ams.common.Constants;
import com.mit.ams.common.Novate.MyAPI;
import com.mit.ams.common.Novate.MyBaseSubscriber;
import com.mit.ams.common.StringUtils;
import com.mit.ams.utils.LifePreferences;
import com.tamic.novate.Novate;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 17.7.6.
 */

public class LoginFragment extends Fragment {

    private String TAG = LoginFragment.class.getSimpleName();

    private static final int MSG_LOGIN = 1;

    private static final String BUNDLE_LOCAL_ERROR = "local_error";
    private static final String BUNDLE_LOGIN_JUMP = "login_jump";
    private static final String BUNDLE_LOGIN_FAIL = "login_fail";

    private TextView actionbarTitle;

    private Button loginBtn;
    private EditText etTel;
    private EditText etPwd;
    private TextView forgetPwdTv;
    private TextView registerTv;

    private String tel;
    private String password;

    private ProgressDialog progressDialog;

    private AppCompatActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (AppCompatActivity) this.getActivity();
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化布局
     *
     * @param view
     */
    private void initView(View view) {
        //把标题栏改为登陆
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.text_titlebar);
        actionbarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title);
        actionbarTitle.setText("登录");
        actionBar.setDisplayHomeAsUpEnabled(true);// 显示返回按钮
        setHasOptionsMenu(true);//这个需要，不然onOptionsItemSelected方法不会被调用

        //页面布局初始化
        loginBtn = (Button) view.findViewById(R.id.login_btn);
        etTel = (EditText) view.findViewById(R.id.et_tel);
        etPwd = (EditText) view.findViewById(R.id.et_pwd);
        forgetPwdTv = (TextView) view.findViewById(R.id.forget_pwd_tv);
        registerTv = (TextView) view.findViewById(R.id.register_tv);
        //添加监听
        loginBtn.setOnClickListener(listener);
        forgetPwdTv.setOnClickListener(listener);
        registerTv.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            switch (v.getId()) {
                case R.id.login_btn:
                    //判断用户是否登陆，如果已登陆，不做操作
                    String userId = (String) LifePreferences.getInstance().initSP(activity).readSpData("userId", "");
                    if(!StringUtils.isEmpty(userId)){
                        Log.d(TAG, "用户已登录，不做操作……");
                        break;
                    }
                    //获取输入框的用户名 和密码
                    tel = etTel.getText().toString().trim();
                    password = etPwd.getText().toString().trim();
                    if (StringUtils.isEmpty(tel)) {
                        showToast("手机号不能为空");
                        break;
                    }
                    if (StringUtils.isEmpty(password)) {
                        showToast("密码不能为空");
                        break;
                    }
                    if (!StringUtils.isMobileNo(tel)) {
                        showToast("手机号格式不正确");
                        break;
                    }
                    progressDialog = ProgressDialog.show(activity, "提示", "正在登录...");
                    login();
                    break;
                case R.id.forget_pwd_tv:
                    transaction.replace(R.id.content_fragment, new FindPwdFragment());
                    transaction.commit();
                    break;
                case R.id.register_tv:
                    //跳转到注册
//                    transaction.replace(R.id.content_fragment, new RegistFragment());
                    transaction.commit();
                    break;
            }
        }
    };

    /**
     * 用户登陆
     */
    private void login() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", tel.toString());
        parameters.put("password", password.toString());
        Novate novate = new Novate.Builder( this.getActivity())
                .connectTimeout(8)
                .baseUrl(Constants.ARS_WEB_URL_1)
                //.addApiManager(ApiManager.class)
                .addLog(true)
                .build();

        MyAPI myAPI = novate.create(MyAPI.class);
        novate.call(myAPI.getLogin(Constants.ARS_LOGIN_URL,parameters),
                new MyBaseSubscriber<ResponseBody>(activity) {
                    @Override
                    public void onError(com.tamic.novate.Throwable e) {
                        Log.e("OkHttp", e.getMessage());
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            Toast.makeText(activity, new String(responseBody.bytes()), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Bundle bundle = new Bundle();
//                try {
//                    final String[] key = {"mobile", "pwd"};
//                    final String[] value = {tel, password};
//                    String rs = WSClient.soapGetInfo("userLogin", key, value);
//                    if (StringUtils.isEmpty(rs)) {
//                        bundle.putString(BUNDLE_LOCAL_ERROR, "登录出错");
//                    } else {
//                        JSONObject resultObj = JSONObject.parseObject(rs);
//                        String status = resultObj.getString("status");
//                        if ("1".equals(status)) {
//                            JSONObject user = resultObj.getJSONObject("content");
//                            String userId = user.getString("userId");
//                            String realName = user.getString("realName");
//                            LifePreferences.getInstance().initSP(activity).writeSpData("realName", realName);
//                            LifePreferences.getInstance().initSP(activity).writeSpData("userId", userId);
//                            LifePreferences.getInstance().initSP(activity).writeSpData("password", password);
//                            LifePreferences.getInstance().initSP(activity).writeSpData("mobile", user.getString("mobile"));
//                            bundle.putString(BUNDLE_LOGIN_JUMP, "登录成功");
//                        } else {
//                            String content = resultObj.getString("content");
//                            bundle.putString(BUNDLE_LOGIN_FAIL, content);
//                        }
//                    }
//                } catch (Exception e) {
//                    bundle.putString(BUNDLE_LOCAL_ERROR, "登录出错");
//                }
//                Message msg = mHandler.obtainMessage();
//                msg.what = MSG_LOGIN;
//                msg.setData(bundle);
//                mHandler.sendMessage(msg);
//            }
//        }).start();
    }

//    /**
//     * 用户登陆
//     */
//    private void login() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Bundle bundle = new Bundle();
//                try {
//                    final String[] key = {"mobile", "pwd"};
//                    final String[] value = {tel, password};
//                    String rs = WSClient.soapGetInfo("userLogin", key, value);
//                    if (StringUtils.isEmpty(rs)) {
//                        bundle.putString(BUNDLE_LOCAL_ERROR, "登录出错");
//                    } else {
//                        JSONObject resultObj = JSONObject.parseObject(rs);
//                        String status = resultObj.getString("status");
//                        if ("1".equals(status)) {
//                            JSONObject user = resultObj.getJSONObject("content");
//                            String userId = user.getString("userId");
//                            String realName = user.getString("realName");
//                            LifePreferences.getInstance().initSP(activity).writeSpData("realName", realName);
//                            LifePreferences.getInstance().initSP(activity).writeSpData("userId", userId);
//                            LifePreferences.getInstance().initSP(activity).writeSpData("password", password);
//                            LifePreferences.getInstance().initSP(activity).writeSpData("mobile", user.getString("mobile"));
//                            bundle.putString(BUNDLE_LOGIN_JUMP, "登录成功");
//                        } else {
//                            String content = resultObj.getString("content");
//                            bundle.putString(BUNDLE_LOGIN_FAIL, content);
//                        }
//                    }
//                } catch (Exception e) {
//                    bundle.putString(BUNDLE_LOCAL_ERROR, "登录出错");
//                }
//                Message msg = mHandler.obtainMessage();
//                msg.what = MSG_LOGIN;
//                msg.setData(bundle);
//                mHandler.sendMessage(msg);
//            }
//        }).start();
//    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOGIN:
                    String key = msg.getData().keySet().iterator().next();
                    if (BUNDLE_LOGIN_FAIL.equals(key)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        showToast(msg.getData().getString(BUNDLE_LOGIN_FAIL));
                    } else if (BUNDLE_LOCAL_ERROR.equals(key)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        showToast(msg.getData().getString(BUNDLE_LOCAL_ERROR));
                    } else if (msg.getData().keySet().contains(BUNDLE_LOGIN_JUMP)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        showToast(msg.getData().getString(BUNDLE_LOGIN_JUMP));
                        //跳转到用户中心
                        FragmentManager manager = activity.getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
//                        transaction.replace(R.id.content_fragment, new CenterFragment());
                        transaction.commit();
                    }
                    break;

                default:
                    break;
            }
            removeMessages(msg.what);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
//                transaction.replace(R.id.content_fragment, new CenterFragment());
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
