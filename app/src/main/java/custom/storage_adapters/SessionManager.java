package custom.storage_adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.duesclerk.R;


public class SessionManager {

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static String PREFERENCE_NAME; // Shared Preference File Name
    // Shared Preference
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    // Shared Preference mode
    int PRIVATE_MODE = 0;

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {

        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE);
        this.editor = sharedPreferences.edit();
        PREFERENCE_NAME = context.getResources().getString(R.string.app_name) +
                "SessionManagerPreference";
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.commit(); // Commit Changes
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}
