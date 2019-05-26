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
    ImageView notifSign;
    ImageView bell;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onStart() {
        SharedPreferences sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Date date = new Date();
        editor.putLong(ScheduleFragment.APP_PREFERENCES_DATE, date.getTime());
        editor.apply();
        super.onStart();
    }

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


        Log.d("myLogs3", String.valueOf(hasGroup));

        oldDataFound = sharedPreferences.getBoolean("oldDataFound", false);
        ableToUpdate = sharedPreferences.getBoolean("ableToUpdate", false);
        updateIsRequired = sharedPreferences.getBoolean("updateIsRequired", false);

        if (oldDataFound) {
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


        if (ableToUpdate) {
            mQueue = Volley.newRequestQueue(getApplicationContext());
            jsonParse(sharedPreferences.getString(groupKey, null));
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

        if (updateIsRequired) {
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

        if (!hasGroup) {
            showAlertDialog();
        } else {
            groupNumber.setText(sharedPreferences.getString(groupKey, null));
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
    protected void onResume() {
        super.onResume();
        groupNumber = findViewById(R.id.groupNumber);
        if (!sharedPreferences.getBoolean("isFirstLaunch", false)) {
            sharedPreferences.edit().putString("oldGroup", groupNumber.getText().toString()).apply();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_schedule:
                    selectedFragment = ScheduleFragment.newInstance();
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


    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        View mView = getLayoutInflater().inflate(R.layout.dialog_group, null);
        EditText edittext = mView.findViewById(R.id.usergroup);
        alertDialog.setView(mView);

        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);

        isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", false);
        Log.d("myLogs3", String.valueOf(sharedPreferences.getBoolean("isFirstLaunch", false)));

        if (isFirstLaunch) {
            alertDialog.setCancelable(false);
        } else {
            alertDialog.setCancelable(true);
        }

        alertDialog.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                groupNumber = findViewById(R.id.groupNumber);
                editTextValue = edittext.getText().toString();
                if (!editTextValue.equals("")) {
                    if (isFirstLaunch) {
                        if (isNetworkAvailable()) {
                            mQueue = Volley.newRequestQueue(getApplicationContext());
                            jsonParse(editTextValue);
                            Log.d("myLogs3", "HERE1");
                            getSharedPreferences(myPreference, Context.MODE_PRIVATE).edit().putString("oldGroup", groupNumber.getText().toString()).apply();
                        } else {
                            StyleableToast.makeText(getApplicationContext(), "Необходим интернет для первого запуска приложения", R.style.NotificationToast).show();
                            Log.d("myLogs3", "HERE4");
                            showAlertDialog();
                        }
                    } else {
                        getSharedPreferences(myPreference, Context.MODE_PRIVATE).edit().putString("oldGroup", groupNumber.getText().toString()).apply();
                        Cursor c;
                        try {
                            c = myDB.getGroupCreateTime(Integer.parseInt(editTextValue));
                            Log.d("myLogs3", String.valueOf(c.getCount()));
                            if (c.getCount() == 0) {
                                Log.d("myLogs3", "HEREyes");
                                mQueue = Volley.newRequestQueue(getApplicationContext());
                                jsonParse(editTextValue);
                            } else {
                                if (((sharedPreferences.getLong("endTime", 0) - 4665600000L) - System.currentTimeMillis()) > 0) {
                                    // оставляем старое расписание
                                    Log.d("myLogs3", "HEREOLD");

                                } else {
                                    if (isNetworkAvailable()) {
                                        Log.d("myLogs3", "HEREEE");
                                        myDB.deleteGroup(Integer.parseInt(editTextValue));
                                        mQueue = Volley.newRequestQueue(getApplicationContext());
                                        jsonParse(editTextValue);
                                    } else {
                                        Log.d("myLogs3", "HEREDialog");
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
                                                } else {
                                                    StyleableToast.makeText(getApplicationContext(), "Все же необходим интернет для обновления информации. ", R.style.NotificationToast).show();
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

                        } catch (NullPointerException e) {
                            Log.d("myLogs3", "here11");
                            if (isNetworkAvailable()) {
                                Log.d("myLogs3", "HERE10");
                                mQueue = Volley.newRequestQueue(getApplicationContext());
                                jsonParse(editTextValue);
                            } else {
                                StyleableToast.makeText(getApplicationContext(), "Необходим интернет для продолжения", R.style.NotificationToast).show();
                                groupNumber.setText(sharedPreferences.getString(groupKey, null));
                                showAlertDialog();
                            }
                        } catch (IndexOutOfBoundsException e) {
                            Log.d("myErr", e.getMessage());
                        }

                        Log.d("log", groupNumber.getText().toString());
                        Log.d("log", sharedPreferences.getString("oldGroup", null));
                    }
                } else {
                    if (isFirstLaunch) {
                        StyleableToast.makeText(getApplicationContext(), "Поле не может быть пустым", R.style.NotificationToast).show();
                        showAlertDialog();
                    } else {
                        StyleableToast.makeText(getApplicationContext(), "Введите группу или вернитесь назад", R.style.NotificationToast).show();
                        groupNumber.setText(sharedPreferences.getString(groupKey, null));
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
        if (!isFirstLaunch) {
            alertDialog.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    groupNumber.setText(sharedPreferences.getString(groupKey, null));
                    dialog.dismiss();
                    groupNumber.clearFocus();
                }
            });
        }
        AlertDialog dialog = alertDialog.create();
        if (isFirstLaunch) {
            dialog.setCanceledOnTouchOutside(false);
        } else {
            dialog.setCanceledOnTouchOutside(true);
        }
        dialog.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        EditText groupN = findViewById(R.id.groupNumber);
        getSharedPreferences(myPreference, Context.MODE_PRIVATE).edit().putString("newGroup", groupN.getText().toString()).apply();
    }

    public void jsonParse(String groupNumberUrl) {
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


                    Log.d("myLogs1", String.valueOf(jsonArrayNumerator.length()));
                    Log.d("myLogs1", String.valueOf(jsonArrayDenominator.length()));
                    for (int i = 0; i < jsonArrayNumerator.length(); i++) {
                        JSONObject numerator = jsonArrayNumerator.getJSONObject(i);
                        //потом всё в метод вынесем
                        weekBool = 1;
                        weekDay = numerator.getInt("weekDay");
                        Log.d("myweekDay", Integer.toString(weekDay));
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

                        isInserted = myDB.insertDataSchedule(Integer.valueOf(groupNumberUrl), weekDay, timeId, duration, optional, title, type, teachers, room, build, dates, weekBool);

                        Log.d("myLogs", String.valueOf(isInserted + Integer.toString(i)));
                    }

                    for (int i = 0; i < jsonArrayDenominator.length(); i++) {
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

                        isInserted = myDB.insertDataSchedule(Integer.valueOf(groupNumberUrl), weekDay, timeId, duration, optional, title, type, teachers, room, build, dates, weekBool);

                        Log.d("myLogs", String.valueOf(isInserted + Integer.toString(i)));
                    }

                    myDB.insertDataGroups(Integer.parseInt(groupNumberUrl), String.valueOf(System.currentTimeMillis()));

                    myDB.close();

                    groupNumber.setText(groupNumberUrl);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (sharedPreferences.getBoolean("isFirstLaunch", false)) {
                        editor.putBoolean("isFirstLaunch", false);
                        Log.d("myLogs3", "HERE2");
                    }
                    editor.putString(groupKey, groupNumberUrl);
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mQueue.stop();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (sharedPreferences.contains(groupKey) && !(sharedPreferences.getString(groupKey, null).equals(""))) {
                    groupNumber.setText(sharedPreferences.getString(groupKey, ""));

                    if (isNetworkAvailable()) {
                        StyleableToast.makeText(getApplicationContext(), "Сервер временно недоступен или группа не найдена", R.style.NotificationToast).show();
                        showAlertDialog();
                    }
                    Log.d("myLogs3", "HERE6");
                } else {
                    Log.d("myLogs3", "HERE5");

                    StyleableToast.makeText(getApplicationContext(), "Сервер временно недоступен или группа не найдена", R.style.NotificationToast).show();
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
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void showOldDataFoundDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);

        mBuilder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.old_data, null);
        CheckBox checkBox = mView.findViewById(R.id.oldCheck);

        mBuilder.setView(mView);

        mBuilder.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean isChecked;
                isChecked = checkBox.isChecked();
                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences(myPreference, Context.MODE_PRIVATE).edit();
                    editor.putBoolean("oldDataFound", false);
                    editor.apply();
                    notifSign.setVisibility(View.INVISIBLE);
                    bell.setEnabled(false);
                    bell.setOnClickListener(null);
                }
                dialog.cancel();
            }
        });


        AlertDialog dialog = mBuilder.create();
        Log.d("myLogs3", "here");
        dialog.show();

    }


    public void showAbleToUpdateDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);

        mBuilder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.ableupdate, null);

        mBuilder.setView(mView);

        mBuilder.setPositiveButton("Хорошо", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor editor = getSharedPreferences(myPreference, Context.MODE_PRIVATE).edit();
                editor.putBoolean("ableToUpdate", false);
                editor.apply();
                notifSign.setVisibility(View.INVISIBLE);
                bell.setOnClickListener(null);
                bell.setEnabled(false);
                dialog.cancel();
            }
        });


        AlertDialog dialog = mBuilder.create();
        Log.d("myLogs3", "here");
        dialog.show();

    }


    public void showUpdateIsRequiredDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);

        mBuilder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.updaterequired, null);

        mBuilder.setView(mView);

        mBuilder.setPositiveButton("Хорошо", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!isNetworkAvailable()) {
                    StyleableToast.makeText(getApplicationContext(), "Включите интернет", R.style.NotificationToast).show();
                } else {
                    jsonParse(sharedPreferences.getString("groupKey", null));
                    SharedPreferences.Editor editor = getSharedPreferences(myPreference, Context.MODE_PRIVATE).edit();
                    editor.putBoolean("updateIsRequired", false);
                    editor.putBoolean("ableToUpdate", true);
                    editor.apply();
                    notifSign.setVisibility(View.INVISIBLE);
                    bell.setEnabled(false);
                    bell.setOnClickListener(null);
                    dialog.cancel();

                }
            }
        });


        AlertDialog dialog = mBuilder.create();
        Log.d("myLogs3", "here");
        dialog.show();

    }


}
