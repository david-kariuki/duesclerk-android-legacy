package com.duesclerk.classes.custom_utilities.application;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public class BroadCastUtils {

    // Broadcast actions
    public static final String bcrActionSetSwitchAccountTypeActionText
            = "SwitchAccountTypeActionText";

    // Reload broadcasts
    public static final String bcrActionReloadPeopleOwingMe
            = "ReloadPeopleOwingMe";
    public static final String bcrActionReloadPeopleIOwe
            = "ReloadPeopleIOwe";
    public static final String bcrActionReloadContactDetailsAndDebtsActivity
            = "ReloadContactActivity";
    public static final String bcrActionSwitchMainActivityTabLayoutPosition
            = "SwitchMainActivityTabLayoutPosition";

    // Sort lists Broadcast
    public static final String bcrAction_SortLists = "SortLists";

    /**
     * Function to register BroadCast
     *
     * @param activity          - Calling activity
     * @param broadcastReceiver - BroadCastReceiver
     * @param broadCastAction   - Broadcast action
     */
    public static void registerBroadCasts(Activity activity,
                                          BroadcastReceiver broadcastReceiver,
                                          String broadCastAction) {

        // Catch errors
        try {

            // Register BroadCast
            activity.registerReceiver(broadcastReceiver, new IntentFilter(broadCastAction));

        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Function to un-register BroadCast
     *
     * @param activity          - Calling activity
     * @param broadcastReceiver - BroadcastReceiver
     */
    public static void unRegisterBroadCast(Activity activity,
                                           BroadcastReceiver broadcastReceiver) {

        // Catch errors
        try {

            // Check If BroadCast Exists
            if ((broadcastReceiver != null)) {

                // Un Register BroadCast
                activity.unregisterReceiver(broadcastReceiver);
            }
        } catch (IllegalArgumentException ignored) {
        }

    }
}
