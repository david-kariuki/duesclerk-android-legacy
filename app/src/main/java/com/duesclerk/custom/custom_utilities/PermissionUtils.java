package com.duesclerk.custom.custom_utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import com.duesclerk.R;
import com.duesclerk.custom.custom_views.dialog_fragments.dialogs.DialogFragment_AddContact;
import com.duesclerk.custom.custom_views.dialog_fragments.dialogs.DialogFragment_GoToPermissionSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class PermissionUtils {

    public static boolean requestContactsPermission(DialogFragment_AddContact fragment) {

        final boolean[] permissionGranted = {false}; // Permission grant status

        // Required permissions
        final String[] permissions = {Manifest.permission.READ_CONTACTS};

        // Request required permission
        Dexter.withActivity(fragment.requireActivity())
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            // Get permission granted status
                            permissionGranted[0] =
                                    grantedPermissions(
                                            fragment.requireActivity().getApplicationContext(),
                                            permissions
                                    );
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {

                            // Launch application details settings
                            ViewsUtils.showDialogFragment(
                                    fragment.getParentFragmentManager(),
                                    new DialogFragment_GoToPermissionSettings(
                                            fragment.requireActivity(),
                                            R.string.permission_tag_contacts,
                                            R.string.permission_purpose_contact),
                                    true
                            );
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken
                            token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        return permissionGranted[0]; // Return permission status
    }

    /**
     * Function to check if any permission is granted
     *
     * @param context     - to check self permission
     * @param permissions - associated permission
     */
    private static boolean grantedPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null
                && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
