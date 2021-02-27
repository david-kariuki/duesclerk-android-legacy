package com.duesclerk.interfaces;

import com.duesclerk.custom.java_beans.JB_Contacts;

import java.util.ArrayList;

/**
 * This interface shares data between activities or fragments that need user contacts
 */
public interface Interface_Contacts {

    /**
     * Interface method to pass contacts JSONArray for people owing me
     *
     * @param contacts - People owing me contacts ArrayList
     */
    void passUserContacts_PeopleOwingMe(ArrayList<JB_Contacts> contacts);

    /**
     * Interface method to pass contacts JSONArray for people I owe
     *
     * @param contacts - People I Owe contacts ArrayList
     */
    void passUserContacts_PeopleIOwe(ArrayList<JB_Contacts> contacts);

    /**
     * Interface method to pass PeopleOwingMe debts total
     *
     * @param peopleOwingMeDebtsTotal - PeopleOwingMe debts total
     */
    void passPeopleOwingMeDebtsTotal(String peopleOwingMeDebtsTotal);

    /**
     * Interface method to pass PeopleIOwe debts total
     *
     * @param peopleIOweDebtsTotal - PeopleIOwe debts total
     */
    void passPeopleIOweDebtsTotal(String peopleIOweDebtsTotal);

    /**
     * Interface method to notify fragment when no contacts were found
     *
     * @param notFound - Boolean  - ( contacts found / not found )
     */
    void setPeopleOwingMeContactsEmpty(boolean notFound);

    /**
     * Interface method to notify fragment when no contacts were found
     *
     * @param notFound - Boolean  - ( contacts found / not found )
     */
    void setPeopleIOweContactsEmpty(boolean notFound);

    /**
     * Interface method to show / hide FAB delete selected contacts
     *
     * @param show - Show / Hide FAB delete selected contacts
     */
    void showFabDeleteContacts(boolean show);
}
