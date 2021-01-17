package com.duesclerk.ui.fragment_contacts.fragment_people_owing_me;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.duesclerk.custom.java_beans.JB_Debts;

public class ViewModel_PeopleOwingMe extends ViewModel {

    // Create a LiveData with a String
    private MutableLiveData<JB_Debts> debts;

    public MutableLiveData<JB_Debts> getDebts() {
        if (debts == null) {
            debts = new MutableLiveData<>();

            FragmentPeopleOwingMe fragmentPeopleOwingMe = new FragmentPeopleOwingMe();
        }
        return debts;
    }

// Rest of the ViewModel...
}
