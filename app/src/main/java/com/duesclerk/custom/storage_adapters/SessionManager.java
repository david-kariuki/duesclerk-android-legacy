package com.duesclerk.custom.storage_adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.duesclerk.R;


public class SessionManager {

    private static final String KEY_IS_SIGNED_IN = "isSignedIn";
    private static String PREFERENCE_NAME; // Shared Preference File Name

    // Shared Preference
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    // Shared Preference mode
    int PRIVATE_MODE = 0;

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {

        this.sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE);
        this.editor = sharedPreferences.edit();
        PREFERENCE_NAME = context.getResources().getString(R.string.app_name) +
                "_SessionManagerPreference";
    }

    public void setSignedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_SIGNED_IN, isLoggedIn);
        editor.commit(); // Commit Changes
    }

    public boolean isSignedIn() {
        return sharedPreferences.getBoolean(KEY_IS_SIGNED_IN, false);
    }
}
