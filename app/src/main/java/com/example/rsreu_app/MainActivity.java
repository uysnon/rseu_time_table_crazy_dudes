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
import android.os.PersistableBundle;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

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
    public static final String OLD_DATA_FOUND = "oldDataFound";
    public static final String ABLE_TO_UPDATE = "ableToUpdate";
    public static final String UPDATE_IS_REQUIRED = "updateIsRequired";
    public static final String IS_FIRST_LAUNCH = "isFirstLaunch";
    public static final String OLD_GROUP = "oldGroup";
    public static final String NEW_GROUP = "newGroup";
    public static final String INET_FIRST_LAUNCH = "Необходим интернет для первого запуска приложения";
    public static final String INET_SECOND = "Все же необходим интернет для обновления информации.";
    public static final String INET_CONTINUE = "Необходим интернет для продолжения";
    public static final String EMPTY_FIELD = "Поле не может быть пустым";
    public static final String GROUP_OR_BACK = "Введите группу или вернитесь назад";
    public static final String SERVER_ERROR = "Сервер временно недоступен или группа не найдена";
    public static final String TURN_INET = "Включите интернет";

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

        hasGroup = sharedPreferences.contains(groupKey);


        oldDataFound = sharedPreferences.getBoolean(OLD_DATA_FOUND,false);
        ableToUpdate = sharedPreferences.getBoolean(ABLE_TO_UPDATE,false);
        updateIsRequired = sharedPreferences.getBoolean(UPDATE_IS_REQUIRED,false);

        if(oldDataFound){
            notifSign.setVisibility(View.VISIBLE);
            bell.bringToFront();
            bell.setClickable(true);
            bell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showOldDataFoundDialog();

                }
            });
        }


        if(ableToUpdate){
            mQueue = Volley.newRequestQueue(getApplicationContext());
            jsonParse(sharedPreferences.getString(groupKey,null));
            notifSign.setVisibility(View.VISIBLE);
            bell.bringToFront();
            bell.setClickable(true);
            bell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showAbleToUpdateDialog();

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

                    showUpdateIsRequiredDialog();

                }
            });
        }

        if(!hasGroup){

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

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
    }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Date date = new Date();
        editor.putLong(ScheduleFragment.APP_PREFERENCES_DATE, date.getTime());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        groupNumber = findViewById(R.id.groupNumber);
        if(!sharedPreferences.getBoolean(IS_FIRST_LAUNCH,false)) {
             sharedPreferences.edit().putString(OLD_GROUP, groupNumber.getText().toString()).apply();
         }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EditText groupN = findViewById(R.id.groupNumber);
        getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit().putString(NEW_GROUP, groupN.getText().toString()).apply();
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
        View mView = getLayoutInflater().inflate(R.layout.dialog_group,null);
        EditText edittext = mView.findViewById(R.id.usergroup);
        alertDialog.setView(mView);

        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);

        isFirstLaunch = sharedPreferences.getBoolean(IS_FIRST_LAUNCH,false);

        if (isFirstLaunch) {
            alertDialog.setCancelable(false);
        } else{
            alertDialog.setCancelable(true);
        }

        alertDialog.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                groupNumber = findViewById(R.id.groupNumber);
                    editTextValue = edittext.getText().toString();
                    if(!editTextValue.equals("")){
                        if(isFirstLaunch){
                            if(isNetworkAvailable()) {
                                mQueue = Volley.newRequestQueue(getApplicationContext());
                                jsonParse(editTextValue);
                                getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit().putString(OLD_GROUP, groupNumber.getText().toString()).apply();
                                bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                            }else{
                                StyleableToast.makeText(getApplicationContext(),INET_FIRST_LAUNCH,R.style.NotificationToast).show();
                                showAlertDialog();
                            }
                        }else{
                            getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit().putString(OLD_GROUP, groupNumber.getText().toString()).apply();
                            Cursor c;
                            try{
                                c = myDB.getGroupCreateTime(Integer.parseInt(editTextValue));
                                if(c.getCount() == 0){
                                    mQueue = Volley.newRequestQueue(getApplicationContext());
                                    jsonParse(editTextValue);
                                }else {
                                    if (((sharedPreferences.getLong("endDate",0) - 4665600000L) - System.currentTimeMillis()) > 0){
                                        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                                    } else {
                                        if (isNetworkAvailable()) {
                                            myDB.deleteGroup(Integer.parseInt(editTextValue));
                                            mQueue = Volley.newRequestQueue(getApplicationContext());
                                            jsonParse(editTextValue);
                                            bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                                        } else {
                                            AlertDialog.Builder updateDialog = new AlertDialog.Builder(MainActivity.this);
                                            updateDialog.setTitle("Группа");
                                            updateDialog.setIcon(R.drawable.ic_users);
                                            updateDialog.setMessage("Вы уже вводили данную группу ранее, но инфо о ней устарела. Нужен интернет. Обновить?");
                                            updateDialog.setCancelable(false);
                                            updateDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (isNetworkAvailable()) {
                                                        myDB.deleteGroup(Integer.parseInt(editTextValue));
                                                        mQueue = Volley.newRequestQueue(getApplicationContext());
                                                        jsonParse(editTextValue);
                                                        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                                                    } else {
                                                        StyleableToast.makeText(getApplicationContext(),INET_SECOND,R.style.NotificationToast).show();
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
                                }

                            }catch (NullPointerException e){
                                if(isNetworkAvailable()) {
                                    mQueue = Volley.newRequestQueue(getApplicationContext());
                                    jsonParse(editTextValue);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                            ScheduleFragment.newInstance()).commit();
                                }else{
                                    StyleableToast.makeText(getApplicationContext(),INET_CONTINUE,R.style.NotificationToast).show();
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
                            StyleableToast.makeText(getApplicationContext(),EMPTY_FIELD,R.style.NotificationToast).show();
                            showAlertDialog();
                        } else{
                            StyleableToast.makeText(getApplicationContext(),GROUP_OR_BACK,R.style.NotificationToast).show();
                            groupNumber.setText(sharedPreferences.getString(groupKey,null));
                            showAlertDialog();
                        }
                    }
                    groupNumber.clearFocus();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                        return;
                    }
                    };
                runnable.run();

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
        String url = "http://rsreu.ru/schedule/";
        url = url.concat(groupNumberUrl + ".json");


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArrayNumerator = response.getJSONArray("numerator");
                    JSONArray jsonArrayDenominator = response.getJSONArray("denominator");

                    int weekBool; // 1 - числитель; 0 - знаменатель
                    int weekDay, timeId, duration, optional;
                    String title, type, teachers, room, build, dates;
                    boolean isInserted;

                    myDB = new DatabaseHelper(getApplicationContext());

                    for(int i = 0; i < jsonArrayNumerator.length(); i++){
                        JSONObject numerator = jsonArrayNumerator.getJSONObject(i);
                        weekBool = 1;
                        weekDay = numerator.getInt("weekDay");
                        timeId = numerator.getInt("timeId");
                        duration = numerator.getInt("duration");
                        optional = numerator.getInt("optional");
                        title = numerator.getString("title");
                        type = numerator.getString("type");
                        teachers = numerator.getString("teachers");
                        room = numerator.getString("room");
                        build = numerator.getString("build");
                        dates = numerator.getString("dates");

                        isInserted = myDB.insertDataSchedule(Integer.valueOf(groupNumberUrl),weekDay,timeId,duration,optional,title,type,teachers,room, build,dates, weekBool);

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
                        dates = denominator.getString("dates");

                        isInserted = myDB.insertDataSchedule(Integer.valueOf(groupNumberUrl),weekDay,timeId,duration,optional,title,type,teachers,room,build,dates, weekBool);
                    }

                    myDB.insertDataGroups(Integer.parseInt(groupNumberUrl), String.valueOf(System.currentTimeMillis()));

                    myDB.close();

                    groupNumber.setText(groupNumberUrl);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (sharedPreferences.getBoolean(IS_FIRST_LAUNCH,false)){
                        editor.putBoolean(IS_FIRST_LAUNCH, false);
                    }
                    editor.putString(groupKey, groupNumberUrl);
                    editor.apply();

                }catch (JSONException e){
                    e.printStackTrace();
                }

                mQueue.stop();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(sharedPreferences.contains(groupKey) && !(sharedPreferences.getString(groupKey,null).equals(""))){
                    groupNumber.setText(sharedPreferences.getString(groupKey, ""));

                    if(isNetworkAvailable()){
                        StyleableToast.makeText(getApplicationContext(),SERVER_ERROR,R.style.NotificationToast).show();
                        showAlertDialog();
                    }
                } else{
                    StyleableToast.makeText(getApplicationContext(),SERVER_ERROR,R.style.NotificationToast).show();
                    groupNumber.setText("");
                    showAlertDialog();
                }
                error.printStackTrace();

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

    public void showOldDataFoundDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);

        mBuilder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.old_data,null);
        CheckBox checkBox = mView.findViewById(R.id.oldCheck);

        mBuilder.setView(mView);

        mBuilder.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean isChecked;
                isChecked = checkBox.isChecked();
                if(isChecked){
                    SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                    editor.putBoolean(OLD_DATA_FOUND, false);
                    editor.apply();
                    notifSign.setVisibility(View.INVISIBLE);
                    bell.setEnabled(false);
                    bell.setOnClickListener(null);
                }
                dialog.cancel();
            }
        });


        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }


    public void showAbleToUpdateDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);

        mBuilder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.ableupdate,null);

        mBuilder.setView(mView);

        mBuilder.setPositiveButton("Хорошо", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                editor.putBoolean(ABLE_TO_UPDATE, false);
                editor.apply();
                notifSign.setVisibility(View.INVISIBLE);
                bell.setOnClickListener(null);
                bell.setEnabled(false);
                dialog.cancel();
            }
        });


        AlertDialog dialog = mBuilder.create();

        dialog.show();

    }


    public void showUpdateIsRequiredDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);

        mBuilder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.updaterequired,null);

        mBuilder.setView(mView);

        mBuilder.setPositiveButton("Хорошо", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!isNetworkAvailable()){
                    StyleableToast.makeText(getApplicationContext(),TURN_INET,R.style.NotificationToast).show();
                }else{
                    jsonParse(sharedPreferences.getString(groupKey,null));
                    SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                    editor.putBoolean(UPDATE_IS_REQUIRED, false);
                    editor.putBoolean(ABLE_TO_UPDATE,true);
                    editor.apply();
                    notifSign.setVisibility(View.INVISIBLE);
                    bell.setEnabled(false);
                    bell.setOnClickListener(null);
                    dialog.cancel();

                }
            }
        });

        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }


}
