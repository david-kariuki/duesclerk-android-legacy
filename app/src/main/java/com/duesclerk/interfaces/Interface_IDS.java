package com.duesclerk.interfaces;

/**
 * This interface shares data between activities and adapters managing debts
 */
public interface Interface_IDS {

    /**
     * Interface to pass contacts ids to requiring activity for debts deletion
     *
     * @param contactsIds - Contact ids for debts deletion
     */
    void passContactsIds(String[] contactsIds);

    /**
     * Interface to pass debts ids to requiring activity for debts deletion
     */
    void passDebtsIds(String[] debtsIds);
}
