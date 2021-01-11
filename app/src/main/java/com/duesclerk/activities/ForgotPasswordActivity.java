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
import com.duesclerk.custom.custom_utilities.ApplicationClass;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.InputFiltersUtils;
import com.duesclerk.custom.custom_utilities.UserAccountUtils;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_utilities.VolleyUtils;
import com.duesclerk.custom.custom_views.toast.CustomToast;
import com.duesclerk.custom.network.InternetConnectivity;
import com.duesclerk.custom.network.NetworkTags;
import com.duesclerk.custom.network.NetworkUrls;
import com.duesclerk.custom.storage_adapters.UserDatabase;
import com.google.android.material.textfield.TextInputEditText;
import com.jkb.vcedittext.VerificationCodeEditText;
import com.kofigyan.stateprogressbar.StateProgressBar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.kofigyan.stateprogressbar.StateProgressBar.StateNumber.FOUR;
import static com.kofigyan.stateprogressbar.StateProgressBar.StateNumber.ONE;
import static com.kofigyan.stateprogressbar.StateProgressBar.StateNumber.THREE;
import static com.kofigyan.stateprogressbar.StateProgressBar.StateNumber.TWO;

public class ForgotPasswordActivity extends AppCompatActivity {

    // private static final String TAG = ForgotPasswordActivity.class.getSimpleName();
    private static final int LAYOUT_ENTER_EMAIL = 0, LAYOUT_ENTER_VERIFICATION_CODE = 1, LAYOUT_ENTER_PASSWORD = 2, LAYOUT_SUCCESS = 3;
    private final String KEY_LAYOUT_NUMBER = "LAYOUT_NUMBER";
    private final String[] statesDescriptions = {"Email", "Verify", "Reset", "Success"};
    private Context mContext;
    private LinearLayout llEnterEmailAddress, llEnterVerificationCode, llEnterPassword, llSuccessLayout;
    private LinearLayout llLogoLayout;
    private TextInputEditText editEmailAddress, editNewPassword, editConfirmNewPassword;
    private VerificationCodeEditText editVerificationCode;
    private TextView textTitle, textCounterVerificationCode, textCounterNewPassword,
            textCounterConfirmNewPassword;
    private String strEmailAddress, strVerificationCode;
    private int lastVisibleLayoutNumber = 0;
    private ImageView imageLogo, imagePasswordToggleNewPassword,
            imagePasswordToggleConfirmNewPassword;
    private ProgressDialog progressDialog;
    private StateProgressBar stateProgressBar;
    private TextView textResendCodeEnabled, textResendCodeDisabled, textSuccessMessage;
    private boolean isResendCountFinished = true;
    private CountDownTimer countDownResendCode;
    private UserDatabase database;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mContext = getApplicationContext(); // Get context

        // LinearLayouts
        llEnterEmailAddress = findViewById(R.id.llForgotPassword_EnterEmail_Layout);
        llEnterVerificationCode = findViewById(
                R.id.llForgotPassword_EnterVerificationCode_Layout);
        llEnterPassword = findViewById(R.id.llForgotPassword_EnterPassword_Layout);
        llSuccessLayout = findViewById(R.id.llForgotPassword_PasswordResetSuccess_Layout);
        LinearLayout llSignIn = findViewById(R.id.llForgotPassword_SignIn);
        LinearLayout llSendCode = findViewById(R.id.llForgotPassword_SendCode);
        LinearLayout llVerifyCode = findViewById(R.id.llForgotPassword_VerifyCode);
        LinearLayout llResetPassword = findViewById(R.id.llForgotPassword_ResetPassword);
        LinearLayout llMainLayout = findViewById(R.id.llForgotPassword_MainLayout);
        llLogoLayout = findViewById(R.id.llForgotPassword_LogoLayout);

        // TextViews
        textTitle = findViewById(R.id.textForgotPassword_Title);
        textCounterVerificationCode = findViewById(
                R.id.textForgotPassword_CounterVerificationCode);
        textCounterNewPassword = findViewById(R.id.textForgotPassword_CounterNewPassword);
        textCounterConfirmNewPassword = findViewById(
                R.id.textForgotPassword_CounterConfirmNewPassword);
        TextView textAlreadyHaveACode = findViewById(R.id.textForgotPassword_AlreadyHaveCode);
        textResendCodeEnabled = findViewById(R.id.textForgotPassword_ResendCode_Enabled);
        textResendCodeDisabled = findViewById(R.id.textForgotPassword_ResendCode_Disabled);
        textSuccessMessage = findViewById(R.id.textForgotPassword_SuccessMessage);

        // EditTexts
        editEmailAddress = findViewById(R.id.editForgotPassword_EmailAddress);
        editVerificationCode = findViewById(R.id.editForgotPassword_VerificationCode);
        editNewPassword = findViewById(R.id.editForgotPassword_NewPassword);
        editConfirmNewPassword = findViewById(R.id.editForgotPassword_ConfirmNewPassword);

        // ImageViews
        imageLogo = findViewById(R.id.imageEmailVerification_Logo);
        imagePasswordToggleNewPassword = findViewById(R.id.imageForgotPassword_NewPasswordToggle);
        imagePasswordToggleConfirmNewPassword = findViewById(
                R.id.imageForgotPassword_ConfirmNewPasswordToggle);

        // ScrollView
        scrollView = findViewById(R.id.scrollViewForgotPasswordActivity);

        // StateProgressBar
        stateProgressBar = findViewById(R.id.stateProgressBarForgotPassword);
        stateProgressBar.setStateDescriptionData(statesDescriptions);

        // Initialize ProgressDialog
        progressDialog = ViewsUtils.initProgressDialog(ForgotPasswordActivity.this,
                false
        );

        database = new UserDatabase(mContext); // Initialize database object

        // Check if resend count has completed
        if (isResendCountFinished) {

            textResendCodeEnabled.setVisibility(View.VISIBLE); // Show resend code enabled text
            textResendCodeDisabled.setVisibility(View.GONE); // Hide resend code disabled text

        } else {

            textResendCodeDisabled.setVisibility(View.VISIBLE); // Show resend code disabled text
            textResendCodeEnabled.setVisibility(View.GONE); // Hide resend code enabled text
        }

        // Underline TextViews
        textAlreadyHaveACode.setPaintFlags(textAlreadyHaveACode.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);
        textResendCodeEnabled.setPaintFlags(textResendCodeEnabled.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);

        // resend code countdown
        countDownResendCode = new CountDownTimer(300000, 1000) {
            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {

                String message, timeString;

                // Set message
                message = getResources().getString(R.string.action_resend_code);

                timeString = "\nafter " + String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                // Replace strings
                try {

                    timeString = timeString.replace(", 0 sec", "");
                    timeString = timeString.replace("0 min,", "");
                    timeString = timeString.replace("after 0 min", "");

                } catch (Exception ignored) {
                }

                // Set counter text
                message += timeString; // Concatenate message to time string
                textResendCodeDisabled.setText(message); // Set resend message

                // Update resend count finish status
                isResendCountFinished = false;
            }

            public void onFinish() {

                // Set resend code message
                textResendCodeDisabled.setText(DataUtils.getStringResource(mContext,
                        R.string.action_resend_code)
                );

                // Hide disabled layout
                textResendCodeDisabled.setVisibility(View.GONE);

                // Show enabled layout
                textResendCodeEnabled.setVisibility(View.VISIBLE);

                // Update resend Count Finish Status
                isResendCountFinished = true;
            }

        };

        // Verification code on text change listener
        editVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Get verification code length
                int codeLength = Objects.requireNonNull(editVerificationCode.getText())
                        .toString().length();

                // Set verification code length to counter
                textCounterVerificationCode.setText(String.valueOf(codeLength));

                // Check code length
                if (codeLength == InputFiltersUtils.LENGTH_VERIFICATION_CODE) {

                    ViewsUtils.hideKeyboard(ForgotPasswordActivity.this); // Hide keyboard
                }
            }
        });

        // New password onTextChangeListener
        editNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (Objects.requireNonNull(editNewPassword.getText()).toString().length() > 0) {

                    // Show toggle password icon and password length counter
                    imagePasswordToggleNewPassword.setVisibility(View.VISIBLE); // Show toggle icon
                    textCounterNewPassword.setVisibility(View.VISIBLE); // Show counter

                    // Get field length
                    int passwordLength = Objects.requireNonNull(editNewPassword.getText()).length();

                    // Set password length
                    textCounterNewPassword.setText(String.valueOf(passwordLength));

                } else {

                    // Hide toggle password icon and password length counter
                    imagePasswordToggleNewPassword.setVisibility(View.INVISIBLE); // Hide toggle icon
                    textCounterNewPassword.setVisibility(View.INVISIBLE); // Show counter
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // New password onTextChangeListener
        editConfirmNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (Objects.requireNonNull(editConfirmNewPassword.getText()).toString().length() > 0) {

                    // Show toggle password icon and password length counter
                    imagePasswordToggleConfirmNewPassword.setVisibility(View.VISIBLE); // Show toggle icon
                    textCounterConfirmNewPassword.setVisibility(View.VISIBLE); // Show counter

                    // Get field length
                    int passwordLength =
                            Objects.requireNonNull(editConfirmNewPassword.getText()).length();

                    // Set password length
                    textCounterConfirmNewPassword.setText(String.valueOf(passwordLength));

                } else {

                    // Hide toggle password icon and password length counter
                    imagePasswordToggleConfirmNewPassword.setVisibility(View.INVISIBLE); // Hide toggle icon
                    textCounterConfirmNewPassword.setVisibility(View.INVISIBLE); // Show counter
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Enter code onClick
        textAlreadyHaveACode.setOnClickListener(v -> {

            // Validate email address
            if (validateEmailAddressValue()) {

                // Switch To layout enter verification code
                switchLayout(LAYOUT_ENTER_VERIFICATION_CODE);
            }
        });

        // Password toggle onClick
        imagePasswordToggleNewPassword.setOnClickListener(view13 -> {
            // Toggle password visibility
            ViewsUtils.togglePasswordField(editNewPassword, imagePasswordToggleNewPassword);
        });

        // Password toggle onClick
        imagePasswordToggleConfirmNewPassword.setOnClickListener(view13 -> {
            // Toggle password visibility
            ViewsUtils.togglePasswordField(editConfirmNewPassword,
                    imagePasswordToggleConfirmNewPassword);
        });

        // Enter code onClick
        textResendCodeEnabled.setOnClickListener(v ->
                sendEmailVerificationCode(strEmailAddress)); // Generate and send new code

        // EditText onClick
        editVerificationCode.setOnClickListener(v -> editVerificationCode.requestFocus());

        // Main layout OnClick
        llMainLayout.setOnClickListener(v -> ViewsUtils.hideKeyboard(
                ForgotPasswordActivity.this)); // Hide keyboard

        // Logo layout layout OnClick
        llLogoLayout.setOnClickListener(v -> ViewsUtils.hideKeyboard(
                ForgotPasswordActivity.this)); // Hide keyboard

        // Enter email layout OnClick
        llEnterEmailAddress.setOnClickListener(v -> ViewsUtils.hideKeyboard(
                ForgotPasswordActivity.this)); // Hide keyboard

        // Enter verification code layout OnClick
//        llEnterVerificationCode.setOnClickListener(v -> ViewsUtils.hideKeyboard(
//                ForgotPasswordActivity.this)); // Hide keyboard

        // Enter password layout OnClick
        llEnterPassword.setOnClickListener(v -> ViewsUtils.hideKeyboard(
                ForgotPasswordActivity.this)); // Hide keyboard

        // Send code LinearLayout onClick
        llSendCode.setOnClickListener(v -> {

            // Validate email address field for verification code Generation
            if (validateEmailAddressValue()) {

                sendEmailVerificationCode(strEmailAddress); // Generate and send new code
            }
        });

        // Verify email code
        llVerifyCode.setOnClickListener(v -> {

            // Validate verification code value
            if (validateVerificationCodeValue()) {

                // Verify email Address
                verifyEmailAddress(strVerificationCode, strEmailAddress);
            }
        });

        // Create password code
        llResetPassword.setOnClickListener(v -> {

            // Validate password field values
            if (validatePasswordFieldsValues()) {

                // Reset password
                resetPassword(strEmailAddress,
                        Objects.requireNonNull(editNewPassword.getText()).toString());
            }
        });

        // SignIn OnClick
        llSignIn.setOnClickListener(v -> {

            // Start SignIn and SignUp activity
            startActivity(new Intent(
                    ForgotPasswordActivity.this, SignInSignupActivity.class));

            finish(); // Exit Activity
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        switchLayout(lastVisibleLayoutNumber); // Set current layout
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Check if layout is enter email
        if (lastVisibleLayoutNumber == LAYOUT_ENTER_EMAIL) {

            // Check for value on email address field
            if (!DataUtils.isEmptyEditText(editEmailAddress)) {

                // Put string
                outState.putString(UserAccountUtils.FIELD_EMAIL_ADDRESS,
                        Objects.requireNonNull(editEmailAddress.getText()).toString());
            }
        }

        // Check if layout is enter verification code
        if (lastVisibleLayoutNumber == LAYOUT_ENTER_VERIFICATION_CODE) {

            // Check for value on verification code field
            if (!DataUtils.isEmptyEditText(editVerificationCode)) {

                // Put String
                outState.putString(UserAccountUtils.FIELD_VERIFICATION_CODE,
                        Objects.requireNonNull(editVerificationCode.getText()).toString());
            }
        }

        // Check if layout is enter password
        if (lastVisibleLayoutNumber == LAYOUT_ENTER_PASSWORD) {

            // Check for value on password field
            if (!DataUtils.isEmptyEditText(editNewPassword)) {

                // Put string
                outState.putString(UserAccountUtils.FIELD_NEW_PASSWORD,
                        Objects.requireNonNull(editNewPassword.getText()).toString());
            }

            // Check for value on confirm password address field
            if (!DataUtils.isEmptyEditText(editConfirmNewPassword)) {

                // Put String
                outState.putString(UserAccountUtils.FIELD_CONFIRM_NEW_PASSWORD,
                        Objects.requireNonNull(editConfirmNewPassword.getText()).toString());
            }
        }

        outState.putInt(KEY_LAYOUT_NUMBER, lastVisibleLayoutNumber); // Put layout Number

        // Put Str email Address
        outState.putString(UserAccountUtils.FIELD_EMAIL_ADDRESS, strEmailAddress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Check if layout Is enter Email
        if (lastVisibleLayoutNumber == LAYOUT_ENTER_EMAIL) {

            if (savedInstanceState.getString(UserAccountUtils.FIELD_EMAIL_ADDRESS) != null) {

                // Set email Address
                editEmailAddress.setText(
                        savedInstanceState.getString(UserAccountUtils.FIELD_EMAIL_ADDRESS));

                // Update email Address
                strEmailAddress = savedInstanceState.getString(UserAccountUtils.FIELD_EMAIL_ADDRESS);

            } else {

                // Set email Address
                editEmailAddress.setText(
                        savedInstanceState.getString(UserAccountUtils.FIELD_EMAIL_ADDRESS));
            }
        }

        // Check if layout Is enter verification code
        if (lastVisibleLayoutNumber == LAYOUT_ENTER_VERIFICATION_CODE) {

            // Set verification code
            editVerificationCode.setText(
                    savedInstanceState.getString(UserAccountUtils.FIELD_VERIFICATION_CODE));
        }

        // Check if layout Is enter password
        if (lastVisibleLayoutNumber == LAYOUT_ENTER_PASSWORD) {

            // Set new password
            editNewPassword.setText(
                    savedInstanceState.getString(UserAccountUtils.FIELD_NEW_PASSWORD));

            // Set confirm new password
            editConfirmNewPassword.setText(
                    savedInstanceState.getString(UserAccountUtils.FIELD_CONFIRM_NEW_PASSWORD));
        }

        // Update last visible layout
        lastVisibleLayoutNumber = savedInstanceState.getInt(KEY_LAYOUT_NUMBER);

        switchLayout(lastVisibleLayoutNumber); // Set layout
    }

    /**
     * Function to switch visible layout
     *
     * @param layoutNumber - Layout number
     */
    private void switchLayout(int layoutNumber) {

        ViewsUtils.hideKeyboard(ForgotPasswordActivity.this); // Hide keyboard
        ViewsUtils.scrollUpScrollView(scrollView); // Scroll up ScrollView

        switch (layoutNumber) {

            case LAYOUT_ENTER_EMAIL:

                showEnterEmailLayout(true); // Show enter email layout

                // Hide other layouts
                showEnterVerificationCodeLayout(false);
                showEnterPasswordLayout(false);
                showSuccessLayout(false);
                break;

            case LAYOUT_ENTER_VERIFICATION_CODE:

                showEnterVerificationCodeLayout(true); // Show enter verification code layout

                // Hide other layouts
                showEnterEmailLayout(false);
                showEnterPasswordLayout(false);
                showSuccessLayout(false);
                break;

            case LAYOUT_ENTER_PASSWORD:

                showEnterPasswordLayout(true); // Show enter password layout

                // Hide other layouts
                showEnterVerificationCodeLayout(false);
                showEnterEmailLayout(false);
                showSuccessLayout(false);
                break;

            case LAYOUT_SUCCESS:

                showSuccessLayout(true); // Show password reset successfully layout

                // Hide other layouts
                showEnterEmailLayout(false);
                showEnterVerificationCodeLayout(false);
                showEnterPasswordLayout(false);
                break;

            default:
                break;
        }
    }

    /**
     * Function to show enter email address layout
     *
     * @param status - boolean - (Show / hide layout)
     */
    private void showEnterEmailLayout(boolean status) {

        if (status) {

            // Update title
            textTitle.setText(DataUtils.getStringResource(
                    mContext, R.string.label_forgot_password)
            );

            llEnterEmailAddress.setVisibility(View.VISIBLE); // Show layout
            showLogoLayout(true); // Show logo layout
            imageLogo.setImageResource(R.drawable.ic_baseline_email_24_white); // Update logo
            setStateNumber(ONE); // Set state number
            lastVisibleLayoutNumber = LAYOUT_ENTER_EMAIL; // Update last visible layout

        } else {

            llEnterEmailAddress.setVisibility(View.GONE); // Hide layout
        }
    }

    /**
     * Function to show enter verification code layout
     *
     * @param status - boolean - (Show / hide layout)
     */
    private void showEnterVerificationCodeLayout(boolean status) {

        if (status) {

            // Update title
            textTitle.setText(DataUtils.getStringResource(mContext,
                    R.string.label_verification));

            llEnterVerificationCode.setVisibility(View.VISIBLE); // Show layout
            showLogoLayout(true); // Show logo layout
            imageLogo.setImageResource(R.drawable.ic_unverified_100px_white); // Update logo
            setStateNumber(TWO); // Set state number
            lastVisibleLayoutNumber = LAYOUT_ENTER_VERIFICATION_CODE; // Update last visible layout

        } else {

            llEnterVerificationCode.setVisibility(View.GONE); // Hide layout
        }
    }

    /**
     * Function to show enter password layout
     *
     * @param status - boolean - (Show / hide layout)
     */
    private void showEnterPasswordLayout(boolean status) {

        if (status) {

            // Update title
            textTitle.setText(DataUtils.getStringResource(mContext,
                    R.string.label_create_password));

            llEnterPassword.setVisibility(View.VISIBLE); // Show layout
            showLogoLayout(true); // Show logo layout

            imageLogo.setImageResource(R.drawable.ic_key_100px_white); // Update logo
            setStateNumber(THREE); // Set state number
            lastVisibleLayoutNumber = LAYOUT_ENTER_PASSWORD; // Update last visible layout

        } else {

            llEnterPassword.setVisibility(View.GONE); // Hide layout
        }
    }

    /**
     * Function to show success layout
     *
     * @param status - boolean - (Show / hide layout)
     */
    private void showSuccessLayout(boolean status) {

        if (status) {

            // Update title
            textTitle.setText(DataUtils.getStringResource(mContext,
                    R.string.msg_password_reset_successfully)
            );

            llSuccessLayout.setVisibility(View.VISIBLE); // Show layout
            showLogoLayout(true); // Show logo layout
            imageLogo.setImageResource(R.drawable.ic_baseline_check_24_white); // Update logo
            setStateNumber(FOUR); // Set state number
            stateProgressBar.setAllStatesCompleted(true); // Set all states completed
            lastVisibleLayoutNumber = LAYOUT_SUCCESS; // Update last visible layout

        } else {

            llSuccessLayout.setVisibility(View.GONE); // Hide layout
        }
    }

    /**
     * Function to show logo layout
     *
     * @param status - boolean - (Show / hide layout)
     */
    private void showLogoLayout(final boolean status) {

        if (status) {

            llLogoLayout.setVisibility(View.VISIBLE); // Show layout

        } else {

            llLogoLayout.setVisibility(View.GONE); // Hide layout
        }
    }

    /**
     * Function to set StateProgressBar state number
     *
     * @param stateNumber - StateNumber
     */
    private void setStateNumber(StateProgressBar.StateNumber stateNumber) {

        stateProgressBar.setCurrentStateNumber(stateNumber); // Set state number
    }

    /**
     * Function to validate email address
     */
    private boolean validateEmailAddressValue() {

        ViewsUtils.hideKeyboard(ForgotPasswordActivity.this); // Hide keyboard

        // Get email address field value
        strEmailAddress = Objects.requireNonNull(editEmailAddress.getText()).toString();

        // Check email address validity
        return InputFiltersUtils.checkEmailAddressValidNotify(
                mContext, editEmailAddress); // Return true if email address is valid
    }

    /**
     * Function to validate verification code
     */
    private boolean validateVerificationCodeValue() {

        ViewsUtils.hideKeyboard(ForgotPasswordActivity.this); // Hide KeyBoard

        // Get verification code field value
        strVerificationCode = Objects.requireNonNull(editVerificationCode.getText()).toString();

        // Return true if verification code length is acceptable
        return InputFiltersUtils.checkVerificationLengthNotify(mContext, editVerificationCode);
    }

    /**
     * Function to check password fields lengths and values and notify with toast if error
     * Checks password length
     * Checks if new password is equal to current and warns
     */
    private boolean validatePasswordFieldsValues() {

        return (InputFiltersUtils.checkPasswordLengthNotify(mContext,
                editNewPassword)
                && InputFiltersUtils.checkPasswordLengthNotify(mContext, editConfirmNewPassword)
                && InputFiltersUtils.compareNewPasswords(mContext, editNewPassword,
                editConfirmNewPassword)
        );
    }

    /**
     * Function to send email verification code
     *
     * @param emailAddress - Users email address
     */
    private void sendEmailVerificationCode(final String emailAddress) {

        // Check for email address
        if (!DataUtils.isEmptyString(strEmailAddress)) {
            // Email address not null

            // Check Internet Connection
            if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
                    // Connected

                    // Check if resend code counter is done
                    if (isResendCountFinished) {
                        // counter done

                        // Hide verification, Cannot Connect And Send code Layouts
                        showLogoLayout(false);
                        showEnterEmailLayout(false);
                        showEnterVerificationCodeLayout(false);
                        showEnterPasswordLayout(false);

                        // Hide resend code layout Enables
                        textResendCodeEnabled.setVisibility(View.GONE);

                        // Show ProgressDialog
                        ViewsUtils.showProgressDialog(progressDialog,
                                DataUtils.getStringResource(
                                        mContext,
                                        R.string.title_mailing_email_verification_code),
                                DataUtils.getStringResource(
                                        mContext,
                                        R.string.msg_mailing_email_verification_code)
                        );

                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                NetworkUrls.UserURLS.URL_SEND_EMAIL_VERIFICATION_CODE,
                                response -> {

                                    // Log response
                                    // Log.d(TAG, "Send email verification code response: "
                                    //        + response);

                                    // Hide ProgressDialog
                                    ViewsUtils.dismissProgressDialog(progressDialog);

                                    try {

                                        JSONObject jsonObject = new JSONObject(response);
                                        boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                                        // Check For Error In Json
                                        if (!error) {

                                            JSONObject objectSendVerificationCode =
                                                    jsonObject.getJSONObject(
                                                    VolleyUtils.KEY_SEND_VERIFICATION_CODE);

                                            // Get verification code and success message
                                            strVerificationCode = objectSendVerificationCode
                                                    .getString(
                                                            UserAccountUtils.FIELD_VERIFICATION_CODE);

                                            // Check for verification code
                                            if (!DataUtils.isEmptyString(strVerificationCode)) {

                                                // Clear email EditText
                                                editEmailAddress.setText(null);

                                                // Clear verification code EditText in case user
                                                // Clicked resend On enter code screen
                                                editVerificationCode.setText(null);

                                                // Show verification layout
                                                switchLayout(LAYOUT_ENTER_VERIFICATION_CODE);

                                                // Show resend code Disabled layout
                                                textResendCodeDisabled.setVisibility(View.VISIBLE);

                                                // Set last used email address for resend code
                                                strEmailAddress = emailAddress;

                                                // Start resend code timer
                                                countDownResendCode.start();

                                                // Toast message
                                                CustomToast.infoMessage(mContext,
                                                        DataUtils.getStringResource(
                                                                mContext,
                                                                R.string.msg_verification_code_sent),
                                                        true, 0);
                                            } else {

                                                // Show enter email layout
                                                showEnterEmailLayout(true);
                                            }
                                        } else {

                                            // Show enter email layout
                                            showEnterEmailLayout(true);

                                            // Get the error message
                                            String errorMsg = jsonObject.getString(
                                                    VolleyUtils.KEY_ERROR_MESSAGE);

                                            // Toast Error message
                                            CustomToast.errorMessage(mContext, errorMsg, 0);
                                        }
                                    } catch (JSONException ignored) {
                                    }
                                }, volleyError -> {

                            // Log response
                            // Log.e(TAG, "Send email verification code Error: "
                            //        + volleyError.getMessage());

                            // Hide Progress Dialog
                            ViewsUtils.dismissProgressDialog(progressDialog);

                            // networkErrorMessage, serverErrorMessage, authFailureErrorMessage, parseErrorMessage, noConnectionErrorMessage, timeoutErrorMessage;
                            if (volleyError.getMessage() == null
                                    || volleyError instanceof NetworkError
                                    || volleyError instanceof ServerError
                                    || volleyError instanceof AuthFailureError
                                    || volleyError instanceof TimeoutError) {

                                // Show connection error message
                                CustomToast.errorMessage(mContext,
                                        DataUtils.getStringResource(mContext,
                                                R.string.error_network_connection_error_message_short),
                                        R.drawable.ic_sad_cloud_100px_white);

                            } else {

                                // Show error message
                                CustomToast.errorMessage(mContext,
                                        volleyError.getMessage(),
                                        R.drawable.ic_sad_cloud_100px_white);
                            }

                            // Show enter email layout
                            showEnterEmailLayout(true);

                            // Cancel pending request
                            ApplicationClass
                                    .getClassInstance()
                                    .cancelPendingRequests(
                                            NetworkTags.User.TAG_SEND_EMAIL_VERIFICATION_STRING_REQUEST
                                    );
                        }
                        ) {
                            @Override
                            protected Map<String, String> getParams() {

                                Map<String, String> params = new HashMap<>();

                                // Put params
                                params.put(UserAccountUtils.FIELD_EMAIL_ADDRESS, emailAddress);
                                params.put(UserAccountUtils.FIELD_VERIFICATION_TYPE,
                                        UserAccountUtils.KEY_VERIFICATION_TYPE_PASSWORD_RESET);
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
                                NetworkTags.User.TAG_SEND_EMAIL_VERIFICATION_STRING_REQUEST);
                    }
            } else {
                // Not Connected

                // Show connection error message
                CustomToast.errorMessage(mContext,
                        DataUtils.getStringResource(mContext,
                                R.string.error_network_connection_error_message_short),
                        R.drawable.ic_sad_cloud_100px_white);
            }
        } else {

            switchLayout(LAYOUT_ENTER_EMAIL); // Switch to layout enter email address
        }
    }

    /**
     * Function to verify users email address
     *
     * @param emailAddress     - Users email address
     * @param verificationCode - Verification code sent on mail
     */
    public void verifyEmailAddress(final String verificationCode, final String emailAddress) {

        // Check for email address value
        if (!DataUtils.isEmptyString(strEmailAddress)) {

            // Check Internet Connection
            if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
                    // Connected

                    showLogoLayout(false); // Hide logo layout
                    showEnterEmailLayout(false); // Hide enter email layout

                    // Hide enter verification code layout
                    showEnterVerificationCodeLayout(false);

                    showEnterPasswordLayout(false); // Hide enter password layout

                    // Show ProgressDialog
                    ViewsUtils.showProgressDialog(progressDialog,
                            DataUtils.getStringResource(
                                    mContext,
                                    R.string.title_verifying_email_address),
                            DataUtils.getStringResource(
                                    mContext,
                                    R.string.msg_verifying_email_address)
                    );

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            NetworkUrls.UserURLS.URL_VERIFY_EMAIL_ADDRESS,
                            response -> {

                                // Log response
                                // Log.d(TAG, "Email verification Response: " + response);

                                // Hide ProgressDialog
                                ViewsUtils.dismissProgressDialog(progressDialog);

                                try {

                                    // Create JSONObject
                                    JSONObject jsonObject = new JSONObject(response);

                                    // Get error from json
                                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                                    // Check for error
                                    if (!error) {

                                        // Get email verification object
                                        JSONObject objectEmailVerification = jsonObject.getJSONObject(
                                                VolleyUtils.KEY_EMAIL_VERIFICATION);

                                        // Get success message
                                        String successMessage = objectEmailVerification.getString(
                                                VolleyUtils.KEY_SUCCESS_MESSAGE);

                                        // Check if message was received
                                        if (!DataUtils.isEmptyString(successMessage)) {

                                            // Stop resend code counter
                                            countDownResendCode.cancel();

                                            // Clear current code input
                                            editVerificationCode.setText(null);

                                            // Show email verified success message
                                            CustomToast.infoMessage(
                                                    mContext,
                                                    DataUtils.getStringResource(mContext,
                                                            R.string.msg_email_address_verified),
                                                    false,
                                                    R.drawable.ic_baseline_email_24_white);

                                            // Show enter password layout
                                            switchLayout(LAYOUT_ENTER_PASSWORD);
                                        }
                                    } else {

                                        // Show verification layout
                                        showEnterVerificationCodeLayout(true);

                                        // Get the error message
                                        String errorMsg = jsonObject.getString(VolleyUtils.KEY_ERROR_MESSAGE);

                                        // Toast Error message
                                        CustomToast.errorMessage(mContext, errorMsg, 0);
                                    }
                                } catch (JSONException ignored) {
                                }
                            }, volleyError -> {

                        // Log response
                        // Log.e(TAG, "Email verification code Error: " + volleyError.getMessage());

                        // Hide ProgressDialog
                        ViewsUtils.dismissProgressDialog(progressDialog);

                        // Clear current code input
                        editVerificationCode.setText(null);

                        // networkErrorMessage, serverErrorMessage, authFailureErrorMessage,
                        // parseErrorMessage, noConnectionErrorMessage, timeoutErrorMessage;
                        if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                                || volleyError instanceof ServerError
                                || volleyError instanceof AuthFailureError
                                || volleyError instanceof TimeoutError
                        ) {

                            // Toast connection error message
                            CustomToast.errorMessage(mContext,
                                    DataUtils.getStringResource(
                                            mContext,
                                            R.string.error_network_connection_error_message_short),
                                    R.drawable.ic_sad_cloud_100px_white);
                        } else {

                            // Show verification layout
                            showEnterVerificationCodeLayout(true);

                            // Toast connection error message
                            CustomToast.errorMessage(mContext,
                                    volleyError.getMessage(),
                                    R.drawable.ic_sad_cloud_100px_white);
                        }
                    }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {

                            Map<String, String> params = new HashMap<>(); // Params map

                            // Put verification code
                            params.put(UserAccountUtils.FIELD_VERIFICATION_CODE, verificationCode);

                            // Put email address
                            params.put(UserAccountUtils.FIELD_EMAIL_ADDRESS, emailAddress);

                            // Put verification type
                            params.put(
                                    UserAccountUtils.FIELD_VERIFICATION_TYPE,
                                    UserAccountUtils.KEY_VERIFICATION_TYPE_PASSWORD_RESET
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
                            NetworkTags.User.TAG_VERIFY_EMAIL_STRING_REQUEST);

            } else {
                    // Not Connected

                    // Toast connection error message
                    CustomToast.errorMessage(mContext,
                            DataUtils.getStringResource(
                                    mContext,
                                    R.string.error_network_connection_error_message_short),
                            R.drawable.ic_sad_cloud_100px_white);
                }
        }
    }

    /**
     * Function to generate email verification code
     *
     * @param emailAddress - Users email address
     * @param newPassword  - Users new password
     */
    public void resetPassword(final String emailAddress, final String newPassword) {

        // Check Internet Connection
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
                // Connected

                showLogoLayout(false); // Hide logo layout
                showEnterEmailLayout(false); // Hide enter email layout

                // Hide enter verification code layout
                showEnterVerificationCodeLayout(false);

                // Hide show password layout
                showEnterPasswordLayout(false);

                // Show ProgressDialog
                ViewsUtils.showProgressDialog(progressDialog,
                        DataUtils.getStringResource(
                                mContext,
                                R.string.title_resetting_password),
                        DataUtils.getStringResource(
                                mContext,
                                R.string.msg_resetting_password)
                );

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        NetworkUrls.UserURLS.URL_PASSWORD_RESET,
                        response -> {

                            // log response
                            // Log.d(TAG, "Password Reset Response: " + response);

                            // Hide ProgressDialog
                            ViewsUtils.dismissProgressDialog(progressDialog);

                            try {

                                // Create JSONObject
                                JSONObject jsonObject = new JSONObject(response);

                                // Get error from json
                                boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                                // Check for error node in json
                                if (!error) {

                                    JSONObject objectPasswordReset = jsonObject.getJSONObject(
                                            VolleyUtils.KEY_PASSWORD_RESET);

                                    String successMessage =
                                            objectPasswordReset.getString(
                                                    VolleyUtils.KEY_SUCCESS_MESSAGE);

                                    String userId = objectPasswordReset.getString(
                                            UserAccountUtils.FIELD_USER_ID);

                                    // Check if all parameters have been received
                                    if (!DataUtils.isEmptyString(successMessage)) {

                                        // Delete user from sqlite if exists
                                        database.deleteUserAccountInfoByUserId(userId);

                                        // Stop resend code counter
                                        countDownResendCode.cancel();

                                        // Clear password inputs
                                        editNewPassword.setText(null);
                                        editConfirmNewPassword.setText(null);

                                        // Toast success message
                                        CustomToast.infoMessage(mContext, successMessage,
                                                false,
                                                R.drawable.ic_baseline_check_24_white);

                                        // Set Success Message
                                        String message = DataUtils.getStringResource(mContext,
                                                R.string.msg_password_reset_successfully_long);
                                        textSuccessMessage.setText(message);

                                        // Show enter password layout
                                        switchLayout(LAYOUT_SUCCESS);
                                    }
                                } else {

                                    // Show verification layout
                                    showEnterPasswordLayout(true);

                                    // Get the error message
                                    String errorMsg = jsonObject.getString(VolleyUtils.KEY_ERROR_MESSAGE);

                                    // Toast Error message
                                    CustomToast.errorMessage(mContext, errorMsg, 0);
                                }
                            } catch (JSONException ignored) {
                            }
                        }, volleyError -> {

                    // Log response
                    // Log.e(TAG, "Password Response Error: " + volleyError.getMessage());

                    // Hide ProgressDialog
                    ViewsUtils.dismissProgressDialog(progressDialog);

                    editVerificationCode.setText(null); // Clear current code Input

                    // networkErrorMessage, serverErrorMessage, authFailureErrorMessage,
                    // parseErrorMessage, noConnectionErrorMessage, timeoutErrorMessage;
                    if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                            || volleyError instanceof ServerError
                            || volleyError instanceof AuthFailureError
                            || volleyError instanceof TimeoutError
                    ) {

                        // Toast connection error message
                        CustomToast.errorMessage(mContext,
                                DataUtils.getStringResource(
                                        mContext,
                                        R.string.error_network_connection_error_message_short),
                                R.drawable.ic_sad_cloud_100px_white);

                    } else {

                        // Show verification layout
                        showEnterPasswordLayout(true);

                        // Toast connection error message
                        CustomToast.errorMessage(mContext,
                                volleyError.getMessage(),
                                R.drawable.ic_sad_cloud_100px_white);
                    }

                    // Cancel Pending Request
                    ApplicationClass.getClassInstance()
                            .cancelPendingRequests(NetworkTags.User.TAG_PASSWORD_RESET_REQUEST);
                }
                ) {
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<>();

                        // Put email address and new password
                        params.put(UserAccountUtils.FIELD_EMAIL_ADDRESS, emailAddress);
                        params.put(UserAccountUtils.FIELD_NEW_PASSWORD, newPassword);

                        // Put verification type
                        params.put(
                                UserAccountUtils.FIELD_VERIFICATION_TYPE,
                                UserAccountUtils.KEY_VERIFICATION_TYPE_PASSWORD_RESET
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
                        NetworkTags.User.TAG_PASSWORD_RESET_REQUEST);

        } else {
            // Not Connected

            // Toast connection error message
            CustomToast.errorMessage(mContext,
                    DataUtils.getStringResource(
                            mContext,
                            R.string.error_network_connection_error_message_short),
                    R.drawable.ic_sad_cloud_100px_white);
        }
    }
}
