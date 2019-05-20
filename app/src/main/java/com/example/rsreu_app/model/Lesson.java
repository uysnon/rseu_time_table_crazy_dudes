package com.example.rsreu_app.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Lesson implements Serializable {

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

    /**
     * Метод для получение время проведения пары в виде строки
     * из поля timeId,
     * в которой хранится время начала и время конца пары
     * @return строка, в которой содержится время начала и конца пары
     */
    public  String getTimeFromTimeId(){
        switch (this.timeId){
            case (1):
                return "8:10 - 9:45";
            case (2):
                return "9:55 - 11:30";
            case (3):
                return "11:40 - 13:15";
            case (4):
                return "13:35 - 15:10";
            case (5):
                return "15:20 - 17:05";
            case(6):
                return "17:05 - 18:40";
            case(7):
                return "18:50 - 20:15";
            case(8):
                return "20:25 - 21:30";
            case(9):
                return "09:00 - 17:00";
            default:
                return "не помню какая пара уже";
        }
    }
}