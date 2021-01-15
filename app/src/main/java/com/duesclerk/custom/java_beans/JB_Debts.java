package com.duesclerk.custom.java_beans;

public class JB_Debts {

    String debtId, debtAmount, debtDateIssued, debtDateDue, debtDescription, contactId,
            contactType, userId;

    /**
     * Default constructor
     */
    public JB_Debts() {
    }

    public JB_Debts(final String debtId, final String debtAmount, final String debtDateIssued,
                    final String debtDateDue, final String debtDescription, final String contactId,
                    final String contactType, final String userId) {

        this.debtId = debtId;
        this.debtAmount = debtAmount;
        this.debtDateIssued = debtDateIssued;
        this.debtDateDue = debtDateDue;
        this.debtDescription = debtDescription;
        this.contactId = contactId;
        this.contactType = contactType;
        this.userId = userId;
    }

    /**
     * Function to get debt id
     */
    public String getDebtId() {
        return debtId;
    }

    /**
     * Function to set debt id
     *
     * @param debtId - Debt id
     */
    public void setDebtId(String debtId) {
        this.debtId = debtId;
    }

    /**
     * Function to get debt amount
     */
    public String getDebtAmount() {
        return debtAmount;
    }

    /**
     * Function to set debt amount
     *
     * @param debtAmount - Debt amount
     */
    public void setDebtAmount(String debtAmount) {
        this.debtAmount = debtAmount;
    }

    /**
     * Function to get date debt incurred
     */
    public String getDebtDateIssued() {
        return debtDateIssued;
    }

    /**
     * Function to set date debt incurred
     *
     * @param dateDebtIssued - Date debt incurred
     */
    public void setDebtDateIssued(String dateDebtIssued) {
        this.debtDateIssued = dateDebtIssued;
    }

    /**
     * Function to get date debt due
     */
    public String getDebtDateDue() {
        return debtDateDue;
    }

    /**
     * Function to set date debt due
     *
     * @param debtDateDue - Date debt due
     */
    public void setDebtDateDue(String debtDateDue) {
        this.debtDateDue = debtDateDue;
    }

    /**
     * Function to get debt description
     */
    public String getDebtDescription() {
        return debtDescription;
    }

    /**
     * Function to set debt description
     *
     * @param debtDescription - Debt description
     */
    public void setDebtDescription(String debtDescription) {
        this.debtDescription = debtDescription;
    }

    /**
     * Function to get contact id
     */
    public String getContactId() {
        return contactId;
    }

    /**
     * Function to set contact id
     *
     * @param contactId - Contact id
     */
    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    /**
     * Function to get contact type
     */
    public String getContactType() {
        return contactType;
    }

    /**
     * Function to set contact type
     *
     * @param contactType - Contact type
     */
    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    /**
     * Function to get user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Function to set user id
     *
     * @param userId - User id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
