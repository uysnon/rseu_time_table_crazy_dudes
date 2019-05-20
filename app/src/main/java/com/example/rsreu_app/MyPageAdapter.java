package com.example.rsreu_app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.rsreu_app.model.DoubleWeek;
import com.example.rsreu_app.model.Week;

public class MyPageAdapter extends FragmentStatePagerAdapter {

    /**
     * Один адаптер отвечает за 1 неделю
     */
    DoubleWeek mDoubleWeek;
    /**
     * Текущий день (пн-1;..вс-7)
     */
    int numDay;

    public MyPageAdapter(FragmentManager fm, DoubleWeek doubleWeek) {
        super(fm);
        mDoubleWeek = doubleWeek;
    }


    @Override
    public int getCount() {
        return (mDoubleWeek.getLongWeek().getDays().size());
    }

    @Override
    public Fragment getItem(int position) {
        return (PageFragment.newInstance(mDoubleWeek.getLongWeek().getDays().get(position)));
    }
}