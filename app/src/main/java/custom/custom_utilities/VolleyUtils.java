package custom.custom_utilities;

import android.content.Context;

import com.duesclerk.R;

public class VolleyUtils {

    // General

    public static final String KEY_ERROR = "Error";
    public static final String KEY_ERROR_MESSAGE = "ErrorMessage";
    public static final String KEY_SUCCESS_MESSAGE = "SuccessMessage";

    // User Account
    public static final String KEY_CLIENT = "Client";

    // SignIN and SignUp
    public static final String KEY_SIGNIN = "SignIn";
    public static final String KEY_SIGNUP = "SignUp";

    // Email Verification
    public static final String KEY_VERIFICATION_CODE = "VerificationCode";
    public static final String KEY_VERIFY = "Verify";
    public static final String KEY_STATUS = "Status";
    public static final String KEY_GET_EMAIL_CODE = "GetEmailCode";
    public static final String KEY_PASSWORD_RESET = "PasswordReset";

    public static final String KEY_API_KEY = "ApiKey";

    public static String getApiKey(Context context){
        return "API_" + DataUtils.getStringResource(context, R.string.app_name) + "php_2020";
    }
}
