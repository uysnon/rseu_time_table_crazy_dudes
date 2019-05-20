package com.example.rsreu_app;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    /**
     * Выбранный (текущий по умолчанию) день недели
     * Формат: пн-1, .. , вс-7
     */
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
            mPageAdapter = new MyPageAdapter(getFragmentManager(), mWeek);
            pager.setAdapter(new MyPageAdapter(getFragmentManager(), mWeek));
        }

//        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            private static final float thresholdOffset = 0.5f;
//            private boolean scrollStarted, checkDirection;
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (checkDirection) {
//                    if (thresholdOffset > positionOffset) {
//                        nextPage();
//                    } else {
//                        prevPage();
//                    }
//                    checkDirection = false;
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {}
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                if (!scrollStarted && state == ViewPager.SCROLL_STATE_DRAGGING) {
//                    scrollStarted = true;
//                    checkDirection = true;
//                } else {
//                    scrollStarted = false;
//                }
//            }
//        });

        mLayoutGoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPage();
            }
        });

        mLayoutGoPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevPage();
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
        updateDayOfWeek();
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

    /**
     * Метод, вызываемый при выборе даты в DatePickerFragment-e.
     * Обновление информации в TextView, отобраающим информацию о текущем дне.
     * Обновление содержания таблицы.
     *
     * @param view       DatePickerDialog
     * @param year       выбранный год
     * @param month      выбранный месяц
     * @param dayOfMonth выбранный день месяца
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d("DATE", year+" "+ month + " " + dayOfMonth);
        GregorianCalendar dateG = new GregorianCalendar(year, month, dayOfMonth);
        mDate = dateG.getTime();
        mCurrentDayOfWeek = gregorianDayOfWeekToRussian(dateG.get(dateG.DAY_OF_WEEK));
        Log.d("dataG", "dataG.get" + dateG.get(dateG.DAY_OF_WEEK));
        Log.d("dataG", "mCurrentDayOfWeek" + mCurrentDayOfWeek);
        updateTimeTable();
        setTextViewDate();


    }

    /**
     * Установление контента в TextView, содержащим информацию об отображаем в настоящее время дне
     */
    private void setTextViewDate() {
        mTextViewDate.setText(
                mSimpleDateFormat.format(mDate) +
                        Week.getNameDayFromItsNum(getActivity(), mCurrentDayOfWeek) +
                        mWeek.getNameNumerator(getActivity())
        );
    }

    /**
     * Обновление содержимого таблицы с учетом установления новой даты;
     */
    private void updateTimeTable() {
        if (mWeek.isNumerator() == Week.isDateNumerator(mDate, mFirstDayOfSemester, mIsFirstDayNumerator)) {
            pager.setCurrentItem(mCurrentDayOfWeek - 1);
        } else {
            if (mWeek.isNumerator() == Week.isDateNumerator(mDate, mFirstDayOfSemester, mIsFirstDayNumerator)) {
                mWeek = Week.createWeek(getActivity(), !mWeek.isNumerator());
                mPageAdapter.setWeek(mWeek);
                pager.setCurrentItem(mCurrentDayOfWeek - 1);
            }
        }
    }

    private void updateDayOfWeek() {
        int year = Integer.valueOf((String) DateFormat.format("yyyy", mDate));
        int month = Integer.valueOf((String)DateFormat.format("MM", mDate))-1;
        int day = Integer.valueOf((String)DateFormat.format("dd", mDate));
        GregorianCalendar dateG = new GregorianCalendar(year, month, day);
        Log.d("DATE", "updaateDayOfWeek(): " + mDate.getYear() +" "+ mDate.getMonth() + " " + mDate.getDay());
        mCurrentDayOfWeek = gregorianDayOfWeekToRussian(dateG.get(dateG.DAY_OF_WEEK));
    }

    /**
     * Первод дня недели из европейской системы (вс-1 .. сб-7)
     * в российскую (пн-1 .. вс-7)
     *
     * @param dayOfWeek день недели в европейском формате
     * @return день недели в русском формате (пн-1 .. вс-7)
     */
    private static int gregorianDayOfWeekToRussian(int dayOfWeek) {
        switch (dayOfWeek) {
            case (1):
                return 7;
            default:
                return (dayOfWeek - 1);
        }
    }

    private void nextPage() {
        Calendar c = Calendar.getInstance();
        c.setTime(mDate);
        c.add(Calendar.DATE, 1);
        mDate = c.getTime();
        updateDayOfWeek();
        setTextViewDate();
        updateTimeTable();
    }

    private void prevPage() {
        Calendar c = Calendar.getInstance();
        c.setTime(mDate);
        c.add(Calendar.DATE, -1);
        mDate = c.getTime();
        updateDayOfWeek();
        setTextViewDate();
        updateTimeTable();
    }


}
