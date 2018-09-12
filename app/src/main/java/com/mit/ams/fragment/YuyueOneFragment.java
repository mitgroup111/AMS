package com.mit.ams.fragment;

import android.os.Bundle;
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
import android.widget.TextView;

import com.mit.ams.R;

/**
 * Created by Administrator on 17.7.6.
 */

public class YuyueOneFragment extends Fragment {

    private AppCompatActivity activity;
    private View view;
    private ActionBar actionBar;
    private TextView actionbarTitle;

    private TextView damageTv, noDamageTv;

    private String factoryId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_yuyue1, container, false);

        activity = (AppCompatActivity) this.getActivity();
        initView();
        return view;
    }

    private void initView() {
        //把标题栏改为登陆
        actionBar = activity.getSupportActionBar();
        actionBar.setCustomView(R.layout.text_titlebar);
        actionbarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title);
        actionbarTitle.setText("维修预约");
        actionBar.setDisplayHomeAsUpEnabled(true);// 显示返回按钮
        setHasOptionsMenu(true);//这个需要，不然onOptionsItemSelected方法不会被调用

        damageTv = (TextView) view.findViewById(R.id.damage_tv);
        noDamageTv = (TextView) view.findViewById(R.id.no_damage_tv);

        damageTv.setOnClickListener(listener);
        noDamageTv.setOnClickListener(listener);

        factoryId = getArguments().getString("FACTORY_ID");
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager manager = activity.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("FACTORY_ID", factoryId);
            switch (v.getId()) {
                case R.id.damage_tv:
                    YuyueTwo1Fragment yuyueTwo1Fragment = new YuyueTwo1Fragment();
                    yuyueTwo1Fragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, yuyueTwo1Fragment);
                    transaction.commit();
                    break;
                case R.id.no_damage_tv:
                    YuyueTwo2Fragment yuyueTwo2Fragment = new YuyueTwo2Fragment();
                    yuyueTwo2Fragment.setArguments(bundle);
                    transaction.replace(R.id.content_fragment, yuyueTwo2Fragment);
                    transaction.commit();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("FACTORY_ID", factoryId);
                RepairFragment repairFragment = new RepairFragment();
                repairFragment.setArguments(bundle);
                transaction.replace(R.id.content_fragment, repairFragment);
                transaction.commit();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

