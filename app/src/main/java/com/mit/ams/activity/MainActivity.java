package com.mit.ams.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mit.ams.R;
import com.mit.ams.common.Constants;
import com.mit.ams.fragment.CenterItemFragment;

public class MainActivity extends BaseActivity  {

    private String TAG = MainActivity.class.getSimpleName();

    private CenterItemFragment centerItemFragment;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    public static MainActivity mainActivity;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ams_main);
        //设置全屏
        ActionBar actionBar = getSupportActionBar();
        //初始化Fragment
        initFragment();
        //界面初始化
        initView();
        //检查更新
//        checkVersion();
    }



    private void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_na);
        navigationView = (NavigationView) findViewById(R.id.nav);
        View headerView = navigationView.getHeaderView(0);//获取头布局
        mainActivity = this;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                centerItemFragment =  new CenterItemFragment();
                Bundle bundle = new Bundle();
                FragmentManager manager = MainActivity.this.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.inputUser:
                        //用户信息管理
                        bundle.putString("center_item_web_url", Constants.ARS_WEB_URL_1 + Constants.ARS_CUS_URL );
                        centerItemFragment.setArguments(bundle);
                        transaction.replace(R.id.content_fragment, centerItemFragment);
                        transaction.commit();
                        drawerLayout.closeDrawer(navigationView);
                        break;
                    case R.id.inputCar:
                        //车辆信息管理
                        bundle.putString("center_item_web_url", Constants.ARS_WEB_URL_1 + Constants.ARS_CAR_URL );
                        centerItemFragment.setArguments(bundle);
                        transaction.replace(R.id.content_fragment, centerItemFragment);
                        transaction.commit();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.kanban:
                        //排程信息
                        bundle.putString("center_item_web_url", Constants.ARS_WEB_URL_1 + Constants.ARS_KANBAN_URL );
                        centerItemFragment.setArguments(bundle);
                        transaction.replace(R.id.content_fragment, centerItemFragment);
                        transaction.commit();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.task:
                        //排程信息
                        bundle.putString("center_item_web_url", Constants.ARS_WEB_URL_1 + Constants.ARS_TASK_URL );
                        centerItemFragment.setArguments(bundle);
                        transaction.replace(R.id.content_fragment, centerItemFragment);
                        transaction.commit();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.exit:
                        System.exit(0);
                        drawerLayout.closeDrawers();
                        break;
                }
                return false;
            }
        });
    }

    private void initFragment() {
        Bundle bundle = new Bundle();
        centerItemFragment =  new CenterItemFragment();
        bundle.putString("center_item_web_url", Constants.ARS_WEB_URL_1 + Constants.ARS_ORDER_URL );
        centerItemFragment.setArguments(bundle);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_fragment, centerItemFragment, centerItemFragment.getClass().getName());
        fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_fragment, fragment, fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
