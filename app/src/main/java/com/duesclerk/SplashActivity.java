package com.duesclerk;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.vstechlab.easyfonts.EasyFonts;

import custom.custom_utilities.DataUtils;

public class SplashActivity extends AppCompatActivity {

    private Thread counterThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove toolbar

        // Make activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Context mContext = getApplicationContext();
        TextView textAppName = findViewById(R.id.textSplashActivity_AppName);

        // Animation
        Animation animSlideDown = DataUtils.getAnimation(mContext, R.anim.anim_slide_down);
        animSlideDown.setDuration(1500);

        // Set app name font family and size
        textAppName.setTypeface(EasyFonts.cac_champagne(this));
        textAppName.setTextSize(25);
        textAppName.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
        textAppName.startAnimation(animSlideDown); // set animation

        counterThread = new Thread() {
            @Override
            public void run() {
                try {
                    int wait = 0; // Thread wait time
                    while (wait < 5000) {
                        sleep(100);
                        wait += 100;
                    }
                    // Launch activity as per status
                    startActivity(new Intent(SplashActivity.this,
                            MainActivity.class));
                } catch (Exception ignored) {
                } finally {
                    SplashActivity.this.finish(); // Finish Activity
                }
            }
        };
        counterThread.start(); // Start thread
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        counterThread.interrupt(); // Interrupt thread on activity exit
    }
}