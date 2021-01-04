package com.duesclerk.interfaces;

/**
 * This interface shares data between activities or fragments that need user contacts
 */
public interface Interface_MainActivity {

    /**
     * Interface to hide add contact fab button
     *
     * @param show - Boolean - show/hide fab
     */
    void showAddContactFAB(boolean show);

    /**
     * Interface to hide add contact fab button
     *
     * @param show - Boolean - show/hide dialog fragment
     */
    void showAddContactDialogFragment(boolean show);
}
