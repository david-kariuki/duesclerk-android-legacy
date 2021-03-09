package com.duesclerk.interfaces;

import com.duesclerk.classes.java_beans.JB_Debts;

public interface Interface_Debts {

    /**
     * Interface method to show / hide FAB delete selected debts
     *
     * @param show - Show / Hide FAB delete selected debts
     */
    void showDeleteDebtsFab(boolean show);

    /**
     * Interface method to pass debt details
     *
     * @param jbDebt - Debt details JavaBean
     */
    void passDebtDetails(JB_Debts jbDebt);
}
