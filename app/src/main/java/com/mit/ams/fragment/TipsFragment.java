package com.mit.ams.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mit.ams.R;

/**
 * Created by Administrator on 17.7.6.
 */

public class TipsFragment extends Fragment {

    private AppCompatActivity activity;
    private View view;
    private ActionBar actionBar;
    private TextView actionbarTitle;

    private PopupWindow popupWindow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tips, container, false);

        activity = (AppCompatActivity) this.getActivity();
        initView();
        return view;
    }

    private void initView() {
        //把标题栏改为登陆
        actionBar = activity.getSupportActionBar();
        actionBar.hide();
        actionBar.setCustomView(R.layout.text_titlebar);
        actionbarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title);
        actionbarTitle.setText("事故定损");
        actionBar.setDisplayHomeAsUpEnabled(false);// 显示返回按钮
        actionBar.hide();

        // 一个自定义的布局，显示提示信息
        View contentView = LayoutInflater.from(activity).inflate(R.layout.tips, null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        //实例化一个ColorDrawable颜色为半透明，已达到变暗的效果
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(dw);
        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.TOP, 0, 0);

        ImageView closeTips = (ImageView) contentView.findViewById(R.id.close_tips);
        closeTips.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            switch (v.getId()) {
                case R.id.close_tips:
                    actionBar.show();
                    popupWindow.dismiss();
                    transaction.replace(R.id.content_fragment, new DamageFragment());
                    transaction.commit();
                    break;

                default:
                    break;
            }
        }
    };
}

