package com.duesclerk.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.duesclerk.R;
import com.duesclerk.ui.fragment_appmenu.FragmentAppMenu;

public class AppMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_menu);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FragmentAppMenu.newInstance())
                    .commitNow();
        }
    }
}