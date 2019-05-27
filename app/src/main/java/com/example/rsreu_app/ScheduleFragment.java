package com.example.rsreu_app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rsreu_app.model.Day;
import com.example.rsreu_app.model.DoubleWeek;
import com.example.rsreu_app.model.Lesson;
import com.example.rsreu_app.model.Week;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ScheduleFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final String DATE_KEY = "day_key";
    public static final String APP_PREFERENCES_DATE = "Date_preference";

    MyPager pager;
    DoubleWeek mDoubleWeek;
    static Date mDate;
    static Date mFirstDayOfSemester;
    static Date mLastDayOfSemester;
    boolean mIsFirstDayNumerator;
    private LinearLayout mLayoutGoNext;
    private LinearLayout mLayoutGoPrev;
    private LinearLayout mLayoutDatePicker;
    private TextView mTextViewDate;
    private ImageView mImageArrowRight;
    private ImageView mImageArrowLeft;
    private ImageView mImageCalendar;
    private DatabaseHelper mDatabaseHelper;
    public static boolean isDateInSemester;
    private LinearLayout mLinearLayoutShareSchedule;
    private ImageView mImageShareSchedule;
    private TextView mTextShareSchedule;
    private SharedPreferences mSharedPreferences;

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

    @Override
    public void onResume() {
        mSharedPreferences = getActivity().getSharedPreferences(MainActivity.myPreference, Context.MODE_PRIVATE);
        Log.d("LALA", "onResume: " + (mSharedPreferences.getString(MainActivity.groupKey, "0")));
        super.onResume();
    }

    @Override
    public void onPause() {
        updateSharedPreferencesDate();
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("lifecycle","onCreateView");
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(MainActivity.myPreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        mLayoutGoNext = view.findViewById(R.id.layoutGoNext);
        mLayoutGoPrev = view.findViewById(R.id.layoutGoPrevious);
        mLayoutDatePicker = view.findViewById(R.id.layout_datePicker);
        mTextViewDate = view.findViewById(R.id.text_date);
        mImageArrowRight = view.findViewById(R.id.imageRightArrow);
        mImageArrowLeft = view.findViewById(R.id.imageLeftArrow);
        mImageCalendar = view.findViewById(R.id.imageCalendar);
        mLinearLayoutShareSchedule = view.findViewById(R.id.layout_share_schedule);
        mImageShareSchedule = view.findViewById(R.id.image_share_schedule);
        mTextShareSchedule = view.findViewById(R.id.text_share_schedule);
        pager = view.findViewById(R.id.pager);
        mDatabaseHelper = new DatabaseHelper(getActivity());

        if (mSharedPreferences.contains(APP_PREFERENCES_DATE)){
            mDate = new Date(mSharedPreferences.getLong(APP_PREFERENCES_DATE, 0));
        } else {
            mDate = new Date();
        }

        Cursor cursor = mDatabaseHelper.getAllDataSemester();
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            mIsFirstDayNumerator = (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IS_NUMERATOR)) == 1) ;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String firstMS = cursor.getString(cursor.getColumnIndex(DatabaseHelper.START_DATE_SEMESTER));
            String lastMS = cursor.getString(cursor.getColumnIndex(DatabaseHelper.END_DATE_SEMESTER));
            try {
                mFirstDayOfSemester = simpleDateFormat.parse(firstMS);
                mLastDayOfSemester = simpleDateFormat.parse(lastMS);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else
        {
            mIsFirstDayNumerator = true;
            mFirstDayOfSemester = new Date(2019 - 1900, 1, 11);
            mFirstDayOfSemester = new Date(2019 - 1900, 1, 11);
        }


        Log.d("LALA", "onCreateView: " +  mSharedPreferences.getString(MainActivity.groupKey, "0"));
        int group = Integer.valueOf(mSharedPreferences.getString(MainActivity.groupKey, "0"));
        mDoubleWeek = new DoubleWeek(Week.createWeek(getActivity(), true, group  ), Week.createWeek(getActivity(), false, group));
        if (mDoubleWeek.getLongWeek() != null) {
            mPageAdapter = new MyPageAdapter(getFragmentManager(), mDoubleWeek, getContext());
            pager.setAdapter(new MyPageAdapter(getFragmentManager(), mDoubleWeek, getContext()));
        }

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                if (!(isDateInCurrentSemester()) ||
                        (mDoubleWeek.getLongWeek().getDays().get(position).getLessons().size() == 0)){
                    mLinearLayoutShareSchedule.setVisibility(View.INVISIBLE);
                } else {
                    mLinearLayoutShareSchedule.setVisibility(View.VISIBLE);
                }
            }
        });


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

        mLinearLayoutShareSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mSharingIntent = new Intent(Intent.ACTION_SEND);
                mSharingIntent.setType("text/plain");
                Day currentDay = mDoubleWeek.getLongWeek().getDay(pager.getCurrentItem()+1);
                String textSharing = getActivity().getString(R.string.message_date_schedule) + " " +
                        mSimpleDateFormat.format(mDate)
                        + " (" + mDoubleWeek.getShortNameDayFromItsNum(getActivity(), mCurrentDayOfWeek) + "): " + "\n";
                for (int i = 0; i < currentDay.getLessons().size(); i++) {
                    Lesson lesson = currentDay.getLessons().get(i);
                    textSharing = textSharing + (i + 1)+ ") "
                            + lesson.getTimeFromTimeId(getActivity())+ " " +
                            lesson.getTitleTypeOptional();

                    if (!lesson.getRoom().equals("")) {
                        textSharing = textSharing + " (" + lesson.getRoomBuilding() + ")" + "\n";
                    } else {
                        textSharing = textSharing + "\n";
                    }
                }
                if (currentDay.getLessons().size() == 0) {
                    textSharing = textSharing + getActivity().getString(R.string.message_no_lessons);
                }
                mSharingIntent.putExtra(Intent.EXTRA_TEXT, textSharing);
                startActivity(Intent.createChooser(mSharingIntent, getActivity().getString(R.string.message_share_schedule)));
            }
        });

        mLinearLayoutShareSchedule.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mImageShareSchedule.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_share_light_blue));
                    mTextShareSchedule.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorLightBlue));
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    mImageShareSchedule.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_share_dark_blue));
                    mTextShareSchedule.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorDarkBlue));
                }
                return false;
            }
        });
        mSimpleDateFormat = new SimpleDateFormat("dd.MM");

        pager.setPagingEnabled(false);
        updateDayOfWeek();
        updateTimeTable();
        return view;
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
        Log.d("DATE", year + " " + month + " " + dayOfMonth);
        GregorianCalendar dateG = new GregorianCalendar(year, month, dayOfMonth);
        mDate = dateG.getTime();
        mCurrentDayOfWeek = gregorianDayOfWeekToRussian(dateG.get(dateG.DAY_OF_WEEK));
        if (!Week.isDateNumerator(mDate, mFirstDayOfSemester, mIsFirstDayNumerator)) {
            mCurrentDayOfWeek = mCurrentDayOfWeek + 7;
        }
        Log.d("dataG", "dataG.get" + dateG.get(dateG.DAY_OF_WEEK));
        Log.d("dataG", "mCurrentDayOfWeek" + mCurrentDayOfWeek);

        updateTimeTable();


    }

    /**
     * Установление контента в TextView, содержащим информацию об отображаем в настоящее время дне
     */
    private void updateTextViewDate() {
        mTextViewDate.setText(
                mSimpleDateFormat.format(mDate) +
                        " " +
                        DoubleWeek.getShortNameDayFromItsNum(getActivity(), mCurrentDayOfWeek) +
                        " " +
                        mDoubleWeek.getShortNameWeek(getActivity(), mCurrentDayOfWeek));
    }

    /**
     * Обновление содержимого таблицы с учетом установления новой даты;
     */
    private void updateTimeTable() {
        updateSharedPreferencesDate();
//        updateMode();
        pager.setCurrentItem(mCurrentDayOfWeek - 1);
        updateTextViewDate();

//        if (mWeek.isNumerator() == Week.isDateNumerator(mDate, mFirstDayOfSemester, mIsFirstDayNumerator)) {
//            pager.setCurrentItem(mCurrentDayOfWeek - 1);
//            updateTextViewDate();
//        } else {
//                mWeek = Week.createWeek(getActivity(), !mWeek.isNumerator());
//                mPageAdapter.setWeek(mWeek);
//                pager.setCurrentItem(mCurrentDayOfWeek - 1);
//                updateTextViewDate();
//            }
    }

    private void updateDayOfWeek() {
        int year = Integer.valueOf((String) DateFormat.format("yyyy", mDate));
        int month = Integer.valueOf((String) DateFormat.format("MM", mDate)) - 1;
        int day = Integer.valueOf((String) DateFormat.format("dd", mDate));
        GregorianCalendar dateG = new GregorianCalendar(year, month, day);
        Log.d("DATE", "updaateDayOfWeek(): " + mDate.getYear() + " " + mDate.getMonth() + " " + mDate.getDay());
        mCurrentDayOfWeek = gregorianDayOfWeekToRussian(dateG.get(dateG.DAY_OF_WEEK));
        if (!Week.isDateNumerator(mDate, mFirstDayOfSemester, mIsFirstDayNumerator)) {
            mCurrentDayOfWeek = mCurrentDayOfWeek + 7;
        }
        ;
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
        updateTimeTable();

    }

    private void prevPage() {
        Calendar c = Calendar.getInstance();
        c.setTime(mDate);
        c.add(Calendar.DATE, -1);
        mDate = c.getTime();
        updateDayOfWeek();
        updateTimeTable();

    }

    public static boolean isDateInCurrentSemester(){
        if ((mDate.compareTo(mFirstDayOfSemester) < 0) ||
                (mDate.compareTo(mLastDayOfSemester) > 0)) {
            return false;
        }
        return true;
    }

//    private static void updateMode(){
//        isDateInSemester = !((mDate.compareTo(mFirstDayOfSemester) < 0) ||
//                (mDate.compareTo(mLastDayOfSemester) > 0));
//
//    }

    private void updateSharedPreferencesDate(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(APP_PREFERENCES_DATE, mDate.getTime());
        editor.apply();
    }



}
