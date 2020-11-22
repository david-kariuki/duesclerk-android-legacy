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

import custom.custom_utilities.AccountUtils;
import custom.custom_utilities.ApplicationClass;
import custom.custom_utilities.DataUtils;
import custom.custom_utilities.ViewsUtils;
import custom.custom_utilities.VolleyUtils;
import custom.custom_views.toast.CustomToast;
import custom.custom_views.view_pager.CustomViewPager;
import custom.custom_views.view_pager.ViewPagerAdapter;
import custom.java_beans.JB_ClientAccountInfo;
import custom.network.InternetConnectivity;
import custom.network.NetworkUtils;
import custom.storage_adapters.SQLiteDB;
import custom.storage_adapters.SessionManager;

public class SignInSignupActivity extends AppCompatActivity implements Interface_SignInSignup {

    @SuppressWarnings("unused")
    private final String TAG = SignInSignupActivity.class.getSimpleName();

    // Java bean object to hold signup details
    JB_ClientAccountInfo jbUserAccountInfo;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private Context mContext;
    private int tabPosition = 0;
    private TextView textTitle;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private SQLiteDB database;
    private ArrayList<JB_ClientAccountInfo> signupDetailsArray;
    private ImageView imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_sign_in);

        mContext = this; // Get Context

        // Progress Dialog
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);

        sessionManager = new SessionManager(mContext); // SessionManager
        database = new SQLiteDB(mContext); // SQLite database

        signupDetailsArray = new ArrayList<>();

        jbUserAccountInfo = new JB_ClientAccountInfo();
        jbUserAccountInfo.clear();

        imageBack = findViewById(R.id.imageSignupBack);
        textTitle = findViewById(R.id.textSignupTitle);

        setupTabLayoutAndViewPager();

        // Select SignIn fragment
        imageBack.setOnClickListener(v -> ViewsUtils.selectTabPosition(0, tabLayout));
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

        setupTabLayoutAndViewPager();
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

    private void setupTabLayoutAndViewPager() {
        setupTabLayout(); // Set up TabLayout
        viewPager.setPagingEnabled(false); // Disabling paging on view pager
        viewPager.setOffscreenPageLimit(1); // Set ViewPager off screen limit
        setupViewPager(viewPager); // Setup ViewPager

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition(); // Get current tab position
                viewPager.setCurrentItem(tabPosition, false); // Set current position

                switch (tabPosition) {
                    case 0:
                        textTitle.setText(DataUtils.getStringResource(mContext,
                                R.string.label_sign_in_to_your_account));
                        imageBack.setVisibility(View.INVISIBLE); // Hide back button
                        break;
                    case 1:
                        textTitle.setText(DataUtils.getStringResource(mContext,
                                R.string.label_create_personal_account));
                        imageBack.setVisibility(View.VISIBLE); // Show back button
                        break;
                    case 2:
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
        // Switch tab position
        ViewsUtils.selectTabPosition(position, tabLayout);
    }

    /**
     * Method to receive personal account signup details from interface
     *
     * @param firstName     - First name
     * @param lastName      - Last name
     * @param phoneNumber   - Phone number
     * @param emailAddress  - Email address
     * @param countryCode   - Country code
     * @param countryAlpha2 - Country alpha2
     * @param password      - Password
     * @param gender        - Gender
     */

    @Override
    public void passPersonalAccountSignupDetails(String firstName, String lastName,
                                                 String phoneNumber, String emailAddress,
                                                 String countryCode, String countryAlpha2,
                                                 String password, String gender) {
        // Clear java bean and ArrayList for re-use
        jbUserAccountInfo.clear();
        signupDetailsArray.clear();

        // Add details
        jbUserAccountInfo.setFirstName(firstName);
        jbUserAccountInfo.setLastName(lastName);
        jbUserAccountInfo.setPhoneNumber(phoneNumber);
        jbUserAccountInfo.setEmailAddress(emailAddress);
        jbUserAccountInfo.setCountryCode(countryCode);
        jbUserAccountInfo.setCountryAlpha2(countryAlpha2);
        jbUserAccountInfo.setPassword(password);
        jbUserAccountInfo.setGender(gender);

        // Add java bean to ArrayList
        signupDetailsArray.add(jbUserAccountInfo);

        // Pass account type and signup details hashMap
        signupUser(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL,
                NetworkUtils.TAG_SIGNUP_PERSONAL_STRING_REQUEST, signupDetailsArray);
    }

    /**
     * Method to receive business account signup details from interface
     *
     * @param businessName  - Business name
     * @param countryCode   - Country code
     * @param countryAlpha2 - Country alpha2
     * @param city          - City
     * @param phoneNumber   - Phone number
     * @param emailAddress  - Email address
     * @param password      - Password
     */
    @Override
    public void passBusinessAccountSignupDetails(String businessName, String countryCode,
                                                 String countryAlpha2, String city,
                                                 String phoneNumber, String emailAddress,
                                                 String password) {
        // Clear java bean and ArrayList for re-use
        jbUserAccountInfo.clear();
        signupDetailsArray.clear();

        // Add details
        jbUserAccountInfo.setBusinessName(businessName);
        jbUserAccountInfo.setCountryCode(countryCode);
        jbUserAccountInfo.setCountryAlpha2(countryAlpha2);
        jbUserAccountInfo.setCity(city);
        jbUserAccountInfo.setPhoneNumber(phoneNumber);
        jbUserAccountInfo.setEmailAddress(emailAddress);
        jbUserAccountInfo.setPassword(password);

        // Add java bean to ArrayList
        signupDetailsArray.add(jbUserAccountInfo);

        // Pass account type and signup details hashMap
        signupUser(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS,
                NetworkUtils.TAG_SIGNUP_BUSINESS_STRING_REQUEST, signupDetailsArray);
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
    private void signupUser(final String signupAccountType, final String signupTag,
                            final ArrayList<JB_ClientAccountInfo> signupDetailsArray) {

        // Hide Keyboard
        ViewsUtils.hideKeyboard(this);

        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            // Show dialog
            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUtils.URL_SIGNUP_CLIENT, response -> {
                // Log Custom_Response
                // Log.d(TAG, "SignUp Response: " + response);

                // Hide Dialog
                ViewsUtils.dismissProgressDialog(progressDialog);

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                    if (!error) {
                        // Account has been created successfully

                        // Get Json Object
                        JSONObject signup = jsonObject.getJSONObject(VolleyUtils.KEY_SIGNUP);
                        String clientId, firstName, lastName, businessName, emailAddress,
                                successMessage = "";

                        // Get signup details
                        clientId = signup.getString(AccountUtils.FIELD_CLIENT_ID);
                        emailAddress = signup.getString(AccountUtils.FIELD_EMAIL_ADDRESS);

                        // Inserting row in users table
                        if (database.storeClientAccountInformation(mContext, clientId, emailAddress,
                                signupDetailsArray.get(0).getPassword())) {

                            // Create login sessionManager
                            sessionManager.setSignedIn(true);

                            if (signupAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {

                                // Get first name and last name
                                firstName = signup.getString(AccountUtils.FIELD_FIRST_NAME);
                                lastName = signup.getString(AccountUtils.FIELD_LAST_NAME);

                                if (!DataUtils.isEmptyString(firstName) && !DataUtils.isEmptyString(lastName)) {
                                    successMessage = DataUtils.getStringResource(
                                            mContext,
                                            R.string.msg_welcome_to,
                                            DataUtils.getStringResource(
                                                    mContext,
                                                    R.string.app_name)
                                                    + ", " + (firstName + " " + lastName));
                                }
                            } else if (signupAccountType.equals(
                                    AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {

                                // Get business name
                                businessName = signup.getString(AccountUtils.FIELD_BUSINESS_NAME);

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
                            if (!DataUtils.isEmptyString(Objects.requireNonNull(successMessage))) {
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

                // Check for the network exceptions below
                // networkErrorMessage, serverErrorMessage, authFailureErrorMessage,
                // parseErrorMessage, noConnectionErrorMessage, timeoutErrorMessage
                if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                        || volleyError instanceof ServerError || volleyError instanceof
                        AuthFailureError || volleyError instanceof TimeoutError) {

                    // Toast Connection Error Message
                    CustomToast.errorMessage(mContext, DataUtils.getStringResource(mContext,
                            R.string.error_network_request_error_message),
                            R.drawable.ic_sad_cloud_100px_white);
                } else {
                    // Toast Connection Error Message
                    CustomToast.errorMessage(mContext, volleyError.getMessage(),
                            R.drawable.ic_sad_cloud_100px_white);
                }

                // Cancel Pending Request
                ApplicationClass.getClassInstance().cancelPendingRequests(signupTag);

                ApplicationClass.getClassInstance().deleteUrlVolleyCache(
                        NetworkUtils.URL_SIGNUP_CLIENT); // Clear url cache
            }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    // Posting params to sign up url
                    Map<String, String> params = new HashMap<>();

                    // Personal account related fields
                    if (signupAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
                        params.put(AccountUtils.FIELD_FIRST_NAME,
                                signupDetailsArray.get(0).getFirstName());
                        params.put(AccountUtils.FIELD_LAST_NAME,
                                signupDetailsArray.get(0).getLastName());
                        params.put(AccountUtils.FIELD_GENDER,
                                signupDetailsArray.get(0).getGender());
                        params.put(AccountUtils.FIELD_ACCOUNT_TYPE,
                                AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL);
                    }

                    // Business account related fields
                    if (signupAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
                        params.put(AccountUtils.FIELD_BUSINESS_NAME,
                                signupDetailsArray.get(0).getBusinessName());
                        params.put(AccountUtils.FIELD_CITY_NAME,
                                signupDetailsArray.get(0).getCity());
                        params.put(AccountUtils.FIELD_ACCOUNT_TYPE,
                                AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS);
                    }

                    // Other shared fields
                    params.put(AccountUtils.FIELD_PHONE_NUMBER,
                            signupDetailsArray.get(0).getPhoneNumber());
                    params.put(AccountUtils.FIELD_EMAIL_ADDRESS,
                            signupDetailsArray.get(0).getEmailAddress());
                    params.put(AccountUtils.FIELD_COUNTRY_CODE,
                            signupDetailsArray.get(0).getCountryCode());
                    params.put(AccountUtils.FIELD_COUNTRY_ALPHA2,
                            signupDetailsArray.get(0).getCountryAlpha2());
                    params.put(AccountUtils.FIELD_PASSWORD,
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
                    NetworkUtils.TAG_SIGNUP_PERSONAL_STRING_REQUEST);

        } else {
            // Not Connected
            CustomToast.errorMessage(mContext, DataUtils.getStringResource(mContext,
                    R.string.error_network_connection_error_message),
                    R.drawable.ic_sad_cloud_100px_white);

            // Stop Progress Dialog
            ViewsUtils.dismissProgressDialog(progressDialog);
        }
    }

    /**
     * Function to show progress dialog
     */
    private void showProgressDialog() {
        // Check if progress dialog is showing
        if (!progressDialog.isShowing()) {

            // Set progress dialog title
            progressDialog.setTitle(DataUtils.getStringResource(mContext,
                    R.string.title_signing_up));

            // Set progress dialog message
            progressDialog.setMessage(DataUtils.getStringResource(mContext,
                    R.string.msg_signing_up));

            progressDialog.show(); // Show progress dialog
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