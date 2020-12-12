package com.duesclerk.custom.java_beans;

import com.duesclerk.custom.custom_utilities.DataUtils;

import java.io.Serializable;

@SuppressWarnings("unused")
public class JB_ClientAccountInfo implements Serializable {

    private String clientId, firstName, lastName, emailAddress, countryName, countryCode,
            countryAlpha2, countryAlpha3, password, gender, businessName, signupDateTime,
            accountType;
    private boolean emailVerified;

    // Empty constructor for objects
    public JB_ClientAccountInfo() {
    }

    /**
     * Constructor for Sqlite database
     *
     * @param clientId     - Account Id
     * @param emailAddress - Email address
     * @param password     - Password
     */
    public JB_ClientAccountInfo(String clientId, String emailAddress, String password) {
        this.clientId = clientId;
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
     * @param gender        - Gender
     */
    public JB_ClientAccountInfo(String clientId, String firstName, String lastName,
                                String emailAddress, String countryCode,
                                String countryAlpha2, String password, String gender,
                                String accountType) {
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.countryCode = countryCode;
        this.countryAlpha2 = countryAlpha2;
        this.password = password;
        this.gender = gender;
        this.accountType = accountType;
    }

    /**
     * Constructor for business SignUup
     *
     * @param clientId      - Account Id
     * @param businessName  - Business name
     * @param countryCode   - Country code
     * @param countryAlpha2 - Country alpha2
     * @param emailAddress  - Email address
     * @param password      - Password
     */
    public JB_ClientAccountInfo(String clientId, String businessName, String countryCode,
                                String countryAlpha2, String emailAddress, String password,
                                String accountTYpe) {
        this.clientId = clientId;
        this.businessName = businessName;
        this.countryCode = countryCode;
        this.countryAlpha2 = countryAlpha2;
        this.emailAddress = emailAddress;
        this.password = password;
        this.accountType = accountTYpe;
    }

    public JB_ClientAccountInfo(String clientId, String firstName, String lastName,
                                String emailAddress, String countryName, String countryCode,
                                String countryAlpha2, String countryAlpha3, String password,
                                String gender, String businessName, String accountType,
                                boolean emailVerified, String signupDateTime) {
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.countryAlpha2 = countryAlpha2;
        this.countryAlpha3 = countryAlpha3;
        this.password = password;
        this.gender = gender;
        this.businessName = businessName;
        this.accountType = accountType;
        this.emailVerified = emailVerified;
        this.signupDateTime = signupDateTime;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryAlpha2() {
        return countryAlpha2;
    }

    public void setCountryAlpha2(String countryAlpha2) {
        this.countryAlpha2 = countryAlpha2;
    }

    public String getCountryAlpha3() {
        return countryAlpha3;
    }

    public void setCountryAlpha3(String countryAlpha3) {
        this.countryAlpha3 = countryAlpha3;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getSignupDateTime() {
        return signupDateTime;
    }

    public void setSignupDateTime(String signupDateTime) {
        this.signupDateTime = signupDateTime;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /**
     * Function to clear POJO
     */
    public void clear() {
        // Clear variables
        this.clientId = null;
        this.firstName = null;
        this.lastName = null;
        this.emailAddress = null;
        this.countryName = null;
        this.countryCode = null;
        this.countryAlpha2 = null;
        this.countryAlpha3 = null;
        this.password = null;
        this.gender = null;
        this.businessName = null;
        this.accountType = null;
        this.signupDateTime = null;
        this.emailVerified = false;
    }

    /**
     * Function to check if POJO is empty
     */
    public boolean isEmpty() {
        return (DataUtils.isEmptyString(clientId)
                && DataUtils.isEmptyString(firstName) && DataUtils.isEmptyString(lastName)
                && DataUtils.isEmptyString(emailAddress)
                && DataUtils.isEmptyString(countryName) && DataUtils.isEmptyString(countryCode)
                && DataUtils.isEmptyString(countryAlpha2) && DataUtils.isEmptyString(countryAlpha3)
                && DataUtils.isEmptyString(password)
                && DataUtils.isEmptyString(gender)
                && DataUtils.isEmptyString(businessName)
                && DataUtils.isEmptyString(accountType)
                && DataUtils.isEmptyString(signupDateTime)
        );
    }
}
