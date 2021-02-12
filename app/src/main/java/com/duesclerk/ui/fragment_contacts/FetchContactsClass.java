package com.duesclerk.ui.fragment_contacts;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.duesclerk.R;
import com.duesclerk.custom.custom_utilities.ApplicationClass;
import com.duesclerk.custom.custom_utilities.ContactUtils;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.DebtUtils;
import com.duesclerk.custom.custom_utilities.UserAccountUtils;
import com.duesclerk.custom.custom_utilities.VolleyUtils;
import com.duesclerk.custom.custom_views.swipe_refresh.MultiSwipeRefreshLayout;
import com.duesclerk.custom.custom_views.toast.CustomToast;
import com.duesclerk.custom.java_beans.JB_Contacts;
import com.duesclerk.custom.network.InternetConnectivity;
import com.duesclerk.custom.network.NetworkTags;
import com.duesclerk.custom.network.NetworkUrls;
import com.duesclerk.interfaces.Interface_Contacts;
import com.duesclerk.ui.fragment_contacts.fragment_people_i_owe.FragmentPeople_I_Owe;
import com.duesclerk.ui.fragment_contacts.fragment_people_owing_me.FragmentPeopleOwingMe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FetchContactsClass {

    private final String TAG = FetchContactsClass.class.getSimpleName();
    private final Context mContext;
    private final Interface_Contacts interfaceContacts;
    private final MultiSwipeRefreshLayout swipeRefreshLayout;

    /**
     * Class constructor for FragmentPeopleOwingMe
     *
     * @param context               - Class context
     * @param fragmentPeopleOwingMe - Calling fragment
     */
    public FetchContactsClass(Context context, FragmentPeopleOwingMe fragmentPeopleOwingMe) {

        mContext = context; // Initialize context
        interfaceContacts = fragmentPeopleOwingMe; // Initialize interface

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = new MultiSwipeRefreshLayout(context);

        // Set color scheme
        swipeRefreshLayout.setColorSchemeColors(DataUtils.getSwipeRefreshColorSchemeResources());
        showSwipeRefresh(); // Start swipe refresh
    }

    /**
     * Class constructor for FragmentPeopleIOwe
     *
     * @param context            - Class context
     * @param fragmentPeopleIOwe - Calling fragment
     */
    public FetchContactsClass(Context context, FragmentPeople_I_Owe fragmentPeopleIOwe) {

        mContext = context; // Initialize context
        interfaceContacts = fragmentPeopleIOwe; // Initialize interface

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = new MultiSwipeRefreshLayout(context);

        // Set color scheme
        swipeRefreshLayout.setColorSchemeColors(DataUtils.getSwipeRefreshColorSchemeResources());
    }

    /**
     * Function to fetch contacts
     *
     * @param userId - Users id
     */
    public void fetchContacts(final String userId) {

        // Check Internet Connection State
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            showSwipeRefresh(); // Start swipe refresh

            // Create string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUrls.ContactURLS.URL_FETCH_USER_CONTACTS, response -> {

                swipeRefreshLayout.setRefreshing(false); // Stop swipe refresh

                // Log Response
                // Log.d(TAG, "Fetching contacts response:" + response);

                swipeRefreshLayout.setRefreshing(false); // Hide SwipeRefreshLayout

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                    // Check for error
                    if (!error) {
                        // Contacts fetched

                        // Get JSONArray From JSONObject
                        JSONArray jsonArray = jsonObject.getJSONArray(
                                ContactUtils.KEY_CONTACTS);

                        // Split JSONArray to get (People owing me) and (People I owe) contacts
                        extractSortJSONArray(jsonArray);

                    } else {
                        // Error updating details

                        String errorMessage = jsonObject.getString(
                                VolleyUtils.KEY_ERROR_MESSAGE);

                        // Toast error message
                        CustomToast.errorMessage(
                                mContext,
                                errorMessage,
                                R.drawable.ic_baseline_edit_24_white);

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkTags.ContactsNetworkTags.TAG_FETCH_USER_CONTACTS_STRING_REQUEST);
                    }
                } catch (Exception ignored) {
                }
            }, volleyError -> {

                // Log Response
                //Log.e(TAG, "Fetch contacts response error : " + volleyError.getMessage());

                swipeRefreshLayout.setRefreshing(false); // Hide SwipeRefreshLayout

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
                        NetworkUrls.ContactURLS.URL_FETCH_USER_CONTACTS);
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
                    NetworkTags.ContactsNetworkTags.TAG_FETCH_USER_CONTACTS_STRING_REQUEST);

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
     * Function to split contacts JSONArray into (People owing me) and (People I owe) arrays
     *
     * @param jsonArray - Contacts JSONArray
     */
    private void extractSortJSONArray(JSONArray jsonArray) {

        ArrayList<JB_Contacts> contactsPeopleOwingMe = new ArrayList<>();
        ArrayList<JB_Contacts> contactsPeopleIOwe = new ArrayList<>();

        if (jsonArray != null) {

            if (jsonArray.length() > 0)
                // Looping through all the elements of the json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Creating a json object of the current index
                    JSONObject jsonObject;
                    JB_Contacts jbContacts = new JB_Contacts();

                    try {

                        // Getting Data json object
                        jsonObject = jsonArray.getJSONObject(i);

                        // Getting Data from json object
                        String contactId, contactFullName, contactPhoneNumber, contactEmailAddress,
                                contactAddress, contactType, contactsTotalDebtsAmount;

                        contactId = jsonObject.getString(ContactUtils.FIELD_CONTACT_ID);
                        contactFullName = jsonObject.getString(ContactUtils.FIELD_CONTACT_FULL_NAME);
                        contactPhoneNumber = jsonObject.getString(
                                ContactUtils.FIELD_CONTACT_PHONE_NUMBER);
                        contactEmailAddress = jsonObject.getString(
                                ContactUtils.FIELD_CONTACT_EMAIL_ADDRESS);
                        contactAddress = jsonObject.getString(ContactUtils.FIELD_CONTACT_ADDRESS);
                        contactType = jsonObject.getString(ContactUtils.FIELD_CONTACT_TYPE);
                        contactsTotalDebtsAmount = jsonObject.getString(
                                DebtUtils.FIELD_DEBTS_TOTAL_AMOUNT);

                        // Check if debts total amount is null to insert 0
                        if (DataUtils.isEmptyString(contactsTotalDebtsAmount)) {
                            contactsTotalDebtsAmount = "0";
                        }

                        // Set data to java bean
                        jbContacts.setContactId(contactId);
                        jbContacts.setContactFullName(contactFullName);
                        jbContacts.setContactPhoneNumber(contactPhoneNumber);
                        jbContacts.setContactEmailAddress(contactEmailAddress);
                        jbContacts.setContactAddress(contactAddress);
                        jbContacts.setContactType(contactType);
                        jbContacts.setDebtsTotalAmount(contactsTotalDebtsAmount);

                        if (contactType.equals(ContactUtils.KEY_CONTACT_TYPE_PEOPLE_OWING_ME)) {
                            // People owing me

                            contactsPeopleOwingMe.add(jbContacts); // Add java bean to ArrayList

                        } else if (contactType.equals(ContactUtils.KEY_CONTACT_TYPE_PEOPLE_I_OWE)) {
                            // People I owe

                            contactsPeopleIOwe.add(jbContacts); // Add java bean to ArrayList
                        }

                    } catch (Exception ignored) {
                    }
                }

            // Pass data to interface
            passContactDataToInterface(contactsPeopleOwingMe, contactsPeopleIOwe);
        }
    }

    /**
     * Function to pass contacts data to interface
     *
     * @param contactsPeopleOwingMe - ArrayList with PeopleOwingMe contacts
     * @param contactsPeopleIOwe    - ArrayList with PeopleIOwe contacts
     */
    private void passContactDataToInterface(ArrayList<JB_Contacts> contactsPeopleOwingMe,
                                            ArrayList<JB_Contacts> contactsPeopleIOwe) {

        // Check for contacts
        if (!DataUtils.isEmptyArrayList(contactsPeopleOwingMe)) {

            // Pass contacts to interface
            interfaceContacts.passUserContacts_PeopleOwingMe(contactsPeopleOwingMe);

            // Set no contacts found to false
            interfaceContacts.setPeopleOwingMeContactsEmpty(false);

        } else {

            // Set no contacts found to true
            interfaceContacts.setPeopleOwingMeContactsEmpty(true);
        }

        // Check for contacts
        if (!DataUtils.isEmptyArrayList(contactsPeopleIOwe)) {

            // Pass contacts to interface
            interfaceContacts.passUserContacts_PeopleIOwe(contactsPeopleIOwe);

            // Set no contacts found to false
            interfaceContacts.setPeopleIOweContactsEmpty(false);

        } else {

            // Set no contacts found to true
            interfaceContacts.setPeopleIOweContactsEmpty(true);
        }
    }

    private void showSwipeRefresh() {

        // Start swipe refresh
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
    }
}
