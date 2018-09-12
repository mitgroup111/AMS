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
import android.widget.ImageView;
import android.widget.TextView;

import com.mit.ams.R;

/**
 * Created by Administrator on 17.7.6.
 */

public class DamageFragment extends Fragment {

    private AppCompatActivity activity;
    private View view;
    private ActionBar actionBar;
    private TextView actionbarTitle;

    private TextView shiguTv, weixiuTv;
    private ImageView insuranceList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_damage, container, false);

        activity = (AppCompatActivity) this.getActivity();
        initView();
        return view;
    }

    private void initView() {
        //把标题栏改为登陆
        actionBar = activity.getSupportActionBar();
        actionBar.setCustomView(R.layout.text_titlebar);
        actionbarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title);
        actionbarTitle.setText("事故定损");
        actionBar.setDisplayHomeAsUpEnabled(false);// 显示返回按钮

        shiguTv = (TextView) view.findViewById(R.id.shigu_tv);
        weixiuTv = (TextView) view.findViewById(R.id.weixiu_tv);
        insuranceList = (ImageView) view.findViewById(R.id.insurance_list);

        shiguTv.setOnClickListener(listener);
        weixiuTv.setOnClickListener(listener);
        insuranceList.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            DamagePhotoFragment damagePhotoFragment = new DamagePhotoFragment();
//            AuthOneFragment damagePhotoFragment = new AuthOneFragment();
            switch (v.getId()) {
                case R.id.shigu_tv:
                    bundle.putInt("flag", 0);
                    damagePhotoFragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, damagePhotoFragment);
                    transaction.commit();
                    break;
                case R.id.weixiu_tv:
                    bundle.putInt("flag", 1);
                    damagePhotoFragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, damagePhotoFragment);
                    transaction.commit();
                    break;

                case R.id.insurance_list:
                    InsuranceFragment insuranceFragment = new InsuranceFragment();
                    transaction.replace(R.id.content_fragment, insuranceFragment);
                    transaction.commit();
                    break;

                default:
                    break;
            }
        }
    };
}

