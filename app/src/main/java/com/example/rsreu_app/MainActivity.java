package com.example.rsreu_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    DatabaseHelper myDB;
    EditText groupNumber;
    String editTextValue;
    boolean hasGroup;
    public RequestQueue mQueue;
    public static final String myPreference = "myPref";
    public static final String groupKey = "groupKey";
    Date mDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groupNumber = findViewById(R.id.groupNumber);

        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);

        hasGroup = sharedPreferences.contains("groupKey");

        if(!hasGroup){
            showAlertDialog();
        }else{
            groupNumber.setText(sharedPreferences.getString(groupKey,null));
        }

        groupNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    showAlertDialog();
                }else{
                    Toast.makeText(getApplicationContext(),"Требуется Интернет-соединение чтобы изменить номер группы",Toast.LENGTH_SHORT).show();
                    groupNumber.setText(sharedPreferences.getString(groupKey,null));

                }
            }
        });



        Toast.makeText(getApplicationContext(),sharedPreferences.getString(groupKey,null),Toast.LENGTH_SHORT).show();


        //Можно вынести в метод работу с BottomNavigationView

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    new NewsFragment()).commit();
//        }

        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_schedule:
                    selectedFragment =  ScheduleFragment.newInstance(mDate);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void showAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Группа");
        alertDialog.setIcon(R.drawable.ic_users);
        final EditText edittext = new EditText(getApplicationContext());
        alertDialog.setView(edittext);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isNetworkAvailable()){
                    editTextValue = edittext.getText().toString();
                    if(!editTextValue.equals("")){
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        jsonParse(editTextValue);
                        dialog.dismiss();
                        dialog.cancel();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                ScheduleFragment.newInstance(mDate)).commit();

                    }else{
                        Toast.makeText(getApplicationContext(),"Поле не может быть пустым",Toast.LENGTH_SHORT).show();
                        showAlertDialog();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Необходим доступ к Интернету для первого запуска приложения",Toast.LENGTH_SHORT).show();
                    showAlertDialog();
                }
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void jsonParse(String groupNumberUrl){

        String url = "http://rsreu.ru/schedule/";
        url = url.concat(groupNumberUrl + ".json");

       // Log.d("myLogs",url);

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

                        isInserted = myDB.insertData(weekDay,timeId,duration,optional,title,type,teachers,room, build,dates, weekBool);

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

                        isInserted = myDB.insertData(weekDay,timeId,duration,optional,title,type,teachers,room,build,dates, weekBool);

                        Log.d("myLogs",String.valueOf(isInserted + Integer.toString(i)));

                    }

                    myDB.close();


                    groupNumber.setText(groupNumberUrl);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(groupKey,editTextValue);
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
                error.printStackTrace();
                showAlertDialog();
            }
        });

        mQueue.add(request);
    }



}
