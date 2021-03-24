package com.duesclerk.classes.custom_views.fragments.dialog_fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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
import com.duesclerk.classes.custom_utilities.user_data.InputFiltersUtils;
import com.duesclerk.classes.custom_utilities.user_data.UserAccountUtils;
import com.duesclerk.classes.custom_views.toast.CustomToast;
import com.duesclerk.classes.network.InternetConnectivity;
import com.duesclerk.classes.network.NetworkTags;
import com.duesclerk.classes.network.NetworkUrls;
import com.duesclerk.classes.storage_adapters.UserDatabase;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DialogFragment_UpdateContact extends DialogFragment {

    // Get class simple name
    // private final String TAG = DialogFragment_UpdateContact.class.getSimpleName();

    private final LayoutInflater inflater;
    private final Context mContext;
    private String contactId, contactFullName, contactPhoneNumber, contactEmailAddress,
            contactAddress;
    private EditText editContactFullName, editContactPhoneNumber, editContactEmailAddress, editContactAddress;
    private ProgressDialog progressDialog;

    /**
     * Class constructor
     *
     * @param context - Context
     */
    public DialogFragment_UpdateContact(Context context) {

        this.mContext = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Class constructor
     *
     * @param contactId           - Contact id
     * @param contactFullName     - Contact full name
     * @param contactPhoneNumber  - Contact phone number
     * @param contactEmailAddress - Contact email address
     * @param contactAddress      - Contact address
     */
    public void setContactDetails(final String contactId, final String contactFullName,
                                  final String contactPhoneNumber,
                                  final String contactEmailAddress,
                                  final String contactAddress) {

        this.contactId = contactId;
        this.contactFullName = contactFullName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.contactEmailAddress = contactEmailAddress;
        this.contactAddress = contactAddress;
    }

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialogEditContact = super.onCreateDialog(savedInstanceState);

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(
                R.layout.dialog_update_contact, null, false);

        // EditTexts
        this.editContactFullName = dialogView.findViewById(R.id.editUpdateContact_FullName);
        this.editContactPhoneNumber = dialogView.findViewById(R.id.editUpdateContact_PhoneNumber);
        this.editContactEmailAddress = dialogView.findViewById(R.id.editUpdateContact_EmailAddress);
        this.editContactAddress = dialogView.findViewById(R.id.editUpdateContact_Address);

        // LinearLayouts
        LinearLayout llCancel = dialogView.findViewById(R.id.llEditContact_Cancel);
        LinearLayout llEditContact = dialogView.findViewById(R.id.llUpdateContact_Update);

        // Initialize ProgressDialog
        this.progressDialog = ViewsUtils.initProgressDialog(requireActivity(), false);

        UserDatabase database = new UserDatabase(mContext); // Initialize user database object

        // Set contact details to dialog fields
        this.editContactFullName.setText(this.contactFullName);
        this.editContactPhoneNumber.setText(this.contactPhoneNumber);
        this.editContactEmailAddress.setText(this.contactEmailAddress);
        this.editContactAddress.setText(this.contactAddress);

        // Cancel onClick
        llCancel.setOnClickListener(v -> dismiss()); // Dismiss dialog

        // Add contact onClick
        llEditContact.setOnClickListener(v -> {

            // Check fields
            if (checkFieldInputs()) {
                // Fields ok

                String userId, newContactFullName, newContactPhoneNumber,
                        newContactEmailAddress = "", newContactAddress = "";

                // Check for email address
                if (!DataUtils.isEmptyEditText(editContactEmailAddress)) {

                    // Get email address
                    newContactEmailAddress = editContactEmailAddress.getText().toString();
                }

                // Check for contact address
                if (!DataUtils.isEmptyEditText(editContactAddress)) {

                    // Get contact address
                    newContactAddress = editContactAddress.getText().toString();
                }

                userId = database.getUserAccountInfo(null).get(0).getUserId();
                newContactFullName = editContactFullName.getText().toString();
                newContactPhoneNumber = editContactPhoneNumber.getText().toString();

                // Add/Upload  contact
                this.updateUserContactDetails(userId, contactId, newContactFullName,
                        newContactPhoneNumber, newContactEmailAddress, newContactAddress);

            }
        });

        // Remove window title
        dialogEditContact.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Set width to match parent and height to wrap content
        Window window = dialogEditContact.getWindow();
        window.setLayout(ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT);

        // Set dialog transparent background
        dialogEditContact.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEditContact.setContentView(dialogView);

        return dialogEditContact;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            // Get dialog inputs from savedInstanceState
            String savedContactFullName =
                    savedInstanceState.getString(ContactUtils.FIELD_CONTACT_FULL_NAME);
            String savedContactPhoneNumber =
                    savedInstanceState.getString(ContactUtils.FIELD_CONTACT_PHONE_NUMBER);
            String savedContactEmailAddress =
                    savedInstanceState.getString(ContactUtils.FIELD_CONTACT_EMAIL_ADDRESS);
            String savedContactAddress =
                    savedInstanceState.getString(ContactUtils.FIELD_CONTACT_ADDRESS);

            // Check for values and set to EditTexts
            if (!DataUtils.isEmptyString(savedContactFullName)) {

                // Set contacts full name
                editContactFullName.setText(savedContactFullName);
            }
            if (!DataUtils.isEmptyString(savedContactPhoneNumber)) {

                // Set contacts phone number
                editContactFullName.setText(savedContactPhoneNumber);
            }
            if (!DataUtils.isEmptyString(savedContactEmailAddress)) {

                // Set contacts email address
                editContactFullName.setText(savedContactEmailAddress);
            }
            if (!DataUtils.isEmptyString(savedContactAddress)) {

                // Set contacts address
                editContactFullName.setText(savedContactAddress);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save dialog inputs to outState

        // Check for field values and set to outState
        if (!DataUtils.isEmptyEditText(editContactFullName)) {

            // Get and put contacts full name
            outState.putString(ContactUtils.FIELD_CONTACT_FULL_NAME,
                    editContactFullName.getText().toString());
        }

        if (!DataUtils.isEmptyEditText(editContactPhoneNumber)) {

            // Get and put contacts phone number
            outState.putString(ContactUtils.FIELD_CONTACT_PHONE_NUMBER,
                    editContactPhoneNumber.getText().toString());
        }

        if (!DataUtils.isEmptyEditText(editContactEmailAddress)) {

            // Get and put contacts email address
            outState.putString(ContactUtils.FIELD_CONTACT_EMAIL_ADDRESS,
                    editContactEmailAddress.getText().toString());
        }

        if (!DataUtils.isEmptyEditText(editContactAddress)) {

            // Get and put contacts address
            outState.putString(ContactUtils.FIELD_CONTACT_ADDRESS,
                    editContactAddress.getText().toString());
        }
    }

    /**
     * Function to check field lengths and values and notify by toast on error
     */
    private boolean checkFieldInputs() {
        boolean fieldOk;

        fieldOk = (InputFiltersUtils.checkFullNameLengthNotify(mContext, editContactFullName)
                && InputFiltersUtils.checkPhoneNumberValidNotify(mContext, editContactPhoneNumber)
        );

        // Validate email address
        if (!DataUtils.isEmptyEditText(editContactEmailAddress)) {
            fieldOk = InputFiltersUtils.checkEmailAddressValidNotify(mContext, editContactEmailAddress);
        }

        return fieldOk;
    }

    /**
     * Function to add contact to remote database
     *
     * @param userId                 - Users id
     * @param contactId              - Contact id
     * @param newContactFullName     - Contact full name
     * @param newContactPhoneNumber  - Contact phone number
     * @param newContactEmailAddress - Contact email address
     * @param newContactAddress      - Contact address
     */
    private void updateUserContactDetails(final String userId, final String contactId,
                                          final String newContactFullName,
                                          final String newContactPhoneNumber,
                                          final String newContactEmailAddress,
                                          final String newContactAddress) {

        // Check Internet Connection states
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            ViewsUtils.hideKeyboard(requireActivity()); // Hide keyboard if showing

            // Show dialog
            ViewsUtils.showProgressDialog(progressDialog,
                    DataUtils.getStringResource(mContext, R.string.title_updating_contact),
                    DataUtils.getStringResource(mContext, R.string.msg_updating_contact)
            );

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUrls.ContactURLS.URL_UPDATE_CONTACT, response -> {

                // Log Response
                // Log.d(TAG, "Update contact response:" + response);

                ViewsUtils.dismissProgressDialog(progressDialog); // Hide Dialog

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);
                    JSONObject jsonObjectUpdateContact =
                            jsonObject.getJSONObject(VolleyUtils.KEY_UPDATE_CONTACT);
                    // Check for error
                    if (!error) {
                        // New contact added successfully

                        if (!DataUtils.isEmptyString(jsonObjectUpdateContact
                                .getString(VolleyUtils.KEY_SUCCESS_MESSAGE))) {
                            // Show success message
                            CustomToast.infoMessage(mContext,
                                    DataUtils.getStringResource(mContext,
                                            R.string.msg_updating_contact_successful),
                                    false,
                                    R.drawable.ic_baseline_person_add_alt_1_24_white);

                            try {

                                // Send broadcast to set switch account type action text
                                Intent intentBroadcast = new Intent(BroadCastUtils
                                        .bcrActionReloadContactDetailsAndDebtsActivity);

                                requireActivity().sendBroadcast(intentBroadcast); // Send broadcast

                            } finally {

                                dismiss(); // Dismiss dialog
                            }
                        }
                    } else {
                        // Error adding contact

                        String errorMessage = jsonObject.getString(
                                VolleyUtils.KEY_ERROR_MESSAGE);

                        // Toast error message
                        CustomToast.errorMessage(
                                mContext, errorMessage,
                                R.drawable.ic_baseline_person_add_alt_1_24_white);

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkTags.ContactsNetworkTags.TAG_UPDATE_CONTACT_STRING_REQUEST);
                    }
                } catch (Exception ignored) {
                }
            }, volleyError -> {

                // Log Response
                // Log.e(TAG, "Add contact response error : "
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
                        NetworkUrls.ContactURLS.URL_UPDATE_CONTACT);
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
                    @SuppressWarnings({"unchecked", "rawtypes"}) Map<String, String> params =
                            new HashMap();

                    // Put userId to Map params
                    params.put(UserAccountUtils.FIELD_USER_ID, userId);

                    // Put contact info to Map params
                    params.put(ContactUtils.FIELD_CONTACT_ID, contactId);
                    params.put(ContactUtils.FIELD_CONTACT_FULL_NAME, newContactFullName);
                    params.put(ContactUtils.FIELD_CONTACT_PHONE_NUMBER, newContactPhoneNumber);

                    // Check for set email address
                    if (!DataUtils.isEmptyEditText(editContactEmailAddress)) {

                        params.put(ContactUtils.FIELD_CONTACT_EMAIL_ADDRESS, newContactEmailAddress);
                    }

                    // Check for set contact address
                    if (!DataUtils.isEmptyEditText(editContactAddress)) {

                        params.put(ContactUtils.FIELD_CONTACT_ADDRESS, newContactAddress);
                    }

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
                    NetworkTags.ContactsNetworkTags.TAG_UPDATE_CONTACT_STRING_REQUEST);

        } else {

            // Toast network connection message
            CustomToast.errorMessage(
                    mContext,
                    DataUtils.getStringResource(mContext,
                            R.string.error_network_connection_error_message_long),
                    R.drawable.ic_sad_cloud_100px_white);
        }
    }
}
