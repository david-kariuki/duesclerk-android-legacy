package com.duesclerk.custom.java_beans;

public class JB_Debts {

    private String debtId, debtAmount, debtDateIssued, debtDateDue, debtDescription, contactId,
            contactType, userId;
    private boolean isExpandedDebtsOptionsMenu = false, isExpandedDebtsDetailsLayout = false;

    /**
     * Default constructor
     */
    public JB_Debts() {
    }

    /**
     * Class constructor
     *
     * @param debtId          - Debt id
     * @param debtAmount      - Debt amount
     * @param debtDateIssued  - Debt date issued
     * @param debtDateDue     - Debt date due
     * @param debtDescription - Debt description
     */
    public JB_Debts(final String debtId, final String debtAmount, final String debtDateIssued,
                    final String debtDateDue, final String debtDescription) {

        this.debtId = debtId;
        this.debtAmount = debtAmount;
        this.debtDateIssued = debtDateIssued;
        this.debtDateDue = debtDateDue;
        this.debtDescription = debtDescription;
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

    /**
     * Function to check if debts details layout is expanded
     */
    public boolean isExpandedDebtDetailsLayout() {
        return this.isExpandedDebtsDetailsLayout;
    }

    /**
     * Function to set debts details layout to expanded
     *
     * @param isExpandedDebtsDetailsLayout - Set debts details layout to expanded value
     */
    public void setExpandedDebtDetailsLayout(boolean isExpandedDebtsDetailsLayout) {
        this.isExpandedDebtsDetailsLayout = isExpandedDebtsDetailsLayout;
    }

    /**
     * Function to check if debts option menu is expanded
     */
    public boolean isExpandedDebtOptionsMenu() {
        return this.isExpandedDebtsOptionsMenu;
    }

    /**
     * Function to set debts options menu to expanded
     *
     * @param isExpandedDebtsOptionsMenu - Set debts options menu to expanded value
     */
    public void setExpandedDebtOptionsMenu(boolean isExpandedDebtsOptionsMenu) {
        this.isExpandedDebtsOptionsMenu = isExpandedDebtsOptionsMenu;
    }
}
