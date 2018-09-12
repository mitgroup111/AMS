package com.mit.ams.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.mit.ams.R;
import com.mit.ams.common.StringUtils;
import com.mit.ams.utils.LifePreferences;
import com.mit.ams.utils.WSClient;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 17.7.6.
 */

public class ResetPwdFragment extends Fragment {

    private String TAG = ResetPwdFragment.class.getSimpleName();

    private static final int MSG_LOGIN = 1;
    private static final int MSG_VALI= 2;

    private static final String BUNDLE_LOCAL_ERROR = "local_error";
    private static final String BUNDLE_LOGIN_JUMP = "login_jump";
    private static final String BUNDLE_LOGIN_FAIL = "login_fail";

    private TextView actionbarTitle;

    private Button nextBtn;
    private EditText etNewPwd;

    private String newPwd, mobile;
    private ProgressDialog progressDialog;

    private AppCompatActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (AppCompatActivity) this.getActivity();
        View view = inflater.inflate(R.layout.fragment_reset_pwd, container, false);
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
        actionbarTitle.setText("设置新密码");
        actionBar.setDisplayHomeAsUpEnabled(true);// 显示返回按钮
        setHasOptionsMenu(true);//这个需要，不然onOptionsItemSelected方法不会被调用

        //页面布局初始化
        nextBtn = (Button) view.findViewById(R.id.next_btn);
        etNewPwd = (EditText) view.findViewById(R.id.new_pwd);
        //添加监听
        nextBtn.setOnClickListener(listener);

        mobile = getArguments().getString("mobile");
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            newPwd = etNewPwd.getText().toString();
            switch (v.getId()) {
                case R.id.next_btn:
                    if (newPwd.length() < 6 || newPwd.length() > 18) {
                        showToast("请保证密码在6-18位之间");
                        break;
                    }
                    changePwd();
                    break;
            }
        }
    };

    /**
     * 用户注册
     */
    private void changePwd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                try {
                    final String[] key = {"mobile", "newPwd"};
                    final String[] value = {mobile, newPwd};
                    String rs = WSClient.soapGetInfo("updateUserPwd", key, value);
                    if (StringUtils.isEmpty(rs)) {
                        bundle.putString(BUNDLE_LOCAL_ERROR, "修改密码出错");
                    } else {
                        JSONObject resultObj = JSONObject.parseObject(rs);
                        String status = resultObj.getString("status");
                        if ("1".equals(status)) {
                            bundle.putString(BUNDLE_LOGIN_JUMP, "修改密码成功");
                        } else {
                            String content = resultObj.getString("content");
                            bundle.putString(BUNDLE_LOGIN_FAIL, content);
                        }
                    }
                } catch (Exception e) {
                    bundle.putString(BUNDLE_LOCAL_ERROR, "修改密码出错");
                }
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_VALI;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_VALI:
                    String key1 = msg.getData().keySet().iterator().next();
                    if (BUNDLE_LOGIN_FAIL.equals(key1)) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        showToast(msg.getData().getString(BUNDLE_LOGIN_FAIL));
                    } else if (BUNDLE_LOCAL_ERROR.equals(key1)) {
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
                        showToast("密码重置成功");
                        FragmentManager manager = activity.getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.content_fragment, new LoginFragment());
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
                transaction.replace(R.id.content_fragment, new LoginFragment());
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
