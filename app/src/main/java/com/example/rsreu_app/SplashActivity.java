package com.example.rsreu_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Thread.sleep;


public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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

        /*
         * Создаем переменную класса Intent, планируется реализация сверки расписания на сервере и в БД,
         * при наличии Интернета на устройстве, если изменилось - то меняем на актуальное, и выводим уведомление пользователю
         */


        /*
         * Именно на сплеш скрине отправляем запрос к http://rsreu.ru/schedule/settings.json
         * и http://rsreu.ru/schedule/times.json, OFC ЕСЛИ ЕСТЬ ИНТЕРНЕТ, ЕСЛИ В ПЕРВЫЙ РАЗ ЗАХОД,
         * ТО МОЖНО ОТПРАВЛЯТЬ ЗАПРОС В MainActivity
         */
        Intent intent = new Intent(this,MainActivity.class);
        Thread timer = new Thread(){
            public void run(){
                try{
                   sleep(2000); // задержку убрать можно
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }

        };

        timer.start();


    }
}
