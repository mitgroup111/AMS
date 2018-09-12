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

import com.alibaba.fastjson.JSONObject;
import com.mit.ams.R;
import com.mit.ams.common.StringUtils;
import com.mit.ams.utils.LifePreferences;
import com.mit.ams.utils.WSClient;

/**
 * Created by Administrator on 17.7.6.
 */

public class ChangePwdFragment extends Fragment {

    private String TAG = ChangePwdFragment.class.getSimpleName();

    private static final int MSG_LOGIN = 1;

    private static final String BUNDLE_LOCAL_ERROR = "local_error";
    private static final String BUNDLE_LOGIN_JUMP = "login_jump";
    private static final String BUNDLE_LOGIN_FAIL = "login_fail";

    private TextView actionbarTitle;

    private Button sureBtn;
    private EditText oriPwd, newPwd, renewPwd;

    private String password, newPassword, renewPassword;

    private ProgressDialog progressDialog;

    private AppCompatActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (AppCompatActivity) this.getActivity();
        View view = inflater.inflate(R.layout.fragment_pwd, container, false);
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
        actionbarTitle.setText("修改密码");
        actionBar.setDisplayHomeAsUpEnabled(true);// 显示返回按钮
        setHasOptionsMenu(true);//这个需要，不然onOptionsItemSelected方法不会被调用

        //页面布局初始化
        sureBtn = (Button) view.findViewById(R.id.sure_btn);
        oriPwd = (EditText) view.findViewById(R.id.ori_pwd);
        newPwd = (EditText) view.findViewById(R.id.new_pwd);
        renewPwd = (EditText) view.findViewById(R.id.renew_pwd);
        //添加监听
        sureBtn.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sure_btn:
                    password = oriPwd.getText().toString();
                    renewPassword = renewPwd.getText().toString();
                    newPassword = newPwd.getText().toString();
                    if (StringUtils.isEmpty(password)) {
                        showToast("原密码不能为空");
                        break;
                    }
                    if (!password.equals(LifePreferences.getInstance().initSP(activity).readSpData("password", ""))) {
                        showToast("原密码不正确");
                        break;
                    }
                    if (StringUtils.isEmpty(renewPassword)) {
                        showToast("新密码不能为空");
                        break;
                    }
                    if (StringUtils.isEmpty(newPassword)) {
                        showToast("确认密码不能为空");
                        break;
                    }
                    if (!newPassword.equals(renewPassword)){
                        showToast("两次输入的密码不一致");
                        break;
                    }
                    if (password.equals(renewPassword)){
                        showToast("新旧密码一致，不需要修改");
                        break;
                    }
                    if (newPassword.length() > 18 || newPassword.length() < 6){
                        showToast("密码长度不合法，请重新输入");
                        break;
                    }
                    //获取输入框的用户名 和密码
                    progressDialog = ProgressDialog.show(activity, "提示", "正在提交...");
                    updatePwd();
                    break;
                case R.id.forget_pwd_tv:

                    break;
                case R.id.register_tv:

                    break;
            }
        }
    };

    /**
     * 用户登陆
     */
    private void updatePwd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                try {
                    String mobile = (String) LifePreferences.getInstance().initSP(activity).readSpData("mobile", "");
                    final String[] key = {"mobile", "newPwd"};
                    final String[] value = {mobile, newPassword};
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
                msg.what = MSG_LOGIN;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }).start();
    }

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
                        LifePreferences.getInstance().clearSpData();
                        //跳转到用户中心
                        FragmentManager manager = activity.getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.content_fragment, new CenterFragment());
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
                transaction.replace(R.id.content_fragment, new CenterFragment());
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
