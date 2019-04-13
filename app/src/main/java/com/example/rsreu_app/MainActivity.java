package com.example.rsreu_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    EditText groupNumber;
    String editTextValue;
    boolean hasGroup;
    public RequestQueue mQueue;
    public static final String myPreference = "myPref";
    public static final String groupKey = "groupKey";



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
                String previousGroup = groupNumber.getText().toString();

                groupNumber.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(groupKey,s.toString());

                        editor.apply();
                        if(isNetworkAvailable()){
                            //получить расписон для группы, но ПЕРЕД этим проверить наличие такой группы
                        }else{
                            Toast.makeText(getApplicationContext(),"Отсутствует Интернет-соединение",Toast.LENGTH_SHORT).show();
                            groupNumber.setText(previousGroup);

                        }
                    }
                });


        Toast.makeText(getApplicationContext(),sharedPreferences.getString(groupKey,null),Toast.LENGTH_SHORT).show();


        //Можно вынести в метод работу с BottomNavigationView

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ScheduleFragment()).commit();
        }

        bottomNavigationView.setSelectedItemId(R.id.nav_schedule);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_schedule:
                    selectedFragment = new ScheduleFragment();
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
                        groupNumber.setText(editTextValue);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        jsonParse(editTextValue);


                        editor.putString(groupKey,editTextValue);
                        editor.apply();
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

    public void jsonParse(String groupNumber){

        String url = "http://rsreu.ru/schedule/";
        url = url.concat(groupNumber + ".json");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArrayNumerator = response.getJSONArray("numerator");
                    JSONArray jsonArrayDenominator = response.getJSONArray("denominator");

                    boolean numeratorBool, denominatorBool;
                    int weekDay, timeId, duration, optional;
                    String title, type, teachers, room, build, dates;

                    for(int i = 0; i < jsonArrayNumerator.length(); i++){
                        JSONObject numerator = jsonArrayNumerator.getJSONObject(i);
                        //потом всё в метод вынесем
                        denominatorBool = false;
                        numeratorBool = true;
                        weekDay = numerator.getInt("weekDay");
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



                    }

                    for(int i = 0; i < jsonArrayDenominator.length(); i++){
                        JSONObject denominator = jsonArrayNumerator.getJSONObject(i);
                        denominatorBool = true;
                        numeratorBool = false;

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

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Сервер временно недоступен, повторите позднее(или такой группы нет)",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                showAlertDialog();
            }
        });
    }
}
