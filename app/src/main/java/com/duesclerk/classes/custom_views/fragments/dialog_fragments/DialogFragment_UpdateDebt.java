package com.duesclerk.classes.custom_views.fragments.dialog_fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.duesclerk.R;
import com.duesclerk.classes.custom_utilities.application.ApplicationClass;
import com.duesclerk.classes.custom_utilities.application.BroadCastUtils;
import com.duesclerk.classes.custom_utilities.application.ViewsUtils;
import com.duesclerk.classes.custom_utilities.application.VolleyUtils;
import com.duesclerk.classes.custom_utilities.user_data.ContactUtils;
import com.duesclerk.classes.custom_utilities.user_data.DataUtils;
import com.duesclerk.classes.custom_utilities.user_data.DateTimeUtils;
import com.duesclerk.classes.custom_utilities.user_data.DebtUtils;
import com.duesclerk.classes.custom_utilities.user_data.InputFiltersUtils;
import com.duesclerk.classes.custom_views.toast.CustomToast;
import com.duesclerk.classes.network.InternetConnectivity;
import com.duesclerk.classes.network.NetworkTags;
import com.duesclerk.classes.network.NetworkUrls;
import com.duesclerk.interfaces.Interface_DatePicker;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DialogFragment_UpdateDebt extends DialogFragment implements Interface_DatePicker,
        TextWatcher {

    // Get class simple name
    // private final String TAG = DialogFragment_AddDebt.class.getSimpleName();

    private final LayoutInflater inflater;
    private final Context mContext;
    private final String contactId, debtId, debtAmount, debtDateIssued, debtDateDue,
            debtDescription;
    private String newDebtAmount = "", newDebtDateIssued = "", newDebtDateDue = "",
            newDebtDescription = "";
    private EditText editDebtAmount, editDebtDescription;
    private EditText editDebtDateIssuedFull, editDebtDateDueFull;
    private String shortDateDebtIssued, shortDateDebtDue;
    private ProgressDialog progressDialog;
    private LinearLayout llUpdateDebtEnabled, llUpdateDebtDisabled;
    private ImageView imageDeleteSelectedDebtDateIssued, imageDeleteSelectedDebtDateDue;

    /**
     * Class constructor
     *
     * @param context         - Context
     * @param contactId       - ContactId
     * @param debtAmount      - DebtAmount
     * @param debtDateIssued  - DebtDateIssued
     * @param debtDateDue     - DebtDateDue
     * @param debtDescription - DebtDescription
     */
    public DialogFragment_UpdateDebt(@NonNull final Context context,
                                     @NonNull final String contactId,
                                     @NonNull final String debtId,
                                     @NonNull final String debtAmount,
                                     final String debtDateIssued,
                                     final String debtDateDue,
                                     final String debtDescription) {

        this.mContext = context; // Set context

        // Set LayoutInflater
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Set strings
        this.contactId = contactId;
        this.debtId = debtId;
        this.debtAmount = debtAmount;
        this.debtDateIssued = debtDateIssued;
        this.debtDateDue = debtDateDue;
        this.debtDescription = debtDescription;
    }

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialogAddContact = super.onCreateDialog(savedInstanceState);

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(
                R.layout.dialog_update_debt, null, false);

        // EditTexts
        this.editDebtAmount = dialogView.findViewById(R.id.editUpdateDebt_DebtAmount);
        this.editDebtDateIssuedFull = dialogView.findViewById(R.id.editUpdateDebt_DateIssued);
        this.editDebtDateDueFull = dialogView.findViewById(R.id.editUpdateDebt_DateDue);
        this.editDebtDescription = dialogView.findViewById(R.id.editUpdateDebt_DebtDescription);

        // LinearLayouts
        LinearLayout llCancel = dialogView.findViewById(R.id.llUpdateDebt_Cancel);
        this.llUpdateDebtEnabled = dialogView.findViewById(R.id.llUpdateDebt_Enabled_Update);
        this.llUpdateDebtDisabled = dialogView.findViewById(R.id.llUpdateDebt_Disabled_Update);

        // ImageViews
        imageDeleteSelectedDebtDateIssued = dialogView.findViewById(
                R.id.imageUpdateDebt_DateIssued_Delete);
        imageDeleteSelectedDebtDateDue = dialogView.findViewById(
                R.id.imageUpdateDebt_DateDue_Delete);

        // Initialize ProgressDialog
        this.progressDialog = ViewsUtils.initProgressDialog(requireActivity(), false);

        DialogFragment_DatePicker datePickerDateIssued = new DialogFragment_DatePicker(
                this, true);
        datePickerDateIssued.setRetainInstance(true);
        datePickerDateIssued.setCancelable(false);

        DialogFragment_DatePicker datePickerDateDue = new DialogFragment_DatePicker(
                this, false);
        datePickerDateDue.setRetainInstance(true);
        datePickerDateDue.setCancelable(false);

        // Set field values
        this.editDebtAmount.setText(debtAmount);
        this.editDebtDateIssuedFull.setText(debtDateIssued);
        this.editDebtDateDueFull.setText(debtDateDue);
        this.editDebtDescription.setText(debtDescription);

        // Add TextWatcher
        this.editDebtAmount.addTextChangedListener(this);
        this.editDebtDateIssuedFull.addTextChangedListener(this);
        this.editDebtDateDueFull.addTextChangedListener(this);
        this.editDebtDescription.addTextChangedListener(this);

        // Delete date onClick
        imageDeleteSelectedDebtDateIssued.setOnClickListener(v -> {

            editDebtDateIssuedFull.setText(""); // Set text to null
        });

        // Delete date onClick
        imageDeleteSelectedDebtDateDue.setOnClickListener(v -> {

            editDebtDateDueFull.setText(""); // Set text to null
        });

        showDeleteDatesButton(); // Show / hide delete dates button

        // Cancel onClick
        llCancel.setOnClickListener(v -> dismiss()); // Dismiss dialog

        // Add person onClick
        this.llUpdateDebtEnabled.setOnClickListener(v -> {

            // Check field inputs
            if (checkFieldInputs()) {
                // Fields ok

                // Update contact
                this.updateContactsDebt(
                        this.contactId, this.debtId, this.newDebtAmount, this.newDebtDateIssued,
                        this.newDebtDateDue, this.newDebtDescription
                );
            }
        });

        // Date issued onClick
        this.editDebtDateIssuedFull.setOnClickListener(v -> {

            // Show add debt dialog
            ViewsUtils.showDialogFragment(getParentFragmentManager(),
                    datePickerDateIssued, true);
        });

        // Date due onClick
        this.editDebtDateDueFull.setOnClickListener(v -> {

            // Show add debt dialog
            ViewsUtils.showDialogFragment(getParentFragmentManager(),
                    datePickerDateDue, true);
        });

        // Remove window title
        dialogAddContact.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Set width to match parent and height to wrap content
        Window window = dialogAddContact.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        // Set dialog transparent background
        dialogAddContact.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddContact.setContentView(dialogView);

        return dialogAddContact;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Check if bundle is null
        if (savedInstanceState != null) {

            // Get dialog inputs from savedInstanceState
            String savedDebtAmount = savedInstanceState
                    .getString(DebtUtils.FIELD_DEBT_AMOUNT);
            String savedDebtDateIssued = savedInstanceState
                    .getString(DebtUtils.FIELD_DEBT_DATE_ISSUED);
            String savedDebtDateDue = savedInstanceState
                    .getString(DebtUtils.FIELD_DEBT_DATE_DUE);
            String savedDebtDescription = savedInstanceState
                    .getString(DebtUtils.FIELD_DEBT_DESCRIPTION);

            // Check for values and set to EditTexts
            if (!DataUtils.isEmptyString(savedDebtAmount)) {

                editDebtAmount.setText(savedDebtAmount); // Set debt amount
            }

            // Check for values and set to EditTexts
            if (!DataUtils.isEmptyString(savedDebtDateIssued)) {

                editDebtDateIssuedFull.setText(savedDebtDateIssued); // Set debt date issued
            }

            // Check for values and set to EditTexts
            if (!DataUtils.isEmptyString(savedDebtDateDue)) {

                editDebtDateDueFull.setText(savedDebtDateDue); // Set debt date due
            }

            // Check for values and set to EditTexts
            if (!DataUtils.isEmptyString(savedDebtDescription)) {

                editDebtDescription.setText(savedDebtDescription); // Set debt description
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save dialog inputs to outState

        // Check for field values and set to outState
        if (!DataUtils.isEmptyEditText(editDebtAmount)) {

            // Get and put debt amount
            outState.putString(DebtUtils.FIELD_DEBT_AMOUNT,
                    editDebtAmount.getText().toString());
        }

        // Check for field values and set to outState
        if (!DataUtils.isEmptyEditText(editDebtDateIssuedFull)) {

            // Get and put debt date issued
            outState.putString(DebtUtils.FIELD_DEBT_DATE_ISSUED,
                    editDebtDateIssuedFull.getText().toString());
        }

        // Check for field values and set to outState
        if (!DataUtils.isEmptyEditText(editDebtDateDueFull)) {

            // Get and put debt date due
            outState.putString(DebtUtils.FIELD_DEBT_DATE_DUE,
                    editDebtDateDueFull.getText().toString());
        }

        // Check for field values and set to outState
        if (!DataUtils.isEmptyEditText(editDebtDescription)) {

            // Get and put debt description
            outState.putString(DebtUtils.FIELD_DEBT_DESCRIPTION,
                    editDebtDescription.getText().toString());
        }
    }

    /**
     * Function to check field lengths and values and notify by toast on error
     */
    private boolean checkFieldInputs() {

        if ((!DataUtils.isEmptyString(shortDateDebtIssued))
                && (!DataUtils.isEmptyString(shortDateDebtDue))) {

            // Check debt amount and date time difference
            return (InputFiltersUtils.checkDebtAmountLengthNotify(mContext, editDebtAmount, true)
                    && !dateDifferenceLessThanZero()
            );

        } else {

            // Check debt amount
            return (InputFiltersUtils.checkDebtAmountLengthNotify(mContext, editDebtAmount, true));
        }
    }

    /**
     * Function to check if field values changed
     */
    private boolean getCheckFieldValueChanges() {

        // Get new inputs if any
        newDebtAmount = editDebtAmount.getText().toString();
        newDebtDateIssued = editDebtDateIssuedFull.getText().toString();
        newDebtDateDue = editDebtDateDueFull.getText().toString();
        newDebtDescription = editDebtDescription.getText().toString();

        // Return boolean if values changed
        return (!newDebtAmount.equals(debtAmount)
                || !newDebtDateIssued.equals(debtDateIssued)
                || !newDebtDateDue.equals(debtDateDue)
                || !newDebtDescription.equals(debtDescription));
    }

    /**
     * Function to show / hide enabled update button
     */
    private void switchUpdateButton() {

        // Check if field values changed
        if (getCheckFieldValueChanges()) {

            llUpdateDebtDisabled.setVisibility(View.GONE); // Hide update-disabled button
            llUpdateDebtEnabled.setVisibility(View.VISIBLE); // Show update-enabled button

        } else {

            llUpdateDebtEnabled.setVisibility(View.GONE); // Hide update-enabled button
            llUpdateDebtDisabled.setVisibility(View.VISIBLE); // Show update-disabled button
        }
    }

    /**
     * Function to check if date difference if greater or equal to zero
     * This prevents a negative time difference or backdating date due past date issued
     */
    private boolean dateDifferenceLessThanZero() {

        // Create DateFormat
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

        // Catch Parse errors
        try {

            // Parse dates
            Date dateDebtIssued = dateFormat.parse(shortDateDebtIssued);
            Date dateDebtDue = dateFormat.parse(shortDateDebtDue);

            // Check if dates are null
            if ((dateDebtIssued != null) && (dateDebtDue != null)) {

                // Check if time difference is less than zero
                if (DateTimeUtils.getDateTimeDifferenceInDays(
                        dateDebtIssued, dateDebtDue) < 0) {

                    // Toast error message
                    CustomToast.errorMessage(mContext, DataUtils.getStringResource(mContext,
                            R.string.error_debt_date_due_behind_date_issued),
                            R.drawable.ic_baseline_date_range_24_white);

                    // Enable error icon
                    editDebtDateDueFull.setError(DataUtils.getStringResource(mContext,
                            R.string.error_debt_check_range));

                } else {

                    return false; // Return false - Time difference is not less than zero
                }
            }
        } catch (ParseException ignored) {
        }

        return true; // Return status
    }

    /**
     * Function to add contact to remote database
     *
     * @param contactId       - ContactId
     * @param debtId          - DebtId
     * @param debtAmount      - DebtAmount
     * @param debtDateIssued  - DebtDateIssued
     * @param debtDateDue     - DebtDateDue
     * @param debtDescription - DebtDescription
     */
    private void updateContactsDebt(final String contactId, final String debtId,
                                    final String debtAmount, final String debtDateIssued,
                                    final String debtDateDue, final String debtDescription) {

        // Check Internet Connection states
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            ViewsUtils.hideKeyboard(requireActivity()); // Hide keyboard if showing

            // Show dialog
            ViewsUtils.showProgressDialog(progressDialog,
                    DataUtils.getStringResource(mContext, R.string.title_updating_debt),
                    DataUtils.getStringResource(mContext, R.string.msg_updating_debt)
            );

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUrls.DebtsURLS.URL_UPDATE_CONTACTS_DEBT, response -> {

                // Log Response
                // Log.d(TAG, "Update contacts debt response:" + response);

                ViewsUtils.dismissProgressDialog(progressDialog); // Hide Dialog

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                    // Check for error
                    if (!error) {
                        // New contact added successfully

                        // Show success message
                        CustomToast.infoMessage(mContext,
                                DataUtils.getStringResource(mContext,
                                        R.string.msg_updating_debt_successful),
                                false, R.drawable.ic_baseline_attach_money_24_white);

                        // Catch errors
                        try {

                            // Broadcast to refresh debts
                            Intent intentBroadcastDebts = new Intent(
                                    BroadCastUtils.bcrActionReloadContactDetailsAndDebtsActivity);

                            // Broadcast to refresh contacts
                            Intent intentBroadcastPeopleOwingMe = new Intent(
                                    BroadCastUtils.bcrActionReloadPeopleOwingMe);

                            // Broadcast to refresh contacts
                            Intent intentBroadcastPeopleIOwe = new Intent(
                                    BroadCastUtils.bcrActionReloadPeopleIOwe);

                            // Send broadcasts
                            requireActivity().sendBroadcast(intentBroadcastDebts);
                            requireActivity().sendBroadcast(intentBroadcastPeopleOwingMe);
                            requireActivity().sendBroadcast(intentBroadcastPeopleIOwe);

                        } finally {

                            dismiss(); // Dismiss dialog
                        }
                    } else {
                        // Error updating details

                        String errorMessage = jsonObject.getString(
                                VolleyUtils.KEY_ERROR_MESSAGE);

                        // Toast error message
                        CustomToast.errorMessage(
                                mContext,
                                errorMessage,
                                R.drawable.ic_baseline_attach_money_24_white);

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkTags.DebtsNetworkTags.TAG_UPDATE_CONTACTS_DEBT_STRING_REQUEST);
                    }
                } catch (Exception ignored) {
                }
            }, volleyError -> {

                // Log Response
                // Log.e(TAG, "Update contacts debt response error : "
                //        + volleyError.getMessage());

                ViewsUtils.dismissProgressDialog(progressDialog); // Hide Dialog

                // Check request response
                if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                        || volleyError instanceof ServerError || volleyError instanceof
                        AuthFailureError || volleyError instanceof TimeoutError) {

                    CustomToast.errorMessage(mContext, DataUtils.getStringResource(mContext,
                            R.string.error_network_connection_error_message_short),
                            R.drawable.ic_sad_cloud_100px_white);

                } else {

                    // Toast Connection Error Message
                    CustomToast.errorMessage(mContext, volleyError.getMessage(),
                            R.drawable.ic_sad_cloud_100px_white);
                }

                // Clear url cache
                ApplicationClass.getClassInstance().deleteUrlVolleyCache(
                        NetworkUrls.DebtsURLS.URL_UPDATE_CONTACTS_DEBT);
            }) {
                @Override
                protected void deliverResponse(String response) {
                    super.deliverResponse(response);
                }

//                @Override
//                public Map<String, String> getHeaders() {
//                    HashMap<String, String> headers = new HashMap<>();
//                    headers.put("Content-Type", "application/json");
//                    // headers.put(VolleyUtils.KEY_API_KEY, VolleyUtils.getApiKey(mContext));
//                    return headers;
//                }

                @Override
                protected Map<String, String> getParams() {

                    @SuppressWarnings({"unchecked", "rawtypes"})
                    Map<String, String> params = new HashMap();

                    // Pud DebtAmount details to Map params
                    params.put(DebtUtils.FIELD_DEBT_AMOUNT, debtAmount);

                    // Check for debt date issued
                    if (!DataUtils.isEmptyString(debtDateIssued)) {

                        params.put(DebtUtils.FIELD_DEBT_DATE_ISSUED, debtDateIssued);
                    }

                    // Check for debt date due
                    if (!DataUtils.isEmptyString(debtDateDue)) {

                        params.put(DebtUtils.FIELD_DEBT_DATE_DUE, debtDateDue);
                    }

                    // Check for debt description
                    if (!DataUtils.isEmptyString(debtDescription)) {

                        // Put debt description to Map params
                        params.put(DebtUtils.FIELD_DEBT_DESCRIPTION, debtDescription);
                    }

                    // Put userId and contactId to Map params
                    params.put(ContactUtils.FIELD_CONTACT_ID, contactId);
                    params.put(DebtUtils.FIELD_DEBT_ID, debtId);

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
                    NetworkTags.DebtsNetworkTags.TAG_UPDATE_CONTACTS_DEBT_STRING_REQUEST);

        } else {

            // Toast network connection message
            CustomToast.errorMessage(
                    mContext,
                    DataUtils.getStringResource(mContext,
                            R.string.error_network_connection_error_message_long),
                    R.drawable.ic_sad_cloud_100px_white);
        }
    }

    /**
     * Function to show / hide delete dates button when Dates are set or removed
     */
    private void showDeleteDatesButton() {

        // Check if DebtDateIssued is null
        if (DataUtils.isEmptyString(editDebtDateIssuedFull.getText().toString())) {

            // Hide delete date button
            imageDeleteSelectedDebtDateIssued.setVisibility(View.GONE);

        } else {

            // Show delete date button
            imageDeleteSelectedDebtDateIssued.setVisibility(View.VISIBLE);
        }

        // Check if DebtDateDue is null
        if (DataUtils.isEmptyString(editDebtDateDueFull.getText().toString())) {

            // Hide delete date button
            imageDeleteSelectedDebtDateDue.setVisibility(View.GONE);

        } else {

            // Show delete date button
            imageDeleteSelectedDebtDateDue.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void passDebtDateIssued(String debtDateIssuedFull, String debtDateIssuedShort) {

        this.editDebtDateIssuedFull.setText(debtDateIssuedFull); // Set full date
        this.shortDateDebtIssued = debtDateIssuedShort; // Set short date
    }

    @Override
    public void passDebtDateDue(String debtDateDueFull, String debtDateDueShort) {

        this.editDebtDateDueFull.setText(debtDateDueFull); // Set full date
        this.shortDateDebtDue = debtDateDueShort; // Set short date
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        switchUpdateButton(); // Switch update button on field value changed
        showDeleteDatesButton(); // Show / hide delete dates button
    }
}
