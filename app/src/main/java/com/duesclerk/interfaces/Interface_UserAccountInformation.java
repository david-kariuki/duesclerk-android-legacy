package com.duesclerk.interfaces;

import java.util.HashMap;

/**
 * This interface shares data between activities or fragments that need user account information
 */
public interface Interface_UserAccountInformation {

    /**
     * Interface to pass user account information
     *
     * @param userAccountInformation - HashMap with fetched user account information
     */
    void passUserAccountInformationData(HashMap<String, String> userAccountInformation);

    /**
     * Interface to pass CountryName
     *
     * @param countryName - Selected CountryName
     */
    void passCountryName(String countryName);

    /**
     * Interface to pass CountryCode
     *
     * @param countryCode - Selected CountryCode
     */
    void passCountryCode(String countryCode);

    /**
     * Interface to pass CountryAlpha2
     *
     * @param countryAlpha2 - Selected CountryAlpha2
     */
    void passCountryAlpha2(String countryAlpha2);

    /**
     * Interface to pass CountryAlpha3
     *
     * @param countryAlpha3 - Selected CountryAlpha3
     */
    void passCountryAlpha3(String countryAlpha3);

    /**
     * Interface to pass CountryFlagId
     *
     * @param countryFlagId - Selected CountryFlagId
     */
    void passCountryFlag(int countryFlagId);

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
