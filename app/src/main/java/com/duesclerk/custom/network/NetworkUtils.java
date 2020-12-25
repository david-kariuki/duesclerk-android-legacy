package com.duesclerk.custom.network;

public class NetworkUtils {

    // This class holds all network urls and network tags needed to handle network requests

    // Domain and Url constants
    private static final String PROTOCOL            = "https://";
    private static final String URL_SEPARATOR       = "/"; // Url separator
    private static final String WEBSITE_URL         = PROTOCOL + "www.duesclerk.com"
            + URL_SEPARATOR;
    private static final String FOLDER_ANDROID      = WEBSITE_URL + "android" + URL_SEPARATOR;
    private static final String FOLDER_APP          = FOLDER_ANDROID + "app" + URL_SEPARATOR;
    private static final String FOLDER_VENDOR_NAME  = FOLDER_APP + "duesclerk" + URL_SEPARATOR;
    private static final String FOLDER_CONTROLLERS  = FOLDER_VENDOR_NAME + "controllers"
            + URL_SEPARATOR;

    /**
     * Network urls
     */

    // URL to Signin user
    public static final String URL_SIGNIN_USER = FOLDER_CONTROLLERS + "signInUser.php";

    // URL to SignUp user
    public static final String URL_SIGNUP_USER = FOLDER_CONTROLLERS + "signUpUser.php";

    // URL to fetch user profile details
    public static final String URL_FETCH_USER_PROFILE_DETAILS = FOLDER_CONTROLLERS
            + "fetchUserProfile.php";

    // URL to update user profile details
    public static final String URL_UPDATE_USER_PROFILE_DETAILS = FOLDER_CONTROLLERS
            + "updateUserProfile.php";

    // URL to Generate email verification code
    public static final String URL_SEND_EMAIL_VERIFICATION_CODE = FOLDER_CONTROLLERS
            + "sendEmailVerificationCode.php";

    // URL to verify email address
    public static final String URL_VERIFY_EMAIL_ADDRESS = FOLDER_CONTROLLERS
            + "verifyEmailAddress.php";

    // URL to reset password
    public static final String URL_PASSWORD_RESET = FOLDER_CONTROLLERS + "resetPassword.php";

    // URL to switch account type
    public static final String URL_SWITCH_ACCOUNT_TYPE = FOLDER_CONTROLLERS
            + "switchAccountType.php";


    // Tag used to cancel SignUp request
    public static final String TAG_SIGNUP_PERSONAL_STRING_REQUEST = "TagRequestSignUpPersonal";
    public static final String TAG_SIGNUP_BUSINESS_STRING_REQUEST = "TagRequestSignUpBusiness";

    // Tag used to cancel Signin request
    public static final String TAG_SIGNIN_STRING_REQUEST = "TagRequestSignIn";

    // Tag used to cancel update user profile details request
    public static final String TAG_UPDATE_USER_DETAILS_STRING_REQUEST =
            "TagRequestUpdateUserProfile";

    // Tag used to cancel user profile details request
    public static final String TAG_FETCH_USER_PROFILE_STRING_REQUEST =
            "TagRequestFetchUserProfile";

    // Tag used to cancel email verification code request
    public static final String TAG_SEND_EMAIL_VERIFICATION_STRING_REQUEST
            = "TagRequestSendEmailVerificationCode";

    // Tag used to cancel email verification request
    public static final String TAG_VERIFY_EMAIL_STRING_REQUEST = "TagRequestVerifyEmailAddress";

    // Tag used to cancel password reset request
    public static final String TAG_PASSWORD_RESET_REQUEST = "TagRequestPasswordReset";

    // Tag used to cancel switch account type request
    public static final String TAG_SWITCH_ACCOUNT_TYPE_REQUEST = "TagRequestSwitchAccountType";
}
