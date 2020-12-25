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
import com.duesclerk.custom.custom_utilities.AccountUtils;
import com.duesclerk.custom.custom_utilities.ApplicationClass;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.InputFiltersUtils;
import com.duesclerk.custom.custom_utilities.TaskUtils;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_utilities.VolleyUtils;
import com.duesclerk.custom.custom_views.dialog_fragments.bottom_sheets.CountryPickerFragment;
import com.duesclerk.custom.custom_views.dialog_fragments.bottom_sheets.EmailNotVerifiedFragment;
import com.duesclerk.custom.custom_views.swipe_refresh.MultiSwipeRefreshLayout;
import com.duesclerk.custom.custom_views.toast.CustomToast;
import com.duesclerk.custom.network.InternetConnectivity;
import com.duesclerk.custom.network.NetworkUtils;
import com.duesclerk.custom.storage_adapters.SQLiteDB;
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
    private CardView cardBusinessName, cardPersonsNames, cardAccountType,
            cardSignupDate;
    private EditText editBusinessName, editFirstName, editLastName,
            editEmailAddress, editCountry;
    private TextView textAccountType, textSignupDate;
    private FloatingActionButton fabEdit, fabSaveEdits, fabCancelEdits;
    private ImageView imageCountryFlag, imageEmailVerificationError;
    private boolean editingProfile = false;
    private SQLiteDB database;
    private CountryPickerFragment countryPickerFragment;
    private EmailNotVerifiedFragment emailNotVerifiedFragment;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout llUserProfileActivity, llUserProfileActivityFABS, llNoConnection;
    private String fetchedFirstName = "", fetchedLastName = "", fetchedBusinessName = "",
            fetchedEmailAddress = "", fetchedCountryName,
            fetchedCountryCode = "", fetchedCountryFlag = "", fetchedCountryAlpha2 = "";
    private boolean profileFetched = false, emailVerified = false, emailNotVerifiedDialogShown =
            false, fetchedUserProfile = false;
    private EditText newSelectedCountryCode = null,
            newSelectedCountryAlpha2 = null;
    private String newFirstName = "", newLastName = "", newBusinessName = "",
            newEmailAddress = "", newCountryCode = "", newCountryAlpha2 = "";
    private String accountType;
    private int CURRENT_TASK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mContext = getApplicationContext(); // Get application context

        swipeRefreshLayout = findViewById(R.id.swipeProfileActivity);
        scrollView = findViewById(R.id.scrollViewProfileActivity);

        // Progress Dialog
        progressDialog = new ProgressDialog(UserProfileActivity.this);
        progressDialog.setCancelable(false);

        // CardViews
        cardBusinessName = findViewById(R.id.cardUserProfileActivity_BusinessName);
        cardPersonsNames = findViewById(R.id.cardUserProfileActivity_PersonsNames);
        cardAccountType = findViewById(R.id.cardUserProfileActivity_AccountType);
        cardSignupDate = findViewById(R.id.cardUserProfileActivity_SignupDate);

        editBusinessName = findViewById(R.id.editUserProfileActivity_BusinessName);
        editFirstName = findViewById(R.id.editUserProfileActivity_FirstName);
        editLastName = findViewById(R.id.editUserProfileActivity_LastName);
        editEmailAddress = findViewById(R.id.editUserProfileActivity_EmailAddress);
        editCountry = findViewById(R.id.editUserProfileActivity_Country);

        // TextViews
        textAccountType = findViewById(R.id.textUserProfileActivity_AccountType);
        textSignupDate = findViewById(R.id.textUserProfileActivity_SignupDate);

        // Initialize EditText objects
        newSelectedCountryCode = new EditText(mContext);
        newSelectedCountryAlpha2 = new EditText(mContext);

        // Set Input Filters
        editFirstName.setFilters(new InputFilter[]{InputFiltersUtils.filterNames,
                new InputFilter.LengthFilter(InputFiltersUtils.LENGTH_MAX_SINGLE_NAME)});
        editLastName.setFilters(new InputFilter[]{InputFiltersUtils.filterNames,
                new InputFilter.LengthFilter(InputFiltersUtils.LENGTH_MAX_SINGLE_NAME)});
        editEmailAddress.setFilters(new InputFilter[]{InputFiltersUtils.filterEmailAddress,
                new InputFilter.LengthFilter(InputFiltersUtils.LENGTH_MAX_EMAIL_ADDRESS)});

        // Add text change listeners
        editFirstName.addTextChangedListener(this);
        editLastName.addTextChangedListener(this);
        editBusinessName.addTextChangedListener(this);
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

        database = new SQLiteDB(mContext); // Initialize database object

        // Get accountType from SQLite database
        accountType = database.getUserAccountInfo(null).get(0).getAccountType();

        // CountryPicker
        countryPickerFragment = new CountryPickerFragment(this);
        countryPickerFragment.setCancelable(true);
        countryPickerFragment.setRetainInstance(true);

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

        // Set refresh listener
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);

        // Edit country onClick
        editCountry.setOnClickListener(v -> ViewsUtils.showBottomSheetDialogFragment(
                getSupportFragmentManager(), countryPickerFragment, true));

        imageEmailVerificationError.setOnClickListener(v -> {
            if ((!fetchedFirstName.equals("") || !fetchedBusinessName.equals(""))
                    && (!editingProfile)) {

                // Start email not verified bottom sheet
                ViewsUtils.showBottomSheetDialogFragment(getSupportFragmentManager(),
                        emailNotVerifiedFragment, true);
            }
        });

        llNoConnection_TryAgain.setOnClickListener(v -> {
            if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
                if (CURRENT_TASK == TaskUtils.TASK_FETCH_USER_PROFILE) {

                    // Start/Stop swipe SwipeRefresh
                    ViewsUtils.startSwipeRefreshLayout(true, swipeRefreshLayout,
                            swipeRefreshListener);

                } else if (CURRENT_TASK == TaskUtils.TASK_UPDATE_USER_PROFILE) {

                    if (editingProfile) {
                        if (fieldValuesChanged(database.getUserAccountInfo(null)
                                .get(0).getAccountType().equals(
                                        AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL))) {
                            fabSaveEdits.performClick();
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
                    .cancelPendingRequests(NetworkUtils.TAG_UPDATE_USER_DETAILS_STRING_REQUEST);
        } else {

            // Cancel any pending requests
            ApplicationClass.getClassInstance()
                    .cancelPendingRequests(NetworkUtils.TAG_FETCH_USER_PROFILE_STRING_REQUEST);
        }
    }

    /**
     * Function to initialize email not verified fragment
     */
    private void initEmailVerificationFragment() {

        // Email not verified
        emailNotVerifiedFragment = new EmailNotVerifiedFragment(this);
        emailNotVerifiedFragment.setCancelable(true);
        emailNotVerifiedFragment.setRetainInstance(true);

        if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
            if (!fetchedFirstName.equals("")) {

                // Pass first name to bottom sheet
                emailNotVerifiedFragment.setUsersName(fetchedFirstName);
            }
        } else if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
            if (!fetchedBusinessName.equals("")) {

                // Pass business name to bottom sheet
                emailNotVerifiedFragment.setUsersName(fetchedBusinessName);
            }
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

        // Start/Stop swipe SwipeRefresh
        ViewsUtils.startSwipeRefreshLayout(true, swipeRefreshLayout,
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

        if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
            // Personal

            enableEditTexts(enable, editFirstName);
            enableEditTexts(enable, editLastName);

            editFirstName.requestFocus(); // Focus on first name

            if (!enable) {

                // Revert previously set details in case they changed
                editFirstName.setText(fetchedFirstName);
                editLastName.setText(fetchedLastName);
            }

        } else if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
            // Business

            enableEditTexts(enable, editBusinessName);

            editBusinessName.requestFocus(); // Focus on first name

            // Revert previously set details in case they changed
            editBusinessName.setText(fetchedBusinessName);
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
     * @param user - JSONObject with user details
     */
    private void setFetchedData(JSONObject user) {

        // Check if JSONObject is null
        if (user != null) {

            // Get profile details
            try {

                profileFetched = true; // Set profile fetched to true
                fetchedEmailAddress = user.getString(AccountUtils.FIELD_EMAIL_ADDRESS);
                fetchedCountryName = user.getString(AccountUtils.FIELD_COUNTRY_NAME);
                fetchedCountryCode = user.getString(AccountUtils.FIELD_COUNTRY_CODE);
                fetchedCountryAlpha2 = user.getString(AccountUtils.FIELD_COUNTRY_ALPHA2);
                fetchedCountryFlag = user.getString(AccountUtils.FIELD_COUNTRY_FLAG);
                emailVerified = Boolean.parseBoolean(user.getString(
                        AccountUtils.FIELD_EMAIL_VERIFIED));
                fetchedUserProfile = true;

                // Set profile details
                editEmailAddress.setText(fetchedEmailAddress);
                String countryCodeAndName = DataUtils.getStringResource(
                        mContext,
                        R.string.placeholder_in_brackets,
                        fetchedCountryCode)
                        + " " + user.getString(AccountUtils.FIELD_COUNTRY_NAME);

                // Set country details
                editCountry.setText(countryCodeAndName);

                // Load country flag
                ViewsUtils.loadImageView(mContext,
                        DataUtils.getDrawableFromName(mContext, fetchedCountryFlag),
                        imageCountryFlag);

                textSignupDate.setText(user.getString(AccountUtils.FIELD_SIGNUP_DATE_TIME));

                // Set to newly EditText to avoid showing save button during field check
                newSelectedCountryCode.setText(fetchedCountryCode);
                newSelectedCountryAlpha2.setText(fetchedCountryAlpha2);

                if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
                    // Business account

                    fetchedFirstName = user.getString(AccountUtils.FIELD_FIRST_NAME);
                    fetchedLastName = user.getString(AccountUtils.FIELD_LAST_NAME);

                    // Hide views
                    cardBusinessName.setVisibility(View.GONE);

                    // Show views
                    cardPersonsNames.setVisibility(View.VISIBLE);

                    // Set profile details
                    editFirstName.setText(fetchedFirstName);
                    editLastName.setText(fetchedLastName);

                    // Set account type
                    textAccountType.setText(DataUtils.getStringResource(mContext,
                            R.string.hint_personal_account));

                } else if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
                    // Business account

                    fetchedBusinessName = user.getString(AccountUtils.FIELD_BUSINESS_NAME);

                    // Hide views
                    cardPersonsNames.setVisibility(View.GONE);

                    // Show views
                    cardBusinessName.setVisibility(View.VISIBLE);

                    // Set profile details
                    editBusinessName.setText(fetchedBusinessName);
                    textAccountType.setText(DataUtils.getStringResource(mContext,
                            R.string.hint_business_account));
                }

                resetNewValueFields(); // Reset new value fields to null

                // Hide ShimmerFrameLayout
                ViewsUtils.showShimmerFrameLayout(false, shimmerFrameLayout);

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
                                            emailNotVerifiedFragment, true);
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
            ViewsUtils.startSwipeRefreshLayout(false, swipeRefreshLayout,
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
                // Show profile layout
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
        if (fieldValuesChanged(accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL))) {

            fabSaveEdits.setVisibility(View.VISIBLE);  // Show save edits fab
        } else {

            fabSaveEdits.setVisibility(View.GONE);  // Hide save edits fab
        }
        fabEdit.setVisibility(View.GONE); // Hide edit fab
    }

    /**
     * Function to check for field value changes from the initial fetched value
     */
    private boolean fieldValuesChanged(boolean isPersonal) {
        if (isPersonal) {
            return ((!editFirstName.getText().toString().equals(fetchedFirstName))
                    || (!editLastName.getText().toString().equals(fetchedLastName))
                    || (!editEmailAddress.getText().toString().equals(fetchedEmailAddress))
                    || (!newSelectedCountryCode.getText().toString().equals(fetchedCountryCode))
                    || (!newSelectedCountryAlpha2.getText().toString().equals(fetchedCountryAlpha2)));
        } else {
            return (
                    (!editBusinessName.getText().toString().equals(fetchedBusinessName))
                            || (!editEmailAddress.getText().toString().equals(fetchedEmailAddress))
                            || (!newSelectedCountryCode.getText().toString().equals(fetchedCountryCode))
                            || (!newSelectedCountryAlpha2.getText().toString().equals(fetchedCountryAlpha2))
            );
        }
    }

    /**
     * Function to check for updated values
     */
    private void compareAndGetUpdatedValues() {
        // Check account type

        if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
            // Personal account

            // FirstName, LastName and Gender
            if (!editFirstName.getText().toString().equals(fetchedFirstName)) {
                newFirstName = editFirstName.getText().toString();
            }

            if (!editLastName.getText().toString().equals(fetchedLastName)) {
                newLastName = editLastName.getText().toString();
            }

        } else if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
            // Business account

            // BusinessName
            if (!editBusinessName.getText().toString().equals(fetchedBusinessName)) {
                newBusinessName = editBusinessName.getText().toString();
            }

        }

        // EmailAddress, CountryCode and CountryAlpha2


        if (!editEmailAddress.getText().toString().equals(fetchedEmailAddress)) {
            newEmailAddress = editEmailAddress.getText().toString();
        }

        if (!newSelectedCountryCode.getText().toString().equals(fetchedCountryCode)) {
            newCountryCode = newSelectedCountryCode.getText().toString();
        }

        if (!newSelectedCountryAlpha2.getText().toString().equals(fetchedCountryAlpha2)) {
            newCountryAlpha2 = newSelectedCountryAlpha2.getText().toString();
        }

    }

    /**
     * Function to reset newly selected values in case of concurrent updates
     */
    private void resetNewValueFields() {
        this.newFirstName = "";
        this.newLastName = "";
        this.newBusinessName = "";
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

        // Check Internet Connection State
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            if (InternetConnectivity.isConnectionFast(mContext)) {
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
                        NetworkUtils.URL_FETCH_USER_PROFILE_DETAILS, response -> {

                    // Log Response
                    // Log.d(TAG, "Profile Response:" + response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                        // Get JSONObject
                        JSONObject objectUser = jsonObject.getJSONObject(
                                VolleyUtils.KEY_USER);

                        // Check for error
                        if (!error) {

                            // Show FABS layout
                            llUserProfileActivityFABS.setVisibility(View.VISIBLE);

                            // Stop swipe SwipeRefresh
                            ViewsUtils.startSwipeRefreshLayout(false, swipeRefreshLayout,
                                    swipeRefreshListener);

                            setFetchedData(objectUser); // Set fetched details
                        } else {
                            // Error fetching details

                            enableProfileEdit(false); // Disable profile edits

                            // Cancel Pending Request
                            ApplicationClass.getClassInstance().cancelPendingRequests(
                                    NetworkUtils.TAG_FETCH_USER_PROFILE_STRING_REQUEST);
                        }
                    } catch (Exception ignored) {
                    }
                }, volleyError -> {
                    // Log Response
                    // Log.e(TAG, "Profile Response Error " + ":" + volleyError.getMessage());

                    // Show FABS layout
                    llUserProfileActivityFABS.setVisibility(View.VISIBLE);

                    // Stop swipe SwipeRefresh
                    ViewsUtils.startSwipeRefreshLayout(false, swipeRefreshLayout,
                            swipeRefreshListener);

                    if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                            || volleyError instanceof ServerError || volleyError instanceof
                            AuthFailureError || volleyError instanceof TimeoutError) {

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkUtils.TAG_FETCH_USER_PROFILE_STRING_REQUEST);

                        // Toast Network Error
                        if (volleyError.getMessage() != null) {
                            CustomToast.errorMessage(mContext, volleyError.getMessage(), 0);
                        }
                    }
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
                        params.put(AccountUtils.FIELD_EMAIL_ADDRESS, strEmailAddress);
                        params.put(AccountUtils.FIELD_PASSWORD, strPassword);
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
                        NetworkUtils.TAG_FETCH_USER_PROFILE_STRING_REQUEST);
            } else {

                // Respond to network connection event
                respondToNetworkConnectionEvent(false, false);
            }
        } else {
            // Respond to network connection event
            respondToNetworkConnectionEvent(false, false);
        }
    }

    /**
     * Function to update user profile
     */
    private void updateUserAccountInfo(final String userId, final String accountType) {

        // Check Internet Connection State
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            if (InternetConnectivity.isConnectionFast(mContext)) {
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
                        NetworkUtils.URL_UPDATE_USER_PROFILE_DETAILS, response -> {

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
                                        AccountUtils.FIELD_EMAIL_ADDRESS);

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
                                                R.string.msg_profile_updated), false,
                                        R.drawable.ic_baseline_person_24_white);

                                // Start SwipeRefreshLayout
                                ViewsUtils.startSwipeRefreshLayout(true, swipeRefreshLayout,
                                        swipeRefreshListener);
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
                                    NetworkUtils.TAG_UPDATE_USER_DETAILS_STRING_REQUEST);

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

                    if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                            || volleyError instanceof ServerError || volleyError instanceof
                            AuthFailureError || volleyError instanceof TimeoutError) {

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkUtils.TAG_UPDATE_USER_DETAILS_STRING_REQUEST);

                        // Toast Network Error
                        if (volleyError.getMessage() != null) {
                            CustomToast.errorMessage(mContext, volleyError.getMessage(), 0);
                        }
                    }
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
                        if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
                            // Check for changed values for personal account
                            if (!DataUtils.isEmptyString(newFirstName)) {
                                params.put(AccountUtils.FIELD_FIRST_NAME, newFirstName);
                            }
                            if (!DataUtils.isEmptyString(newLastName)) {
                                params.put(AccountUtils.FIELD_LAST_NAME, newLastName);
                            }

                        } else if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
                            // Check for changed values for business account

                            if (!DataUtils.isEmptyString(newBusinessName)) {
                                params.put(AccountUtils.FIELD_BUSINESS_NAME, newBusinessName);
                            }
                        }

                        // Check for changed values for shared details
                        if (!DataUtils.isEmptyString(newEmailAddress)) {
                            params.put(AccountUtils.FIELD_EMAIL_ADDRESS, newEmailAddress);
                        }
                        if ((!DataUtils.isEmptyString(newCountryCode))
                                && (!DataUtils.isEmptyString(newCountryAlpha2))) {
                            params.put(AccountUtils.FIELD_COUNTRY_CODE, newCountryCode);
                            params.put(AccountUtils.FIELD_COUNTRY_ALPHA2, newCountryAlpha2);
                        }

                        params.put(AccountUtils.FIELD_USER_ID, userId);
                        params.put(AccountUtils.FIELD_ACCOUNT_TYPE, accountType);

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
                        NetworkUtils.TAG_UPDATE_USER_DETAILS_STRING_REQUEST);

            } else {

                // Respond to network connection event
                respondToNetworkConnectionEvent(false, true);
            }
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