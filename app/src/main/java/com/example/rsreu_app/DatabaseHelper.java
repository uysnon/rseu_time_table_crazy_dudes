package com.example.rsreu_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "app.db";
    public static final String TABLE_NAME_SCHEDULE = "schedule_table";
    public static final String TABLE_NAME_SEMESTER = "semester_table";
    public static final String TABLE_NAME_TIMES = "times_table";
    public static final String TABLE_NAME_GROUPS = "groups_table";
    public static final String TABLE_NAME_NEWS = "news_table";



    public static final String CREATE_TABLE_SCHEDULE = "create table " + TABLE_NAME_SCHEDULE +" (id INTEGER PRIMARY KEY AUTOINCREMENT,groupNumber INTEGER, weekDay INTEGER, timeId INTEGER, duration INTEGER, optional INTEGER, title TEXT, type TEXT, teachers TEXT," +
            "room TEXT, build TEXT, dates TEXT, weekBool INTEGER)";
    public static final String CREATE_TABLE_SEMESTER = "create table " + TABLE_NAME_SEMESTER +" (id INTEGER PRIMARY KEY AUTOINCREMENT, startDate TEXT, endDate TEXT, isNumerator INTEGER)";
    public static final String CREATE_TABLE_TIMES = "create table " + TABLE_NAME_TIMES+" (id INTEGER PRIMARY KEY AUTOINCREMENT, lessonNumber INTEGER, fromTime TEXT, toTime TEXT)";
    public static final String CREATE_TABLE_GROUPS = "create table " + TABLE_NAME_GROUPS+" (id INTEGER PRIMARY KEY AUTOINCREMENT, groupNumber INTEGER, date TEXT)";
    public static final String CREATE_TABLE_NEWS = "create table " + TABLE_NAME_NEWS+" (id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, title TEXT, summary TEXT, author TEXT, date TEXT, image BLOB)";

    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String SUMMARY = "summary";
    public static final String AUTHOR = "author";
    public static final String DATE = "date";
    public static final String IMAGE = "image";
    public static final String GROUP_NUMBER = "groupNumber";
    public static final String WEEK_DAY = "weekDay";
    public static final String TIME_ID = "timeId";
    public static final String DURATION = "duration";
    public static final String OPTIONAL = "optional";
    public static final String TYPE = "type";
    public static final String TEACHERS = "teachers";
    public static final String ROOM = "room";
    public static final String BUILD = "build";
    public static final String DATES = "dates";
    public static final String WEEK_BOOL = "weekBool";
    public static final String FROM_TIME_LESSON = "fromTime";
    public static final String TO_TIME_LESSON = "toTime";
    public static final String START_DATE_SEMESTER = "startDate";
    public static final String END_DATE_SEMESTER = "endDate";
    public static final String IS_NUMERATOR = "isNumerator";
    public static final String LESSON_NUMBER = "lessonNumber";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SCHEDULE);
        db.execSQL(CREATE_TABLE_SEMESTER);
        db.execSQL(CREATE_TABLE_TIMES);
        db.execSQL(CREATE_TABLE_GROUPS);
        db.execSQL(CREATE_TABLE_NEWS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SEMESTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TIMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NEWS);
        onCreate(db);
    }

    public boolean insertNews(String url, String title, String summary, String author, String date, byte[] image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // https://stackoverflow.com/questions/15323844/android-set-bitmap-to-imageview image
        // сейчас это картинка, но в будущем можно будет с помощью  picasso получать изображение по ссылке, пока что изображение такое!

        contentValues.put(URL,url);
        contentValues.put(TITLE,title);
        contentValues.put(SUMMARY,summary);
        contentValues.put(AUTHOR,author);
        contentValues.put(DATE,date);
        contentValues.put(IMAGE,image);

        long result = db.insert(TABLE_NAME_NEWS,null, contentValues);

        return !(result == -1);

    }

    public boolean insertDataSchedule(int groupNumber, int weekDay, int timeId, int duration, int optional, String title, String type,String teachers, String room, String build, String dates, int weekBool){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GROUP_NUMBER,groupNumber);
        contentValues.put(WEEK_DAY,weekDay);
        contentValues.put(TIME_ID,timeId);
        contentValues.put(DURATION,duration);
        contentValues.put(OPTIONAL,optional);
        contentValues.put(TITLE,title);
        contentValues.put(TYPE,type);
        contentValues.put(TEACHERS,teachers);
        contentValues.put(ROOM,room);
        contentValues.put(BUILD,build);
        contentValues.put(DATES,dates);
        contentValues.put(WEEK_BOOL,weekBool);

        long result = db.insert(TABLE_NAME_SCHEDULE,null, contentValues);

        return !(result == -1);
    }

    public boolean insertDataSemester(String startDate, String endDate, int isNumerator){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(START_DATE_SEMESTER,startDate);
        contentValues.put(END_DATE_SEMESTER,endDate);
        contentValues.put(IS_NUMERATOR,isNumerator);

        long result = db.insert(TABLE_NAME_SEMESTER, null, contentValues);
        return !(result == -1);
    }

    public boolean insertDataTimes(int lessonNumber, String fromTime, String toTime){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LESSON_NUMBER,lessonNumber);
        contentValues.put(FROM_TIME_LESSON,fromTime);
        contentValues.put(TO_TIME_LESSON,toTime);

        long result = db.insert(TABLE_NAME_TIMES, null, contentValues);
        return !(result == -1);
    }


    public boolean insertDataGroups(int groupNumber, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GROUP_NUMBER,groupNumber);
        contentValues.put(DATE,date);

        long result = db.insert(TABLE_NAME_GROUPS, null, contentValues);

        return !(result == -1);
    }


    public Integer getNewsCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME_NEWS,null);
        return res.getCount();
    }

    public Cursor getAllNews(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME_NEWS,null);
        return res;
    }


    public Cursor getInfo(int groupNumber,int weekDay, int timeId, int weekBool){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = new String []{ Integer.toString(groupNumber),Integer.toString(weekDay), Integer.toString(timeId), Integer.toString(weekBool)};

        return db.rawQuery("select * from schedule_table where groupNumber=? and weekDay=? and timeId=? and weekBool=?",selectionArgs);
    }

    public Cursor getAllDataSemester(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME_SEMESTER,null);
        return res;
    }


    public Cursor getLessonTime(int lessonNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = {String.valueOf(lessonNumber)};
        Cursor res = db.rawQuery("select * from times_table where lessonNumber=?", selectionArgs);
        return res;
    }

    public Cursor getAllDataTimes(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME_TIMES,null);
        return res;
    }

    public Cursor getAllDataGroups(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME_GROUPS,null);
        return res;
    }

    public Cursor getGroupCreateTime(int groupNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = new String[]{Integer.toString(groupNumber)};
        Cursor res = db.rawQuery("select date from groups_table where groupNumber=?",selectionArgs);
        return res;
    }


    public void deleteNews(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME_NEWS);
    }

    public void deleteTimes(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME_TIMES);
    }


    public void deleteGroup(int groupNumber){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM schedule_table WHERE groupNumber = ?",new String[] { Integer.toString(groupNumber) });

        if(cursor.moveToFirst()){
            do{
                db.delete(TABLE_NAME_SCHEDULE,"groupNumber = ?",new String[]{Integer.toString(groupNumber)});
            }while (cursor.moveToNext());
        }

        cursor = db.rawQuery("SELECT * FROM groups_table WHERE groupNumber = ?",new String[] { Integer.toString(groupNumber) });
        if(cursor.moveToFirst()){
            do{
                db.delete(TABLE_NAME_GROUPS,"groupNumber = ?",new String[]{Integer.toString(groupNumber)});
            }while (cursor.moveToNext());
        }

    }
}
