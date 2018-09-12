package com.mit.ams.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.mit.ams.R;

/**
 * description: $todo$
 * autour: BlueAmer
 * date: $date$ $time$
 * update: $date$
 * version: $version$
 */

public class InfoWinAdapter implements InfoWindowAdapter, View.OnClickListener {

    private Context context;
    private LatLng latLng;
    private ImageView facImg;
    private TextView facName,facTel,facAddr;
    private String factoryName, factoryTel, factoryAddr;
    private Button yuyueBtn;

    public InfoWinAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        initData(marker);
        View view = initView();
        return view;
    }
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void initData(Marker marker) {
        latLng = marker.getPosition();
    }

    @NonNull
    private View initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.infowindow, null);
        facName = (TextView) view.findViewById(R.id.fac_name);
        facTel = (TextView) view.findViewById(R.id.fac_tel);
        facAddr = (TextView) view.findViewById(R.id.fac_addr);

        yuyueBtn = (Button) view.findViewById(R.id.yuyue_btn);

        yuyueBtn.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.yuyue_btn:

                break;
        }
    }
}
