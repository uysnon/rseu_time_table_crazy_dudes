package com.example.rsreu_app;

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
import android.widget.TextView;

import com.example.rsreu_app.model.Week;

import org.w3c.dom.Text;

import java.util.Date;

public class ScheduleFragment extends Fragment {

    private static  final String DATE_KEY = "day_key";
    Button fullSchedule;
    ViewPager pager;
    Week mWeek;
    Date mDate;

    public static ScheduleFragment newInstance(Date date) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATE_KEY, date);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        fullSchedule = view.findViewById(R.id.fullSchedule);
        if (getArguments() != null){
            mDate = (Date) getArguments().getSerializable(DATE_KEY);
        }
        fullSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Log.d("SCHEDULE_FRAGMENT", "ОНКРЕАТЕ ВЬЮ");
        if (mWeek == null) {
            mWeek = Week.createWeek(getActivity(), false);
        }
        if (mWeek != null) {
            pager = view.findViewById(R.id.pager);
            Log.d("SCHEDULE_FRAGMENT", "СОЗДАЕМ ТАБЛИЧКУ");
            pager.setAdapter(new MyPageAdapter(getFragmentManager(), mWeek));
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onStop(){
        super.onStop();
    }

}
