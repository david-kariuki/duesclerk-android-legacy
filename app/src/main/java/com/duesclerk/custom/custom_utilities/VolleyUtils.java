package com.duesclerk.custom.custom_utilities;

import android.content.Context;

import com.duesclerk.R;

public class VolleyUtils {

    // General

    public static final String KEY_ERROR                = "Error";
    public static final String KEY_ERROR_MESSAGE        = "ErrorMessage";
    public static final String KEY_SUCCESS_MESSAGE      = "SuccessMessage";

    // User Account
    public static final String KEY_USER                 = "User";
    public static final String KEY_UPDATE_PROFILE       = "UpdateProfile";

    // SignIn and SignUp
    public static final String KEY_SIGNIN               = "SignIn";
    public static final String KEY_SIGNUP               = "SignUp";

    // Email Verification
    public static final String KEY_SEND_VERIFICATION_CODE   = "SendVerificationCode";
    public static final String KEY_EMAIL_VERIFICATION       = "EmailVerification";
    public static final String KEY_PASSWORD_RESET           = "PasswordReset";

    public static final String KEY_UPDATE_CONTACT       = "UpdateContact";

    public static String getApiKey(Context context){
        return "API_" + DataUtils.getStringResource(context, R.string.app_name) + "php_2020";
    }
}
