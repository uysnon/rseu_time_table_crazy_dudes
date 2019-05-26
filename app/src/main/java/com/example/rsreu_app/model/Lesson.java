package com.example.rsreu_app.model;

import android.content.Context;
import android.database.Cursor;

import com.example.rsreu_app.DatabaseHelper;

import java.io.Serializable;
import java.util.ArrayList;

public class Lesson implements Serializable {
    private static final String FULL_NAME_LECTURE = "Лекция";
    private static final String FULL_NAME_PRACTICAL = "Практическое занятие";
    private static final String FULL_NAME_LW = "Лабораторная работа";
    private static final String BRIEF_NAME_LECTURE = "лк";
    private static final String BRIEF_NAME_PRACTICAL = "пз";
    private static final String BRIEF_NAME_LW = "лб";
    private static final String FULL_NAME_MILITARY_TRAINING = "ВОЕННАЯ ПОДГОТОВКА";
    private static final String FULL_NAME_MILITARY_PE = "ФИЗИЧЕСКАЯ КУЛЬТУРА";
    private static final String FULL_NAME_OPTIONAL = "по выбору";
    private static final String FULL_NAME_MAIN_BUILDING = "Главный корпус";
    private static final String BRIEF_NAME_MAIN_BUILDING = "";
    private static final String FULL_NAME_LABORATORY_BUILDING = "Лабораторный корпус";
    private static final String BRIEF_NAME_LABORATORY_BUILDING = "л.к.";
    private static final String FULL_NAME_BUSINESS_INCUBATOR = "Бизнес инкубатор";
    private static final String BRIEF_NAME_BUSINESS_INCUBATOR = "б.и.";


    private int timeId;
    private int duration;
    private String title;
    private String type;

    private boolean optional;
    private String teachers;
    private String room;
    private String build;
    private String dates;

    public Lesson(
            int timeId,
            int duration,
            String title,
            String type,
            boolean optional,
            String teachers,
            String room,
            String build,
            String dates) {

        this.timeId = timeId;
        this.duration = duration;
        this.title = title;
        this.type = type;
        this.optional = optional;
        this.teachers = teachers;
        this.room = room;
        this.build = build;
        this.dates = dates;
    }


    public int getTimeId() {
        return timeId;
    }

    public int getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    public String  getTeachers()
    {
        return teachers;
    }

    public String getRoom() {
        return room;
    }

    public String getBuild() {
        return build;
    }

    public String getDates() {
        return dates;
    }


    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public void setTeachers(String teachers) {
        this.teachers = teachers;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getTitleTypeOptional() {
        String result = "";
        result = result + this.title;
        if (!(this.title.equals(FULL_NAME_MILITARY_TRAINING)||(this.title.equals(FULL_NAME_MILITARY_PE)))){
            result = result + " (" + getBriefType() + getOptionalType() + ")";
        }
        return result;
    }

    public String getRoomBuilding() {
        String result = "";
        result = result + this.room;
        if (!result.equals("")) {
            if (!getBriefBuild().equals("")) {
                result = result + " (" + getBriefBuild() + ")";
            }
        } else  result = getBriefBuild();
        return result;
    }


    private String getBriefType() {
        switch (this.type) {
            case (FULL_NAME_LECTURE): return BRIEF_NAME_LECTURE;
            case (FULL_NAME_LW) : return BRIEF_NAME_LW;
            case (FULL_NAME_PRACTICAL) : return BRIEF_NAME_PRACTICAL;
            default: return this.type;
        }
    }


    private String getBriefBuild() {
        switch (this.build) {
            case (FULL_NAME_MAIN_BUILDING): return BRIEF_NAME_MAIN_BUILDING;
            case (FULL_NAME_LABORATORY_BUILDING) : return BRIEF_NAME_LABORATORY_BUILDING;
            case (FULL_NAME_BUSINESS_INCUBATOR) : return BRIEF_NAME_BUSINESS_INCUBATOR;
            default: return this.build;
        }
    }

    private String getOptionalType() {
        if (this.optional) return FULL_NAME_OPTIONAL;
        return "";
    }

    /**
     * Метод для получение время проведения пары в виде строки
     * из поля timeId,
     * в которой хранится время начала и время конца пары
     *
     * @return строка, в которой содержится время начала и конца пары
     */
    public String getTimeFromTimeId(Context context) {


        DatabaseHelper myDB = new DatabaseHelper(context);
        if (this.timeId >= 0) {
            if ((this.duration == 2) || (this.timeId == 9)) {
                Cursor cursor = null;
                cursor = myDB.getLessonTime(this.timeId);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    return getTimeLesson(
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.FROM_TIME_LESSON)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.TO_TIME_LESSON))
                    );
                }
            } else {
                if (this.duration >= 2) {
                    Cursor cursorStart = null;
                    Cursor cursorEnd = null;
                    cursorStart = myDB.getLessonTime(this.timeId);
                    cursorEnd = myDB.getLessonTime(this.timeId + (this.duration / 2 - 1));
                    if ((cursorStart.getCount() > 0) && (cursorEnd.getCount() > 0)) {
                        cursorStart.moveToFirst();
                        cursorEnd.moveToFirst();
                        return getTimeLesson(
                                cursorStart.getString(cursorStart.getColumnIndex(DatabaseHelper.FROM_TIME_LESSON)),
                                cursorEnd.getString(cursorEnd.getColumnIndex(DatabaseHelper.TO_TIME_LESSON))
                        );
                    }
                }
            }
        }
        return "";

    }

    private static String getTimeLesson(String from, String to) {
        return (from + "\n - \n" + to);
    }
}
