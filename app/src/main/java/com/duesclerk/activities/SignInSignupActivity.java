package com.duesclerk.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.duesclerk.R;
import com.duesclerk.classes.custom_utilities.application.ApplicationClass;
import com.duesclerk.classes.custom_utilities.application.ViewsUtils;
import com.duesclerk.classes.custom_utilities.application.VolleyUtils;
import com.duesclerk.classes.custom_utilities.user_data.DataUtils;
import com.duesclerk.classes.custom_utilities.user_data.UserAccountUtils;
import com.duesclerk.classes.custom_views.toast.CustomToast;
import com.duesclerk.classes.custom_views.view_pager.CustomViewPager;
import com.duesclerk.classes.custom_views.view_pager.ViewPagerAdapter;
import com.duesclerk.classes.java_beans.JB_UserAccountInfo;
import com.duesclerk.classes.network.InternetConnectivity;
import com.duesclerk.classes.network.NetworkTags;
import com.duesclerk.classes.network.NetworkUrls;
import com.duesclerk.classes.storage_adapters.SessionManager;
import com.duesclerk.classes.storage_adapters.UserDatabase;
import com.duesclerk.interfaces.Interface_SignInSignup;
import com.duesclerk.ui.fragment_business_signup.FragmentBusinessSignup;
import com.duesclerk.ui.fragment_personal_signup.FragmentPersonalSignup;
import com.duesclerk.ui.fragment_signin.FragmentSignIn;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignInSignupActivity extends AppCompatActivity implements Interface_SignInSignup {

    @SuppressWarnings("unused")
    private final String TAG = SignInSignupActivity.class.getSimpleName();

    // Java bean object to hold signup details
    JB_UserAccountInfo jbUserAccountInfo;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private Context mContext;
    private int tabPosition = 0;
    private TextView textTitle;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private UserDatabase database;
    private ArrayList<JB_UserAccountInfo> signupDetailsArray;
    private ImageView imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_sign_in);

        mContext = this; // Get Context

        // ProgressDialog
        progressDialog = ViewsUtils.initProgressDialog(SignInSignupActivity.this,
                false);

        sessionManager = new SessionManager(mContext); // SessionManager
        database = new UserDatabase(mContext); // SQLite database

        signupDetailsArray = new ArrayList<>(); // SignUp details array list

        jbUserAccountInfo = new JB_UserAccountInfo();
        jbUserAccountInfo.clear();

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
                R.string.title_fragment_personal_account)));
        tabLayout.addTab(tabLayout.newTab().setText(DataUtils.getStringResource(mContext,
                R.string.title_fragment_business_account)));

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
        FragmentPersonalSignup fragmentPersonalSignup = new FragmentPersonalSignup();
        FragmentBusinessSignup fragmentBusinessSignup = new FragmentBusinessSignup();

        // Add Fragments To ViewPager Adapter
        viewPagerAdapter.addFragment(fragmentSignIn, DataUtils.getStringResource(mContext,
                R.string.title_fragment_sign_in));
        viewPagerAdapter.addFragment(fragmentPersonalSignup, DataUtils.getStringResource(mContext,
                R.string.title_fragment_personal_account));
        viewPagerAdapter.addFragment(fragmentBusinessSignup, DataUtils.getStringResource(mContext,
                R.string.title_fragment_business_account));
        viewPager.setAdapter(viewPagerAdapter);
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
                                R.string.label_create_personal_account));
                        imageBack.setVisibility(View.VISIBLE); // Show back button
                        break;

                    case 2:
                        // Set tab title
                        textTitle.setText(DataUtils.getStringResource(mContext,
                                R.string.label_create_business_account));
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
     * Method to receive personal account signup details from interface
     *
     * @param firstName     - First name
     * @param lastName      - Last name
     * @param emailAddress  - Email address
     * @param countryCode   - Country code
     * @param countryAlpha2 - Country alpha2
     * @param password      - Password
     */

    @Override
    public void passPersonalAccountSignupDetails(String firstName, String lastName,
                                                 String emailAddress, String countryCode,
                                                 String countryAlpha2, String password) {
        // Clear java bean and ArrayList for re-use
        jbUserAccountInfo.clear();
        signupDetailsArray.clear();

        // Add details
        jbUserAccountInfo.setFirstName(firstName);
        jbUserAccountInfo.setLastName(lastName);
        jbUserAccountInfo.setEmailAddress(emailAddress);
        jbUserAccountInfo.setCountryCode(countryCode);
        jbUserAccountInfo.setCountryAlpha2(countryAlpha2);
        jbUserAccountInfo.setPassword(password);

        // Add java bean to ArrayList
        signupDetailsArray.add(jbUserAccountInfo);

        // Pass account type and signup details hashMap
        signupUser(UserAccountUtils.KEY_ACCOUNT_TYPE_PERSONAL,
                signupDetailsArray);
    }

    /**
     * Method to receive business account signup details from interface
     *
     * @param businessName  - Business name
     * @param countryCode   - Country code
     * @param countryAlpha2 - Country alpha2
     * @param emailAddress  - Email address
     * @param password      - Password
     */
    @Override
    public void passBusinessAccountSignupDetails(String businessName, String countryCode,
                                                 String countryAlpha2, String emailAddress,
                                                 String password) {
        // Clear java bean and ArrayList for re-use
        jbUserAccountInfo.clear();
        signupDetailsArray.clear();

        // Add details
        jbUserAccountInfo.setBusinessName(businessName);
        jbUserAccountInfo.setCountryCode(countryCode);
        jbUserAccountInfo.setCountryAlpha2(countryAlpha2);
        jbUserAccountInfo.setEmailAddress(emailAddress);
        jbUserAccountInfo.setPassword(password);

        // Add java bean to ArrayList
        signupDetailsArray.add(jbUserAccountInfo);

        // Pass account type and signup details hashMap
        signupUser(UserAccountUtils.KEY_ACCOUNT_TYPE_BUSINESS,
                signupDetailsArray);
    }

    /**
     * Function to exit current activity
     */
    @Override
    public void finishActivity() {
        SignInSignupActivity.this.finish(); // Exit activity
    }

    /**
     * Function to SignUp user
     *
     * @param signupAccountType  - Personal / Business account
     * @param signupDetailsArray - ArrayList with signup details
     */
    private void signupUser(final String signupAccountType,
                            final ArrayList<JB_UserAccountInfo> signupDetailsArray) {

        // Hide Keyboard
        ViewsUtils.hideKeyboard(this);

        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            // Show dialog
            ViewsUtils.showProgressDialog(progressDialog,
                    DataUtils.getStringResource(mContext,
                            R.string.title_signing_up),
                    DataUtils.getStringResource(mContext,
                            R.string.msg_signing_up)
            );

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUrls.UserURLS.URL_SIGNUP_USER, response -> {
                // Log Custom_Response
                // Log.d(TAG, "SignUp Response: " + response);

                // Hide Dialog
                ViewsUtils.dismissProgressDialog(progressDialog);

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                    if (!error) {
                        // Account has been created successfully

                        // Get Signup Object
                        JSONObject objectSignUp = jsonObject.getJSONObject(VolleyUtils.KEY_SIGNUP);

                        String userId, firstName, lastName, businessName, emailAddress,
                                accountType, successMessage = "";

                        // Get signup details
                        userId = objectSignUp.getString(UserAccountUtils.FIELD_USER_ID);
                        emailAddress = objectSignUp.getString(UserAccountUtils.FIELD_EMAIL_ADDRESS);
                        accountType = objectSignUp.getString(UserAccountUtils.FIELD_ACCOUNT_TYPE);

                        // Inserting row in users table
                        if (database.storeUserAccountInformation(userId, emailAddress,
                                signupDetailsArray.get(0).getPassword(), accountType)) {

                            // Create login sessionManager
                            sessionManager.setSignedIn(true);

                            if (signupAccountType.equals(
                                    UserAccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {

                                // Get first name and last name
                                firstName = objectSignUp.getString(UserAccountUtils.FIELD_FIRST_NAME);
                                lastName = objectSignUp.getString(UserAccountUtils.FIELD_LAST_NAME);

                                if (!DataUtils.isEmptyString(firstName)
                                        && !DataUtils.isEmptyString(lastName)) {

                                    successMessage = DataUtils.getStringResource(
                                            mContext,
                                            R.string.msg_welcome_to,
                                            DataUtils.getStringResource(
                                                    mContext,
                                                    R.string.app_name)
                                                    + ", " + (firstName + " " + lastName));
                                }
                            } else if (signupAccountType.equals(
                                    UserAccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {

                                // Get business name
                                businessName = objectSignUp.getString(UserAccountUtils.FIELD_BUSINESS_NAME);

                                if (!DataUtils.isEmptyString(businessName)) {
                                    successMessage = DataUtils.getStringResource(
                                            mContext,
                                            R.string.msg_welcome_to,
                                            DataUtils.getStringResource(
                                                    mContext,
                                                    R.string.app_name)
                                                    + ", " + businessName);
                                }
                            }

                            // Toast welcome message
                            if (!DataUtils.isEmptyString(
                                    Objects.requireNonNull(successMessage))) {

                                CustomToast.infoMessage(mContext, successMessage, false,
                                        0);
                            }

                            // Launch MainActivity
                            startActivity(new Intent(SignInSignupActivity.this,
                                    MainActivity.class));

                            // Exit current activity
                            finishActivity();
                        }
                    } else {
                        // Error occurred during signup
                        String errorMessage = jsonObject.getString(VolleyUtils.KEY_ERROR_MESSAGE);

                        // Toast Error Message
                        CustomToast.errorMessage(mContext, errorMessage, 0);
                    }
                } catch (JSONException ignored) {
                }
            }, volleyError -> {

                // Log.e(TAG, "SignUp Error: " + volleyError.getMessage());

                // Stop Progress Dialog
                ViewsUtils.dismissProgressDialog(progressDialog);

                // Check request response
                if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                        || volleyError instanceof ServerError || volleyError instanceof
                        AuthFailureError || volleyError instanceof TimeoutError) {

                    CustomToast.errorMessage(mContext, DataUtils.getStringResource(mContext,
                            R.string.error_network_connection_error_message_short),
                            R.drawable.ic_sad_cloud_100px_white);

                } else {

                    // Toast Connection Error Message
                    CustomToast.errorMessage(mContext, volleyError.getMessage(),
                            R.drawable.ic_sad_cloud_100px_white);
                }

                // Cancel Pending Request
                ApplicationClass.getClassInstance().cancelPendingRequests(
                        NetworkTags.UserNetworkTags.TAG_SIGNIN_STRING_REQUEST);

                // Clear url cache
                ApplicationClass.getClassInstance().deleteUrlVolleyCache(
                        NetworkUrls.UserURLS.URL_SIGNIN_USER);
            }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    // Posting params to sign up url
                    Map<String, String> params = new HashMap<>();

                    // Personal account related fields
                    if (signupAccountType.equals(UserAccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
                        params.put(UserAccountUtils.FIELD_FIRST_NAME,
                                signupDetailsArray.get(0).getFirstName());
                        params.put(UserAccountUtils.FIELD_LAST_NAME,
                                signupDetailsArray.get(0).getLastName());
                        params.put(UserAccountUtils.FIELD_ACCOUNT_TYPE,
                                UserAccountUtils.KEY_ACCOUNT_TYPE_PERSONAL);
                    }

                    // Business account related fields
                    if (signupAccountType.equals(UserAccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
                        params.put(UserAccountUtils.FIELD_BUSINESS_NAME,
                                signupDetailsArray.get(0).getBusinessName());
                        params.put(UserAccountUtils.FIELD_ACCOUNT_TYPE,
                                UserAccountUtils.KEY_ACCOUNT_TYPE_BUSINESS);
                    }

                    // Other shared fields
                    params.put(UserAccountUtils.FIELD_EMAIL_ADDRESS,
                            signupDetailsArray.get(0).getEmailAddress());
                    params.put(UserAccountUtils.FIELD_COUNTRY_CODE,
                            signupDetailsArray.get(0).getCountryCode());
                    params.put(UserAccountUtils.FIELD_COUNTRY_ALPHA2,
                            signupDetailsArray.get(0).getCountryAlpha2());
                    params.put(UserAccountUtils.FIELD_PASSWORD,
                            signupDetailsArray.get(0).getPassword());
                    return params;
                }
            };

            // Set Retry Policy
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(getResources().getInteger(R.integer.int_volley_account_request_initial_timeout_ms), DataUtils.getIntegerResource(
                    mContext, R.integer.int_volley_account_request_max_timeout_retry),
                    1.0f));

            // Set Custom_Request Caching To False
            stringRequest.setShouldCache(false);

            // Set Priority to high (High, low, immediate, normal)
            ApplicationClass.getClassInstance().setPriority(Request.Priority.IMMEDIATE);

            // Adding request to request queue
            ApplicationClass.getClassInstance().addToRequestQueue(stringRequest,
                    NetworkTags.UserNetworkTags.TAG_SIGNUP_PERSONAL_STRING_REQUEST);

        } else {
            // Not Connected
            CustomToast.errorMessage(mContext, DataUtils.getStringResource(mContext,
                    R.string.error_network_connection_error_message_long),
                    R.drawable.ic_sad_cloud_100px_white);

            // Stop Progress Dialog
            ViewsUtils.dismissProgressDialog(progressDialog);
        }
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
