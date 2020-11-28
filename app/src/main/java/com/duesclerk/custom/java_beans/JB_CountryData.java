package com.duesclerk.custom.java_beans;

import java.io.Serializable;

public class JB_CountryData implements Serializable {

    private String countryName, countryCode, countryAlpha2, countryAlpha3, countryFlag;

    // Empty constructor
    public JB_CountryData() {
    }

    public JB_CountryData(String countryName, String countryCode, String countryAlpha2,
                          String countryAlpha3, String countryFlag) {
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.countryAlpha2 = countryAlpha2;
        this.countryAlpha3 = countryAlpha3;
        this.countryFlag = countryFlag;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryAlpha2() {
        return countryAlpha2;
    }

    public void setCountryAlpha2(String countryAlpha2) {
        this.countryAlpha2 = countryAlpha2;
    }


    public String getCountryAlpha3() {
        return countryAlpha3;
    }

    public void setCountryAlpha3(String countryAlpha3) {
        this.countryAlpha3 = countryAlpha3;
    }


    public String getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(String countryFlag) {
        this.countryFlag = countryFlag;
    }

}
