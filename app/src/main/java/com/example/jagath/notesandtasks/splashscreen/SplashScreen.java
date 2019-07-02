package com.example.jagath.notesandtasks.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.jagath.notesandtasks.MainActivityLauncher;
import com.example.jagath.notesandtasks.R;

/**
 * Created by jagath on 27/03/2018.
 */

public class SplashScreen extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageView=findViewById(R.id.splash_image);
        Animation animation= AnimationUtils.loadAnimation(this, R.anim.splash);
        imageView.setAnimation(animation);
        final Intent main=new Intent(this,MainActivityLauncher.class);
        Thread timer=new Thread(){
            public void run(){
                try {
                    sleep(500);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(main);
                    finish();
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(new Intent(this,MainActivityLauncher.class));
    }

}
