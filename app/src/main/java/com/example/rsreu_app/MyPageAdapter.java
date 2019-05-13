package com.example.rsreu_app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.rsreu_app.model.Week;

public class MyPageAdapter extends FragmentStatePagerAdapter {

    /**
     * Один адаптер отвечает за 1 неделю
     */
    Week mWeek;
    /**
     * Текущий день (пн-1;..вс-7)
     */
    int numDay;

    public MyPageAdapter(FragmentManager fm, Week week) {
        super(fm);
        mWeek = week;
    }

    /**
     * Установка новой недели
     * Напр. числитель сменяет знаменатель
     *
     * @param week сменяющая неделя
     */
    public void setWeek(Week week) {
        this.mWeek = week;
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
