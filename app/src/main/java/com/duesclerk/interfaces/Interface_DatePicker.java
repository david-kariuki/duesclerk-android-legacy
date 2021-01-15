package com.duesclerk.interfaces;

/**
 * This interface shares data between activities or fragments that need user account information
 */
public interface Interface_DatePicker {

    /**
     * Interface to pass DatePickers selected debt date issued
     *
     * @param debtDateIssued - Debt date issued
     */
    void passDebtDateIssued(String debtDateIssued);

    /**
     * Interface to pass DatePickers selected debt date due
     *
     * @param debtDateDue - Debt date due
     */
    void passDebtDateDue(String debtDateDue);
}
