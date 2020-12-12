package com.duesclerk.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.duesclerk.R;
import com.duesclerk.custom.custom_utilities.AccountUtils;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_views.dialog_fragments.bottom_sheets.ChangePasswordFragment;
import com.duesclerk.custom.storage_adapters.SQLiteDB;

public class AccountSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        Context mContext = getApplicationContext(); // Initialize context object
        SQLiteDB database = new SQLiteDB(mContext); // Initialize database object

        // TextViews
        TextView textSwitchAccount = findViewById(R.id.textAccountSettings_SwitchAccount);

        // LinearLayouts
        LinearLayout llChangePassword = findViewById(R.id.llAccountSettings_ChangePassword);
        LinearLayout llSwitchAccountType = findViewById(R.id.llAccountSettings_SwitchAccountType);

        // ImageViews
        ImageView imageExit = findViewById(R.id.imageAccountSettings_Exit);

        String accountType = database.getClientAccountInfo(null).get(0).getAccountType();

        // Set switch account label
        if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
            // Personal account

            // Set menu action label
            textSwitchAccount.setText(DataUtils.getStringResource(mContext,
                    R.string.action_switch_account_type,
                    DataUtils.getStringResource(mContext, R.string.hint_business_account)));

        } else if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
            // Business account

            // Set menu action label
            textSwitchAccount.setText(DataUtils.getStringResource(mContext,
                    R.string.action_switch_account_type,
                    DataUtils.getStringResource(mContext, R.string.hint_personal_account)));
        }

        // Change password fragment
        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment(mContext);
        changePasswordFragment.setRetainInstance(true);
        changePasswordFragment.setCancelable(true);

        // Image exit onClick
        imageExit.setOnClickListener(v -> finish());

        llChangePassword.setOnClickListener(v ->
                // Show change password fragment
                ViewsUtils.showBottomSheetDialogFragment(getSupportFragmentManager(),
                        changePasswordFragment, true));

        llSwitchAccountType.setOnClickListener(v -> {

        });
    }
}
