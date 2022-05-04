package com.duesclerk.classes.java_beans;

public class JB_Debts {

    private String debtId, debtAmount, DebtDateAdded, debtDateIssued, debtDateDue,
            debtDescription, contactId, contactType, userId;
    private boolean expandedDebtsOptionsMenu = false, expandedDebtsDetailsLayout = false;
    private boolean showingCheckBox = false, checkBoxChecked = false, shownMenuButtonsLayout = true;

    /**
     * Default constructor
     */
    public JB_Debts() {
    }

    /**
     * Class constructor
     *
     * @param debtId                     - Debt id
     * @param debtAmount                 - Debt amount
     * @param debtDateIssued             - Debt date issued
     * @param debtDateDue                - Debt date due
     * @param debtDateAdded              - Debt date added
     * @param debtDescription            - Debt description
     * @param contactId                  - Contact id
     * @param contactType                - Contact type
     * @param userId                     - User id
     * @param expandedDebtsOptionsMenu   - Expanded debts options menu
     * @param expandedDebtsDetailsLayout - Expanded debts details layout
     * @param showingCheckBox            - Showing CheckBoxes
     * @param checkBoxChecked            - CheckBox checked
     * @param shownMenuButtonsLayout     - Buttons layout shown
     */
    public JB_Debts(final String debtId, final String debtAmount, final String debtDateIssued,
                    final String debtDateDue, final String debtDateAdded,
                    final String debtDescription, final String contactId, final String contactType,
                    final String userId, final boolean expandedDebtsOptionsMenu,
                    final boolean expandedDebtsDetailsLayout, final boolean showingCheckBox,
                    final boolean checkBoxChecked, final boolean shownMenuButtonsLayout) {

        this.debtId = debtId;
        this.debtAmount = debtAmount;
        this.debtDateIssued = debtDateIssued;
        this.debtDateDue = debtDateDue;
        this.DebtDateAdded = debtDateAdded;
        this.debtDescription = debtDescription;
        this.contactId = contactId;
        this.contactType = contactType;
        this.userId = userId;
        this.expandedDebtsOptionsMenu = expandedDebtsOptionsMenu;
        this.expandedDebtsDetailsLayout = expandedDebtsDetailsLayout;
        this.showingCheckBox = showingCheckBox;
        this.checkBoxChecked = checkBoxChecked;
        this.shownMenuButtonsLayout = shownMenuButtonsLayout;

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
     * Function to get debt date added
     */
    public String getDebtDateAdded() {

        return DebtDateAdded;
    }

    /**
     * Function to set debt date added
     *
     * @param DebtDateAdded - Debt date added
     */
    public void setDebtDateAdded(String DebtDateAdded) {

        this.DebtDateAdded = DebtDateAdded;
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
    public boolean expandedDebtDetailsLayout() {

        return this.expandedDebtsDetailsLayout;
    }

    /**
     * Function to set debts details layout to expanded
     *
     * @param setExpandedDebtsDetailsLayout - Set debts details layout to expanded value
     */
    public void setExpandedDebtDetailsLayout(boolean setExpandedDebtsDetailsLayout) {

        this.expandedDebtsDetailsLayout = setExpandedDebtsDetailsLayout;
    }

    /**
     * Function to check if debts option menu is expanded
     */
    public boolean expandedDebtOptionsMenu() {

        return this.expandedDebtsOptionsMenu;
    }

    /**
     * Function to set debts options menu to expanded
     *
     * @param setExpandedDebtsOptionsMenu - Set debts options menu to expanded value
     */
    public void setExpandedDebtOptionsMenu(boolean setExpandedDebtsOptionsMenu) {

        this.expandedDebtsOptionsMenu = setExpandedDebtsOptionsMenu;
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

        return this.checkBoxChecked;
    }

    /**
     * Function to set CheckBox to checked
     *
     * @param checked - Set CheckBox checked
     */
    public void setCheckBoxChecked(boolean checked) {

        this.checkBoxChecked = checked;
    }

    /**
     * Function to check if CheckBox is checked
     */
    public boolean shownMenuButtonsLayout() {

        return this.shownMenuButtonsLayout;
    }

    /**
     * Function to set buttons layout to shown
     *
     * @param shown - Set buttons layout to shown
     */
    public void setShownMenuButtonsLayout(boolean shown) {

        this.shownMenuButtonsLayout = shown;
    }
}
