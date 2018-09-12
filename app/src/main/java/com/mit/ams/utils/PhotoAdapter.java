package com.mit.ams.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.AbsListView.LayoutParams;

import com.mit.ams.R;

/**
 * description: $todo$
 * autour: BlueAmer
 * date: $date$ $time$
 * update: $date$
 * version: $version$
 */

public class PhotoAdapter extends BaseAdapter {

    private Context context;

    public ImageView imageView;

    public PhotoAdapter(Context context){
        this.context = context;
    }

    private int[] imgSrc = {
        R.drawable.photo_zw,
        R.drawable.photo_zw,
        R.drawable.photo_zw,
        R.drawable.photo_zw,
        R.drawable.photo_zw,
        R.drawable.photo_zw
    };

    public int[] getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(int[] imgSrc) {
        this.imgSrc = imgSrc;
    }

    @Override
    public int getCount() {
        return imgSrc.length;
    }

    @Override
    public Object getItem(int position) {
        return imgSrc[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            imageView = new ImageView(context);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(imgSrc[position]);
        imageView.setAdjustViewBounds(true);
        return imageView;
    }
}
