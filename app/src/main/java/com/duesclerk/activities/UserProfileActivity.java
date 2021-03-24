package com.duesclerk.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.duesclerk.R;
import com.duesclerk.classes.custom_utilities.application.ApplicationClass;
import com.duesclerk.classes.custom_utilities.application.TaskUtils;
import com.duesclerk.classes.custom_utilities.application.ViewsUtils;
import com.duesclerk.classes.custom_utilities.application.VolleyUtils;
import com.duesclerk.classes.custom_utilities.user_data.DataUtils;
import com.duesclerk.classes.custom_utilities.user_data.InputFiltersUtils;
import com.duesclerk.classes.custom_utilities.user_data.UserAccountUtils;
import com.duesclerk.classes.custom_views.dialog_fragments.bottom_sheet_dialog_fragments.BottomSheetFragment_CountryPicker;
import com.duesclerk.classes.custom_views.dialog_fragments.bottom_sheet_dialog_fragments.BottomSheetFragment_EmailNotVerified;
import com.duesclerk.classes.custom_views.swipe_refresh.MultiSwipeRefreshLayout;
import com.duesclerk.classes.custom_views.toast.CustomToast;
import com.duesclerk.classes.network.InternetConnectivity;
import com.duesclerk.classes.network.NetworkTags;
import com.duesclerk.classes.network.NetworkUrls;
import com.duesclerk.classes.storage_adapters.UserDatabase;
import com.duesclerk.interfaces.Interface_CountryPicker;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity implements Interface_CountryPicker,
        TextWatcher {

    // private final String TAG = UserProfileActivity.class.getSimpleName();

    private Context mContext; // Create Context object
    private MultiSwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private ScrollView scrollView;
    private ProgressDialog progressDialog;
    private CardView cardPersonsNames, cardAccountType,
            cardSignupDate;
    private EditText editFullNameOrBusinessName, editEmailAddress, editCountry;
    private TextView textAccountType, textSignupDate;
    private FloatingActionButton fabEdit, fabSaveEdits, fabCancelEdits;
    private ImageView imageCountryFlag, imageEmailVerificationError;
    private boolean editingProfile = false;
    private UserDatabase database;
    private BottomSheetFragment_CountryPicker bottomSheetFragmentCountryPicker;
    private BottomSheetFragment_EmailNotVerified bottomSheetFragmentEmailNotVerified;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout llUserProfileActivity, llUserProfileActivityFABS, llNoConnection;
    private String fetchedFullNameOrBusinessName = "";
    private String fetchedEmailAddress = "";
    private String fetchedCountryName;
    private String fetchedCountryCode = "";
    private String fetchedCountryFlag = "";
    private String fetchedCountryAlpha2 = "";
    private boolean profileFetched = false, emailVerified = false,
            emailNotVerifiedDialogShown = false, fetchedUserProfile = false;
    private EditText newSelectedCountryCode = null, newSelectedCountryAlpha2 = null;
    private String newFullNameOrBusinessName = "", newEmailAddress = "", newCountryCode = "",
            newCountryAlpha2 = "";
    private int CURRENT_TASK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mContext = getApplicationContext(); // Get application context

        swipeRefreshLayout = findViewById(R.id.swipeRefreshProfileActivity);
        scrollView = findViewById(R.id.scrollViewProfileActivity);

        // Progress Dialog
        progressDialog = new ProgressDialog(UserProfileActivity.this);
        progressDialog.setCancelable(false);

        // CardViews
        cardPersonsNames = findViewById(R.id.cardUserProfileActivity_PersonsNames);
        cardAccountType = findViewById(R.id.cardUserProfileActivity_AccountType);
        cardSignupDate = findViewById(R.id.cardUserProfileActivity_SignupDate);

        // EDitTexts
        editFullNameOrBusinessName = findViewById(R.id.editUserProfileActivity_FullNameOrBusinessName);
        editEmailAddress = findViewById(R.id.editUserProfileActivity_EmailAddress);
        editCountry = findViewById(R.id.editUserProfileActivity_Country);

        // TextViews
        textAccountType = findViewById(R.id.textUserProfileActivity_AccountType);
        textSignupDate = findViewById(R.id.textUserProfileActivity_SignupDate);

        // Initialize EditText objects
        newSelectedCountryCode = new EditText(mContext);
        newSelectedCountryAlpha2 = new EditText(mContext);

        // Set Input Filters
        editFullNameOrBusinessName.setFilters(new InputFilter[]{InputFiltersUtils.filterNames,
                new InputFilter.LengthFilter(InputFiltersUtils.LENGTH_MAX_SINGLE_NAME)});
        editEmailAddress.setFilters(new InputFilter[]{InputFiltersUtils.filterEmailAddress,
                new InputFilter.LengthFilter(InputFiltersUtils.LENGTH_MAX_EMAIL_ADDRESS)});

        // Add text change listeners
        editFullNameOrBusinessName.addTextChangedListener(this);
        editEmailAddress.addTextChangedListener(this);
        editCountry.addTextChangedListener(this);
        newSelectedCountryCode.addTextChangedListener(this);
        newSelectedCountryAlpha2.addTextChangedListener(this);

        shimmerFrameLayout = findViewById(R.id.shimmerUserProfileActivity);
        llUserProfileActivity = findViewById(R.id.llUserProfileActivity_Profile);
        llUserProfileActivityFABS = findViewById(R.id.llUserProfileActivity_FABS);
        llNoConnection = findViewById(R.id.llNoConnectionBar);
        LinearLayout llNoConnection_TryAgain = findViewById(R.id.llNoConnection_TryAgain);

        // FloatingActionButtons
        fabEdit = findViewById(R.id.fabMainActivity_EditProfile);
        fabSaveEdits = findViewById(R.id.fabMainActivity_SaveProfileEdits);
        fabCancelEdits = findViewById(R.id.fabMainActivity_CancelProfileEdits);

        imageCountryFlag = findViewById(R.id.imageUserProfileActivity_CountryFlag);
        imageEmailVerificationError =
                findViewById(R.id.imageUserProfileActivity_EmailVerificationError);
        ImageView imageExit = findViewById(R.id.imageUserProfileActivity_Exit);

        swipeRefreshLayout.setEnabled(true); // Enable SwipeRefresh
        swipeRefreshLayout.setSwipeableChildren(scrollView.getId()); // Set scrollable children

        database = new UserDatabase(mContext); // Initialize database object

        // CountryPicker
        bottomSheetFragmentCountryPicker = new BottomSheetFragment_CountryPicker(this);
        bottomSheetFragmentCountryPicker.setCancelable(true);
        bottomSheetFragmentCountryPicker.setRetainInstance(true);

        // Initialize email not verified fragment and set first name or business name to it
        initEmailVerificationFragment(); // Initialize fragment

        // FAB edit onClick
        fabEdit.setOnClickListener(v -> enableProfileEdit(true));

        // FAB save edits onClick
        fabSaveEdits.setOnClickListener(v -> {
            compareAndGetUpdatedValues(); // Compare new and old values for update

            // Update changed values
            updateUserAccountInfo(database.getUserAccountInfo(null)
                    .get(0).getUserId(), database.getUserAccountInfo(null)
                    .get(0).getAccountType());
        });

        // Fab cancel edits onClick
        fabCancelEdits.setOnClickListener(v -> {
            enableProfileEdit(false); // Disable profile edits
        });

        // SwipeRefresh listener
        swipeRefreshListener = () -> {
            if (!editingProfile) {
                if (!database.isEmpty()) {
                    fetchUserAccountInfo(database.getUserAccountInfo(null)
                                    .get(0).getEmailAddress(),
                            database.getUserAccountInfo(null).
                                    get(0).getPassword());
                }
            }
        };

        // Set refresh listener to MultiSwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);

        // Edit country onClick
        editCountry.setOnClickListener(v -> {

            // Check if editing is enabled
            if (editingProfile) {
                // Editing enabled

                ViewsUtils.showBottomSheetDialogFragment(
                        getSupportFragmentManager(), bottomSheetFragmentCountryPicker, true);
            }
        });

        imageEmailVerificationError.setOnClickListener(v -> {
            if ((!fetchedFullNameOrBusinessName.equals("")) && (!editingProfile)) {

                // Start email not verified bottom sheet
                ViewsUtils.showBottomSheetDialogFragment(getSupportFragmentManager(),
                        bottomSheetFragmentEmailNotVerified, true);
            }
        });

        llNoConnection_TryAgain.setOnClickListener(v -> {
            if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
                if (CURRENT_TASK == TaskUtils.TASK_FETCH_USER_PROFILE) {

                    // Show SwipeRefreshLayout
                    ViewsUtils.showSwipeRefreshLayout(true, true,
                            swipeRefreshLayout, swipeRefreshListener);

                } else if (CURRENT_TASK == TaskUtils.TASK_UPDATE_USER_PROFILE) {

                    if (editingProfile) {
                        if (fieldValuesChanged()) {

                            fabSaveEdits.performClick(); // Click on FAB save edits
                        }
                    }
                }
            } else {

                // Toast network connection message
                CustomToast.errorMessage(mContext, DataUtils.getStringResource(
                        mContext, R.string.error_network_connection_error_message_short),
                        R.drawable.ic_sad_cloud_100px_white);
            }
        });

        // Image exit onClick
        imageExit.setOnClickListener(v -> finish());
    }

    @Override
    public void onStart() {
        super.onStart();

        loadUserProfile();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (editingProfile) {

            // Cancel any pending requests
            ApplicationClass.getClassInstance()
                    .cancelPendingRequests(NetworkTags.UserNetworkTags.
                            TAG_UPDATE_USER_DETAILS_STRING_REQUEST);
        } else {

            // Cancel any pending requests
            ApplicationClass.getClassInstance()
                    .cancelPendingRequests(NetworkTags.UserNetworkTags.
                            TAG_FETCH_USER_PROFILE_STRING_REQUEST);
        }
    }

    /**
     * Function to initialize email not verified fragment
     */
    private void initEmailVerificationFragment() {

        // Email not verified
        bottomSheetFragmentEmailNotVerified = new BottomSheetFragment_EmailNotVerified(
                this);
        bottomSheetFragmentEmailNotVerified.setCancelable(true);
        bottomSheetFragmentEmailNotVerified.setRetainInstance(true);

        // Check if FullNameOrBusinessName is null
        if (!fetchedFullNameOrBusinessName.equals("")) {

            // Pass first name to bottom sheet
            bottomSheetFragmentEmailNotVerified.setUsersName(fetchedFullNameOrBusinessName);
        }
    }

    /**
     * Function to set default views on activity start
     */
    private void loadUserProfile() {

        fabEdit.setVisibility(View.GONE); // Hide edit button

        // Hide profile layout
        llUserProfileActivity.setVisibility(View.GONE);

        // Hide ShimmerFrameLayout
        ViewsUtils.showShimmerFrameLayout(true, shimmerFrameLayout);

        // Show swipe SwipeRefresh
        ViewsUtils.showSwipeRefreshLayout(true, true, swipeRefreshLayout,
                swipeRefreshListener);

        // Check for network connection and stop refreshing animation on no connection
        if (!InternetConnectivity.isConnectedToAnyNetwork(mContext)) {

            // Stop refresh animation
            swipeRefreshLayout.setRefreshing(false);
        }

        // Disable profile edits
        enableProfileEdit(false);
    }

    /**
     * Function to enable profile fields for editing
     *
     * @param enable - enable status
     */
    private void enableProfileEdit(boolean enable) {

        editingProfile = enable; // Set edit state

        if (enable) {
            cardAccountType.setVisibility(View.GONE); // Hide account type CardView
            cardSignupDate.setVisibility(View.GONE); // Hide signup date CardView
            fabEdit.setVisibility(View.GONE); // Hide edit profile fab
            fabCancelEdits.setVisibility(View.VISIBLE); // Show cancel edits fab

            // Hide email verification error icon
            imageEmailVerificationError.setVisibility(View.INVISIBLE);

        } else {

            cardAccountType.setVisibility(View.VISIBLE); // Show account type CardView
            cardSignupDate.setVisibility(View.VISIBLE); // Show signup date CardView
            fabEdit.setVisibility(View.VISIBLE); // Show edit profile fab
            fabCancelEdits.setVisibility(View.GONE); // Hide cancel profile edits fab
            fabSaveEdits.setVisibility(View.GONE); // Hide save profile edits fab

            // Check if profile is fetched
            if (profileFetched) {
                if (!emailVerified) {

                    // Show email verification error icon if email is not verified
                    imageEmailVerificationError.setVisibility(View.VISIBLE);
                }
            }

            // Hide keyboard
            ViewsUtils.hideKeyboard(UserProfileActivity.this);

            // Scroll up ScrollView
            ViewsUtils.scrollUpScrollView(scrollView);

            // Revert previously set details in case they changed
            editEmailAddress.setText(fetchedEmailAddress);
            String countryCodeAndName = DataUtils.getStringResource(mContext,
                    R.string.placeholder_in_brackets, fetchedCountryCode)
                    + " " + fetchedCountryName;
            editCountry.setText(countryCodeAndName);
            newSelectedCountryCode.setText(fetchedCountryCode);
            newSelectedCountryAlpha2.setText(fetchedCountryAlpha2);

            // Load previous country flag if country changed
            ViewsUtils.loadImageView(mContext,
                    DataUtils.getDrawableFromName(mContext, fetchedCountryFlag),
                    imageCountryFlag);
        }

        // Enable field focus
        enableEditTexts(enable, editEmailAddress);
        enableEditTexts(enable, editFullNameOrBusinessName);

        editFullNameOrBusinessName.requestFocus(); // Focus on full name or business name

        // Check if enabled
        if (!enable) {

            // Revert previously set details in case they changed
            editFullNameOrBusinessName.setText(fetchedFullNameOrBusinessName);
        }

        swipeRefreshLayout.setEnabled(!enable); // Enable/Disable swipe refresh

    }

    /**
     * Function to enable/disable focus on EDitText
     *
     * @param focus    - focus state
     * @param editText - Associated EditText
     */
    private void enableEditTexts(boolean focus, EditText editText) {

        // Enable focus
        editText.setEnabled(focus); // Enable/Disable EditText
        editText.setFocusable(focus); // Enable/Disable focus
        editText.setFocusableInTouchMode(focus); // Enable/Disable focus in touch mode
    }

    /**
     * Function to set fetched details to respective fields
     *
     * @param jsonObjectUser - JSONObject with user details
     */
    private void setFetchedData(JSONObject jsonObjectUser) {

        // Check if JSONObject is null
        if (jsonObjectUser != null) {

            // Get profile details
            try {

                profileFetched = true; // Set profile fetched to true
                fetchedEmailAddress = jsonObjectUser.getString(UserAccountUtils.FIELD_EMAIL_ADDRESS);
                fetchedCountryName = jsonObjectUser.getString(UserAccountUtils.FIELD_COUNTRY_NAME);
                fetchedCountryCode = jsonObjectUser.getString(UserAccountUtils.FIELD_COUNTRY_CODE);
                fetchedCountryAlpha2 = jsonObjectUser.getString(UserAccountUtils.FIELD_COUNTRY_ALPHA2);
                fetchedCountryFlag = jsonObjectUser.getString(UserAccountUtils.FIELD_COUNTRY_FLAG);
                String fetchedAccountType = jsonObjectUser.getString(UserAccountUtils.FIELD_ACCOUNT_TYPE);

                emailVerified = Boolean.parseBoolean(jsonObjectUser.getString(
                        UserAccountUtils.FIELD_EMAIL_VERIFIED));
                fetchedUserProfile = true;

                // Set profile details
                editEmailAddress.setText(fetchedEmailAddress);
                String countryCodeAndName = DataUtils.getStringResource(
                        mContext,
                        R.string.placeholder_in_brackets,
                        fetchedCountryCode)
                        + " " + jsonObjectUser.getString(UserAccountUtils.FIELD_COUNTRY_NAME);

                // Set country details
                editCountry.setText(countryCodeAndName);

                // Load country flag
                ViewsUtils.loadImageView(mContext,
                        DataUtils.getDrawableFromName(mContext, fetchedCountryFlag),
                        imageCountryFlag);

                textSignupDate.setText(jsonObjectUser.getString(UserAccountUtils.FIELD_SIGNUP_DATE_TIME));

                // Set to newly EditText to avoid showing save button during field check
                newSelectedCountryCode.setText(fetchedCountryCode);
                newSelectedCountryAlpha2.setText(fetchedCountryAlpha2);

                fetchedFullNameOrBusinessName = jsonObjectUser
                        .getString(UserAccountUtils.FIELD_FULL_NAME_OR_BUSINESS_NAME);

                // Show views
                cardPersonsNames.setVisibility(View.VISIBLE);

                // Set profile details
                editFullNameOrBusinessName.setText(fetchedFullNameOrBusinessName);

                // Set account type
                String accountTypeLabel;
                if (fetchedAccountType.equals(UserAccountUtils.KEY_ACCOUNT_TYPE_FREE)) {

                    // Set account type label to free account
                    accountTypeLabel = DataUtils.getStringResource(mContext,
                            R.string.hint_account_type_free);

                } else {

                    // Set account type label to PRO account
                    accountTypeLabel = DataUtils.getStringResource(mContext,
                            R.string.hint_account_type_pro);
                }

                // Set account type label
                textAccountType.setText(accountTypeLabel);


                resetNewValueFields(); // Reset new value fields to null

                // Hide ShimmerFrameLayout
                ViewsUtils.showShimmerFrameLayout(false, shimmerFrameLayout);

                // Show FABS layout
                llUserProfileActivityFABS.setVisibility(View.VISIBLE);

                // Show profile layout
                llUserProfileActivity.setVisibility(View.VISIBLE);

                ViewsUtils.scrollUpScrollView(scrollView); // Scroll up ScrollView

                fabEdit.setVisibility(View.VISIBLE); // Show edit button

                // Check email verification
                if (!emailVerified) {
                    new CountDownTimer(3000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {

                            initEmailVerificationFragment(); // Initialize fragment

                            // Hide error icon in case user clicked edit before it was shown
                            if (editingProfile) {

                                // Hide error icon in case user clicked edit before it was shown
                                imageEmailVerificationError.setVisibility(View.INVISIBLE);
                            } else {

                                // Show Email Not Verified Icon
                                imageEmailVerificationError.setVisibility(View.VISIBLE);

                                // Prevent dialog from showing when fetch method is called
                                // after a profile update
                                if (!emailNotVerifiedDialogShown) {

                                    emailNotVerifiedDialogShown = true; // SEt shown to true

                                    // Start Email Not Verified BottomSheet
                                    ViewsUtils.showBottomSheetDialogFragment(getSupportFragmentManager(),
                                            bottomSheetFragmentEmailNotVerified, true);
                                }
                            }
                        }
                    }.start();

                    // Hide error icon in case user clicked edit before it was shown
                    if (editingProfile) {
                        // Hide Email Not Verified Icon
                        imageEmailVerificationError.setVisibility(View.INVISIBLE);
                    }
                } else {

                    // Hide error icon
                    imageEmailVerificationError.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException ignored) {
            }
        }
    }

    /**
     * Function to respond to connection failures
     */
    private void respondToNetworkConnectionEvent(boolean connected,
                                                 boolean updatingProfile) {

        // Check connection state
        if (!connected) {

            // Stop swipe SwipeRefresh
            ViewsUtils.showSwipeRefreshLayout(false, true, swipeRefreshLayout,
                    swipeRefreshListener);

            // Check if updating profile
            if (updatingProfile) {

                // Set current task to updating user profile
                CURRENT_TASK = TaskUtils.TASK_UPDATE_USER_PROFILE;

            } else {

                // Set current task to fetching user profile
                CURRENT_TASK = TaskUtils.TASK_FETCH_USER_PROFILE;

                if (!fetchedUserProfile) {

                    // Show ShimmerFrameLayout
                    ViewsUtils.showShimmerFrameLayout(true, shimmerFrameLayout);
                }

                fabEdit.setVisibility(View.GONE); // Hide save edits fab
            }

            if (!fetchedUserProfile) {
                // Hide profile layout
                llUserProfileActivity.setVisibility(View.GONE);
            }

            // Show no connection bar
            llNoConnection.setVisibility(View.VISIBLE);

        } else {

            // Hide no connection bar
            llNoConnection.setVisibility(View.GONE);
        }
    }

    /**
     * Function to enable save button on profile edits
     */
    private void enableFabButtons() {

        // Check account type and hide edit fab
        if (fieldValuesChanged()) {

            fabSaveEdits.setVisibility(View.VISIBLE);  // Show save edits fab
        } else {

            fabSaveEdits.setVisibility(View.GONE);  // Hide save edits fab
        }
        fabEdit.setVisibility(View.GONE); // Hide edit fab
    }

    /**
     * Function to check for field value changes from the initial fetched value
     */
    private boolean fieldValuesChanged() {

        return ((!editFullNameOrBusinessName.getText().toString().equals(fetchedFullNameOrBusinessName))
                || (!editEmailAddress.getText().toString().equals(fetchedEmailAddress))
                || (!newSelectedCountryCode.getText().toString().equals(fetchedCountryCode))
                || (!newSelectedCountryAlpha2.getText().toString().equals(fetchedCountryAlpha2)));
    }

    /**
     * Function to check for updated values
     */
    private void compareAndGetUpdatedValues() {
        // Check account type

        // FullName or business name and Gender
        if (!editFullNameOrBusinessName.getText().toString()
                .equals(fetchedFullNameOrBusinessName)) {

            newFullNameOrBusinessName = editFullNameOrBusinessName.getText().toString();
        }

        // Check EmailAddress
        if (!editEmailAddress.getText().toString().equals(fetchedEmailAddress)) {

            newEmailAddress = editEmailAddress.getText().toString();
        }

        // Check CountryCode
        if (!newSelectedCountryCode.getText().toString().equals(fetchedCountryCode)) {

            newCountryCode = newSelectedCountryCode.getText().toString();
        }

        // Check CountryAlpha2
        if (!newSelectedCountryAlpha2.getText().toString().equals(fetchedCountryAlpha2)) {

            newCountryAlpha2 = newSelectedCountryAlpha2.getText().toString();
        }
    }

    /**
     * Function to reset newly selected values in case of concurrent updates
     */
    private void resetNewValueFields() {

        // Set string values to empty
        this.newFullNameOrBusinessName = "";
        this.newEmailAddress = "";
        this.newCountryCode = "";
        this.newCountryAlpha2 = "";
    }

    /**
     * Function to fetch/retrieve user account information
     *
     * @param strEmailAddress - Users email address
     * @param strPassword     - Users password
     */
    private void fetchUserAccountInfo(String strEmailAddress, String strPassword) {

        // Check Internet Connection states
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            // Respond to network connection event
            respondToNetworkConnectionEvent(true, false);

            // Hide FABS layout
            llUserProfileActivityFABS.setVisibility(View.GONE);

            // Hide user profile
            llUserProfileActivity.setVisibility(View.GONE);

            // Show ShimmerFrameLayout
            ViewsUtils.showShimmerFrameLayout(true, shimmerFrameLayout);

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUrls.UserURLS.URL_FETCH_USER_PROFILE_DETAILS, response -> {

                // Log Response
                // Log.d(TAG, "Profile Response : " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                    // Get JSONObject
                    JSONObject objectUser = jsonObject.getJSONObject(
                            VolleyUtils.KEY_USER);

                    // Check for error
                    if (!error) {

                        // Stop swipe SwipeRefresh
                        ViewsUtils.showSwipeRefreshLayout(false, true,
                                swipeRefreshLayout, swipeRefreshListener);

                        setFetchedData(objectUser); // Set fetched details

                    } else {
                        // Error fetching details

                        enableProfileEdit(false); // Disable profile edits

                        // Stop swipe SwipeRefresh
                        ViewsUtils.showSwipeRefreshLayout(false, true,
                                swipeRefreshLayout, swipeRefreshListener);

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkTags.UserNetworkTags.TAG_FETCH_USER_PROFILE_STRING_REQUEST);
                    }
                } catch (Exception ignored) {
                }
            }, volleyError -> {
                // Log Response
                // Log.e(TAG, "Profile Response Error " + ":" + volleyError.getMessage());

                // Show FABS layout
                llUserProfileActivityFABS.setVisibility(View.VISIBLE);

                // Stop swipe SwipeRefresh
                ViewsUtils.showSwipeRefreshLayout(false, true,
                        swipeRefreshLayout, swipeRefreshListener);

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

                // Clear url cache
                ApplicationClass.getClassInstance().deleteUrlVolleyCache(
                        NetworkUrls.UserURLS.URL_FETCH_USER_PROFILE_DETAILS);
            }) {
                @Override
                protected void deliverResponse(String response) {
                    super.deliverResponse(response);
                }

                    /*@Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        headers.put(VolleyUtils.KEY_API_KEY, VolleyUtils.getApiKey(mContext));
                        return headers;
                    }*/

                @Override
                protected Map<String, String> getParams() {
                    @SuppressWarnings({"unchecked", "rawtypes"}) Map<String, String> params =
                            new HashMap();
                    params.put(UserAccountUtils.FIELD_EMAIL_ADDRESS, strEmailAddress);
                    params.put(UserAccountUtils.FIELD_PASSWORD, strPassword);
                    return params;
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    return super.parseNetworkError(volleyError);
                }

                @Override
                public void deliverError(VolleyError error) {
                    super.deliverError(error);
                }
            };

            // Set Request Priority
            ApplicationClass.getClassInstance().setPriority(Request.Priority.HIGH);

            // Set retry policy
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DataUtils.getIntegerResource(mContext,
                            R.integer.int_volley_account_request_initial_timeout_ms),
                    DataUtils.getIntegerResource(mContext,
                            R.integer.int_volley_account_request_max_timeout_retry),
                    1.0f));

            // Set request caching to false
            stringRequest.setShouldCache(false);

            // Adding request to request queue
            ApplicationClass.getClassInstance().addToRequestQueue(stringRequest,
                    NetworkTags.UserNetworkTags.TAG_FETCH_USER_PROFILE_STRING_REQUEST);
        } else {
            // Respond to network connection event
            respondToNetworkConnectionEvent(false, false);
        }
    }

    /**
     * Function to update user profile
     */
    private void updateUserAccountInfo(final String userId, final String accountType) {

        // Check Internet Connection states
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            // Respond to network connection event
            respondToNetworkConnectionEvent(true, true);

            // Hide keyboard if showing
            ViewsUtils.hideKeyboard(UserProfileActivity.this);

            // Hide FABS layout
            llUserProfileActivityFABS.setVisibility(View.GONE);

            // Show ProgressDialog
            ViewsUtils.showProgressDialog(progressDialog,
                    DataUtils.getStringResource(mContext,
                            R.string.title_updating_profile),
                    DataUtils.getStringResource(mContext,
                            R.string.msg_updating_profile)
            );

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUrls.UserURLS.URL_UPDATE_USER_PROFILE_DETAILS, response -> {

                // Log Response
                // Log.d(TAG, "Update Response:" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                    // Hide ProgressDialog
                    ViewsUtils.dismissProgressDialog(progressDialog);

                    // Check for error
                    if (!error) {
                        // User profile updated successfully

                        // Show FABS layout
                        llUserProfileActivityFABS.setVisibility(View.VISIBLE);

                        // Variable to indicate update success for other fields apart
                        // from email address
                        boolean updated = true;

                        // Check if updated fields was email address
                        if (!DataUtils.isEmptyString(newEmailAddress)) {

                            // Update email address in SQLite database
                            updated = database.updateUserAccountInformation(mContext,
                                    database.getUserAccountInfo(null)
                                            .get(0).getUserId(),
                                    newEmailAddress,
                                    UserAccountUtils.FIELD_EMAIL_ADDRESS);

                            // Allow email not verified bottom sheet to be shown again since
                            // email verification was revoked after updating email address
                            emailNotVerifiedDialogShown = false;
                        }

                        if (updated) {
                            // Update complete

                            enableProfileEdit(false); // Disable profile edit

                            // Show update successful message
                            CustomToast.infoMessage(mContext,
                                    DataUtils.getStringResource(mContext,
                                            R.string.msg_updating_profile_successful),
                                    false,
                                    R.drawable.ic_baseline_person_24_white);

                            // Start SwipeRefreshLayout
                            ViewsUtils.showSwipeRefreshLayout(true, true,
                                    swipeRefreshLayout, swipeRefreshListener);
                        }
                    } else {
                        // Error updating details

                        // Toast error message
                        CustomToast.errorMessage(
                                mContext,
                                DataUtils.getStringResource(
                                        mContext,
                                        R.string.error_profile_update_failed),
                                R.drawable.ic_baseline_edit_24_white);

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkTags.UserNetworkTags.TAG_UPDATE_USER_DETAILS_STRING_REQUEST);

                        enableProfileEdit(true); // Enable profile edit
                    }
                } catch (Exception ignored) {
                }
            }, volleyError -> {

                // Log Response
                // Log.e(TAG, "Update Response Error " + ":" + volleyError.getMessage());

                // Hide ProgressDialog
                ViewsUtils.dismissProgressDialog(progressDialog);

                enableProfileEdit(false); // Disable profile edit

                // Show FABS layout
                llUserProfileActivityFABS.setVisibility(View.VISIBLE);

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

                // Clear url cache
                ApplicationClass.getClassInstance().deleteUrlVolleyCache(
                        NetworkUrls.UserURLS.URL_UPDATE_USER_PROFILE_DETAILS);
            }) {
                @Override
                protected void deliverResponse(String response) {
                    super.deliverResponse(response);
                }

                    /*@Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        headers.put(VolleyUtils.KEY_API_KEY, VolleyUtils.getApiKey(mContext));
                        return headers;
                    }*/

                @Override
                protected Map<String, String> getParams() {
                    @SuppressWarnings({"unchecked", "rawtypes"}) Map<String, String> params =
                            new HashMap();

                    // Check for changed values for FullNameOrBusinessName
                    if (!DataUtils.isEmptyString(newFullNameOrBusinessName)) {

                        // Put FullNameOrBusinessName
                        params.put(UserAccountUtils.FIELD_FULL_NAME_OR_BUSINESS_NAME,
                                newFullNameOrBusinessName);
                    }

                    // Check for changed EmailAddress values
                    if (!DataUtils.isEmptyString(newEmailAddress)) {

                        // Put EmailAddress
                        params.put(UserAccountUtils.FIELD_EMAIL_ADDRESS, newEmailAddress);
                    }

                    // Check for changed country details values
                    if ((!DataUtils.isEmptyString(newCountryCode))
                            && (!DataUtils.isEmptyString(newCountryAlpha2))) {

                        // Put CountryCode
                        params.put(UserAccountUtils.FIELD_COUNTRY_CODE, newCountryCode);

                        // Put CountryAlpha2
                        params.put(UserAccountUtils.FIELD_COUNTRY_ALPHA2, newCountryAlpha2);
                    }

                    params.put(UserAccountUtils.FIELD_USER_ID, userId); // Put UserId

                    // Put AccountType
                    params.put(UserAccountUtils.FIELD_ACCOUNT_TYPE, accountType);

                    return params; // Return params
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {

                    return super.parseNetworkError(volleyError);
                }

                @Override
                public void deliverError(VolleyError error) {
                    super.deliverError(error);
                }
            };

            // Set Request Priority
            ApplicationClass.getClassInstance().setPriority(Request.Priority.HIGH);

            // Set retry policy
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DataUtils.getIntegerResource(mContext,
                            R.integer.int_volley_account_request_initial_timeout_ms),
                    DataUtils.getIntegerResource(mContext,
                            R.integer.int_volley_account_request_max_timeout_retry),
                    1.0f));

            // Set request caching to false
            stringRequest.setShouldCache(false);

            // Adding request to request queue
            ApplicationClass.getClassInstance().addToRequestQueue(stringRequest,
                    NetworkTags.UserNetworkTags.TAG_UPDATE_USER_DETAILS_STRING_REQUEST);

        } else {

            // Respond to network connection event
            respondToNetworkConnectionEvent(false, false);
        }
    }

    /**
     * Interfaces method to receive country name
     */
    @Override
    public void passCountryName(String countryName) {
    }

    /**
     * Interfaces method to receive country code
     */
    @Override
    public void passCountryCode(String countryCode) {
        this.newSelectedCountryCode.setText(countryCode); // Set newly selected country code
    }

    /**
     * Interfaces method to receive country code with country name
     */
    @Override
    public void passCountryCodeWithCountryName(String countryCodeWithCountryName) {
        this.editCountry.setText(countryCodeWithCountryName); // Set newly selected country code
    }

    /**
     * Interfaces method to receive country alpha2
     */
    @Override
    public void passCountryAlpha2(String countryAlpha2) {
        this.newSelectedCountryAlpha2.setText(countryAlpha2); // Set newly selected country alpha2
    }

    /**
     * Interfaces method to receive country alpha3
     */
    @Override
    public void passCountryAlpha3(String countryAlpha3) {
    }

    /**
     * Interfaces method to receive country flag id
     */
    @Override
    public void passCountryFlag(int countryFlagId) {
        // Load country flag
        ViewsUtils.loadImageView(mContext, countryFlagId, this.imageCountryFlag);
    }

    /**
     * TextWatcher to monitor text changes
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (editingProfile) {
            enableFabButtons(); // Enable/Disable edit and save profile buttons
        }
    }
}
