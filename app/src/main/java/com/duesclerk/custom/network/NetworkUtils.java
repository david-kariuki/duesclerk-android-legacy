package com.duesclerk.custom.network;

public class NetworkUtils {

    // This class holds all network urls and network tags needed by perform network requests
    private static final String strProtocol = "https://";
    private static final String stroke = "/";
    private static final String strWebsiteMainDomain = strProtocol + "www.duesclerk.com" + stroke;
    private static final String backendAndroidFolder = strWebsiteMainDomain + "andr" + stroke;


    // Network urls

    // URL to Signin user
    public static final String URL_SIGNIN_USER = backendAndroidFolder + "SignInUser.php";

    // URL to SignUp user
    public static final String URL_SIGNUP_USER = backendAndroidFolder + "SignUpUser.php";

    // URL to fetch user profile details
    public static final String URL_FETCH_USER_PROFILE_DETAILS = backendAndroidFolder
            + "FetchUserProfile.php";

    // URL to update user profile details
    public static final String URL_UPDATE_USER_PROFILE_DETAILS = backendAndroidFolder
            + "UpdateUserProfile.php";

    // URL to Generate email verification code
    public static final String URL_SEND_EMAIL_VERIFICATION_CODE = backendAndroidFolder
            + "SendEmailVerificationCode.php";

    // URL to verify email address
    public static final String URL_VERIFY_EMAIL_ADDRESS = backendAndroidFolder
            + "VerifyEmailAddress.php";

    // URL to reset password
    public static final String URL_PASSWORD_RESET = backendAndroidFolder + "ResetPassword.php";

    // URL to switch account type
    public static final String URL_SWITCH_ACCOUNT_TYPE = backendAndroidFolder
            + "SwitchAccountType.php";


    // Tag used to cancel SignUp request
    public static final String TAG_SIGNUP_PERSONAL_STRING_REQUEST = "TagRequestSignUpPersonal";
    public static final String TAG_SIGNUP_BUSINESS_STRING_REQUEST = "TagRequestSignUpBusiness";

    // Tag used to cancel Signin request
    public static final String TAG_SIGNIN_STRING_REQUEST = "TagRequestSignIn";

    // Tag used to cancel update user profile details request
    public static final String TAG_UPDATE_USER_DETAILS_STRING_REQUEST =
            "TagRequestUpdateUserProfile";

    // Tag used to cancel user profile details request
    public static final String TAG_FETCH_USER_PROFILE_STRING_REQUEST = "TagRequestFetchUserProfile";

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
