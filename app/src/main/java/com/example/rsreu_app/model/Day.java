package com.example.rsreu_app.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Day implements Serializable {
    private ArrayList<Lesson> lessons;
    private int weekDay;

    public Day(ArrayList<Lesson> lesson) {
        lessons = lesson;
    }

    public Day() {
        lessons = new ArrayList<>();
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(ArrayList<Lesson> lessons) {
        this.lessons = lessons;
    }
    public void addLesson(Lesson lesson){
        lessons.add(lesson);
    }
}
