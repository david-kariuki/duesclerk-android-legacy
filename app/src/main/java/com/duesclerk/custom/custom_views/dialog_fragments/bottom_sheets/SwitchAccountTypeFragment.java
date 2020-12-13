package com.duesclerk.custom.custom_views.dialog_fragments.bottom_sheets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.duesclerk.custom.custom_utilities.ViewsUtils.showProgressDialog;

@SuppressWarnings("rawtypes")
@SuppressLint("ValidFragment")
public class SwitchAccountTypeFragment extends BottomSheetDialogFragment implements TextWatcher {

    // Get class simple name
    private final String TAG = SwitchAccountTypeFragment.class.getSimpleName();

    private final Context mContext;
    private final SQLiteDB database;
    private BottomSheetBehavior bottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private ImageView imagePasswordTogglePassword;
    private EditText editFirstName, editLastName, editBusinessName;
    private TextInputEditText editPassword;
    private ProgressDialog progressDialog;
    private String accountType, switchLabel;

    /**
     * Constructor
     *
     * @param context     - Context
     * @param switchLabel - Switch label for bottom sheet title
     */
    public SwitchAccountTypeFragment(Context context, String switchLabel) {
        this.mContext = context; // Get context
        this.database = new SQLiteDB(context); // Initialize database
        this.switchLabel = switchLabel; // Set switch label
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog)
                super.onCreateDialog(savedInstanceState);

        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_switch_account_type,
                null);

        // TextView
        TextView textTitle = contentView.findViewById(R.id.textSwitchAccountType_Title);

        // ImageViews
        imagePasswordTogglePassword = contentView.findViewById(
                R.id.imageSwitchAccountType_Password);

        // EditTexts
        editFirstName = contentView.findViewById(R.id.editSwitchAccountType_FirstName);
        editLastName = contentView.findViewById(R.id.editSwitchAccountType_LastName);
        editBusinessName = contentView.findViewById(R.id.editSwitchAccountType_BusinessName);
        editPassword = contentView.findViewById(R.id.editSwitchAccountType_Password);

        // LinearLayouts
        LinearLayout llPersonNames = contentView.findViewById(
                R.id.llSwitchAccountType_PersonNames);
        LinearLayout llBusinessName = contentView.findViewById(
                R.id.llSwitchAccountType_BusinessName);

        LinearLayout llSwitchAccountType =
                contentView.findViewById(R.id.llSwitchAccountType_SwitchAccountType);
        LinearLayout llCancel = contentView.findViewById(R.id.llSwitchAccountType_Cancel);

        // Progress Dialog
        progressDialog = ViewsUtils.initProgressDialog(requireActivity(), false);

        // Set Input Filters
        editFirstName.setFilters(new InputFilter[]{InputFiltersUtils.filterNames,
                new InputFilter.LengthFilter(InputFiltersUtils.LENGTH_MAX_SINGLE_NAME)});
        editLastName.setFilters(new InputFilter[]{InputFiltersUtils.filterNames,
                new InputFilter.LengthFilter(InputFiltersUtils.LENGTH_MAX_SINGLE_NAME)});

        accountType = database.getUserAccountInfo(null)
                .get(0).getAccountType();
        String newAccountType = null;

        String title = DataUtils.getStringResource(mContext, R.string.action_switch_account_type,
                switchLabel);
        textTitle.setText(title); // Set title

        // Check account type
        if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {

            llPersonNames.setVisibility(View.GONE); // Hide first and last names
            llBusinessName.setVisibility(View.VISIBLE); // Show business name

            // Set new account type
            newAccountType = AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS;

        } else if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {

            llBusinessName.setVisibility(View.GONE); // Hide business name
            llPersonNames.setVisibility(View.VISIBLE); // Show first and last names

            // Set new account type
            newAccountType = AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL;
        }

        // Add text watcher
        editPassword.addTextChangedListener(this);

        // Password toggle OnClick
        imagePasswordTogglePassword.setOnClickListener(view13 -> {
            // Toggle password visibility
            ViewsUtils.togglePasswordField(editPassword, imagePasswordTogglePassword);
        });

        // Password toggle onClick
        imagePasswordTogglePassword.setOnClickListener(view13 -> {

            // Toggle password visibility
            ViewsUtils.togglePasswordField(editPassword, imagePasswordTogglePassword);
        });

        // Dismiss dialog
        llCancel.setOnClickListener(v -> dismiss());

        // Switch account type onClick
        String finalNewAccountType = newAccountType;
        llSwitchAccountType.setOnClickListener(v -> {
            // Check password fields
            if (checkFieldInputs()) {

                // Clear focus on EditTexts
                editFirstName.clearFocus();
                editLastName.clearFocus();
                editBusinessName.clearFocus();
                editPassword.clearFocus();

                // Update password
                switchAccountType(
                        database.getUserAccountInfo(null).get(0).getUserId(),
                        finalNewAccountType,
                        database.getUserAccountInfo(null).get(0).getPassword()
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
     * Function to check names and password fields lengths and values and notify by toast on error
     */
    private boolean checkFieldInputs() {

        // Check account type
        if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_PERSONAL)) {
            // Personal account

            return (InputFiltersUtils.checkPersonNameLengthNotify(mContext, editFirstName,
                    true)
                    && InputFiltersUtils.checkPersonNameLengthNotify(mContext, editLastName,
                    false)
                    && InputFiltersUtils.checkPasswordLengthNotify(mContext, editPassword)
            );

        } else if (accountType.equals(AccountUtils.KEY_ACCOUNT_TYPE_BUSINESS)) {
            // Business account

            return (InputFiltersUtils.checkBusinessNameLengthNotify(mContext, editBusinessName)
                    && InputFiltersUtils.checkPasswordLengthNotify(mContext, editPassword)
            );
        }

        return false;
    }

    /**
     * Function to update password on remote database
     *
     * @param userId       - Users id
     * @param newAccountType - Users account type
     * @param password       - Current stored SQLite password
     */
    private void switchAccountType(final String userId, final String newAccountType,
                                   final String password) {

        // Check Internet Connection State
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            if (InternetConnectivity.isConnectionFast(mContext)) {
                // Connected

                ViewsUtils.hideKeyboard(requireActivity()); // Hide keyboard if showing

                // Show dialog
                showProgressDialog(progressDialog,
                        DataUtils.getStringResource(mContext,
                                R.string.title_switching_account_type),
                        DataUtils.getStringResource(mContext,
                                R.string.msg_switching_account_type, newAccountType)
                );

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        NetworkUtils.URL_SWITCH_ACCOUNT_TYPE, response -> {

                    // Log Response
                    Log.d(TAG, "Switch account type response:" + response);

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                        ViewsUtils.dismissProgressDialog(progressDialog); // Hide Dialog

                        // Check for error
                        if (!error) {
                            // User password updated successfully

                            // Update password in SQLite database and check if successful
                            if (database.updateUserAccountInformation(
                                    mContext,
                                    database.getUserAccountInfo(null).get(0).getUserId(),
                                    newAccountType, AccountUtils.FIELD_ACCOUNT_TYPE)) {
                                // Update successful

                                // Show update successful message
                                CustomToast.infoMessage(mContext,
                                        DataUtils.getStringResource(mContext,
                                                R.string.msg_account_switching_successful),
                                        false, R.drawable.ic_baseline_person_24_white);

                                dismiss(); // Dismiss dialog
                            }
                        } else {
                            // Error updating details

                            // Toast error message
                            CustomToast.errorMessage(
                                    mContext,
                                    DataUtils.getStringResource(mContext,
                                            R.string.error_account_switching_failed),
                                    R.drawable.ic_baseline_edit_24_white);

                            // Cancel Pending Request
                            ApplicationClass.getClassInstance().cancelPendingRequests(
                                    NetworkUtils.TAG_SWITCH_ACCOUNT_TYPE_REQUEST);
                        }
                    } catch (Exception ignored) {
                    }
                }, volleyError -> {

                    // Log Response
                    Log.e(TAG, "Switch account type Response Error : "
                            + volleyError.getMessage());

                    ViewsUtils.dismissProgressDialog(progressDialog); // Hide Dialog

                    if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                            || volleyError instanceof ServerError || volleyError instanceof
                            AuthFailureError || volleyError instanceof TimeoutError) {

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkUtils.TAG_SWITCH_ACCOUNT_TYPE_REQUEST);

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

                        // Put userId, current and new password to Map params
                        params.put(AccountUtils.FIELD_USER_ID, userId);
                        params.put(AccountUtils.FIELD_PASSWORD, password);
                        params.put(AccountUtils.FIELD_NEW_ACCOUNT_TYPE, newAccountType);

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
                        NetworkUtils.TAG_SWITCH_ACCOUNT_TYPE_REQUEST);

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
        if (Objects.requireNonNull(editPassword.getText()).toString().length() > 0) {

            // Show toggle password icon
            imagePasswordTogglePassword.setVisibility(View.VISIBLE); // Show toggle icon

        } else {

            // Hide toggle password icon
            imagePasswordTogglePassword.setVisibility(View.INVISIBLE); // Hide toggle icon
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
