package custom.network;

public class NetworkUtils {

    /**
     * This class holds all network urls and network tags needed by perform network requests
     */
    private static final String strProtocol = "https://";
    private static final String stroke = "/";
    private static final String strWebsiteMainDomain = strProtocol + "www.duesclerk.com" + stroke;
    private static final String backendAndroidFolder = strWebsiteMainDomain + "andr" + stroke;


    /**
     * Network urls
     */

    // Url To Signin Client
    public static final String URL_SIGNIN_USER = backendAndroidFolder + "SignInClient.php";


    // Url To SignUp Client
    public static final String URL_SIGNUP_CLIENT = backendAndroidFolder + "SignUpClient.php";

    // Url To Fetch Client Profile Details
    public static final String URL_FETCH_CLIENT_PROFILE_DETAILS = backendAndroidFolder
            + "FetchClientProfile.php";

    public static final String URL_UPDATE_CLIENT_DETAILS = backendAndroidFolder
            + "UpdateClientProfile.php";

    public static final String URL_GENERATE_EMAIL_VERIFICATION_CODE = backendAndroidFolder
            + "generateEmailVerificationCode.php";

    // Url To Generate Email verification Code
    public static final String URL_VERIFY_EMAIL_ADDRESS = backendAndroidFolder
            + "verifyEmailAddress.php";

    // Url To Verify Email Address
    public static final String URL_RESET_PASSWORD = backendAndroidFolder + "resetPassword.php";

    // Url To Reset Password


    /*
     * Volley Network Tags
     */

    // Tag used to cancel Signup Request
    public static final String TAG_UPLOAD_COVER_PICTURE_REQUEST = "TagRequestUploadCoverPicture";

    // Tag used to cancel SignUp Request
    public static final String TAG_SIGNUP_PERSONAL_STRING_REQUEST = "TagRequestSignUpPersonal";
    public static final String TAG_SIGNUP_BUSINESS_STRING_REQUEST = "TagRequestSignUpBusiness";

    // Tag used to cancel Signin Request
    public static final String TAG_SIGNIN_STRING_REQUEST = "TagRequestSignIn";

    // Tag Used To Cancel Update Client Profile Details Request
    public static final String TAG_UPDATE_CLIENT_DETAILS_STRING_REQUEST =
            "TagRequestUpdateClientProfile";

    // Tag Used To Cancel Client Profile Details Request
    public static final String TAG_FETCH_CLIENT_PROFILE_STRING_REQUEST = "TagRequestFetchClientProfile";

    // Tag Used To Cancel Email Verification Code Request
    public static final String TAG_GENERATE_EMAIL_VERIFICATION_STRING_REQUEST
            = "TagRequestGenerateEmailVerificationCode";

    // Tag Used To Cancel Email Verification Request
    public static final String TAG_VERIFY_EMAIL_STRING_REQUEST = "TagRequestVerifyEmailAddress";

    // Tag Used To Cancel Password Reset Request
    public static final String TAG_PASSWORD_RESET_REQUEST = "TagRequestResetPassword";
}
