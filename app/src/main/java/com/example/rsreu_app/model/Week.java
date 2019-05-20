package com.example.rsreu_app.model;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;

import com.example.rsreu_app.DatabaseHelper;
import com.example.rsreu_app.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

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

    public void addDay(Day day) {
        days.add(day);
    }

    /**
     * Создание объекта недели
     *
     * @param context   контекст, из которого вызывается метод
     * @param numerator true - числитель
     *                  false - знаменатель
     * @return объект Week
     */
    public static Week createWeek(Context context, boolean numerator) {
        DatabaseHelper myDB;
        myDB = new DatabaseHelper(context);
        Week week = new Week();
        int groupNumber = 543;
        week.setNumerator(numerator);
        for (int weekDay = 1; weekDay <= 7; weekDay++) {
            ArrayList<Lesson> lessons = new ArrayList<>();
            for (int timeId = 1; timeId <= 9; timeId++) {
                Cursor c = null;
                try {
                    c = myDB.getInfo(groupNumber,weekDay, timeId, 0);
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
                    Log.d("myLog", "day_" + weekDay + " timeId_" + timeId + " title_" + title);
                    lessons.add(lesson);
                } catch (CursorIndexOutOfBoundsException e) {
                    Log.e("ERROR_WEEK", "ERROR_WEEK :(");
                } finally {
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

    /**
     * Проверка даты на принадлежность недели к числителю или же знаменателю
     *
     * @param date                 текущая дата
     * @param firstDateOfSemester  дата первого дня семестра
     * @param isFirstWeekNumerator является ли первый день семестра числителем
     * @return true - день текущий даты относится к числителю
     * false - день текущей даты относится к знаменателю
     */
    public static boolean isDateNumerator(Date date, Date firstDateOfSemester, boolean isFirstWeekNumerator) {
        boolean result;
        GregorianCalendar firstDateG = new GregorianCalendar(firstDateOfSemester.getYear(), firstDateOfSemester.getMonth(), firstDateOfSemester.getDay());
        GregorianCalendar dateG = new GregorianCalendar(date.getYear(), firstDateOfSemester.getMonth(), date.getDay());
        int weekFirst = firstDateG.get(firstDateG.WEEK_OF_MONTH);
        int week = dateG.get(dateG.WEEK_OF_MONTH);
        result = ((week - weekFirst % 2) == 1);
        if (isFirstWeekNumerator) {
            return result;
        } else {
            return !result;
        }
    }

    /**
     * Получить название дня неделя по его номеру
     *
     * @param context контекст
     * @param numDay  номер дня в недели (пн-1 .. вс-7)
     * @return название дня недели
     */
    public static String getNameDayFromItsNum(Context context, int numDay) {
        switch (numDay) {
            case (1):
                return context.getString(R.string.monday);
            case (2):
                return context.getString(R.string.tuesday);
            case (3):
                return context.getString(R.string.wednesday);
            case (4):
                return context.getString(R.string.thursday);
            case (5):
                return context.getString(R.string.friday);
            case (6):
                return context.getString(R.string.saturday);
            case (7):
                return context.getString(R.string.sunday);
            default:
                return "";
        }
    }

    /**
     * Получение названия числителя/знаменателя из свойств объекта
     *
     * @param context контекст
     * @return название недели (числитель/знаменатель)
     */
    public String getNameNumerator(Context context) {
        if (this.isNumerator()) return context.getString(R.string.numerator);
        else return context.getString(R.string.denominator);
    }

    /**
     * Возвращение дня, сделано из-за несостыковки индексов
     * Напр. пн - 1 день недели, но индекс - 0
     *
     * @param numDay номер дня недели пн - 1, .. , вс - 7
     * @return искомый день недели
     */
    public Day getDay(int numDay) {
        return getDays().get(numDay - 1);
    }


}
