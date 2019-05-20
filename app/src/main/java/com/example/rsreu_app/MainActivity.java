package com.example.rsreu_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.icu.util.Freezable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    DatabaseHelper myDB;
    EditText groupNumber;
    String editTextValue;
    boolean hasGroup;
    boolean isFirstLaunch;
    boolean oldDataFound;
    boolean ableToUpdate;
    boolean updateIsRequired;
    public RequestQueue mQueue;
    public static final String myPreference = "myPref";
    public static final String groupKey = "groupKey";
    ImageView notifSign;
    ImageView bell;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groupNumber = findViewById(R.id.groupNumber);
        groupNumber.setFocusable(false);

        bell = findViewById(R.id.imageView2);
        notifSign = findViewById(R.id.upd);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);

        LinearLayout linearLayout2 = new LinearLayout(getApplicationContext());
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout2.setOrientation(LinearLayout.VERTICAL);

        hasGroup = sharedPreferences.contains("groupKey");

        oldDataFound = sharedPreferences.getBoolean("oldDataFound",false);
        ableToUpdate = sharedPreferences.getBoolean("ableToUpdate",false);
        updateIsRequired = sharedPreferences.getBoolean("updateIsRequired",false);



        if(oldDataFound){
            notifSign.setVisibility(View.VISIBLE);
            bell.bringToFront();
            bell.setClickable(true);
            bell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //inflater.inflate
                    notifSign.setVisibility(View.INVISIBLE);
                    //пока на сервере все еще прошлая информация
                    // пусть висит пока не измениться на ableToUpdate
                }
            });
        }


        if(ableToUpdate){
            jsonParse(sharedPreferences.getString("groupKey",null));
            notifSign.setVisibility(View.VISIBLE);
            bell.bringToFront();
            bell.setClickable(true);
            bell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //inflater.inflate
                    notifSign.setVisibility(View.INVISIBLE);
                    SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                    editor.putBoolean("ableToUpdate", false);
                    editor.apply();


                    Button button = findViewById(R.id.changed);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //скрыть при клике на ок
                        }
                    });

                }
            });

        }

        if(updateIsRequired) {
            notifSign.setVisibility(View.VISIBLE);
            bell.bringToFront();
            bell.setClickable(true);
            bell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //inflater.inflate
                    Button button = findViewById(R.id.tryToChange);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v){
                            if(!isNetworkAvailable()){
                                Toast.makeText(getApplicationContext(),"Включите интернет",Toast.LENGTH_SHORT);
                            }else{
                                jsonParse(sharedPreferences.getString("groupKey",null));
                                //убрать отображение
                            }


                        }
                    });

                }
            });
        }


        if(!hasGroup){
            SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
            editor.putBoolean("isFirstLaunch",true);
            editor.apply();
            showAlertDialog();
        }else{
            groupNumber.setText(sharedPreferences.getString(groupKey,null));
        }

        groupNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 showAlertDialog();
            }
        });


        Toast.makeText(getApplicationContext(),sharedPreferences.getString(groupKey,null),Toast.LENGTH_SHORT).show();

        //Можно вынести в метод работу с BottomNavigationView

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_schedule:
                    selectedFragment =  ScheduleFragment.newInstance();
                    break;
                case R.id.nav_news:
                    selectedFragment = new NewsFragment();
                    break;
                case R.id.nav_settings:
                    selectedFragment = new SettingsFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();

            return true;
        }
    };


    private void showAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Группа");
        alertDialog.setIcon(R.drawable.ic_users);
        EditText edittext = new EditText(getApplicationContext());
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialog.setView(edittext);

        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);

        isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch",false);

        if (isFirstLaunch) {
            alertDialog.setCancelable(false);
        } else{
            alertDialog.setCancelable(true);
        }

        alertDialog.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    editTextValue = edittext.getText().toString();
                    if(!editTextValue.equals("")){
                        if(isFirstLaunch){
                            if(isNetworkAvailable()) {
                                mQueue = Volley.newRequestQueue(getApplicationContext());
                                jsonParse(editTextValue);
                                sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply();
            /*                    SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                                editor.putString(groupKey, editTextValue);
                                editor.apply();*/
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        ScheduleFragment.newInstance()).commit();
                                bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                            }else{
                                Toast.makeText(getApplicationContext(), "Необходим Интернет для первого запуска приложения", Toast.LENGTH_SHORT).show();
                                showAlertDialog();
                            }
                        }else{
                            Cursor c;
                            try{
                                c = myDB.getGroupCreateTime(Integer.parseInt(editTextValue));
                                    if(System.currentTimeMillis()  < (c.getLong(c.getColumnIndex("date")) + 4924800000L)){
                                         // оставляем старое расписание
                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                ScheduleFragment.newInstance()).commit();
                                        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                                    }else{
                                        if(isNetworkAvailable()){
                                            myDB.deleteGroup(Integer.parseInt(editTextValue));
                                            mQueue = Volley.newRequestQueue(getApplicationContext());
                                            jsonParse(editTextValue);
         /*                                   SharedPreferences.Editor editor = getSharedPreferences(myPreference, Context.MODE_PRIVATE).edit();
                                            editor.putString(groupKey, editTextValue);
                                            editor.apply();*/
                                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                    ScheduleFragment.newInstance()).commit();
                                            bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                                        }else{
                                            AlertDialog.Builder updateDialog = new AlertDialog.Builder(MainActivity.this);
                                            updateDialog.setTitle("Группа");
                                            updateDialog.setIcon(R.drawable.ic_users);
                                            updateDialog.setMessage("Вы уже вводили данную группу ранее, но инфо о ней слишком стара. Нужен Интернет. Обновить?");
                                            updateDialog.setCancelable(false);
                                            updateDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (isNetworkAvailable()) {
                                                        myDB.deleteGroup(Integer.parseInt(editTextValue));
                                                        mQueue = Volley.newRequestQueue(getApplicationContext());
                                                        jsonParse(editTextValue);
                                                        SharedPreferences.Editor editor = getSharedPreferences(myPreference, Context.MODE_PRIVATE).edit();
                                                        editor.putString(groupKey, editTextValue);
                                                        editor.apply();
                                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                ScheduleFragment.newInstance()).commit();
                                                        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                                                    } else{
                                                        Toast.makeText(getApplicationContext(),"Все же необходим Интернет для обновления информации",Toast.LENGTH_SHORT).show();
                                                        showAlertDialog();
                                                    }
                                                }
                                            });
                                            updateDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });

                                            AlertDialog upDialog = updateDialog.create();
                                            upDialog.setCanceledOnTouchOutside(false);
                                            upDialog.show();
                                        }
                                    }


                            }catch (NullPointerException e){
                                if(isNetworkAvailable()) {
                                    mQueue = Volley.newRequestQueue(getApplicationContext());
                                    jsonParse(editTextValue);
                                   /* SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                                    editor.putString(groupKey, editTextValue);
                                    editor.apply();*/
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                            ScheduleFragment.newInstance()).commit();

                                }else{
                                    Toast.makeText(getApplicationContext(),"Необходим Интернет или вернитесь назад",Toast.LENGTH_SHORT).show();
                                    groupNumber.setText(sharedPreferences.getString(groupKey,null));
                                    showAlertDialog();
                                }
                            }
                            catch (IndexOutOfBoundsException e){
                                Log.d("myErr",e.getMessage());
                            }

                        }
                    }else{
                        if (isFirstLaunch) {
                            Toast.makeText(getApplicationContext(), "Поле не может быть пустым", Toast.LENGTH_SHORT).show();
                            showAlertDialog();
                        } else{
                            Toast.makeText(getApplicationContext(),"Введите группу или вернитесь назад",Toast.LENGTH_SHORT).show();
                            groupNumber.setText(sharedPreferences.getString(groupKey,null));
                            showAlertDialog();
                        }
                    }
                    groupNumber.clearFocus();

            }
        });
        if (!isFirstLaunch){
            alertDialog.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    groupNumber.setText(sharedPreferences.getString(groupKey,null));
                    dialog.dismiss();
                    groupNumber.clearFocus();
                }
            });
        }
        AlertDialog dialog = alertDialog.create();
        if(isFirstLaunch) {
            dialog.setCanceledOnTouchOutside(false);
        }else{
            dialog.setCanceledOnTouchOutside(true);
        }
        dialog.show();
    }

    public void jsonParse(String groupNumberUrl){
        Log.d("UPDATE_SHARED_PR", "go to jsonParse");

        String url = "http://rsreu.ru/schedule/";
        url = url.concat(groupNumberUrl + ".json");


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Log.d("myLogs","works");
                    JSONArray jsonArrayNumerator = response.getJSONArray("numerator");
                    JSONArray jsonArrayDenominator = response.getJSONArray("denominator");

                    int weekBool; // 1 - числитель; 0 - знаменатель
                    int weekDay, timeId, duration, optional;
                    String title, type, teachers, room, build, dates;
                    boolean isInserted;

                    myDB = new DatabaseHelper(getApplicationContext());


                    Log.d("myLogs1",String.valueOf(jsonArrayNumerator.length()));
                    Log.d("myLogs1",String.valueOf(jsonArrayDenominator.length()));
                    for(int i = 0; i < jsonArrayNumerator.length(); i++){
                        JSONObject numerator = jsonArrayNumerator.getJSONObject(i);
                        //потом всё в метод вынесем
                        weekBool = 1;
                        weekDay = numerator.getInt("weekDay");
                        Log.d("myweekDay",Integer.toString(weekDay));
                        timeId = numerator.getInt("timeId");
                        duration = numerator.getInt("duration");
                        optional = numerator.getInt("optional");
                        title = numerator.getString("title");
                        type = numerator.getString("type");
                        teachers = numerator.getString("teachers");
                        room = numerator.getString("room");
                        build = numerator.getString("build");
                        //date pattern dd.MM парсить строку до запятой и пихнуть в массив дату, и так пока видим запятые
                        dates = numerator.getString("dates");

                        isInserted = myDB.insertDataSchedule(Integer.valueOf(groupNumberUrl),weekDay,timeId,duration,optional,title,type,teachers,room, build,dates, weekBool);

                        Log.d("myLogs",String.valueOf(isInserted + Integer.toString(i)));
                    }

                    for(int i = 0; i < jsonArrayDenominator.length(); i++){
                        JSONObject denominator = jsonArrayDenominator.getJSONObject(i);
                        weekBool = 0;
                        weekDay = denominator.getInt("weekDay");
                        timeId = denominator.getInt("timeId");
                        duration = denominator.getInt("duration");
                        optional = denominator.getInt("optional");
                        title = denominator.getString("title");
                        type = denominator.getString("type");
                        teachers = denominator.getString("teachers");
                        room = denominator.getString("room");
                        build = denominator.getString("build");
                        //date pattern dd.MM парсить строку до запятой и пихнуть в массив дату, и так пока видим запятые
                        dates = denominator.getString("dates");

                        isInserted = myDB.insertDataSchedule(Integer.valueOf(groupNumberUrl),weekDay,timeId,duration,optional,title,type,teachers,room,build,dates, weekBool);

                        Log.d("myLogs",String.valueOf(isInserted + Integer.toString(i)));
                    }

                    myDB.insertDataGroups(Integer.parseInt(groupNumberUrl), String.valueOf(System.currentTimeMillis()));

                    myDB.close();

                    groupNumber.setText(groupNumberUrl);
                    Log.d("UPDATE_SHARED_PR", "update group number in sp");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(groupKey,groupNumberUrl);
                    editor.apply();

                }catch (JSONException e){
                    e.printStackTrace();
                }

                mQueue.stop();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Сервер временно недоступен или группа не найдена",Toast.LENGTH_SHORT).show();
                if(sharedPreferences.contains(groupKey) && !(sharedPreferences.getString(groupKey,null) == "")){
                    groupNumber.setText(sharedPreferences.getString(groupKey, ""));
                } else{
                    groupNumber.setText("");
                }
                error.printStackTrace();
                showAlertDialog();
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

    private void enableMobileData(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager cm = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(cm.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(cm);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }

}
