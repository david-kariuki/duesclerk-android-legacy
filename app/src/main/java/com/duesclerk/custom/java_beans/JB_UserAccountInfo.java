package com.duesclerk.custom.java_beans;

import com.duesclerk.custom.custom_utilities.user_data.DataUtils;

import java.io.Serializable;

@SuppressWarnings("unused")
public class JB_UserAccountInfo implements Serializable {

    private String userId, firstName, lastName, emailAddress, countryName, countryCode,
            countryAlpha2, countryAlpha3, password, businessName, signupDateTime,
            accountType;
    private boolean emailVerified;

    /**
     * Default constructor
     */
    public JB_UserAccountInfo() {
    }

    /**
     * Constructor for Sqlite database
     *
     * @param userId       - Account Id
     * @param emailAddress - Email address
     * @param password     - Password
     */
    public JB_UserAccountInfo(String userId, String emailAddress, String password) {
        this.userId = userId;
        this.emailAddress = emailAddress;
        this.password = password;
    }

    /**
     * Constructor for personal SignUup
     *
     * @param firstName     - First name
     * @param lastName      - Last name
     * @param emailAddress  - Email address
     * @param countryCode   - Country code
     * @param countryAlpha2 - Country alpha2
     * @param password      - Password
     */
    public JB_UserAccountInfo(String userId, String firstName, String lastName,
                              String emailAddress, String countryCode,
                              String countryAlpha2, String password, String accountType) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.countryCode = countryCode;
        this.countryAlpha2 = countryAlpha2;
        this.password = password;
        this.accountType = accountType;
    }

    /**
     * Constructor for business SignUup
     *
     * @param userId        - Account Id
     * @param businessName  - Business name
     * @param countryCode   - Country code
     * @param countryAlpha2 - Country alpha2
     * @param emailAddress  - Email address
     * @param password      - Password
     * @param accountType   - Account type
     */
    public JB_UserAccountInfo(String userId, String businessName, String countryCode,
                              String countryAlpha2, String emailAddress, String password,
                              String accountType) {
        this.userId = userId;
        this.businessName = businessName;
        this.countryCode = countryCode;
        this.countryAlpha2 = countryAlpha2;
        this.emailAddress = emailAddress;
        this.password = password;
        this.accountType = accountType;
    }

    /**
     * Constructor for business SignUup
     *
     * @param userId         - Account Id
     * @param firstName      - Last name
     * @param lastName       - First name
     * @param emailAddress   - Email address
     * @param countryName    - Country name
     * @param countryCode    - Country code
     * @param countryAlpha2  - Country alpha2
     * @param password       - Password
     * @param businessName   - Business name
     * @param accountType    - Account type
     * @param emailVerified  - Email verified
     * @param signupDateTime - Signup date and time
     */
    public JB_UserAccountInfo(String userId, String firstName, String lastName,
                              String emailAddress, String countryName, String countryCode,
                              String countryAlpha2, String countryAlpha3, String password,
                              String businessName, String accountType,
                              boolean emailVerified, String signupDateTime) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.countryAlpha2 = countryAlpha2;
        this.countryAlpha3 = countryAlpha3;
        this.password = password;
        this.businessName = businessName;
        this.accountType = accountType;
        this.emailVerified = emailVerified;
        this.signupDateTime = signupDateTime;
    }

    /**
     * Function to get user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Function to set user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Function to get first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Function to set first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Function to get last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Function to set last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Function to get email address
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Function to set email address
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Function to get country name
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Function to set country name
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /**
     * Function to get country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Function to set country code
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Function to get country alpha2
     */
    public String getCountryAlpha2() {
        return countryAlpha2;
    }

    /**
     * Function to country alpha2
     */
    public void setCountryAlpha2(String countryAlpha2) {
        this.countryAlpha2 = countryAlpha2;
    }

    /**
     * Function to get country alpha3
     */
    public String getCountryAlpha3() {
        return countryAlpha3;
    }

    /**
     * Function to set country alpha3
     */
    public void setCountryAlpha3(String countryAlpha3) {
        this.countryAlpha3 = countryAlpha3;
    }

    /**
     * Function to get password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Function to set password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Function to get business name
     */
    public String getBusinessName() {
        return businessName;
    }

    /**
     * Function to set business name
     */
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    /**
     * Function to get signup date and time
     */
    public String getSignupDateTime() {
        return signupDateTime;
    }

    /**
     * Function to set signup date and time
     */
    public void setSignupDateTime(String signupDateTime) {
        this.signupDateTime = signupDateTime;
    }

    /**
     * Function to get email verified value
     */
    public boolean isEmailVerified() {
        return emailVerified;
    }

    /**
     * Function to set email verified value
     */
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    /**
     * Function to get account type
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * Function to set account type
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /**
     * Function to clear JavaBean
     */
    public void clear() {
        // Set variables value to null
        this.userId = null;
        this.firstName = null;
        this.lastName = null;
        this.emailAddress = null;
        this.countryName = null;
        this.countryCode = null;
        this.countryAlpha2 = null;
        this.countryAlpha3 = null;
        this.password = null;
        this.businessName = null;
        this.accountType = null;
        this.signupDateTime = null;
        this.emailVerified = false;
    }

    /**
     * Function to check if JavaBean is empty
     */
    public boolean isEmpty() {
        return (DataUtils.isEmptyString(userId)
                && DataUtils.isEmptyString(firstName) && DataUtils.isEmptyString(lastName)
                && DataUtils.isEmptyString(emailAddress)
                && DataUtils.isEmptyString(countryName) && DataUtils.isEmptyString(countryCode)
                && DataUtils.isEmptyString(countryAlpha2) && DataUtils.isEmptyString(countryAlpha3)
                && DataUtils.isEmptyString(password)
                && DataUtils.isEmptyString(businessName)
                && DataUtils.isEmptyString(accountType)
                && DataUtils.isEmptyString(signupDateTime)
        );
    }
}
