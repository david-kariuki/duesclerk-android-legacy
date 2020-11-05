package com.duesclerk.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.duesclerk.R;
import com.duesclerk.ui.fragment_personal_signup.FragmentPersonalSignup;

public class PersonalSignup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_signup);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FragmentPersonalSignup.newInstance())
                    .commitNow();
        }
    }
}