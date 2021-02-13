package com.duesclerk.interfaces;

/**
 * This interface shares data between activities or fragments that need user account information
 */
public interface Interface_DatePicker {

    /**
     * Interface to pass DatePickers selected debt date issued
     *
     * @param debtDateIssuedFull  - Debt date issued (full)
     * @param debtDateIssuedShort - Debt date issued (short)
     */
    void passDebtDateIssued(String debtDateIssuedFull, String debtDateIssuedShort);

    /**
     * Interface to pass DatePickers selected debt date due
     *
     * @param debtDateDueFull  - Debt date due (full)
     * @param debtDateDueShort - Debt date due (short)
     */
    void passDebtDateDue(String debtDateDueFull, String debtDateDueShort);
}
