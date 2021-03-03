package com.duesclerk.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.duesclerk.R;
import com.duesclerk.classes.custom_utilities.application.ViewsUtils;
import com.duesclerk.classes.custom_utilities.user_data.DataUtils;
import com.duesclerk.classes.custom_views.view_pager.ViewPagerAdapter;
import com.duesclerk.classes.java_beans.JB_Contacts;
import com.duesclerk.enums.States;
import com.duesclerk.interfaces.Interface_MainActivity;
import com.duesclerk.ui.fragment_app_menu.FragmentAppMenu;
import com.duesclerk.ui.fragment_contacts.fragment_people_i_owe.FragmentPeople_I_Owe;
import com.duesclerk.ui.fragment_contacts.fragment_people_owing_me.FragmentPeopleOwingMe;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Interface_MainActivity {

    private final String KEY_QUERY_PEOPLE_OWING_ME = "QueryPeopleOwingMe";
    private final String KEY_QUERY_PEOPLE_I_OWE = "QueryPeopleIOwe";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context mContext;
    private ImageView imageTabPeopleOwingMe, imageTabPeopleIOwe, imageTabAppMenu;
    private TextView textTabPeopleOwingMe, textTabPeopleIOwe, textTabAppMenu;
    private int tabPosition = 0;
    private FragmentPeopleOwingMe peopleOwingMe;
    private FragmentPeople_I_Owe peopleIOwe;
    private String queryPeopleOwingMe = "", queryPeopleIOwe = "";
    private boolean searchViewPeopleOwingMeSetToHidden = false;
    private boolean searchViewPeopleIOweSetToHidden = false;
    private boolean appBarLayoutPeopleExpanded = true;
    private CollapsingToolbarLayout collapsingToolBar;
    private AppBarLayout appBarLayout;
    private States appBarState;

    // Shared SearchView for all contacts listing fragments
    private SearchView searchView;

    private boolean isEmptyContactsPeopleOwingMe = true, isEmptyContactsPeopleIOwe = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this; // Get Context


        // Setup SearchView
        searchView = ViewsUtils.initSearchView(this, R.id.searchViewMainActivity);
        collapsingToolBar = findViewById(R.id.collapsingToolBarMainActivity);
        appBarLayout = findViewById(R.id.appBarLayoutMainActivity);

        setupTabLayout(); // Set up TabLayout
        viewPager.setOffscreenPageLimit(2); // Set ViewPager off screen limit
        setupViewPager(viewPager); // Setup ViewPager

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                tabPosition = tab.getPosition(); // Get current tab position
                viewPager.setCurrentItem(tabPosition, false); // Set current position
                switchTabSelection(tabPosition, true); // Switch tab selection
                switchSearchViewQuery(tabPosition); // Switch SearchView query
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                tabPosition = tab.getPosition(); // Get current tab position
                switchTabSelection(tabPosition, false); // Switch tab selection
                switchSearchViewQuery(tabPosition); // Switch SearchView query
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                tabPosition = tab.getPosition(); // Get current tab position
                viewPager.setCurrentItem(tabPosition, false); // Set current position
                switchTabSelection(tabPosition, true); // Switch tab selection
                switchSearchViewQuery(tabPosition); // Switch SearchView query
            }
        });

        // Add page change listener
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        /*
        Add query text listener
            - The SearchView is shared by two fragments. OnText changed, the SearchView will update
              a RecyclerViewAdapter based on current visible fragment.
            - The SearchView query will be set to a holding variable on query text changed
              - This value in the holding variable will be set back to the SearchView as a new query
                when the TabLayout position is changed.
              - If first fragment is visible, the SearchView query at first fragment will be saved
                to a variable same to if next fragment is visible.
              - When the TabLayout position changes, the holding variable values will be set for
                the respective fragments.
              - This will mimic two separate SearchViews for respective fragments.
            - The query will also be passed to the fragments RecyclerViewAdapter filter when user
              is searching a visible fragment.
        */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String arg0) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                // Pass SearchView query to fragments
                switch (tabPosition) {

                    case 0:

                        // Set SearchView query to holding variable
                        queryPeopleOwingMe = query;

                        // Set SearchView query to visible fragment
                        peopleOwingMe.setSearchQuery(query);

                        break;

                    case 1:

                        // Set SearchView query to holding variable
                        queryPeopleIOwe = query;

                        // Set SearchView query to visible fragment
                        peopleIOwe.setSearchQuery(query);

                        break;

                    default:
                        break;
                }
                return false;
            }
        });

        // AppBarLayout onOffsetChanged
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset == 0) {
                if (appBarState != States.APPBAR_LAYOUT_EXPANDED) {

                    // Check TabLayout position
                    if (tabPosition == 0) {

                        // Set AppBarLayout at FragmentPeopleOwingMe expanded
                        appBarLayoutPeopleExpanded = true;

                    } else if (tabPosition == 1) {

                        // Set AppBarLayout at FragmentPeopleOwingMe expanded
                        appBarLayoutPeopleExpanded = true;
                    }
                }

                appBarState = States.APPBAR_LAYOUT_EXPANDED; // Set states

            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {

                if (appBarState != States.APPBAR_LAYOUT_COLLAPSED) {

                    // Check TabLayout position
                    if (tabPosition == 0) {

                        // Set AppBarLayout at FragmentPeopleOwingMe collapsed
                        appBarLayoutPeopleExpanded = false;

                    } else if (tabPosition == 1) {

                        // Set AppBarLayout at FragmentPeopleOwingMe collapsed
                        appBarLayoutPeopleExpanded = false;
                    }
                }

                appBarState = States.APPBAR_LAYOUT_COLLAPSED; // Set states

            } else {

                //noinspection StatementWithEmptyBody
                if (appBarState != States.APPBAR_LAYOUT_IDLE) {
                }

                appBarState = States.APPBAR_LAYOUT_IDLE; // Set states
            }
        });

        // Select TabLayout position
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
    }

    /**
     * Function to saved instance states
     *
     * @param outState - Bundle
     */
    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save ViewPager position
        outState.putInt("position", tabLayout.getSelectedTabPosition());

        // Save SearchView query
        switch (tabPosition) {
            case 0:

                // Put string
                outState.putString(KEY_QUERY_PEOPLE_OWING_ME, searchView.getQuery().toString());
                break;

            case 1:

                // Put string
                outState.putString(KEY_QUERY_PEOPLE_I_OWE, searchView.getQuery().toString());
                break;

            default:
                break;
        }
    }

    /**
     * Function to restore saved instance states
     *
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore ViewPager position
        viewPager.setCurrentItem(savedInstanceState.getInt("position"));

        // Save SearchView query
        switch (tabPosition) {

            case 0:

                // Set SearchView query
                searchView.setQuery(savedInstanceState
                        .getString(KEY_QUERY_PEOPLE_OWING_ME), true);
                break;

            case 1:

                // Set SearchView query
                searchView.setQuery(savedInstanceState
                        .getString(KEY_QUERY_PEOPLE_I_OWE), true);
                break;

            default:

                // Set SearchView query
                searchView.setQuery("", true);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        showHideSearchView(); // Show / Hide SearchView
    }

    @Override
    public void onResume() {
        super.onResume();

        showHideSearchView(); // Show / Hide SearchView
    }

    /**
     * Function to setup TabLayout icons and title
     */
    private void setupTabLayout() {

        // Initialize TabLayout and ViewPager
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

        peopleOwingMe = new FragmentPeopleOwingMe();
        peopleIOwe = new FragmentPeople_I_Owe();
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
     * @param position - tab position
     * @param selected - boolean
     */
    private void switchTabSelection(int position, boolean selected) {

        showHideSearchView(); // Show / Hide SearchView

        // Enable / Disable CollapsingToolbarLayout scroll depending on TabLayout position
        enableAppBarLayoutScroll(position != 2);

        // Switch position
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
     * Function to check if ArrayList is empty using size
     */
    private boolean isEmptyContacts(ArrayList<JB_Contacts> contacts) {

        return contacts.size() > 0; // Check if array size is greater than 0
    }

    /**
     * Function to get current TabLayout position
     */
    public int getCurrentTabPosition() {

        return tabPosition; // Return TabLayout position
    }

    /**
     * Function to enable / disable AppBarLayout scrolling
     *
     * @param enable - enable / disable AppBarLayout scrolling
     */
    private void enableAppBarLayoutScroll(boolean enable) {

        // Create app bar layout params
        final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams)
                collapsingToolBar.getLayoutParams();

        // Check whether to enable scroll
        if (enable) {
            // Enable scroll

            expandAppBarLayout(true); // Expand AppBarLayout

            // Set scroll flags to SCROLL and ENTER_ALWAYS
            params.setScrollFlags(
                    AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                            | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
            );

        } else {
            // Disable scroll

            params.setScrollFlags(0); // Set scroll flags to zero
        }

        collapsingToolBar.setLayoutParams(params); // Set layout params
    }

    /**
     * Function to expand / collapse AppBarLayout
     *
     * @param expand - Expand / collapse AppBarLayout
     */
    private void expandAppBarLayout(@SuppressWarnings("SameParameterValue") boolean expand) {

        // Switch TabLayout position
        switch (tabPosition) {

            case 0:
            case 1:

                // Expand / collapse AppBarLayout
                appBarLayout.setExpanded(appBarLayoutPeopleExpanded, true);
                break;

            default:

                appBarLayout.setExpanded(expand, true); // Expand / collapse AppBarLayout
                break;
        }
    }

    /**
     * Function to return to first tab on back pressed
     */
    @Override
    public void onBackPressed() {

        if (tabPosition != 0) {

            // Run thread
            this.runOnUiThread(() -> {

                TabLayout.Tab tab = tabLayout.getTabAt(0);
                Objects.requireNonNull(tab).select();
            });

        } else {

            finish(); // Exit Activity
        }
    }

    @Override
    public void showAddContactDialogFragment(boolean show) {
    }

    @Override
    public void passUserContacts_PeopleOwingMe(ArrayList<JB_Contacts> contacts) {

        this.isEmptyContactsPeopleOwingMe = isEmptyContacts(contacts); // Set contacts null

        showHideSearchView();
    }

    @Override
    public void passUserContacts_PeopleIOwe(ArrayList<JB_Contacts> contacts) {

        this.isEmptyContactsPeopleIOwe = isEmptyContacts(contacts); // Set contacts null

        showHideSearchView(); // Show or hide SearchView
    }

    @Override
    public void setPeopleOwingMeContactsEmpty(boolean notFound) {

        isEmptyContactsPeopleOwingMe = notFound; // Set contacts empty

        showHideSearchView(); // Show or hide SearchView
    }

    @Override
    public void setPeopleIOweContactsEmpty(boolean notFound) {

        isEmptyContactsPeopleIOwe = notFound; // Set contacts empty

        showHideSearchView(); // Show or hide SearchView
    }

    @Override
    public void setToHiddenAndHideSearchView(boolean setToHiddenAndHide, Fragment fragment) {

        if (fragment != null) {

            if (fragment instanceof FragmentPeopleOwingMe) {
                // FragmentPeopleOwingMe

                // Set SearchView set to hidden value
                searchViewPeopleOwingMeSetToHidden = setToHiddenAndHide;

            } else if (fragment instanceof FragmentPeople_I_Owe) {
                // FragmentPeopleIOwe

                // Set SearchView set to hidden value
                searchViewPeopleIOweSetToHidden = setToHiddenAndHide;
            }

            showHideSearchView(); // Show or hide SearchView
        }
    }

    /**
     * Function to show or hide SearchView
     */
    public void showHideSearchView() {

        switch (tabPosition) {

            case 0:
                if (!isEmptyContactsPeopleOwingMe) {

                    // Check if SearchView is set to not hidden
                    if (!searchViewPeopleOwingMeSetToHidden) {

                        searchView.setVisibility(View.VISIBLE); // Show SearchView
                    }
                } else {

                    searchView.setVisibility(View.GONE); // Hide SearchView

                }

                // Check if SearchView is set to not hidden
                if (searchViewPeopleOwingMeSetToHidden) {

                    searchView.setVisibility(View.GONE); // Hide SearchView
                }
                break;

            case 1:

                if (!isEmptyContactsPeopleIOwe) {

                    // Check if SearchView is set to not hidden
                    if (!searchViewPeopleIOweSetToHidden) {

                        searchView.setVisibility(View.VISIBLE); // Show SearchView
                    }
                } else {

                    searchView.setVisibility(View.GONE); // Hide SearchView
                }

                // Check if SearchView is set to not hidden
                if (searchViewPeopleIOweSetToHidden) {

                    searchView.setVisibility(View.GONE); // Hide SearchView
                }
                break;

            default:

                searchView.setVisibility(View.GONE); // Hide SearchView
                break;
        }
    }

    /**
     * Function to change SearchView query on TabLayout position changed
     *
     * @param tabPosition - TabLayouts' current position
     */
    private void switchSearchViewQuery(int tabPosition) {

        switch (tabPosition) {

            case 0:

                // Set SearchView query to holding variables' text
                searchView.setQuery(queryPeopleOwingMe, true);
                break;

            case 1:

                // Set SearchView query to holding variables' text
                searchView.setQuery(queryPeopleIOwe, true);
                break;

            default:
                // Set SearchView query to null
                searchView.setQuery("", false);
                break;
        }
    }
}
