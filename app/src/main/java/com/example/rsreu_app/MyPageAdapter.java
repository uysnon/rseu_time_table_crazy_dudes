package com.example.rsreu_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.example.rsreu_app.model.DoubleWeek;
import com.example.rsreu_app.model.Week;

import java.util.Date;

public class MyPageAdapter extends FragmentStatePagerAdapter {

    /**
     * Один адаптер отвечает за 1 неделю
     */
    DoubleWeek mDoubleWeek;
    /**
     * Текущий день (пн-1;..вс-7)
     */
    int numDay;
    Context mContext;

    public MyPageAdapter(FragmentManager fm, DoubleWeek doubleWeek, Context context) {
        super(fm);
        mDoubleWeek = doubleWeek;
        mContext = context;
    }


    @Override
    public int getCount() {
        return (mDoubleWeek.getLongWeek().getDays().size());
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("MyAdapter", "position: " + position);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MainActivity.myPreference, Context.MODE_PRIVATE);
        Date date = new Date(sharedPreferences.getLong(ScheduleFragment.APP_PREFERENCES_DATE, 0));
        Date startDateSemester = new Date(sharedPreferences.getLong("startDate", 0));
        Date endDateSemester = new Date(sharedPreferences.getLong("endDate1", 0));
        int a = 1;
        if (!(isDateInCurrentSemester(startDateSemester, endDateSemester, date))) {
            return (PageFragment.newInstance(mDoubleWeek.getDay(position, date), PageFragment.MODE_NOT_DATA_FROM_SEMESTER));
        } else if (mDoubleWeek.getDay(position, date).getLessons().size() == 0) {
            return (PageFragment.newInstance(mDoubleWeek.getDay(position, date), PageFragment.MODE_WEEKEND));
        } else return (PageFragment.newInstance(mDoubleWeek.getDay(position, date)));

    }

    private static boolean isDateInCurrentSemester(Date dateStart, Date dateEnd, Date dateCurrent) {
        if ((dateCurrent.compareTo(dateStart) < 0) ||
                (dateCurrent.compareTo(dateEnd) > 0)) {
            return false;
        }
        return true;
    }

}
