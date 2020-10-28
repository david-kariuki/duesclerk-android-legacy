package com.duesclerk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.duesclerk.ui.peopleowingme.PeopleOwingMeFragment;

public class PeopleOwingMe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_owing_me);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PeopleOwingMeFragment.newInstance())
                    .commitNow();
        }
    }
}