package com.duesclerk.custom.java_beans;

import java.io.Serializable;

public class JB_CountryData implements Serializable {

    private String countryName, countryCode, countryAlpha2, countryAlpha3, countryFlag;

    /**
    * Default constructor
    */
    public JB_CountryData() {
    }

    /**
     * JavaBean constructor
     *
     * @param countryName   - CountryName
     * @param countryCode   - CountryCode
     * @param countryAlpha2 - CountryAlpha2
     * @param countryAlpha3 - CountryAlpha3
     * @param countryFlag   - CountryFlag
     */
    public JB_CountryData(String countryName, String countryCode, String countryAlpha2,
                          String countryAlpha3, String countryFlag) {
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.countryAlpha2 = countryAlpha2;
        this.countryAlpha3 = countryAlpha3;
        this.countryFlag = countryFlag;
    }

    /**
     * Function to get country name
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Function to set country name
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /**
     * Function to get country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Function to set country code
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Function to get country alpha2
     */
    public String getCountryAlpha2() {
        return countryAlpha2;
    }

    /**
     * Function to set country alpha2
     */
    public void setCountryAlpha2(String countryAlpha2) {
        this.countryAlpha2 = countryAlpha2;
    }

    /**
     * Function to get country alpha3
     */
    public String getCountryAlpha3() {
        return countryAlpha3;
    }

    /**
     * Function to set country alpha3
     */
    public void setCountryAlpha3(String countryAlpha3) {
        this.countryAlpha3 = countryAlpha3;
    }

    /**
     * Function to get country flag
     */
    public String getCountryFlag() {
        return countryFlag;
    }

    /**
     * Function to set country flag
     */
    public void setCountryFlag(String countryFlag) {
        this.countryFlag = countryFlag;
    }
}
