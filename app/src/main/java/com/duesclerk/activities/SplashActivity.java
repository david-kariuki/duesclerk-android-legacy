package com.duesclerk.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.duesclerk.R;
import com.vstechlab.easyfonts.EasyFonts;

import custom.custom_utilities.DataUtils;
import custom.storage_adapters.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private Thread counterThread;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove toolbar

        // Make activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Context mContext = getApplicationContext();

        sessionManager = new SessionManager(mContext); // Initialize Session manager object

        TextView textAppName = findViewById(R.id.textSplashActivity_AppName);

        // Animation
        Animation animSlideDown = DataUtils.getAnimation(mContext, R.anim.anim_slide_down);
        animSlideDown.setDuration(1500);

        // Set app name font family and size
        textAppName.setTypeface(EasyFonts.ostrichBold(this));
        textAppName.setTextSize(35);
        textAppName.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
        textAppName.startAnimation(animSlideDown); // set animation

        counterThread = new Thread() {
            @Override
            public void run() {
                try {
                    int wait = 0; // Thread wait time
                    while (wait < 3500) {
                        sleep(100);
                        wait += 100;
                    }

                    if (sessionManager.isSignedIn()) {
                        // Launch MainActivity
                        startActivity(new Intent(SplashActivity.this,
                                MainActivity.class));
                    } else {
                        // Launch Signin and SignUp activity
                        startActivity(new Intent(SplashActivity.this,
                                SignInSignupActivity.class));
                    }

                    SplashActivity.this.finish(); // Exit activity
                } catch (Exception ignored) {
                } finally {
                   SplashActivity.this.finish(); // Exit Activity
                }
            }
        };

        counterThread.start(); // Start thread
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //counterThread.interrupt(); // Interrupt thread on activity exit
    }
}