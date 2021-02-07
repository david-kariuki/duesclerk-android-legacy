package com.duesclerk.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import com.duesclerk.R;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_views.dialog_fragments.dialogs.DialogFragment_AddContact;
import com.duesclerk.custom.custom_views.view_pager.ViewPagerAdapter;
import com.duesclerk.custom.java_beans.JB_Contacts;
import com.duesclerk.interfaces.Interface_Contacts;
import com.duesclerk.interfaces.Interface_MainActivity;
import com.duesclerk.ui.fragment_app_menu.FragmentAppMenu;
import com.duesclerk.ui.fragment_contacts.fragment_people_i_owe.FragmentPeople_I_Owe;
import com.duesclerk.ui.fragment_contacts.fragment_people_owing_me.FragmentPeopleOwingMe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Interface_MainActivity,
        Interface_Contacts {

    private final String KEY_QUERY_PEOPLE_OWING_ME = "QueryPeopleOwingMe";
    private final String KEY_QUERY_PEOPLE_I_OWE = "QueryPeopleIOwe";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context mContext;
    private ImageView imageTabPeopleOwingMe, imageTabPeopleIOwe, imageTabAppMenu;
    private TextView textTabPeopleOwingMe, textTabPeopleIOwe, textTabAppMenu;
    private FloatingActionButton fabAddContact;
    private int tabPosition = 0;
    private FragmentPeopleOwingMe peopleOwingMe;
    private FragmentPeople_I_Owe peopleIOwe;
    private String queryPeopleOwingMe = "", queryPeopleIOwe = "";
    private View dividerSearchView;

    // Shared SearchView for all contacts listing fragments
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mContext = this; // Get Context

        fabAddContact = findViewById(R.id.fabMainActivity_AddContact);

        // Setup SearchView
        searchView = ViewsUtils.initSearchView(this, R.id.searchViewMainActivity);
        dividerSearchView = findViewById(R.id.dividerSearchView);

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
                hideFabButton(tabPosition); // Hide/show fab button
                switchSearchViewQuery(tabPosition); // Switch SearchView query
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                tabPosition = tab.getPosition(); // Get current tab position
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

                        // Check if query is empty
                        if (!DataUtils.isEmptyString(query)) {

                            // Set SearchView query to holding variable
                            queryPeopleOwingMe = query;

                            // Set SearchView query to visible fragment
                            peopleOwingMe.setSearchQuery(query);
                        }
                        break;

                    case 1:

                        // Check if query is empty
                        if (!DataUtils.isEmptyString(query)) {

                            // Set SearchView query to holding variable
                            queryPeopleIOwe = query;

                            // Set SearchView query to visible fragment
                            peopleIOwe.setSearchQuery(query);
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        });

        // Add contact onClick
        fabAddContact.setOnClickListener(v -> {

            // Show add person DialogFragment
            ViewsUtils.showDialogFragment(getSupportFragmentManager(),
                    new DialogFragment_AddContact(mContext, tabPosition), true);
        });

        // Select TabLayout position
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
    }

    /**
     * Function to saved instance state
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
                outState.putString(KEY_QUERY_PEOPLE_OWING_ME, searchView.getQuery().toString());
                break;
            case 1:
                outState.putString(KEY_QUERY_PEOPLE_I_OWE, searchView.getQuery().toString());
                break;
            default:
                break;
        }
    }

    /**
     * Function to restore saved instance state
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

                searchView.setQuery(savedInstanceState
                        .getString(KEY_QUERY_PEOPLE_OWING_ME), true);
                break;

            case 1:

                searchView.setQuery(savedInstanceState
                        .getString(KEY_QUERY_PEOPLE_I_OWE), true);
                break;

            default:

                searchView.setQuery("", true);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        showSearchView(tabPosition != 2); // Show / Hide SearchView
    }

    @Override
    public void onResume() {
        super.onResume();

        showSearchView(tabPosition != 2); // Show / Hide SearchView
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
     * @param position- tab position
     * @param selected  - boolean
     */
    private void switchTabSelection(int position, boolean selected) {

        showSearchView(position != 2); // Show / Hide SearchView

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
            case 0: // Those I Owe fragment
            case 1: // Owing Me fragment

                fabAddContact.setVisibility(View.VISIBLE); // Toggle visibility
                break;

            default: //Others

                fabAddContact.setVisibility(View.GONE); // Toggle visibility
                break;
        }
    }

    /**
     * Function to get current TabLayout position
     */
    public int getCurrentTabPosition() {
        return tabPosition; // Return TabLayout position
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

    @Override
    public void showAddContactFAB(boolean show) {

        // Check for current TabLayout position
        if (tabPosition != 2) {
            // Not on menu fragment

            if (show) {

                this.fabAddContact.setVisibility(View.VISIBLE); // Show fab button

            } else {

                this.fabAddContact.setVisibility(View.GONE); // Hide fab button
            }
        }
    }

    @Override
    public void showAddContactDialogFragment(boolean show) {

        fabAddContact.performClick(); // Click add contact FAB
    }

    @Override
    public void showSearchView(boolean show) {

        if (show) {

            searchView.setVisibility(View.VISIBLE); // Show SearchView
            dividerSearchView.setVisibility(View.VISIBLE); // Show SearchView divider

        } else {

            searchView.setVisibility(View.GONE); // Hide SearchView
            dividerSearchView.setVisibility(View.GONE); // Hide SearchView divider
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

    @Override
    public void passUserContacts_PeopleOwingMe(ArrayList<JB_Contacts> contacts) {

    }

    @Override
    public void passUserContacts_PeopleIOwe(ArrayList<JB_Contacts> contacts) {

    }

    @Override
    public void setNoContactsFound(boolean found) {

    }
}
