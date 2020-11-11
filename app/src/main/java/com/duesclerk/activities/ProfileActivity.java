package com.duesclerk.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import custom.custom_utilities.ViewsUtils;
import custom.custom_utilities.VolleyUtils;
import custom.custom_views.dialog_fragments.bottom_sheets.CountryPickerFragment;
import custom.custom_views.dialog_fragments.bottom_sheets.EmailNotVerifiedFragment;
import custom.custom_views.swipe_refresh.MultiSwipeRefreshLayout;
import custom.custom_views.toast.CustomToast;
import custom.network.InternetConnectivity;
import custom.network.NetworkUtils;
import custom.storage_adapters.SQLiteDB;

public class ProfileActivity extends AppCompatActivity implements Interface_CountryPicker {

    // private final String TAG = ProfileActivity.class.getSimpleName();

    private Context mContext; // Create Context object
    private MultiSwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private ScrollView scrollView;
    private CardView cardBusinessName, cardPersonsNames, cardCity, cardGender, cardAccountType;
    private EditText editBusinessName, editFirstName, editLastName, editPhoneNumber,
            editEmailAddress, editCountry, editCityName;
    private TextView textGender;
    private TextView textAccountType;
    private RadioGroup radioGroupGender;
    private RadioButton radioGenderMale, radioGenderFemale, radioGenderOther;
    private FloatingActionButton fabEdit, fabSaveEdits;
    private ImageView imageEmailVerificationError;
    private boolean editingProfile = false;
    private SQLiteDB database;
    private String newSelectedGender = null, newSelectedCountryCode = null,
            newSelectedCountryAlpha2 = null;
    private CountryPickerFragment countryPickerFragment;
    private EmailNotVerifiedFragment emailNotVerifiedFragment;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout llProfileActivity;
    private String fetchedFirstName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mContext = getApplicationContext(); // Get application context

        swipeRefreshLayout = findViewById(R.id.swipeProfileActivity);
        scrollView = findViewById(R.id.scrollViewProfileActivity);

        // CardViews
        cardBusinessName = findViewById(R.id.cardProfileActivity_BusinessName);
        cardPersonsNames = findViewById(R.id.cardProfileActivity_PersonsNames);
        cardCity = findViewById(R.id.cardProfileActivity_CityTownName);
        cardGender = findViewById(R.id.cardProfileActivity_Gender);
        cardAccountType = findViewById(R.id.cardProfileActivity_AccountType);

        cardAccountType.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this,
                SignInSignupActivity.class)));

        editBusinessName = findViewById(R.id.editProfileActivity_BusinessName);
        editFirstName = findViewById(R.id.editProfileActivity_FirstName);
        editLastName = findViewById(R.id.editProfileActivity_LastName);
        editPhoneNumber = findViewById(R.id.editProfileActivity_PhoneNumber);
        editEmailAddress = findViewById(R.id.editProfileActivity_EmailAddress);
        editCountry = findViewById(R.id.editProfileActivity_Country);
        editCityName = findViewById(R.id.editProfileActivity_CityTown);

        // Radio group and radio buttons
        radioGroupGender = findViewById(R.id.radioGroupProfileActivity_Gender);
        radioGenderMale = findViewById(R.id.radioProfileActivityGenderMale);
        radioGenderFemale = findViewById(R.id.radioProfileActivityGenderFemale);
        radioGenderOther = findViewById(R.id.radioProfileActivityGenderOther);

        // TextViews
        TextView textGenderMale = findViewById(R.id.textProfileActivity_GenderMale);
        TextView textGenderFemale = findViewById(R.id.textProfileActivity_GenderFemale);
        TextView textGenderOther = findViewById(R.id.textProfileActivity_GenderOther);
        textGender = findViewById(R.id.textProfileActivity_Gender);
        textAccountType = findViewById(R.id.textProfileActivity_AccountType);

        shimmerFrameLayout = findViewById(R.id.shimmerProfileActivity);
        llProfileActivity = findViewById(R.id.llProfileActivity_Profile);

        // FloatingActionButtons
        fabEdit = findViewById(R.id.fabMainActivity_EditProfile);
        fabSaveEdits = findViewById(R.id.fabMainActivity_SaveProfileEdits);

        imageEmailVerificationError =
                findViewById(R.id.imageProfileActivity_EmailVerificationError);

        swipeRefreshLayout.setEnabled(true); // Enable SwipeRefresh
        swipeRefreshLayout.setSwipeableChildren(scrollView.getId()); // Set scrollable children

        // Set color scheme
        swipeRefreshLayout.setColorSchemeColors(DataUtils.getSwipeRefreshColorSchemeResources());

        database = new SQLiteDB(mContext); // Initialize database object

        // CountryPicker
        countryPickerFragment = new CountryPickerFragment(this);
        countryPickerFragment.setCancelable(true);
        countryPickerFragment.setRetainInstance(true);

        // Email not verified
        emailNotVerifiedFragment = new EmailNotVerifiedFragment(this);
        emailNotVerifiedFragment.setCancelable(true);
        emailNotVerifiedFragment.setRetainInstance(true);

        // Check if first name was fetched
        if (!fetchedFirstName.equals("")) {
            emailNotVerifiedFragment.setFirstName(fetchedFirstName); // Set first name
        }

        // SwipeRefresh listener
        swipeRefreshListener = () -> {
            if (!editingProfile) {
                fetchUserAccountInfo(database.getClientAccountInfo().get(0).getEmailAddress(),
                        database.getClientAccountInfo().get(0).getPassword());
            }
        };

        // Gender labels on click
        textGenderMale.setOnClickListener(v -> radioGenderMale.setChecked(true));
        textGenderFemale.setOnClickListener(v -> radioGenderFemale.setChecked(true));
        textGenderOther.setOnClickListener(v -> radioGenderOther.setChecked(true));

        radioGenderMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                newSelectedGender = AccountUtils.KEY_GENDER_MALE; // Set gender value
            }
        });

        radioGenderFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                newSelectedGender = AccountUtils.KEY_GENDER_FEMALE; // Set gender value
            }
        });

        radioGenderOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                newSelectedGender = AccountUtils.KEY_GENDER_OTHER; // Set gender value
            }
        });

        editingProfile = false; // Set editing profile to false

        // FAB edit onClick
        fabEdit.setOnClickListener(v -> enableProfileEdit(true));

        // FAB save edits onClick
        fabSaveEdits.setOnClickListener(v -> {

        });

        // Edit country onClick
        editCountry.setOnClickListener(v -> ViewsUtils.showBottomSheetDialogFragment(
                getSupportFragmentManager(), countryPickerFragment, true));

        imageEmailVerificationError.setOnClickListener(v -> {
            if (!fetchedFirstName.equals("")) {
                // Start email not verified bottom sheet
                ViewsUtils.showBottomSheetDialogFragment(getSupportFragmentManager(),
                        emailNotVerifiedFragment, true);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!editingProfile) {

            setViews(); // Set default views on activity start

            // Start swipe SwipeRefresh
            ViewsUtils.startStopSwipeRefreshLayout(true, swipeRefreshLayout,
                    swipeRefreshListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

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

    @Override
    public void onResume() {
        super.onResume();

        setViews(); // Set default views on activity start
    }

    /**
     * Function to set default views on activity start
     */
    private void setViews() {

        // Show ShimmerFrameLayout
        ViewsUtils.showShimmerFrameLayout(true, shimmerFrameLayout);

        // Hide profile layout
        llProfileActivity.setVisibility(View.GONE);
    }

    /**
     * Function to enable profile fields for editing
     *
     * @param enable - enable status
     */
    private void enableProfileEdit(boolean enable) {

        // Get account type
        String accountType = database.getClientAccountInfo().get(0).getAccountType();

        if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
            enableEditTexts(enable, editBusinessName);
            enableEditTexts(enable, editCityName);

            if (enable) {
                textGender.setVisibility(View.GONE); // Hide gender text
                radioGroupGender.setVisibility(View.VISIBLE); // Show gender radio button
                cardAccountType.setVisibility(View.GONE); // Hide account type CardView

                fabEdit.setVisibility(View.GONE); // Hide edit profile fab
                fabSaveEdits.setVisibility(View.VISIBLE); // Show save profile edits fab
            } else {
                textGender.setVisibility(View.VISIBLE); // Show gender text
                radioGroupGender.setVisibility(View.GONE); // Hide gender radio button
                cardAccountType.setVisibility(View.VISIBLE); // Show account type CardView

                fabEdit.setVisibility(View.VISIBLE); // Show edit profile fab
                fabSaveEdits.setVisibility(View.GONE); // Hide save profile edits fab
            }
        } else if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
            enableEditTexts(enable, editFirstName);
            enableEditTexts(enable, editLastName);
        }

        enableEditTexts(enable, editPhoneNumber);
        enableEditTexts(enable, editEmailAddress);
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
     * Function to set fetched data to respective fields
     *
     * @param client - JSONObject with client data
     */
    private void setFetchedData(JSONObject client) {

        // Check if JSONObject is null
        if (client != null) {
            String businessName, firstName = null, lastName,
                    phoneNumber, emailAddress, countryName, countryCode,
                    city, gender,
                    accountType;
            boolean emailVerified;

            // Get profile data
            try {

                ViewsUtils.scrollUpScrollView(scrollView); // Scroll up ScrollView

                emailAddress = client.getString(AccountUtils.KEY_EMAIL_ADDRESS);
                phoneNumber = client.getString(AccountUtils.KEY_PHONE_NUMBER);
                countryCode = client.getString(AccountUtils.KEY_COUNTRY_CODE);
                countryName = client.getString(AccountUtils.KEY_COUNTRY_NAME);
                accountType = client.getString(AccountUtils.KEY_ACCOUNT_TYPE);
                emailVerified = Boolean.parseBoolean(client.getString(
                        AccountUtils.KEY_EMAIL_VERIFIED));

                editEmailAddress.setText(emailAddress);
                editPhoneNumber.setText(phoneNumber);
                String country = DataUtils.getStringResource(mContext,
                        R.string.placeholder_in_brackets, countryCode) + " " + countryName;
                editCountry.setText(country);

                if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
                    // Business account

                    firstName = client.getString(AccountUtils.KEY_FIRST_NAME);
                    lastName = client.getString(AccountUtils.KEY_LAST_NAME);
                    gender = client.getString(AccountUtils.KEY_GENDER);

                    // Hide views
                    cardBusinessName.setVisibility(View.GONE);
                    cardCity.setVisibility(View.GONE);

                    // Show views
                    cardPersonsNames.setVisibility(View.VISIBLE);
                    cardGender.setVisibility(View.VISIBLE);

                    // Set profile data
                    editFirstName.setText(firstName);
                    editLastName.setText(lastName);
                    textGender.setText(gender);
                    textAccountType.setText(DataUtils.getStringResource(mContext,
                            R.string.hint_personal_account));

                } else if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
                    // Business account

                    businessName = client.getString(AccountUtils.KEY_BUSINESS_NAME);
                    city = client.getString(AccountUtils.KEY_CITY_NAME);

                    // Hide views
                    cardPersonsNames.setVisibility(View.GONE);
                    cardGender.setVisibility(View.GONE);

                    // Show views
                    cardBusinessName.setVisibility(View.VISIBLE);
                    cardCity.setVisibility(View.VISIBLE);

                    // Set profile data
                    editBusinessName.setText(businessName);
                    editCityName.setText(city);
                    textAccountType.setText(DataUtils.getStringResource(mContext,
                            R.string.hint_business_account));
                }

                // Hide ShimmerFrameLayout
                ViewsUtils.showShimmerFrameLayout(false, shimmerFrameLayout);

                // Show profile layout
                llProfileActivity.setVisibility(View.VISIBLE);

                // Check email verification
                if (!emailVerified) {
                    fetchedFirstName = firstName;
                    new CountDownTimer(4000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {

                            // Show Email Not Verified Icon
                            imageEmailVerificationError.setVisibility(View.VISIBLE);

                            // Pass first name to bottom sheet
                            emailNotVerifiedFragment.setFirstName(fetchedFirstName);

                            // Start Email Not Verified BottomSheet
                            /*ViewsUtils.showBottomSheetDialogFragment(getSupportFragmentManager(),
                                    emailNotVerifiedFragment,
                                    true);*/
                        }
                    }.start();
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
        ViewsUtils.showShimmerFrameLayout(false, shimmerFrameLayout);

        // Show profile layout
        llProfileActivity.setVisibility(View.VISIBLE);
    }

    /**
     * Function to fetch/retrieve user account information
     *
     * @param strEmailAddress - Clients email address
     * @param strPassword     - Clients password
     */
    private void fetchUserAccountInfo(String strEmailAddress, String strPassword) {

        // Check Internet Connection State
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            if (InternetConnectivity.isConnectionFast(mContext)) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        NetworkUtils.URL_FETCH_CLIENT_PROFILE_DETAILS, response -> {

                    // Log Response
                    // Log.d(TAG, "Profile Response:" + response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                        // Get JSONObject
                        JSONObject client = jsonObject.getJSONObject(VolleyUtils.KEY_CLIENT);

                        // Check for error
                        if (!error) {
                            // Stop swipe SwipeRefresh
                            ViewsUtils.startStopSwipeRefreshLayout(false, swipeRefreshLayout,
                                    swipeRefreshListener);

                            setFetchedData(client); // Set fetched data
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

                    // Stop swipe SwipeRefresh
                    ViewsUtils.startStopSwipeRefreshLayout(false, swipeRefreshLayout,
                            swipeRefreshListener);

                    if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                            || volleyError instanceof ServerError || volleyError instanceof
                            AuthFailureError || volleyError instanceof TimeoutError) {

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkUtils.TAG_FETCH_CLIENT_PROFILE_STRING_REQUEST);

                        // Toast Network Error
                        CustomToast.errorMessage(mContext, volleyError.getMessage(), 0);
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
                        params.put(AccountUtils.KEY_EMAIL_ADDRESS, strEmailAddress);
                        params.put(AccountUtils.KEY_PASSWORD, strPassword);
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

    @Override
    public void passCountryName(String countryName) {
    }

    @Override
    public void passCountryCode(String countryCode) {
        this.newSelectedCountryCode = countryCode; // Set newly selected country code
    }

    @Override
    public void passCountryAlpha2(String countryAlpha2) {
        this.newSelectedCountryAlpha2 = countryAlpha2; // Set newly selected country alpha2
    }

    @Override
    public void passCountryAlpha3(String countryAlpha3) {
    }

    @Override
    public void passCountryFlag(int countryFlagId) {
    }
}