package com.duesclerk.ui.fragment_signin;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.duesclerk.R;
import com.duesclerk.activities.MainActivity;
import com.duesclerk.interfaces.Interface_SignInSignup;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import custom.custom_utilities.AccountUtils;
import custom.custom_utilities.ApplicationClass;
import custom.custom_utilities.DataUtils;
import custom.custom_utilities.InputFiltersUtils;
import custom.custom_utilities.ViewsUtils;
import custom.custom_utilities.VolleyUtils;
import custom.custom_views.toast.CustomToast;
import custom.network.InternetConnectivity;
import custom.network.NetworkUtils;
import custom.storage_adapters.SQLiteDB;
import custom.storage_adapters.SessionManager;

public class FragmentSignIn extends Fragment {

    // private static final String TAG = FragmentSignIn.class.getSimpleName();

    private Context mContext;
    private ProgressDialog progressDialog;
    private TextInputEditText editEmailAddress, editPassword;
    private SQLiteDB database;
    private SessionManager sessionManager;
    private ImageView imagePasswordToggle;
    private Interface_SignInSignup interfaceSignInSignup;

    public static FragmentSignIn newInstance() {
        return new FragmentSignIn();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sign_in, container, false);

        mContext = getContext();

        // Interface to switch SignIn and SignUp tabs
        interfaceSignInSignup = (Interface_SignInSignup) getActivity();

        // Progress Dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        editEmailAddress = view.findViewById(R.id.editSignInActivity_EmailAddress);
        editPassword = view.findViewById(R.id.editSignInActivity_Password);
        TextView textForgotPassword = view.findViewById(R.id.textSignInActivity_ForgotPassword);
        LinearLayout llSignIn = view.findViewById(R.id.llSignInActivity_SignIn);
        LinearLayout llSignUp = view.findViewById(R.id.llSignInActivity_SignUp);
        imagePasswordToggle = view.findViewById(R.id.imageSignInActivity_PasswordToggle);

        // Set Input Filters
        editEmailAddress.setFilters(new InputFilter[]{InputFiltersUtils.filterEmailAddress,
                new InputFilter.LengthFilter(InputFiltersUtils.maxEmailLength)});

        // Initialize database
        database = new SQLiteDB(getActivity());

        // Initialize sessionManager
        sessionManager = new SessionManager(requireActivity());

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

        // Forgot Password On Click
        textForgotPassword.setOnClickListener(v -> {

            // Launch Forgot Password Activity
            // Intent intentForgotPassword = new Intent(getActivity(),
            // ForgotPassword_Activity.class);
            // startActivity(intentForgotPassword);
        });

        // SignUp link
        llSignUp.setOnClickListener(view1 -> Objects.requireNonNull(
                interfaceSignInSignup).setTabPosition(1));

        llSignIn.setOnClickListener(view1 -> {
            if (checkFieldLengths()) {

                String strUsername, strPassword;
                strUsername = Objects.requireNonNull(editEmailAddress.getText())
                        .toString();
                strPassword = Objects.requireNonNull(editPassword.getText()).toString();

                // SignIn User
                signInUser(strUsername, strPassword);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();

        ApplicationClass.getClassInstance().cancelPendingRequests((RequestQueue.RequestFilter)
                customRequest -> {
                    // Cancel
                    return true;
                });
    }

    /**
     * Method to check field inputs
     *
     * @return field input status
     */
    private boolean checkFieldLengths() {
        return (InputFiltersUtils.checkEmailAddressValidNotify(mContext, editEmailAddress)
                && InputFiltersUtils.checkPasswordLengthNotify(mContext, editPassword));
    }

    /**
     * Function to SignIn up user
     *
     * @param signInEmailAddress - EmailAddress
     * @param password           - plain text
     */
    private void signInUser(final String signInEmailAddress, final String password) {
        ViewsUtils.hideKeyboard(requireActivity()); // Hide Keyboard

        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) { // Connected
            showProgressDialog(); // Show progress dialog

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUtils.URL_SIGNIN_USER, response -> {
                // Log.d(TAG, "SignIn Response: " + response); // Log response

                ViewsUtils.dismissProgressDialog(progressDialog); // Hide Progress Dialog

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                    // Check for error node in json
                    if (!error) {
                        String accountId, emailAddress;

                        JSONObject signIn = jsonObject.getJSONObject(VolleyUtils.KEY_SIGNIN);
                        accountId = signIn.getString(AccountUtils.FIELD_CLIENT_ID);
                        emailAddress = signIn.getString(AccountUtils.FIELD_EMAIL_ADDRESS);

                        // Inserting row in users table
                        if (database.storeClientAccountInformation(mContext, accountId,
                                emailAddress,
                                password)) {

                            // User details stored
                            sessionManager.setSignedIn(true); // Create login session

                            // Start MainActivity
                            startActivity(new Intent(requireActivity(), MainActivity.class));

                            // Close current activity
                            interfaceSignInSignup.finishActivity();
                        }
                    } else CustomToast.errorMessage(mContext, jsonObject.getString(
                            // Toast Error Message
                            VolleyUtils.KEY_ERROR_MESSAGE), R.drawable.ic_sad_cloud_100px_white);
                } catch (JSONException ignored) {
                }
            }, volleyError -> {
                ViewsUtils.dismissProgressDialog(progressDialog); // Stop Progress Dialog

                // Log.e(TAG, "SignIn Error: " + volleyError.getMessage()); // Log response

                // Check response
                if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                        || volleyError instanceof ServerError || volleyError instanceof
                        AuthFailureError || volleyError instanceof TimeoutError) {
                    CustomToast.errorMessage(mContext, volleyError.getMessage(),
                            R.drawable.ic_sad_cloud_100px_white); // Toast Connection Error Message
                    // CustomToast.errorMessage(mContext, ViewsAndFieldsUtils.getString(mContext,
                    // R.string.error_network_request_error_message),
                    // R.drawable.ic_sad_cloud_100px_white); // Toast Connection Error Message
                } else CustomToast.errorMessage(mContext, DataUtils.getStringResource(mContext,
                        R.string.error_network_connection_error_message_long),
                        R.drawable.ic_sad_cloud_100px_white);

                ApplicationClass.getClassInstance().cancelPendingRequests(
                        NetworkUtils.TAG_SIGNIN_STRING_REQUEST); // Cancel Pending Request
                ApplicationClass.getClassInstance().deleteUrlVolleyCache(
                        NetworkUtils.URL_SIGNIN_USER); // Clear url cache
            }) {
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to sign in url
                    Map<String, String> params = new HashMap<>();
                    params.put(AccountUtils.FIELD_EMAIL_ADDRESS, signInEmailAddress);
                    params.put(AccountUtils.FIELD_PASSWORD, password);
                    return params;
                }
            };

            // Set Retry Policy
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(getResources().getInteger(R.integer.int_volley_account_request_initial_timeout_ms), DataUtils.getIntegerResource(
                    mContext, R.integer.int_volley_account_request_max_timeout_retry),
                    1.0f));

            stringRequest.setShouldCache(true); // Set request caching to false

            // Set Request Priority
            ApplicationClass.getClassInstance().setPriority(Request.Priority.IMMEDIATE);
            ApplicationClass.getClassInstance().addToRequestQueue(stringRequest,
                    NetworkUtils.TAG_SIGNIN_STRING_REQUEST); // Adding Request to request queue

        } else {
            // Not Connected
            CustomToast.errorMessage(mContext, DataUtils.getStringResource(mContext,
                    R.string.error_network_connection_error_message_long),
                    R.drawable.ic_sad_cloud_100px_white);
            ViewsUtils.dismissProgressDialog(progressDialog); // Stop Progress Dialog
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
                    R.string.title_signing_in));

            // Set progress dialog message
            progressDialog.setMessage(DataUtils.getStringResource(mContext,
                    R.string.msg_signing_in));

            progressDialog.show(); // Show progress dialog
        }
    }

}