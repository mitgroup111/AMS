package com.mit.ams.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mit.ams.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 17.7.6.
 */

public class InsuranceFragment extends ListFragment {

    private String TAG = InsuranceFragment.class.getSimpleName();

    private TextView actionbarTitle;
    private AppCompatActivity activity;
    private ListView listView;

    private List<Map<String, Object>> itemList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (AppCompatActivity) this.getActivity();
        View view = inflater.inflate(R.layout.fragment_insurance, container, false);
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
        actionbarTitle.setText("报案理赔");
        actionBar.setDisplayHomeAsUpEnabled(true);// 显示返回按钮
        setHasOptionsMenu(true);//这个需要，不然onOptionsItemSelected方法不会被调用

        //初始化保险公司列表数据
        for (int i = 0; i < companyNames.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("insurance_name", companyNames[i]);
            map.put("insurance_number", companyNumbers[i]);
            itemList.add(map);
        }
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setDivider(activity.getApplicationContext().getResources().getDrawable(R.drawable.diver));
        SimpleAdapter adapter = new SimpleAdapter(activity, itemList, R.layout.item_insurance,
                new String[]{"insurance_name", "insurance_number"}, new int[]{R.id.insurance_name, R.id.insurance_number});
        listView.setAdapter(adapter);
    }

    //拨打电话
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Log.d(TAG, companyNumbers[position]);
        // 检查是否获得了权限（Android6.0运行时权限）
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // 没有获得授权，申请授权
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
                // 返回值：
                //如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
                //如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
                //如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                // 弹窗需要解释为何需要该权限，再次请求授权
                Toast.makeText(activity, "请授权！", Toast.LENGTH_LONG).show();
                // 帮跳转到该应用的设置界面，让用户手动授权
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } else {
                // 不需要解释为何需要该权限，直接请求授权
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else {
            // 已经获得授权，可以打电话
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            //url:统一资源定位符
            //uri:统一资源标示符（更广）
            intent.setData(Uri.parse("tel:" + companyNumbers[position]));
            //开启系统拨号器
            startActivity(intent);
        }
        super.onListItemClick(l, v, position, id);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content_fragment, new DamageFragment());
                transaction.commit();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String[] companyNames = {"平安汽车保险", "人保汽车保险", "中华联合保险", "大地汽车保险", "永安汽车保险", "天安汽车保险", "阳光汽车保险", "安邦汽车保险", "太平汽车保险", "天平汽车保险", "人寿汽车保险", "永诚汽车保险", "华泰汽车保险", "中银汽车保险", "渤海汽车保险", "英达汽车保险", "都邦汽车保险", "安诚汽车保险"};
    private String[] companyNumbers = {"95511", "95518", "95585", "95590", "95502", "95505", "95510", "95569", "95529", "95550", "95519", "95552", "95509", "95566", "4006-116-666", "4000-188-688", "4008-895-586", "4000-500-000"};

}
