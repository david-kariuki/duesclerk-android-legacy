package com.duesclerk.interfaces;

import java.util.HashMap;

/**
 * This interface shares data between activities or fragments that need user account information
 */
public interface Interface_UserAccountInformation {

    /**
     * Interface to pass DatePickers selected date
     *
     * @param selectedDate - Selected date
     */
    void passDatePickerSelectedDate(String selectedDate);

    /**
     * Interface to pass selected Gender
     *
     * @param selectedGender - Selected gender
     */
    void passSelectedGender(String selectedGender);
}
