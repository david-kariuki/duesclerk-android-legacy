package com.duesclerk.custom.java_beans;

public class JB_Contacts {

    String contactsFullName, contactsPhoneNumber, contactsEmailAddress, contactsAddress,
            contactsType;

    /**
     * Default constructor
     */
    public JB_Contacts() {
    }

    /**
     * JavaBean constructor
     *
     * @param contactsFullName     - Contacts full name
     * @param contactsPhoneNumber  - Contacts email address
     * @param contactsEmailAddress - Contacts email address
     * @param contactsAddress      - Contacts address
     * @param contactsType         - Contacts type
     */
    public JB_Contacts(final String contactsFullName, final String contactsPhoneNumber,
                       final String contactsEmailAddress, final String contactsAddress,
                       final String contactsType) {

        this.contactsFullName = contactsFullName;
        this.contactsPhoneNumber = contactsPhoneNumber;
        this.contactsEmailAddress = contactsEmailAddress;
        this.contactsAddress = contactsAddress;
        this.contactsType = contactsType;

    }

    /**
     * Function to get contacts full name
     */
    public String getContactsFullName() {

        return contactsFullName; // Return contacts full name
    }

    /**
     * Function to set contacts full name
     *
     * @param contactsFullName - Contacts full name
     */
    public void setContactsFullName(String contactsFullName) {

        this.contactsFullName = contactsFullName; // Set contacts full name
    }

    /**
     * Function to get contacts phone number
     */
    public String getContactsPhoneNumber() {

        return contactsPhoneNumber; // Return contacts phone number
    }

    /**
     * Function to set contacts phone number
     *
     * @param contactsPhoneNumber - Contacts phone number
     */
    public void setContactsPhoneNumber(String contactsPhoneNumber) {

        this.contactsPhoneNumber = contactsPhoneNumber; // Set contacts phone number
    }

    /**
     * Function to get contacts email address
     */
    public String getContactsEmailAddress() {

        return contactsEmailAddress; // Return contacts email address
    }

    /**
     * Function to set contacts email address
     *
     * @param contactsEmailAddress - Contacts email address
     */
    public void setContactsEmailAddress(String contactsEmailAddress) {

        this.contactsEmailAddress = contactsEmailAddress; // Set contacts email address
    }

    /**
     * Function to get contacts address
     */
    public String getContactsAddress() {

        return contactsAddress; // Return contacts address
    }

    /**
     * Function to set contacts address
     *
     * @param contactsAddress - Contacts address
     */
    public void setContactsAddress(String contactsAddress) {
        this.contactsAddress = contactsAddress; // Set contacts address
    }

    /**
     * Function to get contacts type
     */
    public String getContactsType() {

        return contactsType; // Return contacts type
    }

    /**
     * Function to set contacts type
     *
     * @param contactsType - Contacts type
     */
    public void setContactsType(String contactsType) {
        this.contactsType = contactsType; // Set contacts type
    }
}
