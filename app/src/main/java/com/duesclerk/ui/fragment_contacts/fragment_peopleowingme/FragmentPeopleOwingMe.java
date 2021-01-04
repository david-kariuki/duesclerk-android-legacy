package com.duesclerk.ui.fragment_contacts.fragment_peopleowingme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duesclerk.R;
import com.duesclerk.custom.custom_utilities.BroadCastUtils;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_views.recycler_view_adapters.RVLA_Contacts;
import com.duesclerk.custom.custom_views.swipe_refresh.MultiSwipeRefreshLayout;
import com.duesclerk.custom.custom_views.view_decorators.Decorators;
import com.duesclerk.custom.java_beans.JB_Contacts;
import com.duesclerk.custom.network.InternetConnectivity;
import com.duesclerk.custom.storage_adapters.UserDatabase;
import com.duesclerk.interfaces.Interface_Contacts;
import com.duesclerk.interfaces.Interface_MainActivity;
import com.duesclerk.ui.fragment_contacts.FetchContactsClass;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FragmentPeopleOwingMe extends Fragment implements Interface_Contacts {

    private Context mContext;
    private ArrayList<JB_Contacts> fetchedContacts = null;
    private MultiSwipeRefreshLayout swipeRefreshLayout;
    private MultiSwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private BroadcastReceiver bcrReloadContacts;
    private RecyclerView recyclerView;
    private LinearLayout llNoConnection, llNoContacts;
    private Interface_MainActivity interfaceMainActivity;
    private UserDatabase database;
    private FetchContactsClass fetchContactsClass;
    private boolean isReload = false;
    private boolean animateSwipeRefresh = false;

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
        llNoConnection = view.findViewById(R.id.llNoConnectionBar);
        LinearLayout llNoConnection_TryAgain = view.findViewById(R.id.llNoConnection_TryAgain);
        llNoContacts = view.findViewById(R.id.llContacts_NoContacts);
        LinearLayout llAddContact = view.findViewById(R.id.llNoContacts_AddContact);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL,
                false);

        // Initialize And Set Item Decorator
        Decorators decorators = new Decorators(
                FragmentPeopleOwingMe.this);

        recyclerView.addItemDecoration(decorators); // Add item decoration
        recyclerView.setLayoutManager(layoutManager); // Set layout manager
        recyclerView.setHasFixedSize(false); // Set has fixed size to false

        // Initialize class objects
        database = new UserDatabase(mContext);
        fetchContactsClass = new FetchContactsClass(mContext,
                FragmentPeopleOwingMe.this);

        // Initialize interface
        interfaceMainActivity = (Interface_MainActivity) getActivity();

        // SwipeRefreshLayout listener
        swipeRefreshListener =
                () -> {

                    animateSwipeRefresh = !isReload; // Set animate swipe refresh

                    if (!DataUtils.isEmptyArrayList(fetchedContacts)) {
                        fetchedContacts.clear(); // Clear contacts array
                    }

                    if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
                        // Network connection established

                        handleNetworkConnectionEvent(true);
                        // Fetch contacts
                        fetchContactsClass.fetchContacts(
                                database.getUserAccountInfo(null).get(0).getUserId(),
                                swipeRefreshLayout,
                                swipeRefreshListener
                        );
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

                    if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
                        // Network connection established

                        handleNetworkConnectionEvent(true);

                        // Fetch contacts
                        fetchContactsClass.fetchContacts(
                                database.getUserAccountInfo(null).get(0).getUserId(),
                                swipeRefreshLayout,
                                swipeRefreshListener
                        );

                    } else {
                        // No internet connection

                        handleNetworkConnectionEvent(false);
                    }
                }
            }
        };


        // Start/Stop swipe SwipeRefresh
        ViewsUtils.showSwipeRefreshLayout(true, swipeRefreshLayout, swipeRefreshListener);

        // Add contact onClick
        llAddContact.setOnClickListener(v -> {

            // Show add contact dialog fragment
            interfaceMainActivity.showAddContactDialogFragment(true);
        });

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
        BroadCastUtils.registerRefreshBroadCasts(requireActivity(), bcrReloadContacts,
                BroadCastUtils.bcrActionReloadPeopleOwingMe);

        // Start SwipeRefreshLayout
        ViewsUtils.showSwipeRefreshLayout(true, swipeRefreshLayout, swipeRefreshListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister BroadcastReceiver
        BroadCastUtils.unRegisterRefreshBroadCast(requireActivity(), bcrReloadContacts);
    }


    /**
     * Function to load contacts into RecyclerView
     *
     * @param contacts - ArrayList with user contacts
     */
    private void loadContacts(ArrayList<JB_Contacts> contacts) {

        if (!DataUtils.isEmptyArrayList(contacts)) {

            this.fetchedContacts = contacts; // Set ArrayList

            showSwipeRefreshLayout(true); // Show main layout

            // Creating RecyclerView adapter object
            RVLA_Contacts rvlaContacts = new RVLA_Contacts(requireActivity(), contacts);

            // Check for adapter observers
            if (!rvlaContacts.hasObservers()) {

                rvlaContacts.setHasStableIds(true); // Set has stable ids
            }

            // Setting Adapter to RecyclerView
            recyclerView.setAdapter(rvlaContacts);

            // Notify Data Set Changed
            rvlaContacts.notifyDataSetChanged();
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

            interfaceMainActivity.showAddContactFAB(false); // Hide fab button

        } else {

            llNoContacts.setVisibility(View.GONE); // Hide RecyclerView
        }
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

            // Check for contacts
            if (DataUtils.isEmptyArrayList(fetchedContacts)) {

                showSwipeRefreshLayout(false); // Hide SwipeRefreshLayout
            }

            llNoConnection.setVisibility(View.VISIBLE); // Show no connection bar
            showSwipeRefreshLayout(false); // Hide SwipeRefreshLayout

        } else {
            // Connection established

            llNoConnection.setVisibility(View.GONE); // Hide no connection bar
        }
    }

    /**
     * Listener to receive contacts ArrayList
     *
     * @param contacts - ArrayList with contacts
     */
    @Override
    public void passUserContacts_PeopleOwingMe(ArrayList<JB_Contacts> contacts) {

        interfaceMainActivity.showAddContactFAB(true); // Show FAB button

        // Check if ArrayList is empty
        if (!DataUtils.isEmptyArrayList(contacts)) {
            // ArrayList not empty

            loadContacts(contacts); // Load contacts to RecyclerView
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
    public void setNoContactsFound(boolean found) {

        showNoContactsLayout(found); // Show or hide no contacts layout
        interfaceMainActivity.showAddContactFAB(true); // Show add contacts FAB
    }
}
