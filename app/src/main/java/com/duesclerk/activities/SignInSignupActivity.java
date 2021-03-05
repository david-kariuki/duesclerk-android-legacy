package com.duesclerk.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.duesclerk.R;
import com.duesclerk.classes.custom_utilities.application.ViewsUtils;
import com.duesclerk.classes.custom_utilities.user_data.DataUtils;
import com.duesclerk.classes.custom_views.view_pager.CustomViewPager;
import com.duesclerk.classes.custom_views.view_pager.ViewPagerAdapter;
import com.duesclerk.interfaces.Interface_SignInSignup;
import com.duesclerk.ui.fragment_signin.FragmentSignIn;
import com.duesclerk.ui.fragment_signup.FragmentSignup;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class SignInSignupActivity extends AppCompatActivity implements Interface_SignInSignup {

    @SuppressWarnings("unused")
    private final String TAG = SignInSignupActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private Context mContext;
    private int tabPosition = 0;
    private TextView textTitle;
    private ImageView imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_sign_in);

        mContext = this; // Get Context

        imageBack = findViewById(R.id.imageSignupBack);
        textTitle = findViewById(R.id.textSignupTitle);

        setupTabLayoutAndViewPager(); // Set up TabLayout and ViewPager

        imageBack.setOnClickListener(v ->
                ViewsUtils.selectTabPosition(0, tabLayout)); // Select SignIn fragment
    }

    @Override
    public void onStart() {
        super.onStart();

        // Select ignIn fragment on fragment start
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
    }

    @Override
    public void onResume() {
        super.onResume();

        setupTabLayoutAndViewPager(); // Setup TabLayout and ViewPager
    }

    /**
     * Function to setup TabLayout icons and title
     */
    private void setupTabLayout() {

        tabLayout = findViewById(R.id.tabLayoutSignup);
        viewPager = findViewById(R.id.viewPagerSignup);

        // Set TabLayout titles
        tabLayout.addTab(tabLayout.newTab().setText(DataUtils.getStringResource(mContext,
                R.string.title_fragment_sign_in)));

        tabLayout.addTab(tabLayout.newTab().setText(DataUtils.getStringResource(mContext,
                R.string.title_fragment_sign_up)));

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

        FragmentSignIn fragmentSignIn = new FragmentSignIn();
        FragmentSignup fragmentSignup = new FragmentSignup();

        // Add Fragments To ViewPager Adapter
        viewPagerAdapter.addFragment(fragmentSignIn, DataUtils.getStringResource(mContext,
                R.string.title_fragment_sign_in));
        viewPagerAdapter.addFragment(fragmentSignup, DataUtils.getStringResource(mContext,
                R.string.title_fragment_sign_up));

        viewPager.setAdapter(viewPagerAdapter); // Set ViewPagerAdapter to adapter
    }

    /**
     * Function to set up TabLayout and ViewPager
     */
    private void setupTabLayoutAndViewPager() {

        setupTabLayout(); // Set up TabLayout

        viewPager.setPagingEnabled(false); // Disabling paging on view pager
        viewPager.setOffscreenPageLimit(1); // Set ViewPager off screen limit

        setupViewPager(viewPager); // Setup ViewPager

        tabLayout.setupWithViewPager(viewPager); // Setup TabLayout with ViewPager

        // TabLayout onTabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition(); // Get current tab position
                viewPager.setCurrentItem(tabPosition, false); // Set current position

                switch (tabPosition) {
                    case 0:

                        // Set tab title
                        textTitle.setText(DataUtils.getStringResource(mContext,
                                R.string.label_sign_in_to_your_account));
                        imageBack.setVisibility(View.INVISIBLE); // Hide back button
                        break;

                    case 1:

                        // Set tab title
                        textTitle.setText(DataUtils.getStringResource(mContext,
                                R.string.label_create_your_account));
                        imageBack.setVisibility(View.VISIBLE); // Show back button
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition(); // Get current tab position
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Add page change listener
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    /**
     * Method to switch tab position
     *
     * @param position - tab position
     */
    @Override
    public void setTabPosition(int position) {
        ViewsUtils.selectTabPosition(position, tabLayout); // Switch tab position
    }

    /**
     * Function to exit current activity
     */
    @Override
    public void finishActivity() {
        SignInSignupActivity.this.finish(); // Exit activity
    }

    /**
     * Function to return to first tab on back pressed
     */
    @Override
    public void onBackPressed() {

        if (tabPosition != 0) {

            this.runOnUiThread(() -> {

                // Return 1 step back
                TabLayout.Tab tab = tabLayout.getTabAt(tabPosition - 1);
                Objects.requireNonNull(tab).select();
            });

        } else {

            finish(); // Exit Activity
        }
    }
}
