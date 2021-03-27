package com.duesclerk.classes.sort;

import androidx.annotation.NonNull;

import com.duesclerk.classes.java_beans.JB_Contacts;
import com.duesclerk.classes.java_beans.JB_Debts;
import com.duesclerk.enums.SortType;

import java.util.ArrayList;
import java.util.Collections;

public class SortLists {

    /**
     * Default class constructor
     */
    public SortLists() {
    }

    /**
     * Function to sort ArrayList in the specified order
     *
     * @param contactsArrayList - Contacts ArrayList
     * @param sortType          - Sort type
     */
    public ArrayList<JB_Contacts> sortContactsList(@NonNull ArrayList<JB_Contacts>
                                                           contactsArrayList,
                                                   @NonNull SortType sortType) {

        // Check sort type
        if (sortType == SortType.CONTACT_NAME_ASCENDING) {

            // Sort ArrayList
            Collections.sort(contactsArrayList,
                    new SortComparator.Contacts.SortBy_ContactName.Ascending());

        } else if (sortType == SortType.CONTACT_NAME_DESCENDING) {

            // Sort ArrayList
            Collections.sort(contactsArrayList,
                    new SortComparator.Contacts.SortBy_ContactName.Descending());

        } else if (sortType == SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_ASCENDING) {

            // Sort ArrayList
            Collections.sort(contactsArrayList,
                    new SortComparator.Contacts.SortBy_ContactsTotalDebtsAmount.Ascending());

        } else if (sortType == SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_DESCENDING) {

            // Sort ArrayList
            Collections.sort(contactsArrayList,
                    new SortComparator.Contacts.SortBy_ContactsTotalDebtsAmount.Descending());

        } else if (sortType == SortType.NO_OF_DEBTS_ASCENDING) {

            // Sort ArrayList
            Collections.sort(contactsArrayList,
                    new SortComparator.Contacts.SortBy_NoOfDebts.Ascending());

        } else if (sortType == SortType.NO_OF_DEBTS_DESCENDING) {

            // Sort ArrayList
            Collections.sort(contactsArrayList,
                    new SortComparator.Contacts.SortBy_NoOfDebts.Descending());
        }

        return contactsArrayList; // Return sorted ArrayList
    }

    /**
     * Function to sort ArrayList in the specified order
     *
     * @param debtsArrayList - Debts ArrayList
     * @param sortType       - Sort type
     */
    public ArrayList<JB_Debts> sortDebtsList(@NonNull ArrayList<JB_Debts>
                                                     debtsArrayList,
                                             @NonNull SortType sortType) {

        // Check sort type
        if (sortType == SortType.DEBT_AMOUNT_ASCENDING) {

            // Sort ArrayList
            Collections.sort(debtsArrayList,
                    new SortComparator.Debts.SortBy_DebtAmount.Ascending());

        } else if (sortType == SortType.DEBT_AMOUNT_DESCENDING) {

            // Sort ArrayList
            Collections.sort(debtsArrayList,
                    new SortComparator.Debts.SortBy_DebtAmount.Descending());

        } else if (sortType == SortType.DEBT_DATE_ISSUED_ASCENDING) {

            // Sort ArrayList
            Collections.sort(debtsArrayList,
                    new SortComparator.Debts.SortBy_DebtDateIssued.Ascending());

        } else if (sortType == SortType.DEBT_DATE_ISSUED_DESCENDING) {

            // Sort ArrayList
            Collections.sort(debtsArrayList,
                    new SortComparator.Debts.SortBy_DebtDateIssued.Descending());

        } else if (sortType == SortType.DEBT_DATE_DUE_ASCENDING) {

            // Sort ArrayList
            Collections.sort(debtsArrayList,
                    new SortComparator.Debts.SortBy_DebtDateDue.Ascending());

        } else if (sortType == SortType.DEBT_DATE_DUE_DESCENDING) {

            // Sort ArrayList
            Collections.sort(debtsArrayList,
                    new SortComparator.Debts.SortBy_DebtDateDue.Descending());

        } else if (sortType == SortType.DEBT_DATE_ADDED_ASCENDING) {

            // Sort ArrayList
            Collections.sort(debtsArrayList,
                    new SortComparator.Debts.SortBy_DebtDateAdded.Ascending());

        } else if (sortType == SortType.DEBT_DATE_ADDED_DESCENDING) {

            // Sort ArrayList
            Collections.sort(debtsArrayList,
                    new SortComparator.Debts.SortBy_DebtDateAdded.Descending());
        }

        return debtsArrayList; // Return sorted ArrayList
    }
}
