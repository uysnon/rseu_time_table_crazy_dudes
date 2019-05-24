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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
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
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.GenericArrayType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);
        ImageView appImage;
        TextView appName;



        appImage = findViewById(R.id.imageView);
        appName = findViewById(R.id.appName);

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

        hasEndTime = sharedPreferences.contains("endTime");

        Cursor c;
        SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();

        try{
            c = myDB.getAllDataGroups();
            int count = c.getCount();
            c.close();
            if (count == 0) {
                editor.putBoolean("hasGroup", false);
                editor.putBoolean("isFirstLaunch", true);
                editor.apply();
            }else{
                editor.putBoolean("hasGroup",true);
                editor.putBoolean("isFirstLaunch",false);
                editor.apply();
            }

        }catch (NullPointerException e){
            e.printStackTrace();

        }



        Intent intent = new Intent(this,MainActivity.class);
        Thread timer = new Thread(){
            public void run(){

                boolean hasGroup, isFirstLaunch;

                if(isNetworkAvailable()){
                    myDB.deleteNews();
                    XmlParseNews();
                }

                hasGroup = sharedPreferences.getBoolean("hasGroup",false);
                isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch",false);

                try {
                    Log.d("hasGroup: ",Boolean.toString(hasGroup));
                    Log.d("isFirstLaunch: ",Boolean.toString(isFirstLaunch));

                    if(!hasGroup && isFirstLaunch){
                        if(isNetworkAvailable()){
                            mQueue = Volley.newRequestQueue(getApplicationContext());
                            jsonParseTimes();
                            mQueue = Volley.newRequestQueue(getApplicationContext());
                            jsonParseSemester();
                        }else{
                            Message message = mHandler.obtainMessage(1);
                            message.sendToTarget();
                        }
                    }else if(hasGroup && !isFirstLaunch){
                        Log.d("myLogs2","HERE");
                        Cursor c;
                        try{
                            c = myDB.getAllDataSemester();
                            c.moveToLast();
                            endDate = c.getString(c.getColumnIndex("endDate"));
                            Log.d("myLogs2","HERE2 " + endDate + c.getCount());
                            c.close();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                            try{
                                SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                                Date date = sdf.parse(endDate);
                                long mills = date.getTime();
                                Log.d("myLogs2FirstData",String.valueOf(mills));
                                editor.putLong("endTime", mills);
                                editor.apply();
                            }catch (ParseException e){
                                e.printStackTrace();
                            }

                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }

                        long currentDate = System.currentTimeMillis();
                        long endDateLong = sharedPreferences.getLong("endTime",0);
                        long halfOfSemester = endDateLong - 4665600000L;

                        if(currentDate > endDateLong){
                            Log.d("myLogs2","HERE3") ;

                            if(currentDate > halfOfSemester){
                                editor.putBoolean("halfOfSemester",true);
                                editor.apply();
                                Log.d("myLogs2","HERE4") ;
                            }

                            if(isNetworkAvailable()){
                                mQueue = Volley.newRequestQueue(getApplicationContext());
                                try{
                                    c = myDB.getAllDataTimes();
                                    Log.d("myLogs2","here 4.5 " + c.getCount());
                                    c.close();
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
                                jsonParseTimes();
                                mQueue = Volley.newRequestQueue(getApplicationContext());
                                jsonParseSemester();

                                Log.d("myLogs2","HERE5") ;

                              try{
                                  c = myDB.getAllDataSemester();
                                  c.moveToLast();
                                  endDate = c.getString(c.getColumnIndex("endDate"));
                                  c.close();
                                  SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                                  try {
                                      Date date = sdf.parse(endDate);
                                      long mills = date.getTime();



                                      long oldDate = sharedPreferences.getLong("endDate",0);
                                      Log.d("myLogs2Old",String.valueOf(oldDate));
                                      Log.d("myLogs2Mills",String.valueOf(mills));
                                      if(mills == oldDate){
                                          editor.putBoolean("oldDataFound", true);
                                          Log.d("myLogs2","HERE6") ;
                                          editor.apply();
                                      }else{
                                          Log.d("myLogs2","HERE7") ;
                                          editor.putBoolean("ableToUpdate", true);
                                          editor.putLong("endDate",mills);
                                          editor.apply();
                                      }
                                  }catch (ParseException e){
                                      e.printStackTrace();
                                  }
                              }catch (NullPointerException e){
                                  e.printStackTrace();
                              }
                            }else{
                                editor.putBoolean("isUpdateRequired",true);
                                editor.apply();


                            }

                        }


                    }

                    sleep(1500);

                    if(!isNetworkAvailable() && sharedPreferences.getBoolean("isFirstLaunch",false)){
                        Log.d("myLogs2","her");
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
        String url = "http://rsreu.ru/schedule/times.json";
        myDB = new DatabaseHelper(getApplicationContext());
        try{
        Cursor c = myDB.getAllDataTimes();
            myDB.deleteTimes();
            c.close();
        }catch (NullPointerException e){

        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{

                    myDB = new DatabaseHelper(getApplicationContext());
                    JSONArray jsonArrayTimes = response.getJSONArray("times");

                    int lessonNumber;
                    String fromTime, toTime;
                    boolean isInserted;

                    for(int i = 0; i < jsonArrayTimes.length(); i++) {
                        JSONObject time = jsonArrayTimes.getJSONObject(i);
                        lessonNumber = time.getInt("id");
                        fromTime = time.getString("from");
                        toTime = time.getString("to");

                        isInserted = myDB.insertDataTimes(lessonNumber,fromTime,toTime);
                        Log.d("times", String.valueOf(isInserted) + i);

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
                    boolean isNumerator, isInserted;
                    int isNumeratorInt;
                    myDB = new DatabaseHelper(getApplicationContext());

                    startDate = response.getString("startDate");
                    endDate = response.getString("endDate");
                    isNumerator = response.getBoolean("isNumerator");

                    isNumeratorInt = (isNumerator) ? 1 : 0;

                    isInserted = myDB.insertDataSemester(startDate,endDate,isNumeratorInt);
                    Log.d("semester", String.valueOf(isInserted));

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


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void XmlParseNews() {

        final String URL = "http://rsreu.ru/component/ninjarsssyndicator/?feed_id=1&format=raw";

        new DownloadXmlTask().execute(URL);


    }


    private class DownloadXmlTask extends AsyncTask<String, Integer, Integer> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                Log.d("newsXml","here2");
               return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return 0;
            } catch (XmlPullParserException e) {
                return 0;
            }

        }

        private Integer loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
            InputStream stream = null;
            NewsXMLParser XmlParser = new NewsXMLParser();
            List<NewsXMLParser.Item> items;

            Bitmap img = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.logomin);
            DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();

            boolean isInserted;

            try {
                Log.d("newsXml","here3");
                stream = downloadUrl(urlString);
                items = XmlParser.parse(stream);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            myDB = new DatabaseHelper(getApplicationContext());

            for (NewsXMLParser.Item item : items) {
             item = new NewsXMLParser.Item(item.titleNews,item.link,item.description,item.date,item.author);
                isInserted =  myDB.insertNews(item.link, item.titleNews, item.description, item.author, item.date, dbBitmapUtility.getBytes(img));
                Log.d("newsXml", String.valueOf(isInserted));
            }

            myDB.close();

            return 1;
        }


        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            Log.d("newsXml","here4");
            return conn.getInputStream();
        }
    }

}
