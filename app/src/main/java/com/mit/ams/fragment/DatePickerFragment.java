package com.mit.ams.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    int _year = 1970;
    int _month = 0;
    int _day = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        // TODO 日期选择完成事件，取消时不会触发
        _year = year;
        _month = monthOfYear + 1;
        _day = dayOfMonth;
        Log.i("DatePickerFragment", "year=" + _year + ",monthOfYear=" + _month + ",dayOfMonth=" + _day);
        if (getTargetFragment() == null) {
            Log.e("dddddd", "al;sdkjfal");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("date", _year + "-" + _month + "-" + _day);
        getTargetFragment().onActivityResult(AddMaintainFragment.REUEST_CODDE, Activity.RESULT_OK, intent);
    }
}