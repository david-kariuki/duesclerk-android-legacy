package com.duesclerk.interfaces;

public interface Interface_SignInSignup {

    /**
     * Method to pass tab position from fragment to parent activity
     *
     * @param position - tab position
     */
    void setTabPosition(int position);

    /**
     * Method to pass personal account signup details
     *
     * @param firstName     - First name
     * @param lastName      - Last name
     * @param phoneNumber   - Phone number
     * @param emailAddress  - Email address
     * @param countryCode   - Country code
     * @param countryAlpha2 - Country alpha2
     * @param password      - Password
     * @param gender        - Gender
     */
    void passPersonalAccountSignupDetails(String firstName, String lastName, String phoneNumber,
                                          String emailAddress, String countryCode,
                                          String countryAlpha2, String password, String gender);

    /**
     * Method to pass personal account signup details
     *
     * @param businessName  - Business name
     * @param countryCode   - Country code
     * @param countryAlpha2 - Country alpha2
     * @param city          - City
     * @param phoneNumber   - Phone number
     * @param emailAddress  - Email address
     * @param password      - Password
     */
    void passBusinessAccountSignupDetails(String businessName, String countryCode,
                                          String countryAlpha2, String city, String phoneNumber,
                                          String emailAddress, String password);

    /**
     * Method to exit activity
     */
    void finishActivity();
}
