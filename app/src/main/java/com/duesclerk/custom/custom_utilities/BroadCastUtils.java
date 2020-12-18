package com.duesclerk.custom.custom_utilities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public class BroadCastUtils {

    // Broadcast actions
    public static final String bcrActionSetSwitchAccountTypeActionText
            = "SwitchAccountTypeActionText";

    public static void registerRefreshBroadCasts(Activity activity,
                                           BroadcastReceiver broadcastReceiver,
                                           String broadCastAction) {

        try {

            // Register BroadCast
            activity.registerReceiver(broadcastReceiver, new IntentFilter(broadCastAction));
        } catch (IllegalArgumentException ignored) {
        }
    }

    public static void unRegisterRefreshBroadCast(Activity activity,
                                            BroadcastReceiver broadcastReceiver) {

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
