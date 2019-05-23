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

        Log.d("myLogs3",String.valueOf(hasGroup));

        oldDataFound = sharedPreferences.getBoolean("oldDataFound",false);
        ableToUpdate = sharedPreferences.getBoolean("ableToUpdate",false);
        updateIsRequired = sharedPreferences.getBoolean("updateIsRequired",false);


        Log.d("myLogs3Bool",String.valueOf(oldDataFound));
        Log.d("myLogs3Bool",String.valueOf(ableToUpdate));
        Log.d("myLogs3Bool",String.valueOf(updateIsRequired));


        if(oldDataFound){
            ableToUpdate = !ableToUpdate;
        }

        if(ableToUpdate){
            oldDataFound = !oldDataFound;
        }

        Context context = getApplicationContext();

        Log.d("myLogs3BoolNew",String.valueOf(oldDataFound));
        Log.d("myLogs3BoolNew",String.valueOf(ableToUpdate));
        Log.d("myLogs3BoolNew",String.valueOf(updateIsRequired));

        if(oldDataFound){
            notifSign.setVisibility(View.VISIBLE);
            bell.bringToFront();
            bell.setClickable(true);
            bell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout ll;
                    ll = findViewById(R.id.ll);
                    ll.setVisibility(View.VISIBLE);
                    LayoutInflater layoutInflater = getLayoutInflater();
                    View mView = layoutInflater.inflate(R.layout.old_data,null,false);
                    ll.addView(mView);
                    ll.bringToFront();

                    /*AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                    mBuilder.setCancelable(true);
                    View mView = getLayoutInflater().inflate(R.layout.old_data,null);
                    CheckBox checkBox = mView.findViewById(R.id.oldCheck);
                    ImageView imageView = mView.findViewById(R.id.krestOld);
                    AlertDialog dialog = mBuilder.create();
                    Log.d("myLogs3","here");
                    dialog.show();

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isChecked;
                            isChecked = checkBox.isChecked();
                            if(isChecked){
                                SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                                editor.putBoolean("oldDataFound", false);
                                editor.apply();
                                notifSign.setVisibility(View.INVISIBLE);
                            }
                            dialog.dismiss();
                        }
                    });

                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            boolean isChecked;
                            isChecked = checkBox.isChecked();
                            if(isChecked){
                                SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                                editor.putBoolean("oldDataFound", false);
                                editor.apply();
                                notifSign.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
*/
                }
            });
        }


        if(ableToUpdate){
            mQueue = Volley.newRequestQueue(getApplicationContext());
            jsonParse(sharedPreferences.getString("groupKey",null));
            notifSign.setVisibility(View.VISIBLE);
            bell.bringToFront();
            bell.setClickable(true);
            bell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //inflater.inflate


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
            showAlertDialog();
            Log.d("myLogs3FirstLaunch",String.valueOf(sharedPreferences.getBoolean("isFirstLaunch",false)));
        }else{
            groupNumber.setText(sharedPreferences.getString(groupKey,null));
            Log.d("myLogs3FirstLaunch","HEERR");
        }

        groupNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 showAlertDialog();
            }
        });

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
        Log.d("myLogs3",String.valueOf(sharedPreferences.getBoolean("isFirstLaunch",false)));

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
                                //sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply();
            /*                    SharedPreferences.Editor editor = getSharedPreferences(myPreference,Context.MODE_PRIVATE).edit();
                                editor.putString(groupKey, editTextValue);
                                editor.apply();*/
                                Log.d("myLogs3","HERE1");

                                bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                            }else{
                                Toast.makeText(getApplicationContext(), "Необходим Интернет для первого запуска приложения", Toast.LENGTH_SHORT).show();
                                Log.d("myLogs3","HERE4");
                                showAlertDialog();
                            }
                        }else{
                            Cursor c;
                            try{
                                c = myDB.getGroupCreateTime(Integer.parseInt(editTextValue));

                                Log.d("myLogs3", String.valueOf(c.getCount()));

                                if(c.getCount() == 0){
                                    Log.d("myLogs3","HEREyes");
                                    mQueue = Volley.newRequestQueue(getApplicationContext());
                                    jsonParse(editTextValue);
                                }else {
                                    if (((sharedPreferences.getLong("endTime",0) - 4665600000L) - System.currentTimeMillis()) > 0){
                                        // оставляем старое расписание
                                        Log.d("myLogs3","HEREOLD");

                                        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                                    } else {
                                        if (isNetworkAvailable()) {
                                            Log.d("myLogs3","HEREREEE");
                                            myDB.deleteGroup(Integer.parseInt(editTextValue));
                                            mQueue = Volley.newRequestQueue(getApplicationContext());
                                            jsonParse(editTextValue);
         /*                                   SharedPreferences.Editor editor = getSharedPreferences(myPreference, Context.MODE_PRIVATE).edit();
                                            editor.putString(groupKey, editTextValue);
                                            editor.apply();*/

                                            bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                                        } else {
                                            Log.d("myLogs3","HEREDialog");
                                            AlertDialog.Builder updateDialog = new AlertDialog.Builder(MainActivity.this);
                                            updateDialog.setTitle("Группа");
                                            updateDialog.setIcon(R.drawable.ic_users);
                                            updateDialog.setMessage("Вы уже вводили данную группу ранее, но инфо о ней устарела. Нужен Интернет. Обновить?");
                                            updateDialog.setCancelable(false);
                                            updateDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (isNetworkAvailable()) {
                                                        myDB.deleteGroup(Integer.parseInt(editTextValue));
                                                        mQueue = Volley.newRequestQueue(getApplicationContext());
                                                        jsonParse(editTextValue);
                                                     /*   SharedPreferences.Editor editor = getSharedPreferences(myPreference, Context.MODE_PRIVATE).edit();
                                                        editor.putString(groupKey, editTextValue);
                                                        editor.apply();*/

                                                        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Все же необходим Интернет для обновления информации. ", Toast.LENGTH_SHORT).show();
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


                                Log.d("myLogs3","here11");
                                if(isNetworkAvailable()) {
                                    Log.d("myLogs3","HERE10");
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
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        ScheduleFragment.newInstance()).commit();
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
                    if (sharedPreferences.getBoolean("isFirstLaunch",false)){
                        editor.putBoolean("isFirstLaunch",false);
                        Log.d("myLogs3","HERE2");
                    }
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
                if(sharedPreferences.contains(groupKey) && !(sharedPreferences.getString(groupKey,null).equals(""))){
                    groupNumber.setText(sharedPreferences.getString(groupKey, ""));

                    if(isNetworkAvailable()){
                        Toast.makeText(getApplicationContext(),"Сервер временно недоступен или группа не найдена",Toast.LENGTH_SHORT).show();
                        showAlertDialog();
                    }
                    Log.d("myLogs3","HERE6");
                } else{
                    Log.d("myLogs3","HERE5");
                    Toast.makeText(getApplicationContext(),"Сервер временно недоступен или группа не найдена",Toast.LENGTH_SHORT).show();
                    groupNumber.setText("");
                    showAlertDialog();
                }
                error.printStackTrace();
               // if(!(sharedPreferences.getBoolean("oldDataFound",false)) || !(sharedPreferences.getBoolean("oldDataFound",false)))
               // showAlertDialog();
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






}
