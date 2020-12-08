package com.duesclerk.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.duesclerk.R;
import com.duesclerk.custom.custom_utilities.AccountUtils;
import com.duesclerk.custom.custom_utilities.ApplicationClass;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_utilities.VolleyUtils;
import com.duesclerk.custom.custom_views.toast.CustomToast;
import com.duesclerk.custom.network.InternetConnectivity;
import com.duesclerk.custom.network.NetworkUtils;
import com.duesclerk.custom.storage_adapters.SQLiteDB;
import com.jkb.vcedittext.VerificationCodeEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EmailVerificationActivity extends AppCompatActivity {

    private static final String strLastLayout = "lastLayout";
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private ProgressDialog progressDialog;
    private LinearLayout llLogoLayout;
    private LinearLayout llVerificationLayout;
    private LinearLayout llCannotConnect;
    private LinearLayout llSendVerificationLayout;
    private SQLiteDB database;
    private TextView textVerifyCodeMessage;
    private TextView textResendCodeEnabled;
    private TextView textResendCodeDisabled;
    private ImageView imageLogo;
    private VerificationCodeEditText editVerificationCode;
    private int lastActiveLayout;
    private boolean isEmailSent, isResendEnabledHidden = false, isResendCountFinished = true;
    private CountDownTimer countDownResendCode;
    private String name = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // Initialize context
        mContext = getApplicationContext();

        // Get intent
        Intent intent = new Intent();

        // Get business name or first name
        name = intent.getStringExtra(AccountUtils.KEY_NAME);

        ImageView ivExit = findViewById(R.id.imageEmailActivation_Exit);
        imageLogo = findViewById(R.id.imageEmailVerification_Logo);

        // TextViews
        TextView textSendVerificationCodeMessage =
                findViewById(R.id.textEmailVerification_SendCode_Message);
        textResendCodeEnabled = findViewById(R.id.textEmailVerification_ResendCode_Enabled);
        textResendCodeDisabled = findViewById(R.id.textEmailVerification_ResendCode_Disabled);
        TextView textAlreadyHaveCode = findViewById(R.id.textEmailVerification_AlreadyHaveCode);
        textVerifyCodeMessage = findViewById(R.id.textEmailVerification_VerifyCode_Message);

        // EditText
        editVerificationCode = findViewById(R.id.editEmailVerification_Code);

        // Linear layouts
        llLogoLayout = findViewById(R.id.llEmailVerification_LogoLayout);
        llVerificationLayout = findViewById(R.id.llEmailVerification_VerificationLayout);
        llCannotConnect = findViewById(R.id.llEmailVerification_CannotConnect);
        LinearLayout llSendVerificationCode =
                findViewById(R.id.llEmailVerification_SendVerificationCode);
        llSendVerificationLayout =
                findViewById(R.id.llEmailVerification_SendVerificationCode_layout);
        LinearLayout llVerifyCode = findViewById(R.id.llEmailVerification_VerifyCode);
        LinearLayout llCannotConnect_Retry = findViewById(R.id.llNoConnection_TryAgain);

        ScrollView scrollView = findViewById(R.id.svEmailVerification); // ScrollView

        database = new SQLiteDB(mContext); // Initialize database object

        // Initialize ProgressDialog
        progressDialog = ViewsUtils.initProgressDialog(
                EmailVerificationActivity.this, false);

        // CountDown timer
        countDownResendCode = new CountDownTimer(300000, 1000) {
            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {

                if (!isResendEnabledHidden) {

                    // Hide resend code Enabled TextView
                    textResendCodeEnabled.setVisibility(View.GONE);

                    isResendEnabledHidden = true; // Set isResendHidden Hidden To True
                }

                String resendCode, timeString, timeString1, timeString2, timeString3 = "";

                // Set message
                resendCode = getResources().getString(R.string.action_resend_code);

                timeString = "\nafter " + String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                );

                try {

                    // Strip texts
                    timeString1 = timeString.replace(", 0 sec", "");
                    timeString2 = timeString1.replace("0 min,", "");
                    timeString3 = timeString2.replace("after 0 min", "");

                } catch (Exception ignored) {
                }

                // Set counter thread text with time
                String resendAfterLabel = resendCode + timeString3;
                textResendCodeDisabled.setText(resendAfterLabel);

                // Update resend count finish status
                isResendCountFinished = false;
            }

            public void onFinish() {

                // Set resend code message
                textResendCodeDisabled.setText(getResources()
                        .getString(R.string.action_resend_code));

                // Hide Disabled layout
                textResendCodeDisabled.setVisibility(View.GONE);

                // Show Enabled layout
                textResendCodeEnabled.setVisibility(View.VISIBLE);

                // Update resend Count Finish Status
                isResendCountFinished = true;

                // Set isResendHidden Hidden To False
                isResendEnabledHidden = false;
            }

        };

        if (isResendCountFinished) {

            textResendCodeEnabled.setVisibility(View.VISIBLE);
            textResendCodeDisabled.setVisibility(View.GONE);

        } else {

            textResendCodeDisabled.setVisibility(View.VISIBLE);
            textResendCodeEnabled.setVisibility(View.GONE);
        }

        // Set verification message
        String sendVerificationCodeMessage = DataUtils.getStringResource(
                mContext,
                R.string.msg_send_verification_code_message
                , name
        );

        textSendVerificationCodeMessage.setText(sendVerificationCodeMessage);

        // Underline Links
        textAlreadyHaveCode.setPaintFlags(textAlreadyHaveCode.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textResendCodeEnabled.setPaintFlags(textResendCodeEnabled.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Exit activity
        ivExit.setOnClickListener(v -> finish());

        editVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = Objects.requireNonNull(editVerificationCode.getText()).length();

                if (length == 6) {
                    ViewsUtils.hideKeyboard(EmailVerificationActivity.this); // Hide keyboard
                }
            }
        });

        textResendCodeEnabled.setOnClickListener(v -> {

            editVerificationCode.setText(null); // Clear current Input

            ViewsUtils.hideKeyboard(EmailVerificationActivity.this); // Hide keyboard

            // Resend verification code
            sendEmailVerificationCode(
                    // Pass ClientId
                    database.getClientAccountInfo().get(0).getClientId()
            );
        });

        textAlreadyHaveCode.setOnClickListener(v -> {

            showSendCodeLayout(false); // Hide send verification code layout
            showVerificationLayout(true); // Show verification layout
        });

        // send verification code LinearLayout onClick
        llSendVerificationCode.setOnClickListener(v -> {

            // Resend verification code
            sendEmailVerificationCode(
                    // Pass clientId
                    database.getClientAccountInfo().get(0).getClientId()
            );
        });

        // Verification code onTouchListener
        editVerificationCode.setOnTouchListener((v, event) -> {

            // Set Error On EditText
            editVerificationCode.setError(null);
            return false;
        });

        // ScrollView onClick
        scrollView.setOnClickListener(v -> {

            editVerificationCode.clearFocus(); // Clear EditText focus
            ViewsUtils.hideKeyboard(EmailVerificationActivity.this); // Hide keyboard
        });

        llVerifyCode.setOnClickListener(v -> {

            // Check code length
            if (Objects.requireNonNull(editVerificationCode.getText()).toString().length() < 6) {

                // Set EditText error
                editVerificationCode.setError(DataUtils.getStringResource(mContext,
                        R.string.error_enter_code));

                CustomToast.errorMessage(mContext,
                        DataUtils.getStringResource(
                                mContext,
                                R.string.error_email_verification_code_length),
                        R.drawable.ic_baseline_email_24_white);
            } else {

                // Hide keyboard
                ViewsUtils.hideKeyboard(EmailVerificationActivity.this);

                // Trim verification code
                String strVerificationCode = editVerificationCode.getText().toString().trim();

                // Verify EmailAddress
                verifyEmailAddress(strVerificationCode);
            }
        });

        llCannotConnect_Retry.setOnClickListener(v -> {

            if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
                if (InternetConnectivity.isConnectionFast(mContext)) {
                    // Connected

                    // Hide no connection layout
                    showConnectionLayout(false);

                    // Check last visible layout
                    switch (lastActiveLayout) {

                        case 0:
                            // Show send verification code layout
                            showSendCodeLayout(true);
                            break;

                        case 1:
                            // Show send verification code layout
                            showVerificationLayout(true);
                            break;

                        case 2:
                            // Check If email was sent
                            if (isEmailSent) {
                                // Last visible layout before Network error was verification layout

                                // Show send verification code layout
                                showVerificationLayout(true);

                            } else {
                                // Last visible layout before network error was send code layout

                                // Show send verification code layout
                                showSendCodeLayout(true);
                            }

                        default:
                            break;
                    }
                }
            } else {
                // No connection

                CustomToast.errorMessage(mContext,
                        DataUtils.getStringResource(
                                mContext,
                                R.string.error_network_connection_error_message_short),
                        R.drawable.ic_sad_cloud_100px_white);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Switch layouts
        switchLayouts();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Hide keyboard
        ViewsUtils.hideKeyboard(EmailVerificationActivity.this);
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save Last visible layout
        savedInstanceState.putInt(strLastLayout, lastActiveLayout);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Set email Address
        lastActiveLayout = savedInstanceState.getInt(strLastLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Switch layouts
        switchLayouts();
    }

    /**
     * Function to switch between required layouts
     */
    private void switchLayouts() {
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Network Connected

            switch (lastActiveLayout) {

                case 0:
                    // send code layout

                    showSendCodeLayout(true); // Show send code layout
                    showVerificationLayout(false); // Hide verification layout
                    showConnectionLayout(false); // Hide no connection layout
                    break;

                case 1:
                    // Show verification layout

                    showVerificationLayout(true); // Show verification layout
                    showSendCodeLayout(false); // Hide send code layout
                    showConnectionLayout(false); // Hide no connection layout
                    break;

                default:
                    break;
            }
        } else {

            showConnectionLayout(true); // Show no connection layout
            showSendCodeLayout(false); // Hide send code layout
        }

    }

    /**
     * Function to generate email verification code
     *
     * @param clientId - Clients ClientId
     */
    private void sendEmailVerificationCode(final String clientId) {

        // Check Internet connection
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            if (InternetConnectivity.isConnectionFast(mContext)) {
                // Connected

                // Hide verification, Cannot Connect And send code layouts
                showLogoLayout(false);
                showVerificationLayout(false);
                showSendCodeLayout(false);
                showConnectionLayout(false);

                // Show ProgressDialog
                showProgressDialog(
                        DataUtils.getStringResource(
                                mContext,
                                R.string.title_mailing_email_verification_code),
                        DataUtils.getStringResource(
                                mContext,
                                R.string.msg_mailing_email_verification_code)
                );

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        NetworkUtils.URL_SEND_EMAIL_VERIFICATION_CODE,
                        response -> {

                            // Log response
                            Log.d(TAG, "Send email verification code response: "
                                    + response);

                            // Hide ProgressDialog
                            ViewsUtils.dismissProgressDialog(progressDialog);

                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                                // Check for error in Json
                                if (!error) {

                                    JSONObject emailVerification = jsonObject.getJSONObject(
                                            VolleyUtils.KEY_EMAIL_VERIFICATION);

                                    // Get verification code and success message
                                    String strVerificationCode = emailVerification.getString(
                                            AccountUtils.FIELD_VERIFICATION_CODE).trim();

                                    // Check for verification code
                                    if (!DataUtils.isEmptyString(strVerificationCode)) {

                                        isEmailSent = true; // Update email Sent Status

                                        // Show verification layout
                                        showVerificationLayout(true);

                                        // Set Verify code message
                                        textVerifyCodeMessage.setText(
                                                DataUtils.getStringResource(
                                                        mContext,
                                                        R.string.msg_email_verification_message)
                                        );

                                        // Clear verification code EditText in case user clicked
                                        // resend on enter code page
                                        editVerificationCode.setText(null);

                                        // Show resend code disabled layout
                                        textResendCodeDisabled.setVisibility(View.VISIBLE);

                                        // Start resend code timer
                                        countDownResendCode.start();

                                        // Toast message
                                        CustomToast.infoMessage(mContext,
                                                DataUtils.getStringResource(
                                                        mContext,
                                                        R.string.msg_verification_code_sent),
                                                true, 0);
                                    }
                                } else {

                                    // Show send code layout
                                    showSendCodeLayout(true);

                                    // Get the error message
                                    String errorMsg =
                                            jsonObject.getString(VolleyUtils.KEY_ERROR_MESSAGE);

                                    // Toast Error message
                                    CustomToast.errorMessage(mContext, errorMsg, 0);
                                }
                            } catch (JSONException ig) {
                                ig.printStackTrace();
                            }
                        }, volleyError -> {

                    // Log response
                    Log.e(TAG, "Send email verification code Error: "
                            + volleyError.getMessage());

                    // Hide ProgressDialog
                    ViewsUtils.dismissProgressDialog(progressDialog);

                    // networkErrorMessage, serverErrorMessage, authFailureErrorMessage,
                    // parseErrorMessage, noConnectionErrorMessage, timeoutErrorMessage;
                    if (volleyError.getMessage() == null
                            || volleyError instanceof NetworkError
                            || volleyError instanceof ServerError
                            || volleyError instanceof AuthFailureError
                            || volleyError instanceof TimeoutError) {

                        // Show no connection layout
                        showConnectionLayout(true);

                    } else {

                        // Show send code layout
                        showSendCodeLayout(true);
                    }

                    CustomToast.errorMessage(mContext, DataUtils.getStringResource(mContext,
                            R.string.error_network_connection_error_message_short),
                            R.drawable.ic_sad_cloud_100px_white);

                    // Cancel pending request
                    ApplicationClass
                            .getClassInstance().cancelPendingRequests(
                            NetworkUtils.TAG_SEND_EMAIL_VERIFICATION_STRING_REQUEST
                    );
                }
                ) {
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<>();

                        params.put(AccountUtils.FIELD_CLIENT_ID, clientId);
                        params.put(AccountUtils.FIELD_VERIFICATION_TYPE,
                                AccountUtils.KEY_VERIFICATION_TYPE_EMAIL_ACCOUNT);

                        return params;
                    }
                };

                // Set retry policy
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        DataUtils.getIntegerResource(mContext,
                                R.integer.int_volley_account_request_initial_timeout_ms),
                        DataUtils.getIntegerResource(mContext,
                                R.integer.int_volley_account_request_max_timeout_retry),
                        1.0f));

                stringRequest.setShouldCache(false); // Disabling caching

                // Set request priority
                ApplicationClass.getClassInstance().setPriority(Request.Priority.HIGH);

                // Adding request to request queue
                ApplicationClass.getClassInstance().addToRequestQueue(stringRequest,
                        NetworkUtils.TAG_SEND_EMAIL_VERIFICATION_STRING_REQUEST);
            }
        } else {
            // Not Connected
            showConnectionLayout(true); // Show no connection layout
        }
    }

    /**
     * Function to verify clients email address
     *
     * @param verificationCode - Verification code sent on mail
     */
    public void verifyEmailAddress(final String verificationCode) {

        // Check Internet connection
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            if (InternetConnectivity.isConnectionFast(mContext)) {
                // Connected

                showLogoLayout(false); // Hide logo layout
                showVerificationLayout(false); // Show verification layout
                showSendCodeLayout(false); // Hide send code layout
                showConnectionLayout(false); // Hide no connection layout

                // Show ProgressDialog
                showProgressDialog(
                        DataUtils.getStringResource(
                                mContext,
                                R.string.title_verifying_email_address),
                        DataUtils.getStringResource(
                                mContext,
                                R.string.msg_verifying_email_address)
                );

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        NetworkUtils.URL_VERIFY_EMAIL_ADDRESS,
                        response -> {

                            // log response
                            Log.d(TAG, "Email verification Response: " + response);

                            // Hide ProgressDialog
                            ViewsUtils.dismissProgressDialog(progressDialog);

                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                                // Check for error node in json
                                if (!error) {

                                    // Get success message
                                    String strSuccessMessage = jsonObject.getString(
                                            VolleyUtils.KEY_SUCCESS_MESSAGE);

                                    // Check if message was received
                                    if (!DataUtils.isEmptyString(strSuccessMessage)) {

                                        countDownResendCode.cancel(); // Stop resend code counter
                                        editVerificationCode.setText(null); // Clear Current code Input

                                        // Show email verified success message
                                        CustomToast.infoMessage(
                                                mContext,
                                                DataUtils.getStringResource(mContext,
                                                        R.string.msg_email_adress_verified),
                                                false,
                                                R.drawable.ic_baseline_email_24_white);

                                        finish(); // Exit activity
                                    }
                                } else {

                                    // Show verification layout
                                    showVerificationLayout(true);

                                    // Get the error message
                                    String errorMsg =
                                            jsonObject.getString(VolleyUtils.KEY_ERROR_MESSAGE);

                                    // Toast Error message
                                    CustomToast.errorMessage(mContext, errorMsg, 0);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, volleyError -> {

                    // Log response
                    Log.e(TAG, "Email verification Error: " + volleyError.getMessage());

                    // Hide ProgressDialog
                    ViewsUtils.dismissProgressDialog(progressDialog);

                    // Clear Current code input
                    editVerificationCode.setText(null);

                    // networkErrorMessage, serverErrorMessage, authFailureErrorMessage,
                    // parseErrorMessage, noConnectionErrorMessage, timeoutErrorMessage;
                    if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                            || volleyError instanceof ServerError
                            || volleyError instanceof AuthFailureError
                            || volleyError instanceof TimeoutError
                    ) {

                        // Show no connection layout
                        showConnectionLayout(true);

                    } else {

                        // Show verification layout
                        showVerificationLayout(true);
                    }

                    CustomToast.errorMessage(mContext,
                            DataUtils.getStringResource(
                                    mContext,
                                    R.string.error_network_connection_error_message_short),
                            R.drawable.ic_sad_cloud_100px_white);
                }
                ) {
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<>();

                        // Put Client Id
                        params.put(AccountUtils.FIELD_CLIENT_ID,
                                database.getClientAccountInfo().get(0).getClientId());

                        // Put verification code
                        params.put(AccountUtils.FIELD_VERIFICATION_CODE, verificationCode);

                        // Put verification type
                        params.put(
                                AccountUtils.FIELD_VERIFICATION_TYPE,
                                AccountUtils.KEY_VERIFICATION_TYPE_EMAIL_ACCOUNT
                        );

                        return params;
                    }
                };

                // Set retry policy
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        DataUtils.getIntegerResource(mContext,
                                R.integer.int_volley_account_request_initial_timeout_ms),
                        DataUtils.getIntegerResource(mContext,
                                R.integer.int_volley_account_request_max_timeout_retry),
                        1.0f));

                // Set Request Caching To False
                stringRequest.setShouldCache(true);

                // Set Request Priority
                ApplicationClass.getClassInstance().setPriority(Request.Priority.HIGH);

                // Adding Request to request queue
                ApplicationClass.getClassInstance().addToRequestQueue(stringRequest,
                        NetworkUtils.TAG_VERIFY_EMAIL_STRING_REQUEST);

            }
        } else {
            // Not Connected

            // Show no connection layout
            showConnectionLayout(true);
        }
    }

    /**
     * Function to show send code layout
     *
     * @param status - boolean - (Show / Hide layout)
     */
    private void showSendCodeLayout(final boolean status) {

        if (status) {
            // True

            // Show Main In layout
            llSendVerificationLayout.setVisibility(View.VISIBLE);

            // Update Last visible layout
            lastActiveLayout = 0;

            // Update Logo
            imageLogo.setImageResource(R.drawable.ic_baseline_email_24_white);

            // Show Logo layout
            showLogoLayout(true);

        } else {
            // False

            // Hide Main In layout
            llSendVerificationLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Function to show logo layout
     *
     * @param status - boolean - (Show / Hide layout)
     */
    private void showLogoLayout(final boolean status) {

        if (status) {
            // True

            // Show layout
            llLogoLayout.setVisibility(View.VISIBLE);

        } else {
            // False

            // Hide layout
            llLogoLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Function to show verification layout
     *
     * @param status - boolean - (Show / Hide layout)
     */
    private void showVerificationLayout(final boolean status) {

        if (status) {
            // True

            // Show layout
            llVerificationLayout.setVisibility(View.VISIBLE);

            // Update Last visible layout
            lastActiveLayout = 1;

            // Update Logo
            imageLogo.setImageResource(R.drawable.ic_unverified_96px_white);

            // Show Logo layout
            showLogoLayout(true);

        } else {
            // False

            // Hide layout
            llVerificationLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Function to show no connection layout
     *
     * @param status - boolean - (Show / Hide layout)
     */
    private void showConnectionLayout(final boolean status) {

        if (status) {

            // Show Cannot Connect to the internet layout
            llCannotConnect.setVisibility(View.VISIBLE);

            // Update Last visible layout
            lastActiveLayout = 2;

            // Hide Other layouts
            showLogoLayout(false);
            showSendCodeLayout(false);
            showVerificationLayout(false);

        } else {

            // Show Cannot Connect to the internet layout
            llCannotConnect.setVisibility(View.GONE);
        }
    }

    /**
     * Function to show progress dialog
     *
     * @param dialogTitle   - Dialog title
     * @param dialogMessage - Dialog message
     */
    private void showProgressDialog(String dialogTitle, String dialogMessage) {

        // Check if progress dialog is showing
        if (!progressDialog.isShowing()) {

            // Set progress dialog title
            progressDialog.setTitle(dialogTitle);

            // Set progress dialog message
            progressDialog.setMessage(dialogMessage);

            progressDialog.show(); // Show progress dialog
        }
    }
}
