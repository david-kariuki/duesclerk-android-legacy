package com.duesclerk.classes.java_beans;

import java.io.Serializable;

public class JB_Contacts implements Serializable {

    String contactId, contactFullName, contactPhoneNumber, contactEmailAddress, contactAddress,
            contactType, contactsNumberOfDebts, singleContactsDebtsTotalAmount;
    private boolean expandedContactsOptionsMenu = false, showingCheckBox = false, checkedBox = false,
            shownButtonsLayout = true;

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
     * @param singleContactsDebtsTotalAmount    - Debts total amount
     */
    public JB_Contacts(final String contactId, final String contactFullName,
                       final String contactPhoneNumber,
                       final String contactEmailAddress, final String contactAddress,
                       final String contactType, final String singleContactsDebtsTotalAmount) {

        this.contactId = contactId;
        this.contactFullName = contactFullName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.contactEmailAddress = contactEmailAddress;
        this.contactAddress = contactAddress;
        this.contactType = contactType;
        this.singleContactsDebtsTotalAmount = singleContactsDebtsTotalAmount;
    }

    /**
     * Function to get contact Id
     */
    public String getContactId() {

        return this.contactId; // Return contact full name
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

        return this.contactFullName; // Return contact full name
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

        return this.contactPhoneNumber; // Return contact phone number
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

        return this.contactEmailAddress; // Return contact email address
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

        return this.contactAddress; // Return contact address
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

        return this.contactType; // Return contact type
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
     * Function to get contacts number of debts
     */
    public String getContactsNumberOfDebts() {

        return this.contactsNumberOfDebts; // Return contact type
    }

    /**
     * Function to set contacts number of debts
     *
     * @param contactsNumberOfDebts - Contacts number of debts
     */
    public void setContactsNumberOfDebts(String contactsNumberOfDebts) {

        this.contactsNumberOfDebts = contactsNumberOfDebts; // Set contacts number of debts
    }

    /**
     * Function to get contacts number of debts
     */
    public String getSingleContactsDebtsTotalAmount() {

        return this.singleContactsDebtsTotalAmount; // Return total contacts amount
    }

    /**
     * Function to set total contacts amount
     *
     * @param singleContactsDebtsTotalAmount - total contacts amount
     */
    public void setSingleContactsDebtsTotalAmount(String singleContactsDebtsTotalAmount) {

        // Set single contact total debts amount
        this.singleContactsDebtsTotalAmount = singleContactsDebtsTotalAmount;
    }

    /**
     * Function to check if contacts option menu is expanded
     */
    public boolean isExpandedContactOptionsMenu() {

        return this.expandedContactsOptionsMenu;
    }

    /**
     * Function to set contacts options menu to expanded
     *
     * @param setExpandedContactsOptionsMenu - Set contacts options menu to expanded value
     */
    public void setExpandedContactOptionsMenu(boolean setExpandedContactsOptionsMenu) {

        this.expandedContactsOptionsMenu = setExpandedContactsOptionsMenu;
    }

    /**
     * Function to check if CheckBox is showing
     */
    public boolean showingCheckbox() {

        return this.showingCheckBox;
    }

    /**
     * Function to set CheckBox to shown
     *
     * @param showCheckBox - Set CheckBox showing value
     */
    public void setShowCheckBox(boolean showCheckBox) {

        this.showingCheckBox = showCheckBox;
    }

    /**
     * Function to check if CheckBox is checked
     */
    public boolean checkBoxChecked() {

        return this.checkedBox;
    }

    /**
     * Function to set CheckBox to checked
     *
     * @param checked - Set CheckBox checked
     */
    public void setCheckBoxChecked(boolean checked) {

        this.checkedBox = checked;
    }

    /**
     * Function to check if CheckBox is checked
     */
    public boolean isShownButtonsLayout() {

        return this.shownButtonsLayout;
    }

    /**
     * Function to set buttons layout to shown
     *
     * @param shown - Set buttons layout to shown
     */
    public void setShownButtonsLayout(boolean shown) {

        this.shownButtonsLayout = shown;
    }
}
