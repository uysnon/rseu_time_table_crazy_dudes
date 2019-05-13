package com.example.rsreu_app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {
    private static String KEY_DATE = "date";
    private Date mDate;

    public static  DatePickerFragment newInstance(Date date){
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_DATE, date);
        datePickerFragment.setArguments(args);
        return datePickerFragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mDate = (Date)getArguments().getSerializable(KEY_DATE);
        int year = mDate.getYear();
        int month = mDate.getMonth();
        int day = mDate.getDay();
        return new DatePickerDialog(
                getActivity(),
                (DatePickerDialog.OnDateSetListener)getTargetFragment(),
                year,
                month,
                day
                );
    }
}
