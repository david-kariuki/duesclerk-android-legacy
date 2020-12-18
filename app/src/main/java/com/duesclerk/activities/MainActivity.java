package com.duesclerk.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.duesclerk.R;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_views.view_pager.ViewPagerAdapter;
import com.duesclerk.ui.fragment_app_menu.FragmentAppMenu;
import com.duesclerk.ui.fragment_people_i_owe.FragmentPeople_I_Owe;
import com.duesclerk.ui.fragment_peopleowingme.FragmentPeopleOwingMe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Context mContext;
    private ImageView imageTabPeopleOwingMe, imageTabPeopleIOwe, imageTabAppMenu;
    private TextView textTabPeopleOwingMe, textTabPeopleIOwe, textTabAppMenu;
    private FloatingActionButton floatingActionButton;
    private int tabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this; // Get Context

        floatingActionButton = findViewById(R.id.fabMainActivity);

        setupTabLayout(); // Set up TabLayout
        viewPager.setOffscreenPageLimit(2); // Set ViewPager off screen limit
        setupViewPager(viewPager); // Setup ViewPager

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition(); // Get current tab position
                viewPager.setCurrentItem(tabPosition, true); // Set current position
                switchTabSelection(tabPosition, true); // Switch tab selection
                hideFabButton(tabPosition); // Hide/show fab button
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition(); // Get current tab position
                switchTabSelection(tabPosition, false); // Switch tab selection
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Add page change listener
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        floatingActionButton.setOnClickListener(v -> fabClickedAction()); // Fab on click

        floatingActionButton.setOnLongClickListener(v -> false);

        Objects.requireNonNull(tabLayout.getTabAt(2)).select();
    }

    /**
     * Function to saved instance state
     *
     * @param outState - Bundle
     */
    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", tabLayout.getSelectedTabPosition());
    }

    /**
     * Function to restore saved instance state
     *
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt("position"));
    }

    /**
     * Function to setup TabLayout icons and title
     */
    private void setupTabLayout() {

        tabLayout = findViewById(R.id.tabLayoutMainActivity);
        viewPager = findViewById(R.id.viewPagerMainActivity);

        // Set TabLayout titles
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_fragment_owing_me));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_fragment_i_owe));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.custom_tab_fragment_app_menu));

        // Get Tabs
        TabLayout.Tab tabPeopleOwingMe = tabLayout.getTabAt(0);
        TabLayout.Tab tabPeopleIOwe = tabLayout.getTabAt(1);
        TabLayout.Tab tabAppMenu = tabLayout.getTabAt(2);

        // TabLayout icons
        imageTabPeopleOwingMe = Objects.requireNonNull(Objects.requireNonNull(tabPeopleOwingMe)
                .getCustomView()).findViewById(R.id.imageTabPeopleOwingMe);
        imageTabPeopleIOwe = Objects.requireNonNull(Objects.requireNonNull(tabPeopleIOwe)
                .getCustomView()).findViewById(R.id.imageTabPeopleIOwe);
        imageTabAppMenu = Objects.requireNonNull(Objects.requireNonNull(tabAppMenu)
                .getCustomView()).findViewById(R.id.imageTabAppMenu);

        // TabLayout titles
        textTabPeopleOwingMe = Objects.requireNonNull(Objects.requireNonNull(tabPeopleOwingMe)
                .getCustomView()).findViewById(R.id.textTabPeopleOwingMe);
        textTabPeopleIOwe = Objects.requireNonNull(Objects.requireNonNull(tabPeopleIOwe)
                .getCustomView()).findViewById(R.id.textTabPeopleIOwe);
        textTabAppMenu = Objects.requireNonNull(Objects.requireNonNull(tabAppMenu)
                .getCustomView()).findViewById(R.id.textTabAppMenu);


        // Set TabLayout titles
        textTabPeopleOwingMe.setText(DataUtils.getStringResource(mContext,
                R.string.title_fragment_people_owing_me));
        textTabPeopleIOwe.setText(DataUtils.getStringResource(mContext,
                R.string.title_fragment_people_i_owe));
        textTabAppMenu.setText(DataUtils.getStringResource(mContext,
                R.string.tittle_fragment_app_menu));

        // Set TabLayout titles text colors
        textTabPeopleOwingMe.setTextColor(DataUtils.getColorResource(mContext,
                R.color.colorPrimaryDark));
        textTabPeopleIOwe.setTextColor(DataUtils.getColorResource(mContext,
                R.color.colorPrimaryGrey));
        textTabAppMenu.setTextColor(DataUtils.getColorResource(mContext, R.color.colorPrimaryGrey));

        // Set TabLayout icons
        imageTabPeopleOwingMe.setImageResource(R.drawable.ic_me_100px_primary_dark);
        imageTabPeopleIOwe.setImageResource(R.drawable.ic_they_100px_primary_grey);
        imageTabAppMenu.setImageResource(R.drawable.ic_baseline_menu_24_primary_grey);

        // Set Mode and Gravity
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    /**
     * Function to setup viewpager
     *
     * @param viewPager - Associated ViewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        FragmentPeopleOwingMe peopleOwingMe = new FragmentPeopleOwingMe();
        FragmentPeople_I_Owe peopleIOwe = new FragmentPeople_I_Owe();
        FragmentAppMenu fragmentAppMenu = new FragmentAppMenu();

        // Add Fragments To ViewPager Adapter
        viewPagerAdapter.addFragment(peopleOwingMe, DataUtils.getStringResource(mContext,
                R.string.title_fragment_people_owing_me));
        viewPagerAdapter.addFragment(peopleIOwe, DataUtils.getStringResource(mContext,
                R.string.title_fragment_people_i_owe));
        viewPagerAdapter.addFragment(fragmentAppMenu, DataUtils.getStringResource(mContext,
                R.string.tittle_fragment_app_menu));
        viewPager.setAdapter(viewPagerAdapter);
    }

    /**
     * Function to switch tab positions
     *
     * @param position- tab position
     * @param selected  - boolean
     */
    private void switchTabSelection(int position, boolean selected) {
        switch (position) {
            case 0:
                if (selected) {
                    // Set tab title color
                    textTabPeopleOwingMe.setTextColor(DataUtils.getColorResource(mContext,
                            R.color.colorPrimaryDark));

                    // Set tab icon color
                    ViewsUtils.loadImageView(mContext, R.drawable.ic_me_100px_primary_dark,
                            imageTabPeopleOwingMe);
                } else {
                    // Set tab title color
                    textTabPeopleOwingMe.setTextColor(DataUtils.getColorResource(mContext,
                            R.color.colorPrimaryGrey));

                    // Set tab icon color
                    ViewsUtils.loadImageView(mContext, R.drawable.ic_me_100px_primary_grey,
                            imageTabPeopleOwingMe);
                }
                break;
            case 1:
                if (selected) {
                    // Set tab title color
                    textTabPeopleIOwe.setTextColor(DataUtils.getColorResource(mContext,
                            R.color.colorPrimaryDark));

                    // Set tab icon color
                    ViewsUtils.loadImageView(mContext, R.drawable.ic_they_100px_primary_dark,
                            imageTabPeopleIOwe);
                } else {
                    // Set tab title color
                    textTabPeopleIOwe.setTextColor(DataUtils.getColorResource(mContext,
                            R.color.colorPrimaryGrey));

                    // Set tab icon color
                    ViewsUtils.loadImageView(mContext, R.drawable.ic_they_100px_primary_grey,
                            imageTabPeopleIOwe);
                }
                break;
            case 2:
                if (selected) {
                    // Set tab title color
                    textTabAppMenu.setTextColor(DataUtils.getColorResource(mContext,
                            R.color.colorPrimaryDark));

                    // Set tab icon color
                    ViewsUtils.loadImageView(mContext, R.drawable.ic_baseline_menu_24_primary_dark,
                            imageTabAppMenu);
                } else {

                    // Set tab title color
                    textTabAppMenu.setTextColor(DataUtils.getColorResource(mContext,
                            R.color.colorPrimaryGrey));

                    // Set tab icon color
                    ViewsUtils.loadImageView(mContext, R.drawable.ic_baseline_menu_24_primary_grey,
                            imageTabAppMenu);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Function to hide fab button in fragments where its not required
     *
     * @param position - tab position
     */
    private void hideFabButton(int position) {
        switch (position) {
            case 0:
            case 1:
                // Those I Owe fragment
                // Owing Me fragment
                floatingActionButton.setVisibility(View.VISIBLE); // Toggle visibility
                break;
            case 2:
            default:
                // Menu fragment
                floatingActionButton.setVisibility(View.GONE); // Toggle visibility
                break;
        }
    }

    /**
     * Function to switch fab click action
     */
    private void fabClickedAction() {
        switch (tabPosition) {
            case 0:
                // Owing Me fragment

                break;
            case 1:
                // Those I Owe fragment
                break;
            case 2:
                // Menu fragment
                break;
            default:
                break;
        }
    }

    /**
     * Function to return to first tab on back pressed
     */
    @Override
    public void onBackPressed() {
        if (tabPosition != 0) {
            this.runOnUiThread(() -> {
                TabLayout.Tab tab = tabLayout.getTabAt(0);
                Objects.requireNonNull(tab).select();
            });
        } else {
            finish(); // Exit Activity
        }
    }
}