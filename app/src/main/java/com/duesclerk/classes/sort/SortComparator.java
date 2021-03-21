package com.duesclerk.classes.sort;

import com.duesclerk.classes.java_beans.JB_Contacts;
import com.duesclerk.classes.java_beans.JB_Debts;

import java.util.Comparator;

public class SortComparator {

    // Contacts class
    public static class Contacts {

        /**
         * Class to sort list by ContactsTotalDebtsAmount
         */
        public static class SortBy_ContactsTotalDebtsAmount {

            /**
             * Class to sort list by ContactsTotalDebtsAmount in ascending order
             */
            public static class Ascending implements Comparator<JB_Contacts> {

                @Override
                public int compare(JB_Contacts jbContacts1, JB_Contacts jbContacts2) {

                    // Get objects
                    float contactsTotalDebtsAmount1 = Float.parseFloat(
                            jbContacts1.getSingleContactsDebtsTotalAmount());
                    float contactsTotalDebtsAmount2 = Float.parseFloat(
                            jbContacts2.getSingleContactsDebtsTotalAmount());

                    // Ascending
                    return (int) (contactsTotalDebtsAmount1 - contactsTotalDebtsAmount2);
                }
            }

            /**
             * Class to sort list by ContactsTotalDebtsAmount in descending order
             */
            public static class Descending implements Comparator<JB_Contacts> {

                @Override
                public int compare(JB_Contacts jbContacts1, JB_Contacts jbContacts2) {

                    // Get objects
                    float contactsTotalDebtsAmount1 = Float.parseFloat(
                            jbContacts1.getSingleContactsDebtsTotalAmount());
                    float contactsTotalDebtsAmount2 = Float.parseFloat(
                            jbContacts2.getSingleContactsDebtsTotalAmount());

                    // Ascending
                    return (int) (contactsTotalDebtsAmount2 - contactsTotalDebtsAmount1);
                }
            }
        }

        /**
         * Class to sort list by ContactName
         */
        public static class SortBy_ContactName {

            /**
             * Class to sort list by ContactName in ascending order
             */
            public static class Ascending implements Comparator<JB_Contacts> {

                @Override
                public int compare(JB_Contacts jbContacts1, JB_Contacts jbContacts2) {

                    // Get objects
                    String contactName1 = jbContacts1.getContactFullName().toUpperCase();
                    String contactName2 = jbContacts2.getContactFullName().toUpperCase();

                    return contactName1.compareTo(contactName2); // Return int for ascending
                }
            }

            /**
             * Class to sort list by ContactName in descending order
             */
            public static class Descending implements Comparator<JB_Contacts> {

                @Override
                public int compare(JB_Contacts jbContacts1, JB_Contacts jbContacts2) {

                    // Get objects
                    String contactName1 = jbContacts1.getContactFullName().toUpperCase();
                    String contactName2 = jbContacts2.getContactFullName().toUpperCase();

                    return contactName2.compareTo(contactName1); // Return int for descending
                }
            }
        }

        /**
         * Class to sort list by NoOfDebts
         */
        public static class SortBy_NoOfDebts {

            /**
             * Class to sort list by NoOfDebts in ascending order
             */
            public static class Ascending implements Comparator<JB_Contacts> {

                @Override
                public int compare(JB_Contacts jbContacts1, JB_Contacts jbContacts2) {

                    // Get objects
                    float noOfDebts1 = Float.parseFloat(jbContacts1.getContactsNumberOfDebts());
                    float noOfDebts2 = Float.parseFloat(jbContacts2.getContactsNumberOfDebts());

                    // Ascending
                    return (int) (noOfDebts1 - noOfDebts2);
                }
            }

            /**
             * Class to sort list by DebtAmount in descending order
             */
            public static class Descending implements Comparator<JB_Contacts> {

                @Override
                public int compare(JB_Contacts jbContacts1, JB_Contacts jbContacts2) {

                    // Get objects
                    float noOfDebts1 = Float.parseFloat(jbContacts1.getContactsNumberOfDebts());
                    float noOfDebts2 = Float.parseFloat(jbContacts2.getContactsNumberOfDebts());

                    // Ascending
                    return (int) (noOfDebts2 - noOfDebts1);
                }
            }
        }
    }

    // Debts class
    public static class Debts {

        /**
         * Class to sort list by DebtAmount
         */
        public static class SortBy_DebtAmount {

            /**
             * Class to sort list by DebtAmount in ascending order
             */
            public static class Ascending implements Comparator<JB_Debts> {

                @Override
                public int compare(JB_Debts jbDebts1, JB_Debts jbDebts2) {

                    // Get objects
                    float debtAmount1 = Float.parseFloat(jbDebts1.getDebtAmount());
                    float debtAmount2 = Float.parseFloat(jbDebts2.getDebtAmount());

                    // Ascending
                    return (int) (debtAmount1 - debtAmount2);
                }
            }

            /**
             * Class to sort list by DebtAmount in descending order
             */
            public static class Descending implements Comparator<JB_Debts> {

                @Override
                public int compare(JB_Debts jbDebts1, JB_Debts jbDebts2) {

                    // Get objects
                    float debtAmount1 = Float.parseFloat(jbDebts1.getDebtAmount());
                    float debtAmount2 = Float.parseFloat(jbDebts2.getDebtAmount());

                    // Descending
                    return (int) (debtAmount2 - debtAmount1);
                }
            }
        }


    }
}
