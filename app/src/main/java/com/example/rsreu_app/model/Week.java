package com.example.rsreu_app.model;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;

import com.example.rsreu_app.DatabaseHelper;

import java.io.Serializable;
import java.util.ArrayList;

public class Week implements Serializable {
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

    /**
     * Создание объекта недели
     * @param context контекст, из которого вызывается метод
     * @param numerator true - числитель
     *                  false - знаменатель
     * @return объект Week
     */
    public static Week createWeek(Context context, boolean numerator) {
        DatabaseHelper myDB;
        myDB = new DatabaseHelper(context);
        Week week = new Week();
        week.setNumerator(numerator);
        for (int weekDay = 1; weekDay <= 7; weekDay++) {
            ArrayList<Lesson> lessons = new ArrayList<>();
            for (int timeId = 1; timeId <= 9; timeId++) {
                Cursor c  = null;
                try {
                    c = myDB.getInfo(weekDay, timeId, 0);
                    int count = c.getCount();
                    c.moveToFirst();
                    String title = c.getString(c.getColumnIndex("title"));
                    Log.e("ERROR_WEEK", "ERROR_WEEK  title");
                    String type = c.getString(c.getColumnIndex("type"));
                    boolean optional = (1 == c.getInt(c.getColumnIndex("optional")));
                    ArrayList<String> teachers = new ArrayList<>();
                    teachers.add(c.getString(c.getColumnIndex("teachers")));
                    Integer duration = c.getInt(c.getColumnIndex("duration"));
                    String room = c.getString(c.getColumnIndex("room"));
                    String build = c.getString(c.getColumnIndex("build"));
                    String dates = c.getString(c.getColumnIndex("dates"));
                    Lesson lesson = new Lesson(
                            timeId,
                            duration,
                            title,
                            type,
                            optional,
                            teachers,
                            room,
                            build,
                            dates
                    );
                    Log.d("myLog", "day_"+weekDay+" timeId_"+timeId+ " title_" + title);
                    lessons.add(lesson);
                }catch (CursorIndexOutOfBoundsException e){
                    Log.e("ERROR_WEEK", "ERROR_WEEK :(");
                }
                finally {
                    c.close();
                }
            }
            Day day = new Day();
            day.setWeekDay(weekDay);
            day.setLessons(lessons);
            week.addDay(day);
        }
        myDB.close();
        return week;
    }


}
