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
import com.mit.ams.utils.WSClient;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 17.7.6.
 */

public class FindPwdFragment extends Fragment {

    private String TAG = FindPwdFragment.class.getSimpleName();

    private static final int MSG_LOGIN = 1;
    private static final int MSG_VALI= 2;

    private static final String BUNDLE_LOCAL_ERROR = "local_error";
    private static final String BUNDLE_LOGIN_JUMP = "login_jump";
    private static final String BUNDLE_LOGIN_FAIL = "login_fail";

    private TextView actionbarTitle;

    private Button nextBtn, getValiBtn;
    private EditText etTel, etVali;

    private String tel, vali, valiNum = "0";
    private ProgressDialog progressDialog;

    private AppCompatActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (AppCompatActivity) this.getActivity();
        View view = inflater.inflate(R.layout.fragment_forget_pwd, container, false);
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
        actionbarTitle.setText("找回密码");
        actionBar.setDisplayHomeAsUpEnabled(true);// 显示返回按钮
        setHasOptionsMenu(true);//这个需要，不然onOptionsItemSelected方法不会被调用

        //页面布局初始化
        nextBtn = (Button) view.findViewById(R.id.next_btn);
        getValiBtn = (Button) view.findViewById(R.id.get_vali);
        etTel = (EditText) view.findViewById(R.id.et_tel);
        etVali = (EditText) view.findViewById(R.id.vali);
        //添加监听
        nextBtn.setOnClickListener(listener);
        getValiBtn.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tel = etTel.getText().toString();
            vali = etVali.getText().toString();
            switch (v.getId()) {
                case R.id.next_btn:
                    if (StringUtils.isEmpty(tel)) {
                        showToast("手机号不能为空");
                        break;
                    }
                    if (!StringUtils.isMobileNo(tel)) {
                        showToast("手机号格式不正确");
                        break;
                    }
                    if (StringUtils.isEmpty(vali)) {
                        showToast("验证码不能为空");
                        break;
                    }
                    if (!valiNum.equals(vali)){
                        showToast("验证码不正确");
                        break;
                    }
                    FragmentManager manager = activity.getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("mobile", tel);
//                    ResetPwdFragment resetPwdFragment = new ResetPwdFragment();
//                    resetPwdFragment.setArguments(bundle);
//                    transaction.replace(R.id.content_fragment, resetPwdFragment);
                    transaction.commit();
                    break;
                case R.id.get_vali:
                    if (StringUtils.isEmpty(tel)) {
                        showToast("手机号不能为空");
                        break;
                    }
                    if (!StringUtils.isMobileNo(tel)) {
                        showToast("手机号格式不正确");
                        break;
                    }
                    sendVali();
                    break;
            }
        }
    };

    class SendValiMessage extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            //sendVali();
            return 1;
        }

        TimerTask task;
        private int time = 60;
        private Timer timer = new Timer();

        @Override
        protected void onPostExecute(Integer result) {
            showToast("我们已发送一条验证短信到您的手机,请注意查收");
            getValiBtn.setEnabled(false);
            task = new TimerTask() {
                @Override
                public void run() {
                    activity.runOnUiThread(new Runnable() { // UI thread
                        @Override
                        public void run() {
                            if (time <= 0) {
                                // 当倒计时小余=0时记得还原图片，可以点击
                                getValiBtn.setEnabled(true);
                                getValiBtn.setTextColor(Color.parseColor("#454545"));
                                getValiBtn.setText("获取验证码");
                                task.cancel();
                            } else {
                                getValiBtn.setText(time + "秒后重试");
                                getValiBtn.setTextColor(Color.rgb(125, 125, 125));
                            }
                            time--;
                        }
                    });
                }
            };

            time = 60;
            timer.schedule(task, 0, 1000);
        }
    }

    /**
     * 用户注册
     */
    private void sendVali() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                try {
                    final String[] key = {"mobile", "flag"};
                    final String[] value = {tel, "2"};
                    String rs = WSClient.soapGetInfo("getVerificationNum", key, value);
                    if (StringUtils.isEmpty(rs)) {
                        bundle.putString(BUNDLE_LOCAL_ERROR, "发送验证码出错");
                    } else {
                        JSONObject resultObj = JSONObject.parseObject(rs);
                        String status = resultObj.getString("status");
                        if ("1".equals(status)) {
                            valiNum = resultObj.getString("content");
                            bundle.putString(BUNDLE_LOGIN_JUMP, "发送成功");
                        } else {
                            String content = resultObj.getString("content");
                            bundle.putString(BUNDLE_LOGIN_FAIL, content);
                        }
                    }
                } catch (Exception e) {
                    bundle.putString(BUNDLE_LOCAL_ERROR, "发送验证码出错");
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
                        new SendValiMessage().execute();
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
