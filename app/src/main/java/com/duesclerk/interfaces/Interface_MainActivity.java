package com.duesclerk.interfaces;

import androidx.fragment.app.Fragment;

import com.duesclerk.classes.java_beans.JB_Contacts;

import java.util.ArrayList;

/**
 * This interface shares data between activities or fragments that need user contacts
 */
public interface Interface_MainActivity {

    /**
     * Interface method to show/hide add contact fab button
     *
     * @param show - Boolean - show/hide dialog fragment
     */
    void showAddContactDialogFragment(boolean show);

    /**
     * Interface to pass contacts JSONArray for people owing me to MainActivity
     *
     * @param contacts - People owing me contacts ArrayList
     */
    void passUserContacts_PeopleOwingMe(ArrayList<JB_Contacts> contacts);

    /**
     * Interface to pass contacts JSONArray for people I owe to MainActivity
     *
     * @param contacts - People I Owe contacts ArrayList
     */
    void passUserContacts_PeopleIOwe(ArrayList<JB_Contacts> contacts);

    /**
     * Interface to notify fragment when no contacts were found
     *
     * @param notFound - Boolean  - ( contacts found / not found )
     */
    void setPeopleOwingMeContactsEmpty(boolean notFound);

    /**
     * Interface to notify fragment when no contacts were found
     *
     * @param notFound - Boolean  - ( contacts found / not found )
     */
    void setPeopleIOweContactsEmpty(boolean notFound);

    /**
     * Function to set SearchView hidden to true / false
     * It also hides and shows the SearchView
     *
     * @param setToHiddenAndHide - set to hidden and hide
     * @param fragment           - Calling Fragment
     */
    void setToHiddenAndHideSearchView(boolean setToHiddenAndHide, Fragment fragment);
}
