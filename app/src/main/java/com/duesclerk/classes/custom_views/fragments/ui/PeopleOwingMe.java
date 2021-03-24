package com.duesclerk.classes.custom_views.fragments.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.duesclerk.R;
import com.duesclerk.classes.custom_views.fragments.ui.fragment_contacts.fragment_people_owing_me.FragmentPeopleOwingMe;

public class PeopleOwingMe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_owing_me);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FragmentPeopleOwingMe.newInstance())
                    .commitNow();
        }
    }
}
