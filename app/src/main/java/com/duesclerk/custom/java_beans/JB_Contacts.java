package com.duesclerk.custom.java_beans;

import java.io.Serializable;

public class JB_Contacts implements Serializable {

    String contactId, contactFullName, contactPhoneNumber, contactEmailAddress, contactAddress,
            contactType, debtsTotalAmount;

    /**
     * Default constructor
     */
    public JB_Contacts() {
    }

    /**
     * JavaBean constructor
     *
     * @param contactId           - Contact id
     * @param contactFullName     - Contact full name
     * @param contactPhoneNumber  - Contact phone number
     * @param contactEmailAddress - Contact email address
     * @param contactAddress      - Contact address
     * @param contactType         - Contact type
     * @param debtsTotalAmount    - Debts total amount
     */
    public JB_Contacts(final String contactId, final String contactFullName,
                       final String contactPhoneNumber,
                       final String contactEmailAddress, final String contactAddress,
                       final String contactType, final String debtsTotalAmount) {

        this.contactId = contactId;
        this.contactFullName = contactFullName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.contactEmailAddress = contactEmailAddress;
        this.contactAddress = contactAddress;
        this.contactType = contactType;
        this.debtsTotalAmount = debtsTotalAmount;
    }

    /**
     * Function to get contact Id
     */
    public String getContactId() {

        return contactId; // Return contact full name
    }

    /**
     * Function to set contact Id
     *
     * @param contactId - Contact Id
     */
    public void setContactId(String contactId) {

        this.contactId = contactId; // Set contact full name
    }

    /**
     * Function to get contact full name
     */
    public String getContactFullName() {

        return contactFullName; // Return contact full name
    }

    /**
     * Function to set contact full name
     *
     * @param contactFullName - Contact full name
     */
    public void setContactFullName(String contactFullName) {

        this.contactFullName = contactFullName; // Set contact full name
    }

    /**
     * Function to get contact phone number
     */
    public String getContactPhoneNumber() {

        return contactPhoneNumber; // Return contact phone number
    }

    /**
     * Function to set contact phone number
     *
     * @param contactPhoneNumber - Contact phone number
     */
    public void setContactPhoneNumber(String contactPhoneNumber) {

        this.contactPhoneNumber = contactPhoneNumber; // Set contact phone number
    }

    /**
     * Function to get contact email address
     */
    public String getContactEmailAddress() {

        return contactEmailAddress; // Return contact email address
    }

    /**
     * Function to set contact email address
     *
     * @param contactEmailAddress - Contact email address
     */
    public void setContactEmailAddress(String contactEmailAddress) {

        this.contactEmailAddress = contactEmailAddress; // Set contact email address
    }

    /**
     * Function to get contact address
     */
    public String getContactAddress() {

        return contactAddress; // Return contact address
    }

    /**
     * Function to set contact address
     *
     * @param contactAddress - Contact address
     */
    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress; // Set contact address
    }

    /**
     * Function to get contact type
     */
    public String getContactType() {

        return contactType; // Return contact type
    }

    /**
     * Function to set contact type
     *
     * @param contactType - Contact type
     */
    public void setContactType(String contactType) {
        this.contactType = contactType; // Set contact type
    }


    /**
     * Function to get total debts amount
     */
    public String getDebtsTotalAmount() {

        return debtsTotalAmount; // Return total debts amount
    }

    /**
     * Function to set total debts amount
     *
     * @param debtsTotalAmount - total debts amount
     */
    public void setDebtsTotalAmount(String debtsTotalAmount) {
        this.debtsTotalAmount = debtsTotalAmount; // Set total debts amount
    }
}
