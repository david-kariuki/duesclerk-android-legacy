package com.duesclerk.interfaces;

public interface Interface_SignInSignup {

    /**
     * Method to pass tab position from fragment to parent activity
     *
     * @param position - tab position
     */
    void setTabPosition(int position);

    /**
     * Method to signup details
     *
     * @param firstName     - First name
     * @param lastName      - Last name
     * @param emailAddress  - Email address
     * @param countryCode   - Country code
     * @param countryAlpha2 - Country alpha2
     * @param password      - Password
     */
    void passSignupDetails(String firstName, String lastName, String emailAddress,
                           String countryCode, String countryAlpha2,
                           String password
    );

    /**
     * Method to exit activity
     */
    void finishActivity();
}
