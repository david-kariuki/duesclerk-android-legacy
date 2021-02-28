package com.duesclerk.ui.fragment_contacts.fragment_people_owing_me;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duesclerk.R;
import com.duesclerk.custom.DeleteContactsDebts;
import com.duesclerk.custom.custom_utilities.application.BroadCastUtils;
import com.duesclerk.custom.custom_utilities.application.ViewsUtils;
import com.duesclerk.custom.custom_utilities.user_data.DataUtils;
import com.duesclerk.custom.custom_views.dialog_fragments.dialogs.DialogFragment_AddContact;
import com.duesclerk.custom.custom_views.recycler_view_adapters.RVLA_Contacts;
import com.duesclerk.custom.custom_views.swipe_refresh.MultiSwipeRefreshLayout;
import com.duesclerk.custom.custom_views.toast.CustomToast;
import com.duesclerk.custom.custom_views.view_decorators.Decorators;
import com.duesclerk.custom.java_beans.JB_Contacts;
import com.duesclerk.custom.network.InternetConnectivity;
import com.duesclerk.custom.storage_adapters.UserDatabase;
import com.duesclerk.interfaces.Interface_Contacts;
import com.duesclerk.interfaces.Interface_IDS;
import com.duesclerk.interfaces.Interface_MainActivity;
import com.duesclerk.ui.fragment_contacts.FetchContactsClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FragmentPeopleOwingMe extends Fragment implements Interface_Contacts, Interface_IDS,
        Interface_MainActivity {

    private Context mContext;
    private ArrayList<JB_Contacts> fetchedContacts = null;
    private MultiSwipeRefreshLayout swipeRefreshLayout;
    private MultiSwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private BroadcastReceiver bcrReloadContacts;
    private RecyclerView recyclerView;
    private LinearLayout llNoConnectionBar, llNoConnectionLayout, llNoContacts;
    private Interface_MainActivity interfaceMainActivity;
    private UserDatabase database;
    private FetchContactsClass fetchContactsClass;
    private RVLA_Contacts rvlaContacts;
    private DeleteContactsDebts deleteContactsOrDebts;
    private ImageView imageDeleteContacts, imageHideCheckBoxes;
    private FloatingActionButton fabAddContact, fabDeleteSelectedContacts;
    private DialogFragment_AddContact dialogFragmentAddContact;
    private TextView textTotalDebtsAmount;
    private String searchQuery = "";

    public static FragmentPeopleOwingMe newInstance() {
        return new FragmentPeopleOwingMe();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

        interfaceMainActivity = (Interface_MainActivity) mContext; // Initialize interface
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_people_owing_me, container,
                false);

        mContext = requireActivity(); // Get context

        // Views
        recyclerView = view.findViewById(R.id.recyclerViewPeopleOwingMe);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshPeopleOwingMe);
        swipeRefreshLayout.setSwipeableChildren(recyclerView.getId()); // Set swipeable children
        llNoConnectionBar = view.findViewById(R.id.llNoConnectionBar);
        llNoConnectionLayout = view.findViewById(R.id.llPeopleOwingMe_NoConnection);
        LinearLayout llNoConnection_TryAgain = view.findViewById(R.id.llNoConnection_TryAgain);
        llNoContacts = view.findViewById(R.id.llContacts_NoContacts);
        LinearLayout llAddContact = view.findViewById(R.id.llNoContacts_AddContact);
        textTotalDebtsAmount = view.findViewById(R.id.textPeopleOwingMe_DebtsTotalAmount);

        // ImageViews
        imageDeleteContacts = view.findViewById(R.id.imagePeopleOwingMe_DeleteContacts);
        imageHideCheckBoxes = view.findViewById(R.id.imagePeopleOwingMe_HideCheckBoxes);

        // FloatingActionButtons
        fabAddContact = view.findViewById(R.id.fabPeopleOwingMe_AddContact);
        fabDeleteSelectedContacts = view.findViewById(R.id.fabPeopleOwingMe_DeleteContacts);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                RecyclerView.VERTICAL, false);

        // Initialize And Set Item Decorator
        Decorators decorators = new Decorators(FragmentPeopleOwingMe.this);

        recyclerView.addItemDecoration(decorators); // Add item decoration
        recyclerView.setLayoutManager(layoutManager); // Set layout manager
        recyclerView.setHasFixedSize(false); // Set has fixed size to false

        // Initialize class objects
        database = new UserDatabase(mContext);
        fetchContactsClass = new FetchContactsClass(mContext,
                FragmentPeopleOwingMe.this);

        // Initialize add contact dialog fragment
        dialogFragmentAddContact = new DialogFragment_AddContact(mContext,
                0);

        // Initialize interface
        interfaceMainActivity = (Interface_MainActivity) getActivity();

        // SwipeRefreshLayout listener
        swipeRefreshListener = () -> {

            if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
                // Network connection established

                handleNetworkConnectionEvent(true);

                // Check if list adapter is null
                if (rvlaContacts != null) {

                    // Check if CheckBoxes are showing
                    if (!DataUtils.isEmptyArrayList(fetchedContacts)
                            && rvlaContacts.showingCheckBoxes()) {

                        swipeRefreshLayout.setRefreshing(false); // Stop SwipeRefresh
                        return; // Break
                    }
                }

                if (!DataUtils.isEmptyArrayList(fetchedContacts)) {

                    fetchedContacts.clear(); // Clear contacts array
                }

                // Fetch contacts
                fetchContactsClass.fetchContacts(
                        database.getUserAccountInfo(null).get(0).getUserId(),
                        swipeRefreshLayout, swipeRefreshListener);

            } else {
                // No internet connection

                handleNetworkConnectionEvent(false);
            }
        };

        // Set refresh listener to SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);

        // Broadcast receiver
        bcrReloadContacts = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction(); // Get action

                if (action.equals(BroadCastUtils.bcrActionReloadPeopleOwingMe)) {

                    // Check if adapter is null
                    if (rvlaContacts != null) {

                        // Empty selected contact ids ArrayList
                        DataUtils.clearArrayList(rvlaContacts.checkedContactsIds);
                    }

                    if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
                        // Network connection established

                        handleNetworkConnectionEvent(true);

                        // Fetch contacts
                        fetchContactsClass.fetchContacts(
                                database.getUserAccountInfo(null).get(0).getUserId(),
                                swipeRefreshLayout, swipeRefreshListener);

                    } else {
                        // No internet connection

                        handleNetworkConnectionEvent(false);
                    }
                }
            }
        };

        // Initialize Class
        deleteContactsOrDebts = new DeleteContactsDebts(FragmentPeopleOwingMe.this);

        // Add contact onClick
        llAddContact.setOnClickListener(v -> {

            // Show add person DialogFragment
            ViewsUtils.showDialogFragment(getParentFragmentManager(),
                    dialogFragmentAddContact, true);
        });

        // Try again onClick
        llNoConnection_TryAgain.setOnClickListener(v -> {

            // Start/Stop swipe SwipeRefresh
            ViewsUtils.showSwipeRefreshLayout(true, true, swipeRefreshLayout,
                    swipeRefreshListener);
        });

        // Add contact onClick
        fabAddContact.setOnClickListener(v -> {

            // Show add person DialogFragment
            ViewsUtils.showDialogFragment(getParentFragmentManager(), dialogFragmentAddContact,
                    true);
        });

        // FAB delete contacts onClick
        fabDeleteSelectedContacts.setOnClickListener(v -> {

            // Get selected contact ids
            String[] contactIds = rvlaContacts.getCheckedContactsIds();

            // Check if contact ids
            if (!DataUtils.isEmptyStringArray(contactIds)) {

                // Delete multiple contacts
                deleteContactsOrDebts.confirmAndDeleteContactsOrDebts(true,
                        null,
                        database.getUserAccountInfo(null).get(0).getUserId(),
                        contactIds);
            }
        });

        // Delete contacts onClick
        imageDeleteContacts.setOnClickListener(v -> {

            // Check if CheckBoxes are not showing
            if (!rvlaContacts.showingCheckBoxes()) {

                fabAddContact.setVisibility(View.GONE); // Hide add contact FAB
                showDeleteButton(false); // Hide delete button
                rvlaContacts.setShownListCheckBoxes(true); // Show list CheckBoxes
            }

            // Set SearchView hidden to true and hide SearchView
            interfaceMainActivity.setToHiddenAndHideSearchView(true,
                    FragmentPeopleOwingMe.this);
        });

        // Hide CheckBoxes onClick
        imageHideCheckBoxes.setOnClickListener(v -> {

            // Check if CheckBoxes are showing
            if (rvlaContacts.showingCheckBoxes()) {

                rvlaContacts.setShownListCheckBoxes(false); // Hide list CheckBoxes
                showDeleteButton(true); // Show delete button
                showFabAddContact(true); // Show FAB add contact
                showFabDeleteSelectedContacts(false); // Hide FAB delete selected contact records
            }

            // Set SearchView hidden to false and show SearchView
            interfaceMainActivity.setToHiddenAndHideSearchView(false,
                    FragmentPeopleOwingMe.this);
        });


        // Create ItemTouchHelper call back
        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NotNull RecyclerView recyclerView,
                                  RecyclerView.@NotNull ViewHolder viewHolder,
                                  RecyclerView.@NotNull ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.@NotNull ViewHolder viewHolder, int direction) {
                switch (direction) {
                    case ItemTouchHelper.LEFT:

                        rvlaContacts.setExpandedContactOptionsMenu(true,
                                viewHolder.getAdapterPosition());
                        break;

                    case ItemTouchHelper.RIGHT:

                        rvlaContacts.setExpandedContactOptionsMenu(false,
                                viewHolder.getAdapterPosition());
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView,
                                    RecyclerView.@NotNull ViewHolder viewHolder, float dX,
                                    float dY, int actionState, boolean isCurrentlyActive) {
            }
        };

        // Initialize ItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);

        // Attach ItemTouchHelper to RecyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Fetch contacts
        ViewsUtils.showSwipeRefreshLayout(true, true, swipeRefreshLayout,
                      swipeRefreshListener);

        return view; // Return inflated view
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        @SuppressWarnings("unused") ViewModel_PeopleOwingMe mViewModel = new ViewModelProvider(
                this).get(ViewModel_PeopleOwingMe.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Register broadcast
        BroadCastUtils.registerBroadCasts(requireActivity(), bcrReloadContacts,
                BroadCastUtils.bcrActionReloadPeopleOwingMe);

        // Start SwipeRefreshLayout
        //ViewsUtils.showSwipeRefreshLayout(true, true, swipeRefreshLayout,
          //      swipeRefreshListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister BroadcastReceiver
        BroadCastUtils.unRegisterBroadCast(requireActivity(), bcrReloadContacts);
    }

    /**
     * Function to load contacts into RecyclerView
     *
     * @param contacts - ArrayList with user contacts
     */
    private void loadContacts(ArrayList<JB_Contacts> contacts) {

        if (!DataUtils.isEmptyArrayList(contacts)) {

            this.fetchedContacts = contacts; // Set ArrayList

            // Creating RecyclerView adapter object
            rvlaContacts = new RVLA_Contacts(contacts,
                    FragmentPeopleOwingMe.this);

            // Check for adapter observers
            if (!rvlaContacts.hasObservers()) {

                rvlaContacts.setHasStableIds(true); // Set has stable ids
            }

            recyclerView.setAdapter(rvlaContacts); // Setting Adapter to RecyclerView
            rvlaContacts.notifyDataSetChanged(); // Notify Data Set Changed

            // Filter text input
            if (!DataUtils.isEmptyString(searchQuery)) {

                rvlaContacts.getFilter().filter(this.searchQuery); // Set adapter filter query
            }

            showDeleteButton(true); // Show delete button
            showFabAddContact(true); // Show add contact FAB

            showSwipeRefreshLayout(true); // Show main layout

        } else {

            imageDeleteContacts.setVisibility(View.GONE); // Hide delete multiple debts button

            // Hide add contact FAB for user to use add debt layout button
            showFabAddContact(false);
        }
    }

    /**
     * Function to show or hide SwipeRefreshLayout
     *
     * @param show - boolean - (show/hide SwipeRefreshLayout)
     */
    private void showSwipeRefreshLayout(boolean show) {

        if (show) {

            swipeRefreshLayout.setVisibility(View.VISIBLE); // Show SwipeRefreshLayout

        } else {

            swipeRefreshLayout.setVisibility(View.GONE); // Hide SwipeRefreshLayout
        }
    }

    /**
     * Function to show or hide no contacts layout
     *
     * @param show - boolean - (show/hide no contacts layout)
     */
    private void showNoContactsLayout(boolean show) {

        if (show) {

            llNoContacts.setVisibility(View.VISIBLE); // Show RecyclerView

        } else {

            llNoContacts.setVisibility(View.GONE); // Hide RecyclerView
        }

        showSwipeRefreshLayout(!show); // Show / Hide SwipeRefreshLayout
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
            ViewsUtils.showSwipeRefreshLayout(false, false, swipeRefreshLayout,
                    swipeRefreshListener);

            // Check for contacts
            if (DataUtils.isEmptyArrayList(fetchedContacts)) {

                showSwipeRefreshLayout(false); // Hide SwipeRefreshLayout
            }

            showSwipeRefreshLayout(false); // Hide SwipeRefreshLayout

            // Check if contacts ArrayList is null
            if (DataUtils.isEmptyArrayList(fetchedContacts)) {

                llNoConnectionLayout.setVisibility(View.VISIBLE); // Show no connection layout

            } else {

                llNoConnectionBar.setVisibility(View.VISIBLE); // Show no connection bar
            }

            // Toast connection error message
            CustomToast.errorMessage(mContext,
                    DataUtils.getStringResource(mContext,
                            R.string.error_network_connection_error_message_short),
                    R.drawable.ic_sad_cloud_100px_white);
        } else {
            // Connection established

            llNoConnectionBar.setVisibility(View.GONE); // Hide no connection bar
            llNoConnectionLayout.setVisibility(View.GONE); // Hide no connection layout
        }
    }

    /**
     * Function to set search query received from MainActivity
     *
     * @param searchQuery - SearchView query
     */
    public void setSearchQuery(String searchQuery) {

        try {

            this.searchQuery = searchQuery; // Set received search query to global search query

            // Filter text input
            rvlaContacts.getFilter().filter(searchQuery);

        } catch (Exception ignored) {
        }
    }

    /**
     * Function to show / hide delete button and (Hide CheckBoxes) button
     *
     * @param show - Show / hide delete button
     */
    private void showDeleteButton(boolean show) {

        if (show) {

            imageDeleteContacts.setVisibility(View.VISIBLE); // Show delete button
            imageHideCheckBoxes.setVisibility(View.GONE); // Hide (Hide delete) button

        } else {

            imageDeleteContacts.setVisibility(View.GONE); // HIde delete button
            imageHideCheckBoxes.setVisibility(View.VISIBLE); // Show (Hide delete) button
        }
    }

    /**
     * Function to show / hide delete selected debts fab
     *
     * @param show - boolean - (show / hide view)
     */
    private void showFabDeleteSelectedContacts(boolean show) {

        if (show) {

            fabDeleteSelectedContacts.setVisibility(View.VISIBLE); // Show FAB

        } else {

            fabDeleteSelectedContacts.setVisibility(View.GONE); // Hide FAB
        }
    }

    /**
     * Function to show / hide add contacts fab
     *
     * @param show - boolean - (show / hide view)
     */
    private void showFabAddContact(boolean show) {

        if (show) {

            fabAddContact.setVisibility(View.VISIBLE); // Show FAB
            showFabDeleteSelectedContacts(false); // Hide delete contacts FAB

        } else {

            fabAddContact.setVisibility(View.GONE); // Hide FAB
        }
    }

    @Override
    public void showAddContactDialogFragment(boolean show) {

    }

    @Override
    public void setToHiddenAndHideSearchView(boolean setToHiddenAndHide, Fragment fragment) {

    }

    /**
     * Listener to receive contacts ArrayList
     *
     * @param contacts - ArrayList with contacts
     */
    @Override
    public void passUserContacts_PeopleOwingMe(ArrayList<JB_Contacts> contacts) {

        // Check if ArrayList is empty
        if (!DataUtils.isEmptyArrayList(contacts)) {
            // ArrayList not empty

            loadContacts(contacts); // Load contacts to RecyclerView

            // Pass contacts to MainActivity
            interfaceMainActivity.passUserContacts_PeopleOwingMe(contacts);
        }
    }

    /**
     * Listener to receive contacts ArrayList
     *
     * @param contacts - ArrayList with contacts
     */
    @Override
    public void passUserContacts_PeopleIOwe(ArrayList<JB_Contacts> contacts) {
    }

    @Override
    public void passPeopleOwingMeDebtsTotal(String peopleOwingMeDebtsTotal) {

        // Set total debts amount
        textTotalDebtsAmount.setText(
                DataUtils.getStringResource(mContext, R.string.label_debts_total_amount,
                        peopleOwingMeDebtsTotal)
        );
    }

    @Override
    public void passPeopleIOweDebtsTotal(String peopleIOweDebtsTotal) {

    }

    /**
     * Interface method to set contacts PeopleOwingMe to empty
     */
    @Override
    public void setPeopleOwingMeContactsEmpty(boolean empty) {

        showNoContactsLayout(empty); // Show or hide no contacts layout

        // Set contact empty
        interfaceMainActivity.setPeopleOwingMeContactsEmpty(empty);

        // Check if value is true
        if (empty) {

            showFabAddContact(false); // Hide add debt FAB
        }
    }

    /**
     * Interface method to set contacts PeopleIOwe to empty
     */
    @Override
    public void setPeopleIOweContactsEmpty(boolean notFound) {

        // Set FragmentPeopleIOwe contacts to empty
        interfaceMainActivity.setPeopleIOweContactsEmpty(notFound);
        fabDeleteSelectedContacts.setVisibility(View.GONE); // Hide FAB delete selected contacts
    }

    @Override
    public void showFabDeleteContacts(boolean show) {

        showFabDeleteSelectedContacts(show); // Show / hide fab delete selected debts
    }

    @Override
    public void passContactsIds(String[] contactsIds) {

        // Delete contact(s)
        deleteContactsOrDebts.confirmAndDeleteContactsOrDebts(true,
                null,
                database.getUserAccountInfo(null).get(0).getUserId(),
                contactsIds);

        // Empty selected debt ids ArrayList
        DataUtils.clearArrayList(rvlaContacts.checkedContactsIds);
    }

    @Override
    public void passDebtsIds(String[] debtsIds) {
    }
}
