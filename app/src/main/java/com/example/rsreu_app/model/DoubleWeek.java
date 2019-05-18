package com.example.rsreu_app.model;

import android.content.Context;

import java.util.ArrayList;

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

}
