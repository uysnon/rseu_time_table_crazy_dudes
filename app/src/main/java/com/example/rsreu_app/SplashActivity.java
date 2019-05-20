package com.example.rsreu_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;


public class SplashActivity extends AppCompatActivity {

    DatabaseHelper myDB;
    public RequestQueue mQueue;
    public static final String myPreference = "myPref";
    SharedPreferences sharedPreferences;
    Boolean hasEndTime;
    String endDate;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        myDB = new DatabaseHelper(getApplicationContext());
        ImageView appImage;
        TextView appName;

        appImage = findViewById(R.id.imageView);
        appName = findViewById(R.id.appName);

        /*
        * Загружаем анимацию из xml-файла и запускаем
        */
        Animation animationTransition = AnimationUtils.loadAnimation(this,R.anim.transition);
        appImage.startAnimation(animationTransition);
        appName.startAnimation(animationTransition);


        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1){
                    Toast.makeText(getApplicationContext(),"Необходим Интернет для первого запуска приложения",Toast.LENGTH_LONG).show();
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        finishAndRemoveTask();
                    } else{
                        finishAffinity();
                    }
                }

            }
        };

        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);
        hasEndTime = sharedPreferences.contains("endTime");


        Intent intent = new Intent(this,MainActivity.class);
        Thread timer = new Thread(){
            public void run(){

                if(isNetworkAvailable()){
                  myDB.deleteNews();
                  mQueue = Volley.newRequestQueue(getApplicationContext());
                  Log.d("Connection","here");
                  jsonParseNews();
                }

                try{
                    if (!hasEndTime && !sharedPreferences.contains("isFirstLaunch")){
                        if(isNetworkAvailable()){
                            mQueue = Volley.newRequestQueue(getApplicationContext());
                            jsonParseTimes();
                            mQueue = Volley.newRequestQueue(getApplicationContext());
                            jsonParseSemester();
                            SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                            editor.putBoolean("isLaunched", true);
                            editor.apply();

                        }else{
                            Message message = mHandler.obtainMessage(1);
                            message.sendToTarget();

                        }
                    }else if(hasEndTime || sharedPreferences.contains("isFirstLaunch") || sharedPreferences.contains("isLaunched")){
                        // сравнить текущую дату и дату окончания семестра
                        Log.d("secretLogs","here");
                        Cursor c = null;
                        myDB = new DatabaseHelper(getApplicationContext());
                        try{
                            c = myDB.getAllDataSemester();
                            c.moveToFirst();
                            endDate = c.getString(c.getColumnIndex("endDate"));
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                            try{
                              Date date = sdf.parse(endDate);
                              long mills = date.getTime();
                                SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                                editor.putLong("endTime", mills);
                                editor.apply();

                            }catch (ParseException e){
                                e.printStackTrace();
                            }
                        }catch (CursorIndexOutOfBoundsException e){
                            e.printStackTrace();
                        }finally {
                            c.close();
                        }

                        long currentDate = System.currentTimeMillis();
                        long endDateLong = sharedPreferences.getLong("endTime",0);
                        Log.d("secretLogs",Long.toString(currentDate));
                        Log.d("secretLogs",Long.toString(endDateLong));

                        if(currentDate > endDateLong){  // and updateIsDone(чтобы обновилось, можно поместить в колокольчик)
                            if (isNetworkAvailable()){
                                // пока на сервере все еще старая информация...попробуйте позднее..можно в колокольчк это перенести
                                //можно сделать преференсез updateIsRequired()
                                myDB.deleteTimes();
                                myDB.deleteSettings();
                                mQueue = Volley.newRequestQueue(getApplicationContext());
                                jsonParseTimes();
                                mQueue = Volley.newRequestQueue(getApplicationContext());
                                jsonParseSemester();
                                try{
                                    c = myDB.getAllDataSemester();
                                    c.moveToFirst();
                                    endDate = c.getString(c.getColumnIndex("endDate"));
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                                    try{
                                        Date date = sdf.parse(endDate);
                                        long mills = date.getTime();

                                        if(mills == endDateLong){ //все еще старая инфа на сервере хранится
                                            SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                                            editor.putBoolean("oldDataFound", true);
                                            editor.apply();
                                        }else{
                                            myDB.deleteGroups();
                                            SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                                            editor.putBoolean("oldDataFound", false);
                                            editor.putLong("endTime", mills);
                                            editor.putBoolean("ableToUpdate", true); //можем обновить, новая инфа на сервере
                                            editor.apply();
                                        }

                                    }catch (ParseException e){
                                        e.printStackTrace();
                                    }
                                }catch (CursorIndexOutOfBoundsException e){
                                    e.printStackTrace();
                                }finally {
                                    c.close();
                                }
                            }else{
                                //поместить в преференсез значение и в мейнактивити вывести диалог, что нужно обновить информацию
                                SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                                editor.putBoolean("isUpdateRequired",true);
                                Log.d("secretLogs","isUpdate");
                                editor.apply();
                                startActivity(intent);
                            }
                        }

                    }

                    sleep(1500);

                    if(!isNetworkAvailable()){
                        finish();
                    } else{
                        startActivity(intent);
                        finish();
                    }


                }catch (InterruptedException e){

                    e.printStackTrace();

                }
            }

        };

        timer.start();




    }


    public void jsonParseTimes(){
        String url = "http://rsreu.ru/schedule/times.json";;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray jsonArrayTimes = response.getJSONArray("times");

                    int lessonNumber;
                    String fromTime, toTime;
                    boolean isInserted;

                    myDB = new DatabaseHelper(getApplicationContext());

                    for(int i = 0; i < jsonArrayTimes.length(); i++) {
                        JSONObject time = jsonArrayTimes.getJSONObject(i);
                        lessonNumber = time.getInt("id");
                        fromTime = time.getString("from");
                        toTime = time.getString("to");

                        isInserted = myDB.insertDataTimes(lessonNumber,fromTime,toTime);

                        Log.d("myLogs",String.valueOf(isInserted + Integer.toString(i)));
                    }

                    myDB.close();


                }catch (JSONException e){
                    e.printStackTrace();
                }

                mQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(request);
    }


    public void jsonParseSemester(){
        String url = "http://rsreu.ru/schedule/settings.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String startDate, endDate;
                    Boolean isNumerator, isInserted;
                    int isNumeratorInt;

                    myDB = new DatabaseHelper(getApplicationContext());

                    startDate = response.getString("startDate");
                    endDate = response.getString("endDate");
                    isNumerator = response.getBoolean("isNumerator");

                    isNumeratorInt = (isNumerator) ? 1 : 0;

                    isInserted = myDB.insertDataSemester(startDate,endDate,isNumeratorInt);

                    Log.d("myLogs",String.valueOf(isInserted));


                }catch (JSONException e){
                    e.printStackTrace();
                }
                mQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        mQueue.add(request);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void jsonParseNews() {
        String url = "https://feed2json.org/convert?url=http%3A%2F%2Frsreu.ru%2Fcomponent%2Fninjarsssyndicator%2F%3Ffeed_id%3D1%26format%3Draw";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    Log.d("myLogs","here11");
                    JSONArray jsonArrayItems = response.getJSONArray("items");

                    String urlNews, title, summary, author, date;
                    Bitmap img = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.logomin); //
                    DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();
                    boolean isInserted = false;
                    myDB = new DatabaseHelper(getApplicationContext());
                    for(int i = 0; i < jsonArrayItems.length(); i++) {
                        JSONObject item = jsonArrayItems.getJSONObject(i);
                        urlNews = item.getString("url");
                        title = item.getString("title");
                        summary = item.getString("summary");
                        author = item.getJSONObject("author").getString("name");
                        date = item.getString("date_published");
                        Log.d("SecretLogs",date);

                        try {
                            isInserted = myDB.insertNews(urlNews, title, summary, author, date, dbBitmapUtility.getBytes(img));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.d("mySecLogs",isInserted + "!" + i);
                    }

                    myDB.close();


                }catch (JSONException e){
                    e.printStackTrace();
                }

                mQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myLogs","error");
            }
        });

        mQueue.add(request);
    }



}
