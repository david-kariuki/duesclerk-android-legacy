package com.duesclerk.ui.fragment_signup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.duesclerk.R;
import com.duesclerk.activities.MainActivity;
import com.duesclerk.classes.custom_utilities.application.ApplicationClass;
import com.duesclerk.classes.custom_utilities.application.ViewsUtils;
import com.duesclerk.classes.custom_utilities.application.VolleyUtils;
import com.duesclerk.classes.custom_utilities.user_data.DataUtils;
import com.duesclerk.classes.custom_utilities.user_data.InputFiltersUtils;
import com.duesclerk.classes.custom_utilities.user_data.UserAccountUtils;
import com.duesclerk.classes.custom_views.dialog_fragments.bottom_sheet_dialog_fragments.BottomSheetFragment_CountryPicker;
import com.duesclerk.classes.custom_views.toast.CustomToast;
import com.duesclerk.classes.network.InternetConnectivity;
import com.duesclerk.classes.network.NetworkTags;
import com.duesclerk.classes.network.NetworkUrls;
import com.duesclerk.classes.storage_adapters.SessionManager;
import com.duesclerk.classes.storage_adapters.UserDatabase;
import com.duesclerk.interfaces.Interface_CountryPicker;
import com.duesclerk.interfaces.Interface_SignInSignup;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FragmentSignup extends Fragment implements Interface_CountryPicker {

    // Get class tag
    // private final String TAG = FragmentSignup.class.getSimpleName();

    private Context mContext;
    private EditText editFullNameOrBusinessName, editEmailAddress, editCountry;
    private TextInputEditText editPassword;
    private String countryCode, countryAlpha2;
    private ImageView imagePasswordToggle, imageCountryFlag;
    private BottomSheetFragment_CountryPicker bottomSheetFragmentCountryPicker;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private UserDatabase database;

    public static FragmentSignup newInstance() {
        return new FragmentSignup();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_signup, container, false);

        mContext = getContext(); // Context

        Interface_SignInSignup interfaceSignUpSignIn = (Interface_SignInSignup) getActivity();

        // BottomSheetDialogFragments
        bottomSheetFragmentCountryPicker = new BottomSheetFragment_CountryPicker(this);
        bottomSheetFragmentCountryPicker.setRetainInstance(true);
        bottomSheetFragmentCountryPicker.setCancelable(true);

        editFullNameOrBusinessName = view
                .findViewById(R.id.editSignUpActivity_FullNameOrBusinessName);
        editEmailAddress = view.findViewById(R.id.editSignUpActivity_EmailAddress);
        editCountry = view.findViewById(R.id.editSignUpActivity_Country);
        editPassword = view.findViewById(R.id.editSignUpActivity_Password);

        imageCountryFlag = view.findViewById(R.id.imageSignupActivity_CountryFlag);

        imagePasswordToggle = view.findViewById(R.id.imageSignupActivity_PasswordToggle);

        LinearLayout llSignIn = view.findViewById(R.id.llSignUpActivity_SignIn);
        LinearLayout llSignUp = view.findViewById(R.id.llSignUpActivity_SignUp);

        // Set Input Filters
        editFullNameOrBusinessName.setFilters(new InputFilter[]{
                InputFiltersUtils.filterNames,
                new InputFilter.LengthFilter(InputFiltersUtils.LENGTH_MAX_SINGLE_NAME)
        });

        editEmailAddress.setFilters(new InputFilter[]{InputFiltersUtils.filterEmailAddress,
                new InputFilter.LengthFilter(InputFiltersUtils.LENGTH_MAX_EMAIL_ADDRESS)});

        editFullNameOrBusinessName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Block leading spaces
                InputFiltersUtils.blockLeadingSpaces(mContext, s,
                        editFullNameOrBusinessName, " ");
            }
        });

        // ProgressDialog
        progressDialog = ViewsUtils.initProgressDialog(requireActivity(), false);

        sessionManager = new SessionManager(mContext); // SessionManager
        database = new UserDatabase(mContext); // SQLite database

        editCountry.setOnClickListener(v ->
                ViewsUtils.showBottomSheetDialogFragment(getParentFragmentManager(),
                        bottomSheetFragmentCountryPicker, true));

        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Objects.requireNonNull(editPassword.getText()).toString().length() > 0) {
                    // Show toggle password icon

                    imagePasswordToggle.setVisibility(View.VISIBLE); // Show toggle icon

                } else {
                    // Hide toggle password icon

                    imagePasswordToggle.setVisibility(View.INVISIBLE); // Hide toggle icon
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        imagePasswordToggle.setOnClickListener(view13 -> {

            // Toggle password visibility
            ViewsUtils.togglePasswordField(editPassword, imagePasswordToggle);
        });

        // Select SignIn tab
        llSignIn.setOnClickListener(v ->
                Objects.requireNonNull(interfaceSignUpSignIn).setTabPosition(0));

        // Pass personal signup details to parent activity for signup
        llSignUp.setOnClickListener(v -> {

            // Check fields lengths
            if (checkFieldLengths()) {

                // SignUp user
                signupUser(
                        editFullNameOrBusinessName.getText().toString(),
                        editEmailAddress.getText().toString(),
                        countryCode,
                        countryAlpha2,
                        Objects.requireNonNull(editPassword.getText()).toString()
                );

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Method to check field inputs
     *
     * @return field input status
     */
    private boolean checkFieldLengths() {

        // Check field lengths
        return (InputFiltersUtils.checkFullNameOrBusinessNameLengthNotify(mContext,
                editFullNameOrBusinessName)
                && InputFiltersUtils.checkEmailAddressValidNotify(mContext, editEmailAddress)
                && InputFiltersUtils.checkCountryLengthNotify(mContext, editCountry)
                && InputFiltersUtils.checkPasswordLengthNotify(mContext, editPassword));
    }

    /**
     * Function to SignUp user
     *
     * @param fullNameOrBusinessName - Full name or business name
     * @param emailAddress           - Email address
     * @param countryCode            - Country code
     * @param countryAlpha2          - Country alpha2
     * @param password               - Password
     */
    private void signupUser(final String fullNameOrBusinessName, final String emailAddress,
                            final String countryCode, final String countryAlpha2,
                            final String password) {

        ViewsUtils.hideKeyboard(requireActivity()); // Hide Keyboard

        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            // Show dialog
            ViewsUtils.showProgressDialog(progressDialog,
                    DataUtils.getStringResource(mContext,
                            R.string.title_signing_up), DataUtils.getStringResource(mContext,
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

                        String userId, successMessage = "";

                        // Get signup details
                        userId = objectSignUp.getString(UserAccountUtils.FIELD_USER_ID);

                        // Inserting row in users table
                        if (database.storeUserAccountInformation(
                                userId,
                                emailAddress,
                                password,
                                UserAccountUtils.KEY_ACCOUNT_TYPE_FREE)
                        ) {

                            // Create login sessionManager
                            sessionManager.setSignedIn(true);

                            // Get FullNameOrBusinessName
                            String displayName = objectSignUp
                                    .getString(UserAccountUtils.FIELD_FULL_NAME_OR_BUSINESS_NAME);

                            if (!DataUtils.isEmptyString(displayName)) {

                                successMessage = DataUtils.getStringResource(
                                        mContext,
                                        R.string.msg_welcome_to,
                                        DataUtils.getStringResource(
                                                mContext,
                                                R.string.app_name)
                                                + ", " + (fullNameOrBusinessName));
                            }

                            // Toast welcome message
                            if (!DataUtils.isEmptyString(
                                    Objects.requireNonNull(successMessage))) {

                                CustomToast.infoMessage(mContext, successMessage, false,
                                        0);
                            }

                            // Launch MainActivity
                            startActivity(new Intent(requireActivity(), MainActivity.class));

                            requireActivity().finish(); // Exit current activity
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

                    params.put(UserAccountUtils.FIELD_FULL_NAME_OR_BUSINESS_NAME,
                            fullNameOrBusinessName);
                    params.put(UserAccountUtils.FIELD_EMAIL_ADDRESS, emailAddress);
                    params.put(UserAccountUtils.FIELD_COUNTRY_CODE, countryCode);
                    params.put(UserAccountUtils.FIELD_COUNTRY_ALPHA2, countryAlpha2);
                    params.put(UserAccountUtils.FIELD_PASSWORD, password);

                    return params; // Return params
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

    @Override
    public void passCountryName(String countryName) {
    }

    @Override
    public void passCountryCode(String countryCode) {
        this.countryCode = countryCode;
        ViewsUtils.hideKeyboard(requireActivity()); // Hide keyboard
    }

    @Override
    public void passCountryCodeWithCountryName(String countryCodeAndName) {
        editCountry.setText(countryCodeAndName); // Set country name
        ViewsUtils.hideKeyboard(requireActivity()); // Hide keyboard
    }

    @Override
    public void passCountryAlpha2(String countryAlpha2) {
        this.countryAlpha2 = countryAlpha2;
        ViewsUtils.hideKeyboard(requireActivity()); // Hide keyboard
    }

    @Override
    public void passCountryAlpha3(String countryAlpha3) {

    }

    @Override
    public void passCountryFlag(int countryFlagId) {
        // Load flag to ImageView
        ViewsUtils.loadImageView(mContext, countryFlagId, imageCountryFlag);
    }
}
