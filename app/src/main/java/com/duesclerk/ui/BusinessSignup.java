package com.duesclerk.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.duesclerk.R;
import com.duesclerk.ui.fragment_business_signup.FragmentBusinessSignup;

public class BusinessSignup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_signup);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FragmentBusinessSignup.newInstance())
                    .commitNow();
        }
    }
}