package com.duesclerk.classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

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
import com.duesclerk.classes.custom_utilities.application.ApplicationClass;
import com.duesclerk.classes.custom_utilities.application.BroadCastUtils;
import com.duesclerk.classes.custom_utilities.application.ViewsUtils;
import com.duesclerk.classes.custom_utilities.application.VolleyUtils;
import com.duesclerk.classes.custom_utilities.user_data.ContactUtils;
import com.duesclerk.classes.custom_utilities.user_data.DataUtils;
import com.duesclerk.classes.custom_utilities.user_data.DebtUtils;
import com.duesclerk.classes.custom_utilities.user_data.UserAccountUtils;
import com.duesclerk.classes.custom_views.toast.CustomToast;
import com.duesclerk.classes.network.InternetConnectivity;
import com.duesclerk.classes.network.NetworkTags;
import com.duesclerk.classes.network.NetworkUrls;
import com.duesclerk.ui.fragment_contacts.fragment_people_i_owe.FragmentPeople_I_Owe;
import com.duesclerk.ui.fragment_contacts.fragment_people_owing_me.FragmentPeopleOwingMe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeleteContactsDebts {

    // Get class simple name
    private final String TAG = DeleteContactsDebts.class.getSimpleName();

    private final Context mContext;
    private final ProgressDialog progressDialog;
    private final boolean calledByActivity;
    private Activity activity;
    private String userId, contactIdForDebtsDeletion;
    private String[] contactsIdsToDelete, debtsIdsToDelete;
    private Fragment fragment;

    /**
     * Class constructor for activities
     *
     * @param activity - Calling activity
     */
    public DeleteContactsDebts(final ContactDetailsAndDebtsActivity activity) {

        this.activity = activity;
        this.mContext = activity.getApplicationContext();
        this.calledByActivity = true; // Set called by an Activity to true

        // Initialize ProgressDialog
        progressDialog = ViewsUtils.initProgressDialog(activity, false);
    }

    /**
     * Class constructor for FragmentPeopleOwingMe
     *
     * @param peopleOwingMe - Calling activity
     */
    public DeleteContactsDebts(final FragmentPeopleOwingMe peopleOwingMe) {

        this.fragment = peopleOwingMe;
        this.mContext = peopleOwingMe.requireContext();
        this.calledByActivity = false; // Set called by an Activity to false

        // Initialize ProgressDialog
        progressDialog = ViewsUtils.initProgressDialog(peopleOwingMe.requireActivity(),
                false);
    }

    /**
     * Class constructor for FragmentPeopleIOwe
     *
     * @param fragmentPeopleIOwe - Calling activity
     */
    public DeleteContactsDebts(final FragmentPeople_I_Owe fragmentPeopleIOwe) {

        this.fragment = fragmentPeopleIOwe;
        this.mContext = fragmentPeopleIOwe.requireContext();
        this.calledByActivity = false; // Set called by an Activity to false

        // Initialize ProgressDialog
        progressDialog = ViewsUtils.initProgressDialog(fragmentPeopleIOwe.requireActivity(),
                false);
    }

    /**
     * Function to create custom dialog to confirm contact deletion
     *
     * @param isContact          - Deleting contact or debt
     * @param contactFullName    - Contacts full name
     * @param userOrContactId    - User id or contact id
     * @param contactsOrDebtsIds - Contacts or debts ids
     */
    public void confirmAndDeleteContactsOrDebts(final boolean isContact,
                                                final String contactFullName,
                                                String userOrContactId,
                                                String[] contactsOrDebtsIds) {

        Dialog dialog; // Create custom dialog

        if (calledByActivity) {

            dialog = new Dialog(activity); // Initialize Dialog

        } else {

            dialog = new Dialog(fragment.requireActivity()); // Initialize Dialog
        }

        // Create and initialize layout inflater
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

            // Check if contact name is null
            if (contactFullName != null) {

                // Get dialog message
                dialogMessage = DataUtils.getStringResource(mContext,
                        R.string.msg_delete_contact_confirmation, contactFullName);
            } else {

                // Get dialog message
                dialogMessage = DataUtils.getStringResource(mContext,
                        R.string.msg_delete_contact_confirmation,
                        DataUtils.getStringResource(mContext, R.string.label_the_selected_contacts));
            }

        } else {

            // Get dialog title
            dialogTitle = DataUtils.getStringResource(mContext, R.string.label_delete_debt);

            // Check if contact name is null
            if (contactFullName != null) {

                // Get dialog message
                dialogMessage = DataUtils.getStringResource(mContext,
                        R.string.msg_delete_debt_confirmation, contactFullName);
            } else {

                // Get dialog message
                dialogMessage = DataUtils.getStringResource(mContext,
                        R.string.msg_delete_contact_confirmation,
                        DataUtils.getStringResource(mContext, R.string.label_the_selected_debts));
            }
        }

        textDialogTitle.setText(dialogTitle); // Set dialog title
        textDialogMessage.setText(dialogMessage); // Set dialog message

        // Cancel onClick - Dismiss dialog
        textCancel.setOnClickListener(v -> dialog.dismiss());

        // Delete contact onClick - Show delete contact confirmation
        textDeleteContactOrDebt.setOnClickListener(v -> {

            dialog.dismiss(); // Dismiss dialog

            if (isContact) {

                this.userId = userOrContactId; // Set user id
                DataUtils.clearStringArray(contactsIdsToDelete);
                contactsIdsToDelete = contactsOrDebtsIds; // Set ids to contact ids

                deleteContacts(); // Delete contacts

            } else {

                this.contactIdForDebtsDeletion = userOrContactId; // Set contact id
                DataUtils.clearStringArray(debtsIdsToDelete);
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

        // Check Internet Connection States
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            // Hide keyboard if showing
            if (calledByActivity) {
                // Called by Activities

                ViewsUtils.hideKeyboard(activity); // Hide keyboard

            } else {
                // Called by fragments

                ViewsUtils.hideKeyboard(fragment.requireActivity()); // Hide keyboard
            }

            // Show dialog
            ViewsUtils.showProgressDialog(progressDialog,
                    DataUtils.getStringResource(mContext, R.string.title_deleting_contact),
                    DataUtils.getStringResource(mContext, R.string.msg_deleting_contact)
            );

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUrls.ContactURLS.URL_DELETE_CONTACTS, response -> {

                // Log Response
                Log.d(TAG, "Delete contacts response:" + response);

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

                            // Create refresh broadcasts broadcast
                            Intent intentBroadcast = null;

                            // Check if delete was called by Activity
                            if (calledByActivity) {
                                // Delete called by an activity

                                // Check Activity instance
                                if (activity instanceof ContactDetailsAndDebtsActivity) {
                                    // Activity instance of ContactDetailsAndDebtsActivity

                                    // Initialize refresh broadcasts broadcast
                                    intentBroadcast = new Intent(BroadCastUtils
                                            .bcrActionReloadContactDetailsAndDebtsActivity);
                                }
                            } else {
                                // Delete called by a fragment

                                // Check Fragment instance
                                if (fragment instanceof FragmentPeopleOwingMe) {
                                    // Fragment instance of FragmentPeopleOwingMe

                                    // Initialize refresh broadcasts broadcast
                                    intentBroadcast = new Intent(BroadCastUtils
                                            .bcrActionReloadPeopleOwingMe);

                                } else if (fragment instanceof FragmentPeople_I_Owe) {
                                    // Fragment instance of FragmentPeopleIOwe

                                    // Initialize refresh broadcasts broadcast
                                    intentBroadcast = new Intent(BroadCastUtils
                                            .bcrActionReloadPeopleIOwe);
                                }
                            }

                            // Check if intent broadcast is null
                            if (intentBroadcast != null) {
                                // Intent broadcast not null

                                // Send broadcast
                                if (calledByActivity) {

                                    activity.sendBroadcast(intentBroadcast); // Send broadcast

                                } else {

                                    // Send broadcast
                                    fragment.requireActivity().sendBroadcast(intentBroadcast);
                                }
                            }
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
                //      + volleyError.getMessage());

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

                    // Create ContactsIds JSONArray
                    JSONArray contactsIds = new JSONArray();

                    // Loop through contacts ids
                    for (String s : contactsIdsToDelete) {

                        contactsIds.put(s); // Add contact ids to contactIds array
                    }

                    // Put contact id to Map params
                    params.put(ContactUtils.KEY_CONTACTS_IDS, contactsIds.toString());

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

        // Check Internet Connection States
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            // Hide keyboard if showing
            if (calledByActivity) {
                // Called by Activities

                ViewsUtils.hideKeyboard(activity); // Hide keyboard

            } else {
                // Called by fragments

                ViewsUtils.hideKeyboard(fragment.requireActivity()); // Hide keyboard
            }

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

                    // Create JSONObject
                    JSONObject jsonObject = new JSONObject(response);

                    // Get error from JSONObject
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                    // Check for error
                    if (!error) {
                        // Contacts debts deleted successfully

                        // Get JSONObject
                        JSONObject jsonObjectDeleteDebt = jsonObject
                                .getJSONObject(VolleyUtils.KEY_DELETE_DEBTS);

                        // Check for success message
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

                            // Send broadcast
                            if (calledByActivity) {

                                activity.sendBroadcast(intentBroadcast); // Send broadcast

                            } else {

                                // Send broadcast
                                fragment.requireActivity().sendBroadcast(intentBroadcast);
                            }
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

                    // Create debts ids JSONArray
                    JSONArray debtsIds = new JSONArray();

                    // Loop through debts ids
                    for (String s : debtsIdsToDelete) {

                        // Add debt id to debts ids array
                        debtsIds.put(s);
                    }

                    // Put debts ids to Map params
                    params.put(DebtUtils.KEY_DEBTS_IDS, debtsIds.toString());

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
