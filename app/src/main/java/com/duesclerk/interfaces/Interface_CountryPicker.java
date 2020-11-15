package com.duesclerk.interfaces;

/**
 * This interface shares data between activities or fragments that need country information
 */
public interface Interface_CountryPicker {

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
     * Interface to pass CountryCode with CountryName
     *
     * @param countryCodeAndName - Selected CountryCode and CountryName
     */
    void passCountryCodeWithCountryName(String countryCodeAndName);

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

}
