package com.example.rsreu_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "app.db";
    public static final String TABLE_NAME = "schedule_table";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (id INTEGER PRIMARY KEY AUTOINCREMENT, weekDay INTEGER, timeId INTEGER, duration INTEGER, optional INTEGER, title TEXT, type TEXT, teachers TEXT," +
                "room TEXT, build TEXT, dates TEXT, numerator INTEGER, denominator INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(int weekDay, int timeId, int duration, int optional, String title, String type,String teachers, String room, String build, String dates, int numerator, int denominator){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("weekDay",weekDay);
        contentValues.put("timeId",timeId);
        contentValues.put("duration",duration);
        contentValues.put("optional",optional);
        contentValues.put("title",title);
        contentValues.put("type",type);
        contentValues.put("teachers",teachers);
        contentValues.put("room",room);
        contentValues.put("build",build);
        contentValues.put("dates",dates);
        contentValues.put("numerator",numerator);
        contentValues.put("denominator",denominator);
        long result = db.insert(TABLE_NAME,null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String id,int weekDay, int timeId, int duration, int optional, String title, String type,String teachers, String room, String build, String dates, int numerator, int denominator) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("weekDay",weekDay);
        contentValues.put("timeId",timeId);
        contentValues.put("duration",duration);
        contentValues.put("optional",optional);
        contentValues.put("title",title);
        contentValues.put("type",type);
        contentValues.put("teachers",teachers);
        contentValues.put("room",room);
        contentValues.put("build",build);
        contentValues.put("dates",dates);
        contentValues.put("numerator",numerator);
        contentValues.put("denominator",denominator);
        db.update(TABLE_NAME, contentValues, "id = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id = ?",new String[] {id});
    }
}
