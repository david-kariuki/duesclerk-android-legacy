package com.duesclerk.classes.network;

public class NetworkUrls {

    // This class holds all network urls and network tags needed to handle network requests

    // Domain and Url constants
    private static final String PROTOCOL = "https://";
    private static final String URL_SEPARATOR = "/"; // Url separator
    private static final String WEBSITE_URL = (PROTOCOL + "www.duesclerk.com"
            + URL_SEPARATOR).trim();
    private static final String FOLDER_ANDROID = (WEBSITE_URL + "android" + URL_SEPARATOR)
            .trim();
    private static final String FOLDER_APP =
            (FOLDER_ANDROID + "application" + URL_SEPARATOR)
                    .trim();
    private static final String FOLDER_VENDOR_NAME = (FOLDER_APP + "duesclerk" + URL_SEPARATOR)
            .trim();
    private static final String FOLDER_CONTROLLERS = (FOLDER_VENDOR_NAME + "controllers"
            + URL_SEPARATOR).trim();

    /**
     * Class with user network urls
     */
    public static class UserURLS {

        private static final String FOLDER_CONTROLLERS_USER = FOLDER_CONTROLLERS + "user"
                + URL_SEPARATOR;

        // URL to Signin user
        public static final String URL_SIGNIN_USER = FOLDER_CONTROLLERS_USER + "signInUser.php";

        // URL to SignUp user
        public static final String URL_SIGNUP_USER = FOLDER_CONTROLLERS_USER + "signUpUser.php";

        // URL to fetch user profile details
        public static final String URL_FETCH_USER_PROFILE_DETAILS = FOLDER_CONTROLLERS_USER
                + "fetchUserProfile.php";

        // URL to update user profile details
        public static final String URL_UPDATE_USER_PROFILE_DETAILS = FOLDER_CONTROLLERS_USER
                + "updateUserProfile.php";

        // URL to Generate email verification code
        public static final String URL_SEND_EMAIL_VERIFICATION_CODE = FOLDER_CONTROLLERS_USER
                + "sendEmailVerificationCode.php";

        // URL to verify email address
        public static final String URL_VERIFY_EMAIL_ADDRESS = FOLDER_CONTROLLERS_USER
                + "verifyEmailAddress.php";

        // URL to reset password
        public static final String URL_PASSWORD_RESET = FOLDER_CONTROLLERS_USER + "resetPassword.php";
    }

    /**
     * Class with contacts network urls
     */
    public static class ContactURLS {

        private static final String FOLDER_CONTROLLERS_CONTACTS = FOLDER_CONTROLLERS + "contacts"
                + URL_SEPARATOR;

        // URL to fetch users contacts
        public static final String URL_FETCH_USER_CONTACTS = FOLDER_CONTROLLERS_CONTACTS +
                "fetchUserContacts.php";

        // URL to fetch contact data
        public static final String URL_FETCH_CONTACT_DETAILS_AND_DEBTS = FOLDER_CONTROLLERS_CONTACTS
                + "fetchContactDetailsAndDebts.php";

        // URL to add contact
        public static final String URL_ADD_CONTACT = FOLDER_CONTROLLERS_CONTACTS +
                "addUserContact.php";

        // URL to add contact
        public static final String URL_UPDATE_CONTACT = FOLDER_CONTROLLERS_CONTACTS +
                "updateUserContact.php";

        // URL to delete contacts
        public static final String URL_DELETE_CONTACTS = FOLDER_CONTROLLERS_CONTACTS +
                "deleteUserContacts.php";
    }

    /**
     * Class with debts network urls
     */
    public static class DebtsURLS {

        private static final String FOLDER_CONTROLLERS_DEBTS = FOLDER_CONTROLLERS + "debt"
                + URL_SEPARATOR;

        // URL to add contacts debt
        public static final String URL_ADD_CONTACTS_DEBT = FOLDER_CONTROLLERS_DEBTS +
                "addContactsDebt.php";

        // URL to delete contacts debts
        public static final String URL_DELETE_CONTACTS_DEBTS = FOLDER_CONTROLLERS_DEBTS +
                "deleteContactsDebts.php";
    }
}
