package com.duesclerk.classes.custom_views.fragments.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.duesclerk.R;
import com.duesclerk.classes.custom_views.fragments.ui.fragment_signup.FragmentSignup;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FragmentSignup.newInstance())
                    .commitNow();
        }
    }
}
