package com.duesclerk;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.duesclerk.ui.people_i_owe.People_I_OweFragment;
import com.duesclerk.ui.peopleowingme.PeopleOwingMeFragment;
import com.google.android.material.tabs.TabLayout;

import custom.custom_views.view_pager.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private PeopleOwingMeFragment peopleOwingMe;
    private People_I_OweFragment peopleIOwe;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayoutMainActivity);
        viewPager = findViewById(R.id.viewPagerMainActivity);

        // Adding tabs
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_me_100px_primary_dark)
                .setText("Owing me"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_they_100px_primary_dark)
                .setText("I owe"));

        // Tab gravity and mode
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

    }

    private void setupViewPager(ViewPager viewPager) {


        // Add Fragments To ViewPager Adapter
        viewPagerAdapter.addFragment(peopleOwingMe, "PeopleOwingMe");
        viewPagerAdapter.addFragment(peopleIOwe, "PeopleIOwe");


        viewPager.setAdapter(viewPagerAdapter);
    }

}