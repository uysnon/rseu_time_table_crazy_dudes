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

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences preferences;
    EditText groupNumber;
    String editTextValue;
    boolean hasGroup;
    public static final String myPreference = "myPref";
    public static final String preferenceIsGroup = "isPref";
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
                       // Toast.makeText(getApplicationContext(),sharedPreferences.getString(groupKey,null),Toast.LENGTH_SHORT).show();
                        //вытащить нужную группу

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
                        // добавить проверку, имеется ли на сервере такая группа
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(groupKey,editTextValue);
                        editor.apply();
                    }else{
                        Toast.makeText(getApplicationContext(),"Введите Ваш действительный номер группы",Toast.LENGTH_SHORT).show();
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
}
