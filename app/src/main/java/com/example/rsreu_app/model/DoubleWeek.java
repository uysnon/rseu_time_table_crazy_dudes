package com.example.rsreu_app.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;

public class DoubleWeek {
    Week mDoubleWeek;
    Week mNumerator;
    Week mDenominator;

    public DoubleWeek(Week numerator, Week denominator) {
        mNumerator = numerator;
        mDenominator = denominator;
        ArrayList<Day> days = new ArrayList<>();
        days.addAll(numerator.getDays());
        days.addAll(denominator.getDays());
        mDoubleWeek = new Week(days, false);
    }

    public Week getLongWeek() {
        return mDoubleWeek;
    }

    public Day getDay(int dayOfWeek, Date date){
        Day day = getLongWeek().getDays().get(dayOfWeek);
        for (int i = 0; i < day.getLessons().size(); i++){
            Lesson lesson = day.getLessons().get(i);
            if (!lesson.isDateInDates(date)){
                day.getLessons().remove(i);
            }
        }
        return day;
    }

    public Week getNumerator() {
        return mNumerator;
    }

    public Week getDenominator() {
        return mDenominator;
    }

    public String getNameWeek(Context context, int day) {
        if (day <= 7) {
            return mNumerator.getNameNumerator(context);
        } else {
            return mDenominator.getNameNumerator(context);
        }
    }

    public static String getNameDayFromItsNum(Context context, int num) {
        if (num > 7) {
            num = num - 7;
        }
        return Week.getNameDayFromItsNum(context, num);
    }


    public String getShortNameWeek(Context context, int day) {
        if (day <= 7) {
            return mNumerator.getShortNameNumerator(context);
        } else {
            return mDenominator.getShortNameNumerator(context);
        }
    }

    public static String getShortNameDayFromItsNum(Context context, int num) {
        if (num > 7) {
            num = num - 7;
        }
        return Week.getShortNameDayFromItsNum(context, num);
    }

}
