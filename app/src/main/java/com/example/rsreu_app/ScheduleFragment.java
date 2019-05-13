package com.example.rsreu_app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rsreu_app.model.Week;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ScheduleFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final String DATE_KEY = "day_key";
    Button fullSchedule;
    ViewPager pager;
    Week mWeek;
    Date mDate;
    Date mFirstDayOfSemester;
    boolean mIsFirstDayNumerator;
    private LinearLayout mLayoutGoNext;
    private LinearLayout mLayoutGoPrev;
    private LinearLayout mLayoutDatePicker;
    private TextView mTextViewDate;
    private int mCurrentDayOfWeek;
    private boolean mIsNumerator;
    private SimpleDateFormat mSimpleDateFormat;
    private MyPageAdapter mPageAdapter;


    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        mLayoutGoNext = view.findViewById(R.id.layoutGoNext);
        mLayoutGoPrev = view.findViewById(R.id.layoutGoPrevious);
        mLayoutDatePicker = view.findViewById(R.id.layout_datePicker);
        mTextViewDate = view.findViewById(R.id.text_date);
        fullSchedule = view.findViewById(R.id.fullSchedule);
        pager = view.findViewById(R.id.pager);
        mDate = new Date();
        /*
        Покачто данные взяты "с воздуха", далее должны быть заменены взтыми из бд
         */
        mIsFirstDayNumerator = true;
        mFirstDayOfSemester = new Date(2019, 2, 11);
        /*
         */
        fullSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Log.d("SCHEDULE_FRAGMENT", "ОНКРЕАТЕ ВЬЮ");

        mWeek = Week.createWeek(getActivity(), Week.isDateNumerator(mDate, mFirstDayOfSemester, mIsFirstDayNumerator));
        if (mWeek != null) {
            mPageAdapter= new MyPageAdapter(getFragmentManager(), mWeek);
            pager.setAdapter(new MyPageAdapter(getFragmentManager(), mWeek));
        }
        mLayoutGoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() + 1);
            }
        });
        mLayoutGoPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() - 1);
            }
        });
        mLayoutDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePicker = DatePickerFragment.newInstance(mDate);
                datePicker.setTargetFragment(ScheduleFragment.this, 0);
                datePicker.show(getActivity().getSupportFragmentManager(), "date picker");
            }
        });
        mSimpleDateFormat = new SimpleDateFormat("dd/MM");
        Date date = new Date();
        GregorianCalendar dateG = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDay());
        mCurrentDayOfWeek = dateG.get(dateG.DAY_OF_WEEK)-1;
        setTextViewDate();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        GregorianCalendar dateG = new GregorianCalendar(year, month, dayOfMonth);
        mDate =  dateG.getTime();
        mCurrentDayOfWeek = dateG.get(dateG.DAY_OF_WEEK)-1;
        updateTimeTable();
        setTextViewDate();


    }

    private void setTextViewDate() {
        mTextViewDate.setText(
                mSimpleDateFormat.format(mDate) +
                Week.getNameDayFromItsNum(getActivity(), mCurrentDayOfWeek) +
                mWeek.getNameNumerator(getActivity())
        );
    }

    private void updateTimeTable() {
        if (mWeek.isNumerator() == Week.isDateNumerator(mDate, mFirstDayOfSemester, mIsFirstDayNumerator)) {
            pager.setCurrentItem(mCurrentDayOfWeek);
        } else {
            if (mWeek.isNumerator() == Week.isDateNumerator(mDate, mFirstDayOfSemester, mIsFirstDayNumerator)) {
                mWeek = Week.createWeek(getActivity(), !mWeek.isNumerator());
                mPageAdapter.setWeek(mWeek);
                pager.setCurrentItem(mCurrentDayOfWeek);
            }
        }
    }
}
