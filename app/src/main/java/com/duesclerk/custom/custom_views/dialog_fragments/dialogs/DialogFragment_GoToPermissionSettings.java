package com.duesclerk.custom.custom_views.dialog_fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.duesclerk.R;
import com.duesclerk.custom.custom_utilities.DataUtils;

import org.jetbrains.annotations.NotNull;

public class DialogFragment_GoToPermissionSettings extends DialogFragment {

    private final LayoutInflater inflater;
    private final Activity callingActivity;
    private final int permissionTagStringId;
    private final int permissionPurposeStringId;

    /**
     * Class constructor
     */
    public DialogFragment_GoToPermissionSettings(Activity activity, int permissionTagStringId,
                                                 int permissionPurposeStringId) {

        Context mContext = activity.getApplicationContext(); // Get context from activity

        // Get layout inflater
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.callingActivity = activity;
        this.permissionTagStringId = permissionTagStringId;
        this.permissionPurposeStringId = permissionPurposeStringId;

    }

    /**
     * Function to open applications permission settings
     *
     * @param activity - to get PackageName and pass activity for result
     */
    private static void launchApplicationsDetailsSettings(Activity activity) {

        // Create intent to launch applications details settings
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri); // Set uri data

        // Start activity for result
        activity.startActivityForResult(intent, 101);
    }

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialogAddPerson = super.onCreateDialog(savedInstanceState);

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(
                R.layout.dialog_permission_goto_settings, null, false);

        // Get title and message
        TextView textTitle = dialogView.findViewById(R.id.textGrantPermission_Title);
        TextView textMessage = dialogView.findViewById(R.id.textGrantPermission_Message);

        // Get buttons
        TextView textCancel = dialogView.findViewById(R.id.textGrantPermission_Cancel);
        TextView textGOTOSettings = dialogView.findViewById(R.id.textGrantPermission_GOTOSettings);

        // Set dialog title and message
        textTitle.setText(DataUtils.getStringResource(callingActivity.getApplicationContext(),
                R.string.msg_grant_permission_title, permissionTagStringId));
        textMessage.setText(DataUtils.getStringResource(callingActivity.getApplicationContext(),
                R.string.msg_grant_permission_message,
                permissionTagStringId, permissionPurposeStringId));

        // Cancel onClick listener
        textCancel.setOnClickListener(v -> dialogAddPerson.cancel()); // Cancel Dialog

        // GOTO settings onClick listener
        textGOTOSettings.setOnClickListener(v -> {

            dialogAddPerson.cancel(); // Cancel Dialog

            launchApplicationsDetailsSettings(callingActivity); // Launch permission settings
        });

        // Remove dialog, title, set background to transparent and set dialog view
        // Remove window title
        dialogAddPerson.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Set width to match parent and height to wrap content
        Window window = dialogAddPerson.getWindow();
        window.setLayout(ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT);

        // Set dialog transparent background
        dialogAddPerson.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddPerson.setContentView(dialogView);

        return dialogAddPerson;
    }

    @Override
    public void onResume() {
        super.onResume();

        //PermissionUtils.grantedPermissions(mContext, permissions);
    }
}
