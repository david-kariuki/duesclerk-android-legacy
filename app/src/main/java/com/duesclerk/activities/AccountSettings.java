package com.duesclerk.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.duesclerk.R;
import com.duesclerk.classes.custom_utilities.application.BroadCastUtils;
import com.duesclerk.classes.custom_utilities.application.ViewsUtils;
import com.duesclerk.classes.custom_views.dialog_fragments.bottom_sheets.BottomSheetFragment_ChangePassword;
import com.duesclerk.classes.storage_adapters.UserDatabase;

public class AccountSettings extends AppCompatActivity {

    private Context mContext;
    private BroadcastReceiver bcrSetActionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mContext = getApplicationContext(); // Initialize context object

        // LinearLayouts
        LinearLayout llChangePassword = findViewById(R.id.llAccountSettings_ChangePassword);

        // ImageViews
        ImageView imageExit = findViewById(R.id.imageAccountSettings_Exit);

        // Change password fragment
        BottomSheetFragment_ChangePassword bottomSheetFragmentChangePassword = new BottomSheetFragment_ChangePassword(mContext);
        bottomSheetFragmentChangePassword.setRetainInstance(true);
        bottomSheetFragmentChangePassword.setCancelable(false);

        // Set switch account type action text
        setSwitchAccountTypeAction();

        // Broadcast receiver
//        bcrSetActionText = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent intent) {
//
//                String action = intent.getAction(); // Get action
//
//                if (action.equals(BroadCastUtils.bcrActionSetSwitchAccountTypeActionText)) {
//
//                    // Set switch account type action text
//                    setSwitchAccountTypeAction();
//                }
//            }
//        };

        // Image exit onClick
        imageExit.setOnClickListener(v -> finish());

        llChangePassword.setOnClickListener(v ->
                // Show change password fragment
                ViewsUtils.showBottomSheetDialogFragment(getSupportFragmentManager(),
                        bottomSheetFragmentChangePassword, true));
    }

    @Override
    public void onStart() {
        super.onStart();

        // Register broadcast
        BroadCastUtils.registerBroadCasts(AccountSettings.this, bcrSetActionText,
                BroadCastUtils.bcrActionSetSwitchAccountTypeActionText);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister BroadcastReceiver
        BroadCastUtils.unRegisterBroadCast(AccountSettings.this, bcrSetActionText);
    }

    /**
     * Function to set switch account type action text
     */
    private void setSwitchAccountTypeAction() {

        UserDatabase database = new UserDatabase(mContext); // Create and initialize database object

        // Get account type
        String accountType = database.getUserAccountInfo(null).get(0).getAccountType();

//        // Set switch account label
//        if (accountType.equals(UserAccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
//            // Personal account
//
//            // Set switch label for bottom sheet title
//            switchLabel = DataUtils.getStringResource(mContext, R.string.hint_business_account);
//
//        } else if (accountType.equals(UserAccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
//            // Business account
//
//            // Set switch label for bottom sheet title
//            switchLabel = DataUtils.getStringResource(mContext, R.string.hint_personal_account);
//        }

        // Set menu action label
//        textSwitchAccount.setText(DataUtils.getStringResource(mContext,
//                R.string.action_switch_account_type, switchLabel
//        ));
    }
}
