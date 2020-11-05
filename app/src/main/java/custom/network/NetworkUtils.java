package custom.network;

public class NetworkUtils {
    // Tag used to cancel Signup Request
    public static final String TAG_UPLOAD_COVER_PICTURE_REQUEST = "RequestUploadCoverPicture";
    // Tag used to cancel SignUp Request
    public static final String TAG_SIGNUP_PERSONAL_STRING_REQUEST = "RequestSignUpPersonal";
    public static final String TAG_SIGNUP_BUSINESS_STRING_REQUEST = "RequestSignUpBusiness";
    // Tag used to cancel SignIn Request
    public static final String TAG_UPLOAD_PROFILE_PICTURE_REQUEST = "RequestUploadProfilePicture";
    // Tag used to cancel Signin Request
    public static final String TAG_SIGNIN_STRING_REQUEST = "RequestSignIn";
    // Network urls
    // Tag Used To Cancel User Profile Details Request
    public static final String TAG_FETCH_USER_PROFILE_STRING_REQUEST = "RequestFetchUserProfile";
    // Tag Used To Cancel Update User Profile Details Request
    public static final String TAG_UPDATE_USER_DETAILS_STRING_REQUEST = "RequestUpdateUserProfile";
    // Tag Used To Cancel Email Verification Code Request
    public static final String TAG_GENERATE_EMAIL_VERIFICATION_STRING_REQUEST
            = "RequestGenerateEmailVerificationCode";
    // Tag Used To Cancel Email Verification Request
    public static final String TAG_VERIFY_EMAIL_STRING_REQUEST = "RequestVerifyEmailAddress";
    // Tag Used To Cancel Password Reset Request
    public static final String TAG_PASSWORD_RESET_REQUEST = "RequestResetPassword";
    /**
     * This class holds all network urls and network tags needed by perform network requests
     */
    private static final String strProtocol = "https://";
    private static final String stroke = "/";
    private static final String strWebsiteMainDomain = strProtocol + "www.duesclerk.com" + stroke;
    private static final String backendAndroidFolder = strWebsiteMainDomain + "andr" + stroke;

    /*
     * Volley Network Tags
     */
    // Url Profile Photo Upload
    public static final String URL_UPLOAD_PROFILE_PHOTO = backendAndroidFolder
            + "uploadProfilePhoto.php";

    // Url To Signin User
    public static final String URL_SIGNIN_CLIENT = backendAndroidFolder + "SignUpClient.php";

    // Url To SignUp User
    public static final String URL_SIGNUP_CLIENT = backendAndroidFolder + "SignUpClient.php";

    // Url To Fetch User Profile Details
    public static final String URL_FETCH_USER_PROFILE_DETAILS = backendAndroidFolder
            + "fetchUserProfileDetails.php";

    public static final String URL_UPDATE_USER_DETAILS = backendAndroidFolder
            + "updateUserProfileDetails.php";

    // Url To Update User Profile Details
    public static final String URL_GENERATE_EMAIL_VERIFICATION_CODE = backendAndroidFolder
            + "generateEmailVerificationCode.php";
    // Url To Generate Email verification Code
    public static final String URL_VERIFY_EMAIL_ADDRESS = backendAndroidFolder
            + "verifyEmailAddress.php";
    // Url To Verify Email Address
    public static final String URL_RESET_PASSWORD = backendAndroidFolder + "resetPassword.php";
    // Url To Reset Password
    public static final String URL_SIGNIN_USER = backendAndroidFolder + "SignInClient.php";
}
