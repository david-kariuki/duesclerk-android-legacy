package com.duesclerk.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.duesclerk.custom.custom_utilities.BroadCastUtils;
import com.duesclerk.custom.custom_utilities.ContactUtils;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.DebtUtils;
import com.duesclerk.custom.custom_utilities.UserAccountUtils;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_utilities.VolleyUtils;
import com.duesclerk.custom.custom_views.dialog_fragments.dialogs.DialogFragment_AddDebt;
import com.duesclerk.custom.custom_views.dialog_fragments.dialogs.DialogFragment_UpdateContact;
import com.duesclerk.custom.custom_views.recycler_view_adapters.RVLA_Debts;
import com.duesclerk.custom.custom_views.swipe_refresh.MultiSwipeRefreshLayout;
import com.duesclerk.custom.custom_views.toast.CustomToast;
import com.duesclerk.custom.custom_views.view_decorators.Decorators;
import com.duesclerk.custom.java_beans.JB_Debts;
import com.duesclerk.custom.network.InternetConnectivity;
import com.duesclerk.custom.network.NetworkTags;
import com.duesclerk.custom.network.NetworkUrls;
import com.duesclerk.custom.storage_adapters.UserDatabase;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactDetailsAndDebtsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    // Get class simple name
    // private final String TAG = ContactDetailsAndDebtsActivity.class.getSimpleName();

    RelativeLayout rlNoConnection;
    FloatingActionButton fabAddDebt;
    RVLA_Debts rvlaDebts;
    private Context mContext;
    private TextView textTitle, textContactFullName, textContactPhoneNumber, textContactEmailAddress,
            textContactAddress, textNoDebtMessage, textDebtsTotalAmount;
    private MultiSwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private JSONArray fetchedContactDetails;
    private ArrayList<JB_Debts> debtRecords;
    private AppBarLayout appBarLayout;
    private UserDatabase database;
    private String contactId, contactType, contactFullName;
    private ShimmerFrameLayout shimmerContactDetails;
    private LinearLayout llContactDetails;
    private LinearLayout llNoDebts;
    private RecyclerView recyclerView;
    private BroadcastReceiver bcrReloadDebts;
    private SearchView searchView;
    private DialogFragment_UpdateContact dialogFragmentEditContact;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details_and_debts);

        mContext = this; // Get application context

        ImageView imageExit = findViewById(R.id.imageContactDetailsAndDebtsActivity_Exit);
        textTitle = findViewById(R.id.textContactDetailsAndDebtsActivity_Title);
        appBarLayout = findViewById(R.id.appBarLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshContactDetailsAndDebtsActivity);

        LinearLayout llEditContact = findViewById(
                R.id.llContactDetailsAndDebtsActivity_EditContact);
        LinearLayout llDeleteContact = findViewById(
                R.id.llContactDetailsAndDebtsActivity_DeleteContact);

        llContactDetails = findViewById(R.id.llContactDetailsAndDebtsActivity_ContactDetails);
        LinearLayout llNoConnectionTryAgain = findViewById(R.id.llNoConnection_TryAgain);
        llNoDebts = findViewById(R.id.llDebts_NoDebt);
        LinearLayout llAddDebt = findViewById(R.id.llNoDebts_AddDebt);

        rlNoConnection = findViewById(R.id.rlContactDetailsAndDebtsActivity_NoConnection);

        recyclerView = findViewById(R.id.recyclerViewDebts);
        fabAddDebt = findViewById(R.id.fabContactDetailsAndDebtsActivity_AddDebt);

        textContactFullName = findViewById(R.id.textContactDetailsAndDebtsActivity_ContactFullName);
        textContactPhoneNumber = findViewById(R.id.textContactDetailsAndDebtsActivity_ContactPhoneNumber);
        textContactEmailAddress = findViewById(R.id.textContactDetailsAndDebtsActivity_ContactEmailAddress);
        textContactAddress = findViewById(R.id.textContactDetailsAndDebtsActivity_ContactAddress);
        textDebtsTotalAmount = findViewById(R.id.textContactDetailsAndDebtsActivity_DebtsTotalAmount);
        textNoDebtMessage = findViewById(R.id.textNoDebt_Message);

        shimmerContactDetails = findViewById(R.id.shimmerContactDetailsAndDebtsActivity);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL,
                false);

        // Initialize And Set Item Decorator
        Decorators decorators = new Decorators(this);

        recyclerView.addItemDecoration(decorators); // Add item decoration
        recyclerView.setLayoutManager(layoutManager); // Set layout manager
        recyclerView.setHasFixedSize(false); // Set has fixed size to false

        // Initialize ProgressDialog
        progressDialog = ViewsUtils.initProgressDialog(ContactDetailsAndDebtsActivity.this,
                false);

        // Broadcast receiver
        bcrReloadDebts = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction(); // Get action

                // Check for BroadCast action
                if (action.equals(BroadCastUtils.bcrActionReloadContactDetailsAndDebtsActivity)) {

                    // Start/Stop swipe SwipeRefresh
                    ViewsUtils.showSwipeRefreshLayout(true, swipeRefreshLayout,
                            swipeRefreshListener);
                }
            }
        };

        debtRecords = new ArrayList<>(); // Initialize ArrayList

        // Get intent and values passed
        Intent intent = getIntent();

        this.contactId = intent.getStringExtra(ContactUtils.FIELD_CONTACT_ID); // Get contact id
        this.contactFullName = intent.getStringExtra(ContactUtils.FIELD_CONTACT_FULL_NAME);
        this.contactType = intent.getStringExtra(ContactUtils.FIELD_CONTACT_TYPE);

        setActivityTitle(contactType, contactFullName); // Set activity title

        DialogFragment_AddDebt dialogFragmentAddDebt = new DialogFragment_AddDebt(mContext,
                contactId, contactFullName);
        dialogFragmentAddDebt.setCancelable(false); // Disable cancelable
        dialogFragmentAddDebt.setRetainInstance(true); // Set retain instance

        dialogFragmentEditContact = new DialogFragment_UpdateContact(
                mContext);
        dialogFragmentEditContact.setCancelable(false); // Disable cancelable
        dialogFragmentEditContact.setRetainInstance(true); // Set retain instance

        // Add CoordinatorLayout as swipeable child
        swipeRefreshLayout.setSwipeableChildren(R.id.coordinator);

        database = new UserDatabase(mContext); // Initialize database

        // SwipeRefreshLayout listener
        swipeRefreshListener =
                () -> {

                    if (!DataUtils.isEmptyJSONArray(fetchedContactDetails)) {
                        fetchedContactDetails = null; // Clear contact details JSONArray
                    }

                    if (!DataUtils.isEmptyString(contactId)) {

                        // Fetch contact details
                        fetchContactData(contactId);
                    }
                };

        // Add refresh listener to SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);

        // Set view offset
        swipeRefreshLayout.setProgressViewOffset(false,
                DataUtils.getIntegerResource(mContext, R.integer.int_swipe_refresh_offset_start),
                DataUtils.getIntegerResource(mContext, R.integer.int_swipe_refresh_offset_end));

        // Setup SearchView
        searchView = ViewsUtils.initSearchView(this, R.id.searchViewDebts);

        // Add query text listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String arg0) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                // Filter text input
                rvlaDebts.getFilter().filter(query);
                return false;
            }
        });

        // Exit onClick
        imageExit.setOnClickListener(v -> finish());

        // No connection try again onClick
        llNoConnectionTryAgain.setOnClickListener(v -> {

            // Start/Stop swipe SwipeRefresh
            ViewsUtils.showSwipeRefreshLayout(true, swipeRefreshLayout, swipeRefreshListener);
        });

        // FAB add debt onClick
        fabAddDebt.setOnClickListener(v -> {

            // Show add debt dialog
            ViewsUtils.showDialogFragment(getSupportFragmentManager(),
                    dialogFragmentAddDebt, true);
        });

        // LinearLayout add debt onClick
        llAddDebt.setOnClickListener(v -> {

            fabAddDebt.performClick(); // Click fab add debt
        });

        // Edit contact onClick - Show edit contact dialog
        llEditContact.setOnClickListener(v -> {

            // Show add debt dialog
            ViewsUtils.showDialogFragment(getSupportFragmentManager(),
                    dialogFragmentEditContact, true);
        });

        // Delete contact onClick - Show delete contact confirmation
        llDeleteContact.setOnClickListener(v -> showDialogDeleteContact());
    }

    @Override
    public void onStart() {
        super.onStart();

        // Register broadcast
        BroadCastUtils.registerBroadCasts(ContactDetailsAndDebtsActivity.this, bcrReloadDebts,
                BroadCastUtils.bcrActionReloadContactDetailsAndDebtsActivity);

        // Start/Stop swipe SwipeRefresh
        ViewsUtils.showSwipeRefreshLayout(true, swipeRefreshLayout, swipeRefreshListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister BroadcastReceiver
        BroadCastUtils.unRegisterBroadCast(ContactDetailsAndDebtsActivity.this,
                bcrReloadDebts);

        // Broadcast to refresh contacts
        Intent intentBroadcastPeopleOwingMe = new Intent(
                BroadCastUtils.bcrActionReloadPeopleOwingMe);
        Intent intentBroadcastPeopleIOwe = new Intent(
                BroadCastUtils.bcrActionReloadPeopleIOwe);

        // Send broadcasts
        sendBroadcast(intentBroadcastPeopleOwingMe);
        sendBroadcast(intentBroadcastPeopleIOwe);
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save dialog inputs to outState

        // Check for field values and set to outState
        if (!DataUtils.isEmptyString(contactId)) {

            outState.putString(ContactUtils.FIELD_CONTACT_ID, contactId); // Put contact id
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {

            // Get dialog inputs from savedInstanceState
            String savedContactId = savedInstanceState.getString(ContactUtils.FIELD_CONTACT_ID);

            // Check for values
            if (!DataUtils.isEmptyString(savedContactId)) {

                contactId = savedContactId;// Set contact id
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        appBarLayout.removeOnOffsetChangedListener(this); // Remove offset changed listener
    }

    @Override
    protected void onResume() {
        super.onResume();

        appBarLayout.addOnOffsetChangedListener(this); // Add offset changed listener
    }

    /**
     * Function to set and update activity title
     *
     * @param contactType     - String activity title
     * @param contactFullName - Contact full name
     */
    private void setActivityTitle(String contactType, String contactFullName) {

        String title = "";
        if (contactType.equals(ContactUtils.KEY_CONTACT_TYPE_PEOPLE_OWING_ME)) {

            title = DataUtils.getStringResource(mContext, R.string.title_debts_people_owing_me,
                    contactFullName);

        } else if (contactType.equals(ContactUtils.KEY_CONTACT_TYPE_PEOPLE_I_OWE)) {

            title = DataUtils.getStringResource(mContext, R.string.title_debts_people_i_owe,
                    contactFullName);
        }
        textTitle.setText(title); // Set activity title
    }

    /**
     * Function to show / hide SearchView
     *
     * @param show - boolean - (show / hide view)
     */
    private void showSearchView(boolean show) {

        if (show) {

            searchView.setVisibility(View.VISIBLE); // Show SearchView

        } else {

            searchView.setVisibility(View.GONE); // Hide SearchView
        }
    }

    /**
     * Function to show / hide add debt fab
     *
     * @param show - boolean - (show / hide view)
     */
    private void showAddDebtFab(boolean show) {

        if (show) {

            fabAddDebt.setVisibility(View.VISIBLE); // Show FAB

        } else {

            fabAddDebt.setVisibility(View.GONE); // Hide FAB
        }
    }

    /**
     * Function to show / hide contact details
     *
     * @param show - boolean - (show / hide view)
     */
    private void showContactDetails(boolean show) {

        if (show) {

            swipeRefreshLayout.setVisibility(View.VISIBLE); // Show SwipeRefreshLayout
            llContactDetails.setVisibility(View.VISIBLE); // Show contact details

        } else {

            llContactDetails.setVisibility(View.GONE); // Hide contact details
        }

        showNoConnectionLayout(!show); // Show / hide no connection layout
    }

    /**
     * Function to show / hide connection layout
     *
     * @param show - boolean - (show / hide view)
     */
    private void showNoConnectionLayout(boolean show) {

        if (show) {

            rlNoConnection.setVisibility(View.VISIBLE); // Show SwipeRefreshLayout
            swipeRefreshLayout.setVisibility(View.GONE); // Hide contact details

        } else {

            rlNoConnection.setVisibility(View.GONE); // Hide no connection layout
        }
    }

    /**
     * Function to show / hide RecyclerView
     *
     * @param show - boolean - (show / hide view)
     */
    private void showRecyclerView(boolean show) {

        if (show) {

            recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
            textDebtsTotalAmount.setVisibility(View.VISIBLE); // Show total debts amount
            showNoDebtsLayout(false); // Hide no debts layout

        } else {

            recyclerView.setVisibility(View.GONE); // Hide RecyclerView
            textDebtsTotalAmount.setVisibility(View.GONE); // Hide total debts amount
        }
    }

    /**
     * Function to show / hide debts layout
     *
     * @param show - boolean - (show / hide view)
     */
    private void showNoDebtsLayout(boolean show) {

        if (show) {

            String noDebtsMessage = DataUtils.getStringResource(mContext,
                    R.string.msg_debts_empty, contactFullName);

            textNoDebtMessage.setText(noDebtsMessage); // Set no debts message

            llNoDebts.setVisibility(View.VISIBLE); // Show no debts layout
            showRecyclerView(false); // Hide RecyclerView

        } else {

            llNoDebts.setVisibility(View.GONE); // Hide no debts layout
        }

        showSearchView(!show); // Show / Hide SearchView
    }

    /**
     * Function to respond to connection failures
     *
     * @param connected - boolean - (network connected / not connected)
     */
    private void handleNetworkConnectionEvent(boolean connected) {

        // Check connection state
        if (!connected) {
            // No connection

            // Hide swipe SwipeRefresh
            ViewsUtils.showSwipeRefreshLayout(false, swipeRefreshLayout,
                    swipeRefreshListener);

            showContactDetails(false); // Hide contact details

        }

        showNoConnectionLayout(!connected); // Show / hide no connection layout
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        // Check if SwipeRefreshLayout is refreshing
        if (swipeRefreshLayout.isRefreshing()) {

            swipeRefreshLayout.setEnabled(true); // Enable SwipeRefreshLayout

        } else {

            // Refresh will only be enabled when the offset is zero
            swipeRefreshLayout.setEnabled(verticalOffset == 0);
        }
    }

    /**
     * Function to create custom dialog to confirm contact deletion
     */
    private void showDialogDeleteContact() {

        Dialog dialog = new Dialog(this);
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_confirm_delete_contact, null,
                false);

        TextView textDialogMessage = dialogView.findViewById(R.id.textDeleteContact_DialogMessage);
        TextView textCancel = dialogView.findViewById(R.id.textDeleteContact_Cancel);
        TextView textDeleteContact = dialogView.findViewById(R.id.textDeleteContact_Delete);

        textDialogMessage.setText(DataUtils.getStringResource(mContext,
                R.string.msg_delete_contact_confirmation, this.contactFullName));

        // Cancel onClick - Dismiss dialog
        textCancel.setOnClickListener(v -> dialog.dismiss());

        // Delete contact onClick - Show delete contact confirmation
        textDeleteContact.setOnClickListener(v -> {

                    dialog.dismiss(); // Dismiss dialog

                    // Delete contact
                    this.deleteContact(
                            database.getUserAccountInfo(null).get(0).getUserId(),
                            new String[]{this.contactId});
                }
        );

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove window title
        dialog.setContentView(dialogView); // Set content view
        dialog.create(); // Create dialog
        dialog.show(); // Show dialog
    }

    /**
     * Function to fetch/retrieve contact data
     *
     * @param contactId - contact id
     */
    private void fetchContactData(String contactId) {

        // Check internet connection state
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            handleNetworkConnectionEvent(true); // Set connection established to true

            // Create string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUrls.ContactURLS.URL_FETCH_CONTACT_DETAILS_AND_DEBTS, response -> {

                // Log Response
                // Log.d(TAG, "Fetching contact data response:" + response);

                showAddDebtFab(false); // Hide add debt FAB

                // Hide SwipeRefreshLayout
                ViewsUtils.showSwipeRefreshLayout(false,
                        swipeRefreshLayout, swipeRefreshListener);

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                    // Check for error
                    if (!error) {
                        // Contact data fetched

                        extractContactDetails(jsonObject); // Extract contact details

                        // Get JSONArray From JSONObject
                        JSONArray contactDebts;
                        contactDebts = jsonObject.getJSONArray(
                                DebtUtils.KEY_DEBTS);

                        // Split JSONArray to get contact debts records
                        extractDebtsJSONArray(contactDebts);

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
                                NetworkTags.Contacts.TAG_FETCH_CONTACT_DETAILS_AND_DEBTS_STRING_REQUEST);
                    }
                } catch (Exception ignored) {
                }
            }, volleyError -> {

                // Log Response
                // Log.e(TAG, "Fetch contact data response error : "
                // + volleyError.getMessage());

                // Hide SwipeRefreshLayout
                ViewsUtils.showSwipeRefreshLayout(false,
                        swipeRefreshLayout, swipeRefreshListener);


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
                        NetworkUrls.ContactURLS.URL_FETCH_CONTACT_DETAILS_AND_DEBTS);
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

                    // Put userId and contactId to Map params
                    params.put(UserAccountUtils.FIELD_USER_ID,
                            database.getUserAccountInfo(null).get(0).getUserId());
                    params.put(ContactUtils.FIELD_CONTACT_ID, contactId);

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
                    NetworkTags.Contacts.TAG_FETCH_CONTACT_DETAILS_AND_DEBTS_STRING_REQUEST);

        } else {

            handleNetworkConnectionEvent(false); // Handle no connection event

            // Toast network connection message
            CustomToast.errorMessage(
                    mContext,
                    DataUtils.getStringResource(mContext,
                            R.string.error_network_connection_error_message_long),
                    R.drawable.ic_sad_cloud_100px_white);
        }
    }

    /**
     * Function to extract debts from jsonArray
     *
     * @param jsonObject - JSONObject to get contact details JSONObject
     */
    private void extractContactDetails(JSONObject jsonObject) {

        // Check if JSONObject is empty
        if (!DataUtils.isEmptyJSONObject(jsonObject)) {

            try {

                // Get JSONObject from JSONObject
                JSONObject contactDetails = jsonObject.getJSONObject(
                        ContactUtils.KEY_CONTACT_DETAILS);

                // Get contact details
                this.contactFullName = contactDetails.getString(
                        ContactUtils.FIELD_CONTACT_FULL_NAME);
                String contactPhoneNumber = contactDetails.getString(
                        ContactUtils.FIELD_CONTACT_PHONE_NUMBER);
                String contactEmailAddress = contactDetails.getString(
                        ContactUtils.FIELD_CONTACT_EMAIL_ADDRESS);
                String contactAddress = contactDetails.getString(
                        ContactUtils.FIELD_CONTACT_ADDRESS);
                String debtsTotalAmount = DataUtils.getStringResource(
                        mContext,
                        R.string.label_debts_total_amount,
                        contactDetails.getString(DebtUtils.FIELD_DEBTS_TOTAL_AMOUNT)
                );

                setActivityTitle(contactType, contactFullName); // Update activity title

                // Set contact details
                this.textContactFullName.setText(contactFullName);
                this.textContactPhoneNumber.setText(contactPhoneNumber);
                this.textContactEmailAddress.setText(contactEmailAddress);
                this.textContactAddress.setText(contactAddress);
                this.textDebtsTotalAmount.setText(debtsTotalAmount);

                // Show ShimmerFrameLayout
                ViewsUtils.showShimmerFrameLayout(false, shimmerContactDetails);

                showContactDetails(true); // Show contact details

                showAddDebtFab(true); // Show add debt FAB

                // Pass contact details to
                dialogFragmentEditContact.setContactDetails(contactId, contactFullName,
                        contactPhoneNumber, contactEmailAddress, contactAddress);

            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Function to extract debts from jsonArray
     *
     * @param jsonArray - JSONArray with debts
     */
    private void extractDebtsJSONArray(JSONArray jsonArray) {

        if (jsonArray != null) {

            if (jsonArray.length() > 0) {
                // Looping through all the elements of the json array

                debtRecords.clear(); // Clear ArrayList

                for (int i = 0; i < jsonArray.length(); i++) {

                    // Creating a json object of the current index
                    JSONObject jsonObject;
                    JB_Debts jbDebts = new JB_Debts();

                    try {

                        // Getting Data json object
                        jsonObject = jsonArray.getJSONObject(i);

                        // Getting Data from json object
                        String debtId, debtAmount, debtDateIssued, debtDateDue, debtDescription,
                                contactId, contactType, userId;

                        debtId = jsonObject.getString(DebtUtils.FIELD_DEBT_ID);
                        debtAmount = jsonObject.getString(DebtUtils.FIELD_DEBT_AMOUNT);
                        debtDateIssued = jsonObject.getString(DebtUtils.FIELD_DEBT_DATE_ISSUED);
                        debtDateDue = jsonObject.getString(DebtUtils.FIELD_DEBT_DATE_DUE);
                        debtDescription = jsonObject.getString(DebtUtils.FIELD_DEBT_DESCRIPTION);
                        contactId = jsonObject.getString(ContactUtils.FIELD_CONTACT_ID);
                        contactType = jsonObject.getString(ContactUtils.FIELD_CONTACT_TYPE);
                        userId = jsonObject.getString(UserAccountUtils.FIELD_USER_ID);

                        // Set data to java bean
                        jbDebts.setDebtId(debtId);
                        jbDebts.setDebtAmount(debtAmount);
                        jbDebts.setDebtDateIssued(debtDateIssued);
                        jbDebts.setDebtDateDue(debtDateDue);
                        jbDebts.setDebtDescription(debtDescription);
                        jbDebts.setContactId(contactId);
                        jbDebts.setContactType(contactType);
                        jbDebts.setUserId(userId);

                        // Add java bean to ArrayList
                        debtRecords.add(jbDebts);

                    } catch (Exception ignored) {
                    }
                }

                // Check for fetched debt records
                if (!DataUtils.isEmptyArrayList(debtRecords)) {
                    // Debt records found

                    showNoConnectionLayout(false); // Hide no debts view
                    showRecyclerView(true); // Show RecyclerView

                    // Creating RecyclerView adapter object
                    rvlaDebts = new RVLA_Debts(mContext, debtRecords);

                    // Check for adapter observers
                    if (!rvlaDebts.hasObservers()) {

                        rvlaDebts.setHasStableIds(true); // Set has stable ids
                    }

                    recyclerView.setAdapter(rvlaDebts); // Setting Adapter to RecyclerView
                    rvlaDebts.notifyDataSetChanged(); // Notify Data Set Changed

                    showSearchView(true); // Show SearchView
                } else {

                    showNoDebtsLayout(true); // Show no debts view
                }
            } else {

                showNoDebtsLayout(true); // Show no debts view
            }
        } else {

            showNoDebtsLayout(true); // Show no debts view
        }
    }

    /**
     * Function to add contact to remote database
     *
     * @param userId      - Users id
     * @param contactsIds - Contact id
     */
    private void deleteContact(final String userId, final String[] contactsIds) {

        // Check Internet Connection State
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            ViewsUtils.hideKeyboard(ContactDetailsAndDebtsActivity.this); // Hide keyboard if showing

            // Show dialog
            ViewsUtils.showProgressDialog(progressDialog,
                    DataUtils.getStringResource(mContext, R.string.title_updating_contact),
                    DataUtils.getStringResource(mContext, R.string.msg_updating_contact)
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
                        // New contact added successfully

                        JSONObject jsonObjectDeleteContact = jsonObject
                                .getJSONObject(VolleyUtils.KEY_DELETE_CONTACTS);

                        if (!DataUtils.isEmptyString(jsonObjectDeleteContact
                                .getString(VolleyUtils.KEY_SUCCESS_MESSAGE))) {

                            // Show success message
                            String plurals = "";
                            if (contactsIds.length > 1) {
                                plurals = "s";
                            }

                            CustomToast.infoMessage(mContext, DataUtils.getStringResource(mContext,
                                    R.string.msg_contact_deleted_successfully, plurals),
                                    false,
                                    R.drawable.ic_baseline_person_remove_alt_1_24_white);

                            finish(); // Exit activity
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
                                NetworkTags.Contacts.TAG_DELETE_CONTACTS_STRING_REQUEST);
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
                    params.put(ContactUtils.KEY_CONTACTS_IDS, contactId);

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
                    NetworkTags.Contacts.TAG_UPDATE_CONTACT_STRING_REQUEST);

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
