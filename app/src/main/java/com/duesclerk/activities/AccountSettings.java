package com.duesclerk.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.duesclerk.R;
import com.duesclerk.custom.custom_utilities.AccountUtils;
import com.duesclerk.custom.custom_utilities.BroadCastUtils;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_views.dialog_fragments.bottom_sheets.ChangePasswordFragment;
import com.duesclerk.custom.custom_views.dialog_fragments.bottom_sheets.SwitchAccountTypeFragment;
import com.duesclerk.custom.storage_adapters.SQLiteDB;

public class AccountSettings extends AppCompatActivity {

    private Context mContext;
    private String switchLabel;
    private TextView textSwitchAccount;
    private BroadcastReceiver bcrSetActionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mContext = getApplicationContext(); // Initialize context object

        // TextViews
        textSwitchAccount = findViewById(R.id.textAccountSettings_SwitchAccount);

        // LinearLayouts
        LinearLayout llChangePassword = findViewById(R.id.llAccountSettings_ChangePassword);
        LinearLayout llSwitchAccountType = findViewById(R.id.llAccountSettings_SwitchAccountType);

        // ImageViews
        ImageView imageExit = findViewById(R.id.imageAccountSettings_Exit);

        // Change password fragment
        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment(mContext);
        changePasswordFragment.setRetainInstance(true);
        changePasswordFragment.setCancelable(false);

        // Set switch account type action text
        setSwitchAccountTypeAction();

        SwitchAccountTypeFragment switchAccountTypeFragment = new SwitchAccountTypeFragment(
                mContext, switchLabel);
        switchAccountTypeFragment.setRetainInstance(true);
        switchAccountTypeFragment.setCancelable(false);

        // Receive Broadcast
        bcrSetActionText = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                assert action != null;
                if (action.equals(BroadCastUtils.bcrActionSetSwitchAccountTypeActionText)) {

                    // Set switch account type action text
                    setSwitchAccountTypeAction();
                }
            }
        };

        // Image exit onClick
        imageExit.setOnClickListener(v -> finish());

        llChangePassword.setOnClickListener(v ->
                // Show change password fragment
                ViewsUtils.showBottomSheetDialogFragment(getSupportFragmentManager(),
                        changePasswordFragment, true));

        llSwitchAccountType.setOnClickListener(v -> {

                // Set switch account type action text
                setSwitchAccountTypeAction();

                // Show switch account type fragment
                ViewsUtils.showBottomSheetDialogFragment(getSupportFragmentManager(),
                        switchAccountTypeFragment, true);
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Register broadcast
        BroadCastUtils.registerRefreshBroadCasts(AccountSettings.this, bcrSetActionText,
                BroadCastUtils.bcrActionSetSwitchAccountTypeActionText);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister BroadcastReceiver
        BroadCastUtils.unRegisterRefreshBroadCast(AccountSettings.this, bcrSetActionText);
    }

    /**
     * Function to set switch account type action text
     */
    private void setSwitchAccountTypeAction() {

        SQLiteDB database = new SQLiteDB(mContext); // Create and initialize database object

        // Get account type
        String accountType = database.getUserAccountInfo(null).get(0).getAccountType();

        // Set switch account label
        if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
            // Personal account

            // Set switch label for bottom sheet title
            switchLabel = DataUtils.getStringResource(mContext, R.string.hint_business_account);

        } else if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
            // Business account

            // Set switch label for bottom sheet title
            switchLabel = DataUtils.getStringResource(mContext, R.string.hint_personal_account);
        }

        // Set menu action label
        textSwitchAccount.setText(DataUtils.getStringResource(mContext,
                R.string.action_switch_account_type, switchLabel
        ));
    }
}
