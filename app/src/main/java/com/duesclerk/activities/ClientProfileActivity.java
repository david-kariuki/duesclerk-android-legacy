package com.duesclerk.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.duesclerk.interfaces.Interface_CountryPicker;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import custom.custom_utilities.AccountUtils;
import custom.custom_utilities.ApplicationClass;
import custom.custom_utilities.DataUtils;
import custom.custom_utilities.InputFiltersUtils;
import custom.custom_utilities.ViewsUtils;
import custom.custom_utilities.VolleyUtils;
import custom.custom_views.dialog_fragments.bottom_sheets.CountryPickerFragment;
import custom.custom_views.dialog_fragments.bottom_sheets.EmailNotVerifiedFragment;
import custom.custom_views.swipe_refresh.MultiSwipeRefreshLayout;
import custom.custom_views.toast.CustomToast;
import custom.network.InternetConnectivity;
import custom.network.NetworkUtils;
import custom.storage_adapters.SQLiteDB;

public class ClientProfileActivity extends AppCompatActivity implements Interface_CountryPicker,
        TextWatcher {

    private final String TAG = ClientProfileActivity.class.getSimpleName();

    private Context mContext; // Create Context object
    private MultiSwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private ScrollView scrollView;
    private ProgressDialog progressDialog;
    private CardView cardBusinessName, cardPersonsNames, cardCity, cardGender, cardAccountType,
            cardSignupDate;
    private EditText editBusinessName, editFirstName, editLastName, editPhoneNumber,
            editEmailAddress, editCountry, editCityName;
    private TextView textGender, textAccountType, textSignupDate;
    private RadioGroup radioGroupGender;
    private RadioButton radioGenderMale, radioGenderFemale, radioGenderOther;
    private FloatingActionButton fabEdit, fabSaveEdits, fabCancelEdits;
    private ImageView imageEmailVerificationError;
    private boolean editingProfile = false;
    private SQLiteDB database;
    private CountryPickerFragment countryPickerFragment;
    private EmailNotVerifiedFragment emailNotVerifiedFragment;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout llClientProfileActivity, llClientProfileActivityFABS;
    private String fetchedFirstName = "", fetchedLastName = "", fetchedBusinessName = "",
            fetchedPhoneNumber = "", fetchedEmailAddress = "", fetchedCountryName,
            fetchedCountryCode = "", fetchedCountryAlpha2 = "", fetchedCityName = "",
            fetchedGender = "", fetchedAccountType = "";
    private boolean emailVerified = false, emailNotVerifiedDialogShown = false;
    private EditText newSelectedGender = null, newSelectedCountryCode = null,
            newSelectedCountryAlpha2 = null;
    private String newFirstName = "", newLastName = "", newBusinessName = "", newPhoneNumber = "",
            newEmailAddress = "", newCountryCode = "", newCountryAlpha2 = "", newCityName = "",
            newGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        mContext = getApplicationContext(); // Get application context

        swipeRefreshLayout = findViewById(R.id.swipeProfileActivity);
        scrollView = findViewById(R.id.scrollViewProfileActivity);

        // Progress Dialog
        progressDialog = new ProgressDialog(ClientProfileActivity.this);
        progressDialog.setCancelable(false);

        // CardViews
        cardBusinessName = findViewById(R.id.cardClientProfileActivity_BusinessName);
        cardPersonsNames = findViewById(R.id.cardClientProfileActivity_PersonsNames);
        cardCity = findViewById(R.id.cardClientProfileActivity_CityTownName);
        cardGender = findViewById(R.id.cardClientProfileActivity_Gender);
        cardAccountType = findViewById(R.id.cardClientProfileActivity_AccountType);
        cardSignupDate = findViewById(R.id.cardClientProfileActivity_SignupDate);

        editBusinessName = findViewById(R.id.editClientProfileActivity_BusinessName);
        editFirstName = findViewById(R.id.editClientProfileActivity_FirstName);
        editLastName = findViewById(R.id.editClientProfileActivity_LastName);
        editPhoneNumber = findViewById(R.id.editClientProfileActivity_PhoneNumber);
        editEmailAddress = findViewById(R.id.editClientProfileActivity_EmailAddress);
        editCountry = findViewById(R.id.editClientProfileActivity_Country);
        editCityName = findViewById(R.id.editClientProfileActivity_CityTown);

        // Radio group and radio buttons
        radioGroupGender = findViewById(R.id.radioGroupClientProfileActivity_Gender);
        radioGenderMale = findViewById(R.id.radioProfileActivityGenderMale);
        radioGenderFemale = findViewById(R.id.radioProfileActivityGenderFemale);
        radioGenderOther = findViewById(R.id.radioProfileActivityGenderOther);

        // TextViews
        TextView textGenderMale = findViewById(R.id.textClientProfileActivity_GenderMale);
        TextView textGenderFemale = findViewById(R.id.textClientProfileActivity_GenderFemale);
        TextView textGenderOther = findViewById(R.id.textClientProfileActivity_GenderOther);
        textGender = findViewById(R.id.textClientProfileActivity_Gender);
        textAccountType = findViewById(R.id.textClientProfileActivity_AccountType);
        textSignupDate = findViewById(R.id.textClientProfileActivity_SignupDate);

        newSelectedGender = new EditText(mContext);
        newSelectedCountryCode = new EditText(mContext);
        newSelectedCountryAlpha2 = new EditText(mContext);

        // Set Input Filters
        editFirstName.setFilters(new InputFilter[]{InputFiltersUtils.filterNames,
                new InputFilter.LengthFilter(InputFiltersUtils.maxSingleNameLength)});
        editLastName.setFilters(new InputFilter[]{InputFiltersUtils.filterNames,
                new InputFilter.LengthFilter(InputFiltersUtils.maxSingleNameLength)});
        editPhoneNumber.setFilters(new InputFilter[]{InputFiltersUtils.filterPhoneNumber,
                new InputFilter.LengthFilter(InputFiltersUtils.maxPhoneNumberLength)});
        editEmailAddress.setFilters(new InputFilter[]{InputFiltersUtils.filterEmailAddress,
                new InputFilter.LengthFilter(InputFiltersUtils.maxEmailLength)});

        // Add text change listeners
        editFirstName.addTextChangedListener(this);
        editLastName.addTextChangedListener(this);
        editBusinessName.addTextChangedListener(this);
        editPhoneNumber.addTextChangedListener(this);
        editEmailAddress.addTextChangedListener(this);
        editCountry.addTextChangedListener(this);
        editCityName.addTextChangedListener(this);
        newSelectedGender.addTextChangedListener(this);
        newSelectedCountryCode.addTextChangedListener(this);
        newSelectedCountryAlpha2.addTextChangedListener(this);

        shimmerFrameLayout = findViewById(R.id.shimmerClientProfileActivity);
        llClientProfileActivity = findViewById(R.id.llClientProfileActivity_Profile);
        llClientProfileActivityFABS = findViewById(R.id.llClientProfileActivity_FABS);

        // FloatingActionButtons
        fabEdit = findViewById(R.id.fabMainActivity_EditProfile);
        fabSaveEdits = findViewById(R.id.fabMainActivity_SaveProfileEdits);
        fabCancelEdits = findViewById(R.id.fabMainActivity_CancelProfileEdits);

        imageEmailVerificationError =
                findViewById(R.id.imageClientProfileActivity_EmailVerificationError);

        swipeRefreshLayout.setEnabled(true); // Enable SwipeRefresh
        swipeRefreshLayout.setSwipeableChildren(scrollView.getId()); // Set scrollable children

        database = new SQLiteDB(mContext); // Initialize database object

        // CountryPicker
        countryPickerFragment = new CountryPickerFragment(this);
        countryPickerFragment.setCancelable(true);
        countryPickerFragment.setRetainInstance(true);

        // Check if account type was fetched to initialize email not verified fragment and
        // set first name or business name to it
        if (!fetchedAccountType.equals("")) {

            initEmailVerificationFragment(); // Initialize fragment
        }
        // Gender labels on click
        textGenderMale.setOnClickListener(v -> radioGenderMale.setChecked(true));
        textGenderFemale.setOnClickListener(v -> radioGenderFemale.setChecked(true));
        textGenderOther.setOnClickListener(v -> radioGenderOther.setChecked(true));

        radioGenderMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                newSelectedGender.setText(AccountUtils.KEY_GENDER_MALE); // Set gender value
            }
        });

        radioGenderFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                newSelectedGender.setText(AccountUtils.KEY_GENDER_FEMALE); // Set gender value
            }
        });

        radioGenderOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                newSelectedGender.setText(AccountUtils.KEY_GENDER_OTHER); // Set gender value
            }
        });

        // FAB edit onClick
        fabEdit.setOnClickListener(v -> enableProfileEdit(true));

        // FAB save edits onClick
        fabSaveEdits.setOnClickListener(v -> {
            compareAndGetUpdatedValues(); // Compare new and old values for update

            // Update changed values
            updateClientAccountInfo(database.getClientAccountInfo().get(0).getClientId(),
                    fetchedAccountType);
        });

        // Fab cancel edits onClick
        fabCancelEdits.setOnClickListener(v -> {
            enableProfileEdit(false); // Disable profile edits
        });

        // SwipeRefresh listener
        swipeRefreshListener = () -> {
            if (!editingProfile) {
                if (!database.isEmpty()) {
                    fetchClientAccountInfo(database.getClientAccountInfo().get(0).getEmailAddress(),
                            database.getClientAccountInfo().get(0).getPassword());
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

        editingProfile = false; // Set editing profile to false
    }

    @Override
    public void onStart() {
        super.onStart();

        loadClientProfile();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (editingProfile) {

            // Cancel any pending requests
            ApplicationClass.getClassInstance()
                    .cancelPendingRequests(NetworkUtils.TAG_UPDATE_CLIENT_DETAILS_STRING_REQUEST);
        } else {

            // Cancel any pending requests
            ApplicationClass.getClassInstance()
                    .cancelPendingRequests(NetworkUtils.TAG_FETCH_CLIENT_PROFILE_STRING_REQUEST);
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

        if (fetchedAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
            if (!fetchedFirstName.equals("")) {

                // Pass first name to bottom sheet
                emailNotVerifiedFragment.setClientsName(fetchedFirstName);
            }
        } else if (fetchedAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
            if (!fetchedBusinessName.equals("")) {

                // Pass business name to bottom sheet
                emailNotVerifiedFragment.setClientsName(fetchedBusinessName);
            }
        }
    }

    /**
     * Function to set default views on activity start
     */
    private void loadClientProfile() {

        fabEdit.setVisibility(View.GONE); // Hide edit button

        // Start/Stop swipe SwipeRefresh
        ViewsUtils.startSwipeRefreshLayout(true, swipeRefreshLayout,
                swipeRefreshListener);

        // Disable profile edits
        enableProfileEdit(false);
    }

    /**
     * Function to enable profile fields for editing
     *
     * @param enable - enable status
     */
    private void enableProfileEdit(boolean enable) {

        // Check if account type is null
        if (!fetchedAccountType.equals("")) {

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

                if (!emailVerified) {

                    // Show email verification error icon if email is not verified
                    imageEmailVerificationError.setVisibility(View.VISIBLE);
                }

                // Hide keyboard
                ViewsUtils.hideKeyboard(ClientProfileActivity.this);

                // Scroll up ScrollView
                ViewsUtils.scrollUpScrollView(scrollView);

                // Revert previously set details in case they changed
                editPhoneNumber.setText(fetchedPhoneNumber);
                editEmailAddress.setText(fetchedEmailAddress);
                String countryCodeAndName = DataUtils.getStringResource(mContext,
                        R.string.placeholder_in_brackets, fetchedCountryCode)
                        + " " + fetchedCountryName;
                editCountry.setText(countryCodeAndName);
                newSelectedCountryCode.setText(fetchedCountryCode);
                newSelectedCountryAlpha2.setText(fetchedCountryAlpha2);
            }

            // Enable field focus
            enableEditTexts(enable, editPhoneNumber);
            enableEditTexts(enable, editEmailAddress);

            if (fetchedAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
                // Personal

                enableEditTexts(enable, editFirstName);
                enableEditTexts(enable, editLastName);

                editFirstName.requestFocus(); // Focus on first name

                if (enable) {

                    textGender.setVisibility(View.GONE); // Hide gender text
                    radioGroupGender.setVisibility(View.VISIBLE); // Show gender radio button

                } else {

                    textGender.setVisibility(View.VISIBLE); // Show gender text
                    radioGroupGender.setVisibility(View.GONE); // Hide gender radio button

                    // Revert previously set details in case they changed
                    editFirstName.setText(fetchedFirstName);
                    editLastName.setText(fetchedLastName);
                    textGender.setText(fetchedGender);
                    newSelectedGender.setText(fetchedGender);
                }

            } else if (fetchedAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
                // Business

                enableEditTexts(enable, editBusinessName);
                enableEditTexts(enable, editCityName);

                editBusinessName.requestFocus(); // Focus on first name

                // Revert previously set details in case they changed
                editBusinessName.setText(fetchedBusinessName);
                editCityName.setText(fetchedCityName);
            }

            swipeRefreshLayout.setEnabled(!enable); // Enable/Disable swipe refresh
        }
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
     * @param client - JSONObject with client details
     */
    private void setFetchedData(JSONObject client) {

        // Check if JSONObject is null
        if (client != null) {

            // Get profile details
            try {

                fetchedEmailAddress = client.getString(AccountUtils.FIELD_EMAIL_ADDRESS);
                fetchedPhoneNumber = client.getString(AccountUtils.FIELD_PHONE_NUMBER);
                fetchedCountryName = client.getString(AccountUtils.FIELD_COUNTRY_NAME);
                fetchedCountryCode = client.getString(AccountUtils.FIELD_COUNTRY_CODE);
                fetchedCountryAlpha2 = client.getString(AccountUtils.FIELD_COUNTRY_ALPHA2);
                fetchedAccountType = client.getString(AccountUtils.FIELD_ACCOUNT_TYPE);
                emailVerified = Boolean.parseBoolean(client.getString(
                        AccountUtils.FIELD_EMAIL_VERIFIED));

                // Set profile details
                editEmailAddress.setText(fetchedEmailAddress);
                editPhoneNumber.setText(fetchedPhoneNumber);
                String countryCodeAndName = DataUtils.getStringResource(
                        mContext,
                        R.string.placeholder_in_brackets,
                        fetchedCountryCode)
                        + " " + client.getString(AccountUtils.FIELD_COUNTRY_NAME);
                editCountry.setText(countryCodeAndName);
                textSignupDate.setText(client.getString(AccountUtils.FIELD_SIGNUP_DATE_TIME));

                // Set to newly EditText to avoid showing save button during field check
                newSelectedCountryCode.setText(fetchedCountryCode);
                newSelectedCountryAlpha2.setText(fetchedCountryAlpha2);

                if (fetchedAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
                    // Business account

                    fetchedFirstName = client.getString(AccountUtils.FIELD_FIRST_NAME);
                    fetchedLastName = client.getString(AccountUtils.FIELD_LAST_NAME);
                    fetchedGender = client.getString(AccountUtils.FIELD_GENDER);

                    // Hide views
                    cardBusinessName.setVisibility(View.GONE);
                    cardCity.setVisibility(View.GONE);

                    // Show views
                    cardPersonsNames.setVisibility(View.VISIBLE);
                    cardGender.setVisibility(View.VISIBLE);

                    // Set profile details
                    editFirstName.setText(fetchedFirstName);
                    editLastName.setText(fetchedLastName);

                    textGender.setText(fetchedGender);
                    // Set to newly EditText to avoid showing save button during field check
                    newSelectedGender.setText(fetchedGender);

                    textAccountType.setText(DataUtils.getStringResource(mContext,
                            R.string.hint_personal_account));

                } else if (fetchedAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
                    // Business account

                    fetchedBusinessName = client.getString(AccountUtils.FIELD_BUSINESS_NAME);
                    fetchedCityName = client.getString(AccountUtils.FIELD_CITY_NAME);

                    // Hide views
                    cardPersonsNames.setVisibility(View.GONE);
                    cardGender.setVisibility(View.GONE);

                    // Show views
                    cardBusinessName.setVisibility(View.VISIBLE);
                    cardCity.setVisibility(View.VISIBLE);

                    // Set profile details
                    editBusinessName.setText(fetchedBusinessName);
                    editCityName.setText(fetchedCityName);
                    textAccountType.setText(DataUtils.getStringResource(mContext,
                            R.string.hint_business_account));
                }

                resetNewValueFields(); // Reset new value fields to null

                // Hide ShimmerFrameLayout
                ViewsUtils.showShimmerFrameLayout(false, shimmerFrameLayout);

                // Show profile layout
                llClientProfileActivity.setVisibility(View.VISIBLE);

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
    private void respondToConnectionFailure() {

        // Hide ShimmerFrameLayout
        ViewsUtils.showShimmerFrameLayout(true, shimmerFrameLayout);

        // Stop swipe SwipeRefresh
        ViewsUtils.startSwipeRefreshLayout(false, swipeRefreshLayout,
                swipeRefreshListener);

        // Show profile layout
        llClientProfileActivity.setVisibility(View.GONE);
    }

    /**
     * Function to enable save button on profile edits
     */
    private void enableFabButtons() {

        // Check account type and hide edit fab
        if (fieldValuesChanged(fetchedAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL))) {

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
                    || (!editPhoneNumber.getText().toString().equals(fetchedPhoneNumber))
                    || (!editEmailAddress.getText().toString().equals(fetchedEmailAddress))
                    || (!newSelectedCountryCode.getText().toString().equals(fetchedCountryCode))
                    || (!newSelectedCountryAlpha2.getText().toString().equals(fetchedCountryAlpha2))
                    || (!newSelectedGender.getText().toString().equals(fetchedGender)));
        } else {
            return (
                    (!editBusinessName.getText().toString().equals(fetchedBusinessName))
                            || (!editPhoneNumber.getText().toString().equals(fetchedPhoneNumber))
                            || (!editEmailAddress.getText().toString().equals(fetchedEmailAddress))
                            || (!newSelectedCountryCode.getText().toString().equals(fetchedCountryCode))
                            || (!newSelectedCountryAlpha2.getText().toString().equals(fetchedCountryAlpha2))
                            || (!editCityName.getText().toString().equals(fetchedCityName))
            );
        }
    }

    /**
     * Function to check for updated values
     */
    private void compareAndGetUpdatedValues() {
        // Check account type

        if (fetchedAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
            // Personal account

            // FirstName, LastName and Gender
            if (!editFirstName.getText().toString().equals(fetchedFirstName)) {
                newFirstName = editFirstName.getText().toString();
            }

            if (!editLastName.getText().toString().equals(fetchedLastName)) {
                newLastName = editLastName.getText().toString();
            }

            if (!newSelectedGender.getText().toString().equals(fetchedGender)) {
                newGender = newSelectedGender.getText().toString();
            }

        } else if (fetchedAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
            // Business account

            // BusinessName and CityName
            if (!editBusinessName.getText().toString().equals(fetchedBusinessName)) {
                newBusinessName = editBusinessName.getText().toString();
            }

            if (!editCityName.getText().toString().equals(fetchedCityName)) {
                newCityName = editCityName.getText().toString();
            }
        }

        // PhoneNumber, EmailAddress, CountryCode and CountryAlpha2
        if (!editPhoneNumber.getText().toString().equals(fetchedPhoneNumber)) {
            newPhoneNumber = editPhoneNumber.getText().toString();
        }

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
    private void resetNewValueFields(){
        this.newFirstName = "";
        this.newLastName = "";
        this.newBusinessName = "";
        this.newPhoneNumber = "";
        this.newEmailAddress = "";
        this.newCountryCode = "";
        this.newCountryAlpha2 = "";
        this.newCityName = "";
        this.newGender = "";
    }

    /**
     * Function to fetch/retrieve user account information
     *
     * @param strEmailAddress - Clients email address
     * @param strPassword     - Clients password
     */
    private void fetchClientAccountInfo(String strEmailAddress, String strPassword) {

        // Check Internet Connection State
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            if (InternetConnectivity.isConnectionFast(mContext)) {
                // Connected

                // Hide FABS layout
                llClientProfileActivityFABS.setVisibility(View.GONE);

                // Hide client profile
                llClientProfileActivity.setVisibility(View.GONE);

                // Show ShimmerFrameLayout
                ViewsUtils.showShimmerFrameLayout(true, shimmerFrameLayout);

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        NetworkUtils.URL_FETCH_CLIENT_PROFILE_DETAILS, response -> {

                    // Log Response
                    Log.d(TAG, "Profile Response:" + response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                        // Get JSONObject
                        JSONObject client = jsonObject.getJSONObject(VolleyUtils.KEY_CLIENT);

                        // Check for error
                        if (!error) {

                            // Show FABS layout
                            llClientProfileActivityFABS.setVisibility(View.VISIBLE);

                            // Stop swipe SwipeRefresh
                            ViewsUtils.startSwipeRefreshLayout(false, swipeRefreshLayout,
                                    swipeRefreshListener);

                            setFetchedData(client); // Set fetched details
                        } else {
                            // Error fetching details

                            // Cancel Pending Request
                            ApplicationClass.getClassInstance().cancelPendingRequests(
                                    NetworkUtils.TAG_FETCH_CLIENT_PROFILE_STRING_REQUEST);
                        }
                    } catch (Exception ignored) {
                    }
                }, volleyError -> {
                    // Log Response
                    // Log.e(TAG, "Profile Response Error " + ":" + volleyError.getMessage());

                    // Show FABS layout
                    llClientProfileActivityFABS.setVisibility(View.VISIBLE);

                    // Stop swipe SwipeRefresh
                    ViewsUtils.startSwipeRefreshLayout(false, swipeRefreshLayout,
                            swipeRefreshListener);

                    if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                            || volleyError instanceof ServerError || volleyError instanceof
                            AuthFailureError || volleyError instanceof TimeoutError) {

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkUtils.TAG_FETCH_CLIENT_PROFILE_STRING_REQUEST);

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
                        NetworkUtils.TAG_FETCH_CLIENT_PROFILE_STRING_REQUEST);
            } else {
                respondToConnectionFailure(); // Respond to connection failure
            }
        } else {
            respondToConnectionFailure(); // Respond to connection failure
        }
    }

    /**
     * Function to update client profile
     */
    private void updateClientAccountInfo(final String clientId, final String accountType) {

        // Check Internet Connection State
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            if (InternetConnectivity.isConnectionFast(mContext)) {
                // Connected

                // Hide keyboard if showing
                ViewsUtils.hideKeyboard(ClientProfileActivity.this);

                // Hide FABS layout
                llClientProfileActivityFABS.setVisibility(View.GONE);

                // Show dialog
                showProgressDialog();

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        NetworkUtils.URL_UPDATE_CLIENT_DETAILS, response -> {

                    // Log Response
                    // Log.d(TAG, "Update Response:" + response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                        // Hide Dialog
                        ViewsUtils.dismissProgressDialog(progressDialog);

                        // Check for error
                        if (!error) {
                            // Client profile updated successfully

                            // Show FABS layout
                            llClientProfileActivityFABS.setVisibility(View.VISIBLE);

                            // Variable to indicate update success for other fields apart
                            // from email address
                            boolean updated = true;

                            // Check if updated fields was email address
                            if (!DataUtils.isEmptyString(newEmailAddress)) {

                                // Update email address in SQLite database
                                updated = database.updateClientAccountInformation(mContext,
                                        database.getClientAccountInfo().get(0).getClientId(),
                                        newEmailAddress, AccountUtils.FIELD_EMAIL_ADDRESS);

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
                                    NetworkUtils.TAG_UPDATE_CLIENT_DETAILS_STRING_REQUEST);
                            
                            enableProfileEdit(true); // Enable profile edit
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, volleyError -> {

                    // Log Response
                    // Log.e(TAG, "Profile Response Error " + ":" + volleyError.getMessage());

                    // Hide Dialog
                    ViewsUtils.dismissProgressDialog(progressDialog);

                    enableProfileEdit(false); // Disable profile edit

                    // Show FABS layout
                    llClientProfileActivityFABS.setVisibility(View.VISIBLE);

                    if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                            || volleyError instanceof ServerError || volleyError instanceof
                            AuthFailureError || volleyError instanceof TimeoutError) {

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkUtils.TAG_UPDATE_CLIENT_DETAILS_STRING_REQUEST);

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
                        if (fetchedAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
                            // Check for changed values for personal account
                            if (!DataUtils.isEmptyString(newFirstName)) {
                                params.put(AccountUtils.FIELD_FIRST_NAME, newFirstName);
                            }
                            if (!DataUtils.isEmptyString(newLastName)) {
                                params.put(AccountUtils.FIELD_LAST_NAME, newLastName);
                            }
                            if (!DataUtils.isEmptyString(newGender)) {
                                params.put(AccountUtils.FIELD_GENDER, newGender);
                            }

                        } else if (fetchedAccountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
                            // Check for changed values for business account

                            if (!DataUtils.isEmptyString(newBusinessName)) {
                                params.put(AccountUtils.FIELD_BUSINESS_NAME, newBusinessName);
                            }
                            if (!DataUtils.isEmptyString(newCityName)) {
                                params.put(AccountUtils.FIELD_CITY_NAME, newCityName);
                            }
                        }

                        // Check for changed values for shared details
                        if (!DataUtils.isEmptyString(newPhoneNumber)) {
                            params.put(AccountUtils.FIELD_PHONE_NUMBER, newPhoneNumber);
                        }
                        if (!DataUtils.isEmptyString(newEmailAddress)) {
                            params.put(AccountUtils.FIELD_EMAIL_ADDRESS, newEmailAddress);
                        }
                        if ((!DataUtils.isEmptyString(newCountryCode))
                                && (!DataUtils.isEmptyString(newCountryAlpha2))) {
                            params.put(AccountUtils.FIELD_COUNTRY_CODE, newCountryCode);
                            params.put(AccountUtils.FIELD_COUNTRY_ALPHA2, newCountryAlpha2);
                        }

                        params.put(AccountUtils.FIELD_CLIENT_ID, clientId);
                        params.put(AccountUtils.FIELD_ACCOUNT_TYPE, accountType);
                        Log.e(TAG, "\n\n" + params.toString() + "\n\n");
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
                        NetworkUtils.TAG_UPDATE_CLIENT_DETAILS_STRING_REQUEST);
            } else {
                respondToConnectionFailure(); // Respond to connection failure
            }
        } else {
            respondToConnectionFailure(); // Respond to connection failure
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
                    R.string.title_updating_profile));

            // Set progress dialog message
            progressDialog.setMessage(DataUtils.getStringResource(mContext,
                    R.string.msg_updating_profile));

            progressDialog.show(); // Show progress dialog
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