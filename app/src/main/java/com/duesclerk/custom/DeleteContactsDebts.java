package com.duesclerk.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.duesclerk.R;
import com.duesclerk.activities.ContactDetailsAndDebtsActivity;
import com.duesclerk.custom.custom_utilities.ApplicationClass;
import com.duesclerk.custom.custom_utilities.BroadCastUtils;
import com.duesclerk.custom.custom_utilities.ContactUtils;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.DebtUtils;
import com.duesclerk.custom.custom_utilities.UserAccountUtils;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_utilities.VolleyUtils;
import com.duesclerk.custom.custom_views.toast.CustomToast;
import com.duesclerk.custom.network.InternetConnectivity;
import com.duesclerk.custom.network.NetworkTags;
import com.duesclerk.custom.network.NetworkUrls;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DeleteContactsDebts {

    // Get class simple name
    // private final String TAG = DeleteContactsDebts.class.getSimpleName();

    private final Context mContext;
    private final Activity activity;
    private final ProgressDialog progressDialog;
    private String userId, contactIdForDebtsDeletion;
    private String[] contactsIdsToDelete, debtsIdsToDelete;

    /**
     * Class constructor
     */
    public DeleteContactsDebts(final ContactDetailsAndDebtsActivity activity) {

        this.activity = activity;
        this.mContext = activity.getApplicationContext();

        // Initialize ProgressDialog
        progressDialog = ViewsUtils.initProgressDialog(activity, false);
    }

    /**
     * Function to create custom dialog to confirm contact deletion
     */
    public void confirmAndDeleteContactsOrDebts(final boolean isContact,
                                                final String contactFullName,
                                                String userOrContactId,
                                                String[] contactsOrDebtsIds) {

        Dialog dialog = new Dialog(activity);
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_confirm_delete_contact_or_debt, null,
                false);

        TextView textDialogTitle = dialogView.findViewById(R.id.textDeleteContactOrDebt_Title);
        TextView textDialogMessage = dialogView.findViewById(R.id.textDeleteContactOrDebt_DialogMessage);
        TextView textCancel = dialogView.findViewById(R.id.textDeleteContactOrDebt_Cancel);
        TextView textDeleteContactOrDebt = dialogView.findViewById(R.id.textDeleteContactOrDebt_Delete);

        String dialogTitle, dialogMessage;

        if (isContact) {

            // Get dialog title
            dialogTitle = DataUtils.getStringResource(mContext, R.string.label_delete_contact);

            // Get dialog message
            dialogMessage = DataUtils.getStringResource(mContext,
                    R.string.msg_delete_contact_confirmation, contactFullName);

        } else {

            // Get dialog title
            dialogTitle = DataUtils.getStringResource(mContext, R.string.label_delete_debt);

            // Get dialog message
            dialogMessage = DataUtils.getStringResource(mContext,
                    R.string.msg_delete_debt_confirmation, contactFullName);
        }

        textDialogTitle.setText(dialogTitle); // Set dialog title
        textDialogMessage.setText(dialogMessage); // Set dialog message

        // Cancel onClick - Dismiss dialog
        textCancel.setOnClickListener(v -> dialog.dismiss());

        // Delete contact onClick - Show delete contact confirmation
        textDeleteContactOrDebt.setOnClickListener(v -> {

            // Set details

            dialog.dismiss(); // Dismiss dialog

            if (isContact) {

                this.userId = userOrContactId;
                contactsIdsToDelete = contactsOrDebtsIds; // Set ids to contact ids

                deleteContacts(); // Delete contacts

            } else {

                this.contactIdForDebtsDeletion = userOrContactId;
                debtsIdsToDelete = contactsOrDebtsIds; // Set ids to debts ids

                deleteDebts(); // Delete debts
            }
        });

        // Set width to match parent and height to wrap content
        Window window = dialog.getWindow();

        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove window title

        // Set dialog transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(dialogView); // Set content view
        dialog.create(); // Create dialog
        dialog.show(); // Show dialog
    }

    /**
     * Function to delete contact
     */
    public void deleteContacts() {

        // Check Internet Connection State
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            // Hide keyboard if showing
            ViewsUtils.hideKeyboard(activity);

            // Show dialog
            ViewsUtils.showProgressDialog(progressDialog,
                    DataUtils.getStringResource(mContext, R.string.title_deleting_contact),
                    DataUtils.getStringResource(mContext, R.string.msg_deleting_contact)
            );

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUrls.ContactURLS.URL_DELETE_CONTACTS, response -> {

                // Log Response
                // Log.d(TAG, "Delete contacts response:" + response);

                ViewsUtils.dismissProgressDialog(progressDialog); // Hide Dialog

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                    // Check for error
                    if (!error) {
                        // Contacts deleted successfully

                        JSONObject jsonObjectDeleteContact = jsonObject
                                .getJSONObject(VolleyUtils.KEY_DELETE_CONTACTS);

                        if (!DataUtils.isEmptyString(jsonObjectDeleteContact
                                .getString(VolleyUtils.KEY_SUCCESS_MESSAGE))) {

                            // Show success message
                            String plurals = "";
                            if (contactsIdsToDelete.length > 1) {
                                plurals = "s";
                            }

                            CustomToast.infoMessage(mContext, DataUtils.getStringResource(mContext,
                                    R.string.msg_contact_deleted_successfully, plurals),
                                    false,
                                    R.drawable.ic_baseline_person_remove_alt_1_24_white);

                            activity.finish(); // Exit activity
                        }
                    } else {
                        // Error deleting contact

                        String errorMessage = jsonObject.getString(
                                VolleyUtils.KEY_ERROR_MESSAGE);

                        // Toast error message
                        CustomToast.errorMessage(
                                mContext, errorMessage,
                                R.drawable.ic_baseline_person_add_alt_1_24_white);

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkTags.ContactsNetworkTags.TAG_DELETE_CONTACTS_STRING_REQUEST);
                    }
                } catch (Exception ignored) {
                }
            }, volleyError -> {

                // Log Response
                // Log.e(TAG, "Delete contacts response error : "
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
                        NetworkUrls.ContactURLS.URL_DELETE_CONTACTS);
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

                    // Put contact id to Map params
                    params.put(ContactUtils.KEY_CONTACTS_IDS, Arrays.toString(contactsIdsToDelete));

                    // Put userId to Map params
                    params.put(UserAccountUtils.FIELD_USER_ID, userId);

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
                    NetworkTags.ContactsNetworkTags.TAG_DELETE_CONTACTS_STRING_REQUEST);

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
     * Function to delete contacts debt
     */
    public void deleteDebts() {

        // Check Internet Connection State
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            // Hide keyboard if showing
            ViewsUtils.hideKeyboard(activity);

            // Show dialog
            ViewsUtils.showProgressDialog(progressDialog,
                    DataUtils.getStringResource(mContext, R.string.title_deleting_debt),
                    DataUtils.getStringResource(mContext, R.string.msg_deleting_debt)
            );

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUrls.DebtsURLS.URL_DELETE_CONTACTS_DEBTS, response -> {

                // Log Response
                // Log.d(TAG, "Delete debts response:" + response);

                ViewsUtils.dismissProgressDialog(progressDialog); // Hide Dialog

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                    // Check for error
                    if (!error) {
                        // Contacts debts deleted successfully

                        JSONObject jsonObjectDeleteDebt = jsonObject
                                .getJSONObject(VolleyUtils.KEY_DELETE_DEBTS);

                        if (!DataUtils.isEmptyString(jsonObjectDeleteDebt
                                .getString(VolleyUtils.KEY_SUCCESS_MESSAGE))) {

                            // Show success message
                            String plurals = "";
                            if (debtsIdsToDelete.length > 1) {
                                plurals = "s";
                            }

                            CustomToast.infoMessage(mContext, DataUtils.getStringResource(mContext,
                                    R.string.msg_debt_deleted_successfully, plurals),
                                    false,
                                    R.drawable.ic_baseline_attach_money_24_white);

                            // Send broadcast to set switch account type action text
                            Intent intentBroadcast = new Intent(BroadCastUtils
                                    .bcrActionReloadContactDetailsAndDebtsActivity);

                            activity.sendBroadcast(intentBroadcast); // Send broadcast
                        }
                    } else {
                        // Error deleting contacts debts

                        String errorMessage = jsonObject.getString(
                                VolleyUtils.KEY_ERROR_MESSAGE);

                        // Toast error message
                        CustomToast.errorMessage(
                                mContext, errorMessage,
                                R.drawable.ic_baseline_attach_money_24_white);

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkTags.DebtsNetworkTags.TAG_DELETE_CONTACTS_DEBTS_STRING_REQUEST);
                    }
                } catch (Exception ignored) {
                }
            }, volleyError -> {

                // Log Response
                // Log.e(TAG, "Delete debts response error : "
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
                        NetworkUrls.DebtsURLS.URL_DELETE_CONTACTS_DEBTS);
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

                    // Put debts ids to Map params
                    params.put(DebtUtils.KEY_DEBTS_IDS, Arrays.toString(debtsIdsToDelete));

                    // Put userId to Map params
                    params.put(ContactUtils.FIELD_CONTACT_ID, contactIdForDebtsDeletion);

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
                    NetworkTags.DebtsNetworkTags.TAG_DELETE_CONTACTS_DEBTS_STRING_REQUEST);

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
