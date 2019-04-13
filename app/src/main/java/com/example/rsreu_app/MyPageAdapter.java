package com.example.rsreu_app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyPageAdapter extends FragmentPagerAdapter {

    public MyPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return (10);
    }

    @Override
    public Fragment getItem(int position) {
        return (PageFragment.newInstance(position));
    }
}
