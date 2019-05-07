package com.example.rsreu_app.model;

import java.util.ArrayList;

public class Lesson {

    private int timeId;
    private int duration;
    private String title;
    private String type;
    private boolean optional;
    private ArrayList<String> teachers;
    private String room;
    private String build;
    private String dates;

    public Lesson(
                  int timeId,
                  int duration,
                  String title,
                  String type,
                  boolean optional,
                  ArrayList<String> teachers,
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

    public ArrayList<String> getTeachers() {
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

    public void setTeachers(ArrayList<String> teachers) {
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
}
