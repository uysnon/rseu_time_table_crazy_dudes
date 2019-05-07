package com.example.rsreu_app.model;

import java.util.ArrayList;

public class Week {
    private ArrayList<Day> days;
    private boolean numerator;


    public Week(ArrayList<Day> days, boolean numerator) {
        this.days = days;
        this.numerator = numerator;
    }

    public Week() {
        days = new ArrayList<>();
    }

    public Week(ArrayList<Day> days) {
        this.days = days;
    }



    public void setNumerator(boolean numerator) {
        this.numerator = numerator;
    }

    public void setDays(ArrayList<Day> days) {
        this.days = days;
    }

    public ArrayList<Day> getDays() {
        return days;
    }

    public boolean isNumerator() {
        return numerator;
    }

    public void addDay(Day day){
        days.add(day);
    }


}
