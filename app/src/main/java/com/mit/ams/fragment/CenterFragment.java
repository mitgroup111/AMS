package com.mit.ams.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mit.ams.R;
import com.mit.ams.common.Constants;
import com.mit.ams.common.StringUtils;
import com.mit.ams.utils.LifePreferences;

/**
 * Created by Administrator on 17.7.6.
 */

public class CenterFragment extends Fragment {

    private AppCompatActivity activity;
    private View view;
    private ActionBar actionBar;

    private RoundedImageView touxiangImg;
    private TextView actionbarTitle, realNameTv;
    private Button exitBtn;
    private LinearLayout yuyue,dianping,dingsun,weixiu,baoyang,car,msg,pwd;

    private String userId;
    private String userName;
    private boolean isLogin = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_center, container, false);
        activity = (AppCompatActivity) this.getActivity();
        userId = (String) LifePreferences.getInstance().initSP(activity).readSpData("userId", "");
        if(!StringUtils.isEmpty(userId)){
            isLogin = true;
            userName = (String) LifePreferences.getInstance().initSP(activity).readSpData("mobile", "");
        }
        initView(view);
        return view;
    }

    private void initView(View view){
        //把标题栏改为登陆
        actionBar = activity.getSupportActionBar();
        actionBar.setCustomView(R.layout.text_titlebar);
        actionbarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title);
        actionbarTitle.setText("个人中心");
        actionBar.setDisplayHomeAsUpEnabled(false);// 显示返回按钮

        touxiangImg = (RoundedImageView) view.findViewById(R.id.touxiang_img);
        exitBtn = (Button) view.findViewById(R.id.exit_btn);
        realNameTv = (TextView) view.findViewById(R.id.real_name);

        yuyue = (LinearLayout) view.findViewById(R.id.center_yuyue);
        dianping = (LinearLayout) view.findViewById(R.id.center_dianping);
        dingsun = (LinearLayout) view.findViewById(R.id.center_dingsun);
        weixiu = (LinearLayout) view.findViewById(R.id.center_weixiu);
        baoyang = (LinearLayout) view.findViewById(R.id.center_baoyang);
        car = (LinearLayout) view.findViewById(R.id.center_car);
        msg = (LinearLayout) view.findViewById(R.id.center_msg);
        pwd = (LinearLayout) view.findViewById(R.id.center_pwd);

        yuyue.setOnClickListener(listener);
        dianping.setOnClickListener(listener);
        dingsun.setOnClickListener(listener);
        weixiu.setOnClickListener(listener);
        baoyang.setOnClickListener(listener);
        car.setOnClickListener(listener);
        msg.setOnClickListener(listener);
        pwd.setOnClickListener(listener);

        //用户尚未登陆，隐藏退出按钮，并将头像设为不可点击，用户名设为空，提示用户点击头像登陆
        if(isLogin){
            realNameTv.setText(userName);
            exitBtn.setVisibility(View.VISIBLE);
            exitBtn.setOnClickListener(listener);
        } else {
            exitBtn.setVisibility(View.GONE);
            touxiangImg.setOnClickListener(listener);
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CenterItemFragment centerItemFragment =  new CenterItemFragment();
            Bundle bundle = new Bundle();
            FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            switch(v.getId()){
                case R.id.touxiang_img ://点击头像
                    transaction.replace(R.id.content_fragment, new LoginFragment());
                    transaction.commit();
                    break;
                case R.id.exit_btn ://退出登陆
                    LifePreferences.getInstance().clearSpData();
                    //跳转到用户中心 TODO:没找到更新UI的方法，暂时先重新加载一遍吧
                    transaction.replace(R.id.content_fragment, new CenterFragment());
                    transaction.commit();
                    break;
                case R.id.center_yuyue:
                    if(StringUtils.isEmpty(userId)){
                        showToast("对不起，请先登录");
                        break;
                    }
                    bundle.putString("center_item_web_url", Constants.YUYUE_URL.replace("REPLACE_USERID", userId));
                    centerItemFragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, centerItemFragment);
                    transaction.commit();
                    break;
                case R.id.center_dianping:
                    if(StringUtils.isEmpty(userId)){
                        showToast("对不起，请先登录");
                        break;
                    }
                    bundle.putString("center_item_web_url", Constants.DIANPING_URL.replace("REPLACE_USERID", userId));
                    centerItemFragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, centerItemFragment);
                    transaction.commit();
                    break;
                case R.id.center_dingsun:
                    if(StringUtils.isEmpty(userId)){
                        showToast("对不起，请先登录");
                        break;
                    }
                    bundle.putString("center_item_web_url", Constants.DINGSUN_URL.replace("REPLACE_USERID", userId));
                    centerItemFragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, centerItemFragment);
                    transaction.commit();
                    break;
                case R.id.center_weixiu:
                    if(StringUtils.isEmpty(userId)){
                        showToast("对不起，请先登录");
                        break;
                    }
                    bundle.putString("center_item_web_url", Constants.WEIXIU_URL.replace("REPLACE_USERID", userId));
                    centerItemFragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, centerItemFragment);
                    transaction.commit();
                    break;
                case R.id.center_baoyang:
                    if(StringUtils.isEmpty(userId)){
                        showToast("对不起，请先登录");
                        break;
                    }
                    bundle.putString("center_item_web_url", Constants.BAOYANG_URL.replace("REPLACE_USERID", userId));
                    centerItemFragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, centerItemFragment);
                    transaction.commit();
                    break;
                case R.id.center_car:
                    if(StringUtils.isEmpty(userId)){
                        showToast("对不起，请先登录");
                        break;
                    }
                    bundle.putString("center_item_web_url", Constants.CAR_URL.replace("REPLACE_USERID", userId));
                    centerItemFragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, centerItemFragment);
                    transaction.commit();
                    break;
                case R.id.center_msg:
                    if(StringUtils.isEmpty(userId)){
                        showToast("对不起，请先登录");
                        break;
                    }
                    bundle.putString("center_item_web_url", Constants.MSG_URL.replace("REPLACE_USERID", userId));
                    centerItemFragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, centerItemFragment);
                    transaction.commit();
                    break;
                case R.id.center_pwd:
                    if(StringUtils.isEmpty(userId)){
                        showToast("对不起，请先登录");
                        break;
                    }
                    transaction.replace(R.id.content_fragment, new ChangePwdFragment());
                    transaction.commit();
                    break;
                default:
                    break;

            }
        }
    };

    private void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }

}
