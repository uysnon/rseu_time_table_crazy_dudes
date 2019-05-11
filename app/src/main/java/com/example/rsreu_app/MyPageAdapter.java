package com.example.rsreu_app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.rsreu_app.model.Week;

public class MyPageAdapter extends FragmentPagerAdapter {

    /**
     * Один адаптер отвечает за 1 неделю
     */
    Week mWeek;

    public MyPageAdapter(FragmentManager fm, Week week) {
        super(fm);
        mWeek  = week;
    }

    @Override
    public int getCount() {
        return (mWeek.getDays().size());
    }

    @Override
    public Fragment getItem(int position) {
        return (PageFragment.newInstance(mWeek.getDays().get(position)));
    }
}
