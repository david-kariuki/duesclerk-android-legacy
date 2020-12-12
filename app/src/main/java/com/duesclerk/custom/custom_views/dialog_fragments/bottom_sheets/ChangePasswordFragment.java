package com.duesclerk.custom.custom_views.dialog_fragments.bottom_sheets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.duesclerk.R;
import com.duesclerk.custom.custom_utilities.AccountUtils;
import com.duesclerk.custom.custom_utilities.ApplicationClass;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.InputFiltersUtils;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_utilities.VolleyUtils;
import com.duesclerk.custom.custom_views.toast.CustomToast;
import com.duesclerk.custom.network.InternetConnectivity;
import com.duesclerk.custom.network.NetworkUtils;
import com.duesclerk.custom.storage_adapters.SQLiteDB;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.duesclerk.custom.custom_utilities.ViewsUtils.showProgressDialog;

@SuppressWarnings("rawtypes")
@SuppressLint("ValidFragment")
public class ChangePasswordFragment extends BottomSheetDialogFragment implements TextWatcher {

    // Get class simple name
    // private final String TAG = ChangePasswordFragment.class.getSimpleName();

    private final Context mContext;
    private final SQLiteDB database;
    private BottomSheetBehavior bottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private ImageView imagePasswordToggleCurrentPassword, imagePasswordToggleNewPassword,
            imagePasswordToggleConfirmNewPassword;
    private EditText editCurrentPassword, editNewPassword, editConfirmNewPassword;
    private ProgressDialog progressDialog;

    public ChangePasswordFragment(Context mContext) {
        this.mContext = mContext; // Get context
        this.database = new SQLiteDB(mContext); // Initialize database
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog)
                super.onCreateDialog(savedInstanceState);

        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_change_password,
                null);

        // ImageViews
        imagePasswordToggleCurrentPassword = contentView.findViewById(
                R.id.imageBSChangePassword_PasswordToggle_CurrentPassword);
        imagePasswordToggleNewPassword = contentView.findViewById(
                R.id.imageBSChangePassword_PasswordToggle_NewPassword);
        imagePasswordToggleConfirmNewPassword = contentView.findViewById(
                R.id.imageBSChangePassword_PasswordToggle_ConfirmPassword);

        // EditTexts
        editCurrentPassword = contentView.findViewById(R.id.editBSChangePassword_CurrentPassword);
        editNewPassword = contentView.findViewById(R.id.editBSChangePassword_NewPassword);
        editConfirmNewPassword =
                contentView.findViewById(R.id.editBSChangePassword_ConfirmNewPassword);

        // LinearLayouts
        LinearLayout llChangePassword =
                contentView.findViewById(R.id.llBSChangePassword_ChangePassword);
        LinearLayout llCancel = contentView.findViewById(R.id.llBSChangePassword_Cancel);

        // Progress Dialog
        progressDialog = ViewsUtils.initProgressDialog(requireActivity(), false);

        // Add text watcher
        editCurrentPassword.addTextChangedListener(this);
        editNewPassword.addTextChangedListener(this);
        editConfirmNewPassword.addTextChangedListener(this);

        // Dismiss dialog
        llCancel.setOnClickListener(v -> dismiss());

        // Password toggle OnClick
        imagePasswordToggleCurrentPassword.setOnClickListener(view13 -> {
            // Toggle password visibility
            ViewsUtils.togglePasswordField(editCurrentPassword, imagePasswordToggleCurrentPassword);
        });

        imagePasswordToggleNewPassword.setOnClickListener(view13 -> {
            // Toggle password visibility
            ViewsUtils.togglePasswordField(editNewPassword, imagePasswordToggleNewPassword);
        });

        imagePasswordToggleConfirmNewPassword.setOnClickListener(view13 -> {
            // Toggle password visibility
            ViewsUtils.togglePasswordField(editConfirmNewPassword,
                    imagePasswordToggleConfirmNewPassword);
        });

        llChangePassword.setOnClickListener(v -> {
            // Check password fields
            if (checkPasswordChangeFields()) {

                // Clear focus on EditTexts
                editCurrentPassword.clearFocus();
                editNewPassword.clearFocus();
                editConfirmNewPassword.clearFocus();

                // Update password
                updatePassword(
                        database.getClientAccountInfo(null).get(0).getClientId(),
                        database.getClientAccountInfo(null).get(0).getPassword(),
                        editNewPassword.getText().toString()
                );
            }
        });

        // Set BottomSheet callback
        this.bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) bottomSheetBehavior
                        .setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE); // Remove window title

        // Set transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(contentView); // Set Custom View To Dialog
        dialog.setCancelable(false); // Set cancelable to false

        // Set BottomSheet behaviour
        this.bottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());

        // Set background to transparent
        ((View) contentView.getParent()).setBackgroundColor(Color.TRANSPARENT);

        return dialog; // Return custom dialog
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        this.bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
    }

    /**
     * Function to check password fields lengths and values and notify with toast if error
     * Checks password length
     * Checks if new password is equal to current and warns
     */
    private boolean checkPasswordChangeFields() {
        return (InputFiltersUtils.checkPasswordLengthNotify(mContext, editCurrentPassword)
                && InputFiltersUtils.checkPasswordLengthNotify(mContext, editNewPassword)
                && InputFiltersUtils.checkPasswordLengthNotify(mContext, editConfirmNewPassword)
                && InputFiltersUtils.comparePasswordChangeNotify(mContext, editCurrentPassword,
                editNewPassword)
                && InputFiltersUtils.compareNewPasswords(mContext, editNewPassword,
                editConfirmNewPassword)
                && matchCurrentPasswordWithSavedPassword());
    }

    /**
     * Function to match current password field value with SQLite stored password
     */
    private boolean matchCurrentPasswordWithSavedPassword() {

        boolean valueAcceptable = false; // Value acceptable status

        if (!editCurrentPassword.getText().toString().equals(
                database.getClientAccountInfo(null).get(0).getPassword())) {

            // Toast error message
            CustomToast.errorMessage(mContext, DataUtils.getStringResource(mContext,
                    R.string.error_password_incorrect),
                    R.drawable.ic_baseline_lock_24_white);

            // Enable error icon
            editCurrentPassword.setError(DataUtils.getStringResource(mContext,
                    R.string.error_password_incorrect));

        } else {

            valueAcceptable = true; // Set value acceptable to true
        }

        return valueAcceptable; // Return status
    }

    /**
     * Function to update password on remote database
     *
     * @param clientId        - Clients id
     * @param currentPassword - Current stored SQLite password
     * @param newPassword     - New clients password
     */
    private void updatePassword(final String clientId, final String currentPassword,
                                final String newPassword) {

        // Check Internet Connection State
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            if (InternetConnectivity.isConnectionFast(mContext)) {
                // Connected

                ViewsUtils.hideKeyboard(requireActivity()); // Hide keyboard if showing

                // Show dialog
                showProgressDialog(progressDialog,
                        DataUtils.getStringResource(mContext,
                                R.string.title_updating_password),
                        DataUtils.getStringResource(mContext,
                                R.string.msg_updating_password)
                );

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        NetworkUtils.URL_UPDATE_CLIENT_PROFILE_DETAILS, response -> {

                    // Log Response
                    // Log.d(TAG, "Update Password Response:" + response);

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                        ViewsUtils.dismissProgressDialog(progressDialog); // Hide Dialog

                        // Check for error
                        if (!error) {
                            // Client password updated successfully

                            // Update password in SQLite database and check if successful
                            if (database.updateClientAccountInformation(
                                    mContext,
                                    database.getClientAccountInfo(null).get(0).getClientId(),
                                    newPassword, AccountUtils.FIELD_PASSWORD)) {
                                // Update successful

                                // Show update successful message
                                CustomToast.infoMessage(mContext,
                                        DataUtils.getStringResource(mContext,
                                                R.string.msg_password_updated), false,
                                        R.drawable.ic_baseline_person_24_white);

                                dismiss(); // Dismiss dialog
                            }
                        } else {
                            // Error updating details

                            // Toast error message
                            CustomToast.errorMessage(
                                    mContext,
                                    DataUtils.getStringResource(
                                            mContext,
                                            R.string.error_password_update_failed),
                                    R.drawable.ic_baseline_edit_24_white);

                            // Cancel Pending Request
                            ApplicationClass.getClassInstance().cancelPendingRequests(
                                    NetworkUtils.TAG_UPDATE_CLIENT_DETAILS_STRING_REQUEST);
                        }
                    } catch (Exception ignored) {
                    }
                }, volleyError -> {

                    // Log Response
                    // Log.e(TAG, "Profile Response Error " + ":" + volleyError.getMessage());

                    // Hide Dialog
                    ViewsUtils.dismissProgressDialog(progressDialog);

                    if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                            || volleyError instanceof ServerError || volleyError instanceof
                            AuthFailureError || volleyError instanceof TimeoutError) {

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkUtils.TAG_UPDATE_CLIENT_DETAILS_STRING_REQUEST);

                        // Toast Network Error
                        if (volleyError.getMessage() != null) {
                            CustomToast.errorMessage(mContext, volleyError.getMessage(), 0);
                        }
                    }
                }) {
                    @Override
                    protected void deliverResponse(String response) {
                        super.deliverResponse(response);
                    }

                    /*@Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        headers.put(VolleyUtils.KEY_API_KEY, VolleyUtils.getApiKey(mContext));
                        return headers;
                    }*/

                    @Override
                    protected Map<String, String> getParams() {
                        @SuppressWarnings({"unchecked", "rawtypes"}) Map<String, String> params =
                                new HashMap();

                        // Put clientId, current and new password to Map params
                        params.put(AccountUtils.FIELD_CLIENT_ID, clientId);
                        params.put(AccountUtils.FIELD_PASSWORD, currentPassword);
                        params.put(AccountUtils.FIELD_NEW_PASSWORD, newPassword);

                        return params; // Return params
                    }

                    @Override
                    protected VolleyError parseNetworkError(VolleyError volleyError) {
                        return super.parseNetworkError(volleyError);
                    }

                    @Override
                    public void deliverError(VolleyError error) {
                        super.deliverError(error);
                    }
                };

                // Set Request Priority
                ApplicationClass.getClassInstance().setPriority(Request.Priority.HIGH);

                // Set retry policy
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        DataUtils.getIntegerResource(mContext,
                                R.integer.int_volley_account_request_initial_timeout_ms),
                        DataUtils.getIntegerResource(mContext,
                                R.integer.int_volley_account_request_max_timeout_retry),
                        1.0f));

                // Set request caching to false
                stringRequest.setShouldCache(false);

                // Adding request to request queue
                ApplicationClass.getClassInstance().addToRequestQueue(stringRequest,
                        NetworkUtils.TAG_UPDATE_CLIENT_DETAILS_STRING_REQUEST);

            } else {

                // Toast network connection message
                CustomToast.errorMessage(
                        mContext,
                        DataUtils.getStringResource(mContext,
                                R.string.error_network_connection_error_message_long),
                        R.drawable.ic_sad_cloud_100px_white);
            }
        } else {

            // Toast network connection message
            CustomToast.errorMessage(
                    mContext,
                    DataUtils.getStringResource(mContext,
                            R.string.error_network_connection_error_message_long),
                    R.drawable.ic_sad_cloud_100px_white);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (Objects.requireNonNull(editCurrentPassword.getText()).toString().length() > 0) {

            // Show toggle password icon
            imagePasswordToggleCurrentPassword.setVisibility(View.VISIBLE); // Show toggle icon

        } else {

            // Hide toggle password icon
            imagePasswordToggleCurrentPassword.setVisibility(View.INVISIBLE); // Hide toggle icon
        }

        if (Objects.requireNonNull(editNewPassword.getText()).toString().length() > 0) {

            // Show toggle password icon
            imagePasswordToggleNewPassword.setVisibility(View.VISIBLE); // Show toggle icon

        } else {

            // Hide toggle password icon
            imagePasswordToggleNewPassword.setVisibility(View.INVISIBLE); // Hide toggle icon
        }

        if (Objects.requireNonNull(editConfirmNewPassword.getText()).toString().length() > 0) {

            // Show toggle password icon
            imagePasswordToggleConfirmNewPassword.setVisibility(View.VISIBLE); // Show toggle icon

        } else {

            // Hide toggle password icon
            imagePasswordToggleConfirmNewPassword.setVisibility(View.INVISIBLE); // Hide toggle icon
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
