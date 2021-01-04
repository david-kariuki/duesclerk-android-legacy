package com.duesclerk.interfaces;

import com.duesclerk.custom.java_beans.JB_Contacts;

import java.util.ArrayList;

/**
 * This interface shares data between activities or fragments that need user contacts
 */
public interface Interface_Contacts {

    /**
     * Interface to pass contacts JSONArray for people owing me
     *
     * @param contacts - People owing me contacts ArrayList
     */
    void passUserContacts_PeopleOwingMe(ArrayList<JB_Contacts> contacts);

    /**
     * Interface to pass contacts JSONArray for people I owe
     *
     * @param contacts - People I Owe contacts ArrayList
     */
    void passUserContacts_PeopleIOwe(ArrayList<JB_Contacts> contacts);

    /**
     * Interface to notify fragment when no contacts were found
     *
     * @param found - Boolean  - ( contacts found / not found )
     */
    void setNoContactsFound(boolean found);
}
