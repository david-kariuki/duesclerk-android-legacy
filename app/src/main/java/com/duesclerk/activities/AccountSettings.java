package com.duesclerk.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.duesclerk.R;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_views.dialog_fragments.bottom_sheets.ChangePasswordFragment;

public class AccountSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        Context mContext = getApplicationContext();
        LinearLayout llChangePassword = findViewById(R.id.llAccountSettings_ChangePassword);
        LinearLayout llSwitchAccountType = findViewById(R.id.llAccountSettings_SwitchAccountType);
        ImageView imageExit = findViewById(R.id.imageAccountSettings_Exit);
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
