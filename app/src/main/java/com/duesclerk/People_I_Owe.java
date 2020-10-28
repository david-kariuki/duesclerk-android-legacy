package com.duesclerk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.duesclerk.ui.people_i_owe.People_I_OweFragment;

public class People_I_Owe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_i_owe);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, People_I_OweFragment.newInstance())
                    .commitNow();
        }
    }
}