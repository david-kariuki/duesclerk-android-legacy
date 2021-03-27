package com.duesclerk.classes.custom_views.fragments.bottom_sheet_dialog_fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.duesclerk.R;
import com.duesclerk.classes.custom_utilities.application.BroadCastUtils;
import com.duesclerk.classes.custom_utilities.user_data.DataUtils;
import com.duesclerk.enums.ListType;
import com.duesclerk.enums.SortType;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
@SuppressLint("ValidFragment")
public class BottomSheetFragment_SortLists extends BottomSheetDialogFragment {

    private final Context mContext;
    private final ListType listType;
    private BottomSheetBehavior bottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private TextView textTitle;

    // Sort options
    private LinearLayout llSortByName, llSortByDebtAmount, llSortByNoOfDebts,
            llSortByDebtDateIssued, llSortByDebtDateDue, llSortByDebtDateAdded;

    // Sort in ascending buttons
    private ImageView imageSortByNameAscending, imageSortByDebtAmountAscending,
            imageSortByNoOfDebtsAscending, imageSortByDebtDateIssuedAscending,
            imageSortByDebtDateDueAscending, imageSortByDebtDateAddedAscending;

    // Sort in descending buttons
    private ImageView imageSortByNameDescending, imageSortByDebtAmountDescending,
            imageSortByNoOfDebtsDescending, imageSortByDebtDateIssuedDescending,
            imageSortByDebtDateDueDescending, imageSortByDebtDateAddedDescending;

    // Currently selected sort type
    private SortType currentlySelectedSortType;

    public BottomSheetFragment_SortLists(@NonNull Context mContext, ListType listType,
                                         @NonNull SortType currentListSorting) {

        this.mContext = mContext; // Get context
        this.listType = listType; // Set list type

        this.currentlySelectedSortType = currentListSorting;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog)
                super.onCreateDialog(savedInstanceState);

        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_sort_lists, null);

        // TextViews
        textTitle = contentView.findViewById(R.id.textSortLists_Title);

        // ImageViews
        ImageView imageDismiss = contentView.findViewById(R.id.imageSortLists_Dismiss);

        // LinearLayouts
        llSortByName = contentView.findViewById(R.id.llSortLists_SortByName);
        llSortByDebtAmount = contentView.findViewById(R.id.llSortLists_SortByDebtAmount);
        llSortByNoOfDebts = contentView.findViewById(R.id.llSortLists_SortByNoOfDebts);
        llSortByDebtDateIssued = contentView.findViewById(R.id.llSortLists_SortByDebtDateIssued);
        llSortByDebtDateDue = contentView.findViewById(R.id.llSortLists_SortByDebtDateDue);
        llSortByDebtDateAdded = contentView.findViewById(R.id.llSortLists_SortByDebtDateAdded);

        // ImageViews
        imageSortByNameAscending = contentView.findViewById(
                R.id.imageSortLists_SortByName_Ascending);
        imageSortByNameDescending = contentView.findViewById(
                R.id.imageSortLists_SortByName_Descending);
        imageSortByDebtAmountAscending = contentView.findViewById(
                R.id.imageSortLists_SortByDebtAmount_Ascending);
        imageSortByDebtAmountDescending = contentView.findViewById(
                R.id.imageSortLists_SortByDebtAmount_Descending);
        imageSortByNoOfDebtsAscending = contentView.findViewById(
                R.id.imageSortLists_SortByNoOfDebts_Ascending);
        imageSortByNoOfDebtsDescending = contentView.findViewById(
                R.id.imageSortLists_SortByNoOfDebts_Descending);
        imageSortByDebtDateIssuedAscending = contentView.findViewById(
                R.id.imageSortLists_SortByDebtDateIssued_Ascending);
        imageSortByDebtDateIssuedDescending = contentView.findViewById(
                R.id.imageSortLists_SortByDebtDateIssued_Descending);
        imageSortByDebtDateDueAscending = contentView.findViewById(
                R.id.imageSortLists_SortByDebtDateDue_Ascending);
        imageSortByDebtDateDueDescending = contentView.findViewById(
                R.id.imageSortLists_SortByDebtDateDue_Descending);
        imageSortByDebtDateAddedAscending = contentView.findViewById(
                R.id.imageSortLists_SortByDebtDateAdded_Ascending);
        imageSortByDebtDateAddedDescending = contentView.findViewById(
                R.id.imageSortLists_SortByDebtDateAdded_Descending);

        showRequiredOptionsSetTitle(); // Show required list options

        showCurrentLisSorting(this.currentlySelectedSortType);

        // Sort by ContactName onClick
        llSortByName.setOnClickListener(v -> sortByName(null));

        // Sort by DebtAmount onClick
        llSortByDebtAmount.setOnClickListener(v -> sortByDebtAmount(null));

        // Sort by NoOfDebts onClick
        llSortByNoOfDebts.setOnClickListener(v -> sortByNoOfDebts(null));

        // Sort by DebtDateIssued onClick
        llSortByDebtDateIssued.setOnClickListener(v -> sortByDebtDateIssued(null));

        // Sort by DebtDateDue onClick
        llSortByDebtDateDue.setOnClickListener(v -> sortByDebtDateDue(null));

        // Sort by DebtDateAdded onClick
        llSortByDebtDateAdded.setOnClickListener(v -> sortByDebtDateAdded(null));

        // Sort by ContactName ascending onClick
        imageSortByNameAscending.setOnClickListener(v ->
                sortByName(SortType.CONTACT_NAME_ASCENDING));

        // Sort by ContactName descending onClick
        imageSortByNameDescending.setOnClickListener(v ->
                sortByName(SortType.CONTACT_NAME_DESCENDING));

        // Sort by DebtAmount ascending onClick
        imageSortByDebtAmountAscending.setOnClickListener(v -> {

            // Check list type
            if (listType == ListType.LIST_CONTACTS) {

                sortByDebtAmount(SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_ASCENDING);

            } else if (listType == ListType.LIST_DEBTS) {

                sortByDebtAmount(SortType.DEBT_AMOUNT_ASCENDING);
            }
        });

        // Sort by DebtAmount descending onClick
        imageSortByDebtAmountDescending.setOnClickListener(v -> {

            // Check list type
            if (listType == ListType.LIST_CONTACTS) {

                sortByDebtAmount(SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_DESCENDING);

            } else if (listType == ListType.LIST_DEBTS) {

                sortByDebtAmount(SortType.DEBT_AMOUNT_DESCENDING);
            }
        });

        // Sort by NoOfDebts ascending onClick
        imageSortByNoOfDebtsAscending.setOnClickListener(v ->
                sortByNoOfDebts(SortType.NO_OF_DEBTS_ASCENDING));

        // Sort by NoOfDebts descending onClick
        imageSortByNoOfDebtsDescending.setOnClickListener(v ->
                sortByNoOfDebts(SortType.NO_OF_DEBTS_DESCENDING));

        // Sort by DebtDateIssued ascending onClick
        imageSortByDebtDateIssuedAscending.setOnClickListener(v ->
                sortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_ASCENDING));

        // Sort by DebtDateIssued descending onClick
        imageSortByDebtDateIssuedDescending.setOnClickListener(v ->
                sortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_DESCENDING));

        // Sort by DebtDateDue ascending onClick
        imageSortByDebtDateDueAscending.setOnClickListener(v ->
                sortByDebtDateDue(SortType.DEBT_DATE_DUE_ASCENDING));

        // Sort by DebtDateDue descending onClick
        imageSortByDebtDateDueDescending.setOnClickListener(v ->
                sortByDebtDateDue(SortType.DEBT_DATE_DUE_DESCENDING));

        // Sort by DebtDateAdded ascending onClick
        imageSortByDebtDateAddedAscending.setOnClickListener(v ->
                sortByDebtDateAdded(SortType.DEBT_DATE_ADDED_ASCENDING));

        // Sort by DebtDateAdded ascending onClick
        imageSortByDebtDateAddedDescending.setOnClickListener(v ->
                sortByDebtDateAdded(SortType.DEBT_DATE_ADDED_DESCENDING));

        // Dismiss onClick
        imageDismiss.setOnClickListener(v -> dismiss());

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

        // Set Custom View To Dialog
        dialog.setContentView(contentView);

        // Set BottomSheet behaviour
        this.bottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());

        // Set dialog transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
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
     * Function to show list sorting options based on list type
     */
    private void showRequiredOptionsSetTitle() {

        // Show amount option
        llSortByDebtAmount.setVisibility(View.VISIBLE); // Required for contacts and debts

        if (listType == ListType.LIST_CONTACTS) {
            // Sorting contacts thus display name, number of debts

            // Set BottomSheet title
            textTitle.setText(DataUtils.getStringResource(mContext, R.string.label_sort,
                    DataUtils.getStringResource(mContext, R.string.label_contacts)));

            // Show sort by SortBy_ContactName options
            llSortByName.setVisibility(View.VISIBLE);

            // Show sort by contacts NoOfDebts options
            llSortByNoOfDebts.setVisibility(View.VISIBLE);

            // Hide sort by DebtDateIssued name options
            llSortByDebtDateIssued.setVisibility(View.GONE);

            // Hide sort by DebtDateDue name options
            llSortByDebtDateDue.setVisibility(View.GONE);

            // Hide sort by DebtDateAdded
            llSortByDebtDateAdded.setVisibility(View.GONE);

        } else if (listType == ListType.LIST_DEBTS) {
            // Sorting debts thus show DebtDateIssued and DebtDateDue

            // Set BottomSheet title
            textTitle.setText(DataUtils.getStringResource(mContext, R.string.label_sort,
                    DataUtils.getStringResource(mContext, R.string.label_debts)));

            // Show sort by DebtDateIssued name options
            llSortByDebtDateIssued.setVisibility(View.VISIBLE);

            // Show sort by DebtDateDue name options
            llSortByDebtDateDue.setVisibility(View.VISIBLE);

            // Show sort by DebtDateAdded
            llSortByDebtDateAdded.setVisibility(View.VISIBLE);

            // Hide sort by SortBy_ContactName options
            llSortByName.setVisibility(View.GONE);

            // Hide sort by contacts NoOfDebts options
            llSortByNoOfDebts.setVisibility(View.GONE);
        }
    }

    /**
     * Function to show current list sorting on the BottomSheet
     *
     * @param sortType - Sort type
     */
    private void showCurrentLisSorting(@NonNull SortType sortType) {

        // Check list type
        if (listType == ListType.LIST_CONTACTS) {

            // Check sort type
            if ((sortType == SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_ASCENDING)
                    || (sortType == SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_DESCENDING)) {

                // Select sort option
                selectSortByDebtAmount(sortType, true);

            } else if ((sortType == SortType.CONTACT_NAME_ASCENDING)
                    || (sortType == SortType.CONTACT_NAME_DESCENDING)) {

                // Select sort option
                selectSortByContactName(sortType, true);

            } else if ((sortType == SortType.NO_OF_DEBTS_ASCENDING)
                    || (sortType == SortType.NO_OF_DEBTS_DESCENDING)) {

                // Select sort option
                selectSortByNoOfDebts(sortType, true);
            }
        } else if (listType == ListType.LIST_DEBTS) {

            if ((sortType == SortType.DEBT_AMOUNT_ASCENDING)
                    || (sortType == SortType.DEBT_AMOUNT_DESCENDING)) {

                // Select sort option
                selectSortByDebtAmount(sortType, true);

            } else if ((sortType == SortType.DEBT_DATE_ISSUED_ASCENDING)
                    || (sortType == SortType.DEBT_DATE_ISSUED_DESCENDING)) {

                // Select sort option
                selectSortByDebtDateIssued(sortType, true);

            } else if ((sortType == SortType.DEBT_DATE_DUE_ASCENDING)
                    || (sortType == SortType.DEBT_DATE_DUE_DESCENDING)) {

                // Select sort option
                selectSortByDebtDateDue(sortType, true);

            } else if ((sortType == SortType.DEBT_DATE_ADDED_ASCENDING)
                    || (sortType == SortType.DEBT_DATE_ADDED_DESCENDING)) {

                // Select sort option
                selectSortByDebtDateDue(sortType, true);
            }
        }
    }

    /**
     * Function to select and un-select sort by ContactName ascending and descending buttons
     *
     * @param select   - Select / un-select sort buttons
     * @param sortType - Sort type
     */
    private void selectSortByContactName(SortType sortType, boolean select) {

        // Check if selecting
        if (select) {
            // Selecting sort options

            // Check sort type
            if (sortType == SortType.CONTACT_NAME_ASCENDING) {
                // Sorting by SortBy_ContactName ascending

                // Set current sort type
                currentlySelectedSortType = SortType.CONTACT_NAME_ASCENDING;

                // Change background resource to primary
                imageSortByNameAscending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_primary);

                // Set image resource
                imageSortByNameAscending.setImageResource(R.drawable.ic_ascending_white_100px);

            } else if (sortType == SortType.CONTACT_NAME_DESCENDING) {
                // Sorting by SortBy_ContactName descending

                // Set current sort type
                currentlySelectedSortType = SortType.CONTACT_NAME_DESCENDING;

                // Change background resource to primary
                imageSortByNameDescending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_primary);

                // Set image resource
                imageSortByNameDescending.setImageResource(R.drawable.ic_descending_white_100px);
            }
        } else {
            // Un-selecting sort options

            // Check sort type
            if (sortType == SortType.CONTACT_NAME_ASCENDING) {
                // Sorting by SortBy_ContactName ascending

                // Change background resource to primary
                imageSortByNameAscending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_grey);

                // Set image resource
                imageSortByNameAscending.setImageResource(R.drawable.ic_ascending_black_100px);

            } else if (sortType == SortType.CONTACT_NAME_DESCENDING) {
                // Sorting by SortBy_ContactName descending

                // Change background resource to primary
                imageSortByNameDescending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_grey);

                // Set image resource
                imageSortByNameDescending.setImageResource(R.drawable.ic_descending_black_100px);
            }
        }
    }

    /**
     * Function to sort list by SortBy_ContactName
     *
     * @param sortType - Sort type
     */
    private void sortByName(SortType sortType) {

        // Check if sort type is null
        if (sortType == null) {

            // Check current sort type
            if (currentlySelectedSortType == SortType.CONTACT_NAME_ASCENDING) {

                // Select sort by SortBy_ContactName in ascending order
                selectSortByContactName(SortType.CONTACT_NAME_DESCENDING, true);

                // Un-select sort by SortBy_ContactName in ascending order
                selectSortByContactName(SortType.CONTACT_NAME_ASCENDING, false);

            } else if (currentlySelectedSortType == SortType.CONTACT_NAME_DESCENDING) {

                // Select sort by SortBy_ContactName in descending order
                selectSortByContactName(SortType.CONTACT_NAME_ASCENDING, true);

                // Un-select sort by SortBy_ContactName in descending order
                selectSortByContactName(SortType.CONTACT_NAME_DESCENDING, false);

            } else {

                // Select sort by SortBy_ContactName in ascending order
                selectSortByContactName(SortType.CONTACT_NAME_ASCENDING, true);

                // Un-select sort by SortBy_ContactName in ascending order
                selectSortByContactName(SortType.CONTACT_NAME_DESCENDING, false);
            }
        } else {

            // Check current sort type
            if (sortType == SortType.CONTACT_NAME_ASCENDING) {

                // Select sort by SortBy_ContactName in ascending order
                selectSortByContactName(SortType.CONTACT_NAME_ASCENDING, true);

                // Un-select sort by SortBy_ContactName in descending order
                selectSortByContactName(SortType.CONTACT_NAME_DESCENDING, false);

            } else if (sortType == SortType.CONTACT_NAME_DESCENDING) {

                // Select sort by SortBy_ContactName in descending order
                selectSortByContactName(SortType.CONTACT_NAME_DESCENDING, true);

                // Un-select sort by SortBy_ContactName in ascending order
                selectSortByContactName(SortType.CONTACT_NAME_ASCENDING, false);
            }
        }

        // Un-select other sort options
        selectSortByDebtAmount(SortType.DEBT_AMOUNT_ASCENDING, false);
        selectSortByDebtAmount(SortType.DEBT_AMOUNT_DESCENDING, false);

        // Check list type
        if (listType == ListType.LIST_CONTACTS) {

            selectSortByNoOfDebts(SortType.NO_OF_DEBTS_ASCENDING, false);
            selectSortByNoOfDebts(SortType.NO_OF_DEBTS_DESCENDING, false);

        } else if (listType == ListType.LIST_DEBTS) {

            selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_ASCENDING, false);
            selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_DESCENDING, false);

            selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_ASCENDING, false);
            selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_DESCENDING, false);

            selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_ASCENDING, false);
            selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_DESCENDING, false);
        }

        // Send broadcast for the selected sort operation
        sendSortTypeBroadCast();
    }

    /**
     * Function to select and un-select sort by amount ascending and descending buttons
     *
     * @param select   - Select / un-select sort buttons
     * @param sortType - Sort type
     */
    private void selectSortByDebtAmount(SortType sortType, boolean select) {

        // Check if selecting
        if (select) {
            // Selecting sort options

            // Check sort type
            if ((sortType == SortType.DEBT_AMOUNT_ASCENDING)
                    || (sortType == SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_ASCENDING)) {
                // Sorting by DebtAmount / ContactsTotalDebtsAmount ascending

                currentlySelectedSortType = sortType; // Set current sort type

                // Change background resource to primary
                imageSortByDebtAmountAscending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_primary);

                // Set image resource
                imageSortByDebtAmountAscending
                        .setImageResource(R.drawable.ic_ascending_white_100px);

            } else if ((sortType == SortType.DEBT_AMOUNT_DESCENDING)
                    || (sortType == SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_DESCENDING)) {
                // Sorting by DebtAmount / ContactsTotalDebtsAmount descending

                currentlySelectedSortType = sortType; // Set current sort type

                // Change background resource to primary
                imageSortByDebtAmountDescending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_primary);

                // Set image resource
                imageSortByDebtAmountDescending
                        .setImageResource(R.drawable.ic_descending_white_100px);
            }
        } else {
            // Un-selecting sort options

            // Check sort type
            if ((sortType == SortType.DEBT_AMOUNT_ASCENDING)
                    || (sortType == SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_ASCENDING)) {
                // Sorting by DebtAmount / ContactsTotalDebtsAmount ascending

                // Change background resource to primary
                imageSortByDebtAmountAscending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_grey);

                // Set image resource
                imageSortByDebtAmountAscending
                        .setImageResource(R.drawable.ic_ascending_black_100px);

            } else if ((sortType == SortType.DEBT_AMOUNT_DESCENDING)
                    || (sortType == SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_DESCENDING)) {
                // Sorting by DebtAmount / ContactsTotalDebtsAmount descending

                // Change background resource to primary
                imageSortByDebtAmountDescending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_grey);

                // Set image resource
                imageSortByDebtAmountDescending
                        .setImageResource(R.drawable.ic_descending_black_100px);
            }
        }
    }

    /**
     * Function to sort list by amount
     *
     * @param sortType - Sort type
     */
    private void sortByDebtAmount(SortType sortType) {

        //Check if sort type is null
        if (sortType == null) {

            // Check sort type
            if (listType == ListType.LIST_CONTACTS) {
                // Contacts

                // Check current sort type
                if (currentlySelectedSortType == SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_ASCENDING) {

                    // Select sort by ContactsTotalDebtsAmount in ascending order
                    selectSortByDebtAmount(
                            SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_DESCENDING, true);

                    // Un-select sort by ContactsTotalDebtsAmount in ascending order
                    selectSortByDebtAmount(
                            SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_ASCENDING, false);

                } else if (currentlySelectedSortType ==
                        SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_DESCENDING) {

                    // Select sort by amount in descending order
                    selectSortByDebtAmount(
                            SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_ASCENDING, true);

                    // Un-select sort by amount in descending order
                    selectSortByDebtAmount(
                            SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_DESCENDING, false);

                } else {

                    // Select sort by ContactsTotalDebtsAmount in ascending order
                    selectSortByDebtAmount(
                            SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_ASCENDING, true);

                    // Un-select sort by ContactsTotalDebtsAmount in ascending order
                    selectSortByDebtAmount(
                            SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_DESCENDING, false);
                }
            } else if (listType == ListType.LIST_DEBTS) {
                // Debts

                // Check current sort type
                if (currentlySelectedSortType == SortType.DEBT_AMOUNT_ASCENDING) {

                    // Select sort by DebtAmount in ascending order
                    selectSortByDebtAmount(
                            SortType.DEBT_AMOUNT_DESCENDING, true);

                    // Un-select sort by DebtAmount in ascending order
                    selectSortByDebtAmount(
                            SortType.DEBT_AMOUNT_ASCENDING, false);

                } else if (currentlySelectedSortType ==
                        SortType.DEBT_AMOUNT_DESCENDING) {

                    // Select sort by amount in descending order
                    selectSortByDebtAmount(
                            SortType.DEBT_AMOUNT_ASCENDING, true);

                    // Un-select sort by amount in descending order
                    selectSortByDebtAmount(
                            SortType.DEBT_AMOUNT_DESCENDING, false);

                } else {

                    // Select sort by DebtAmount in ascending order
                    selectSortByDebtAmount(
                            SortType.DEBT_AMOUNT_ASCENDING, true);

                    // Un-select sort by DebtAmount in ascending order
                    selectSortByDebtAmount(
                            SortType.DEBT_AMOUNT_DESCENDING, false);
                }
            }
        } else {

            // Check sort type
            if (listType == ListType.LIST_CONTACTS) {
                // Contacts

                // Check current sort type
                if (sortType == SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_ASCENDING) {

                    // Select sort by ContactsTotalDebtsAmount in ascending order
                    selectSortByDebtAmount(
                            SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_ASCENDING, true);

                    // Un-select sort by ContactsTotalDebtsAmount in descending order
                    selectSortByDebtAmount(
                            SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_DESCENDING, false);

                } else if (sortType == SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_DESCENDING) {

                    // Select sort by ContactsTotalDebtsAmount in descending order
                    selectSortByDebtAmount(
                            SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_DESCENDING, true);

                    // Un-select sort by ContactsTotalDebtsAmount in ascending order
                    selectSortByDebtAmount(
                            SortType.CONTACTS_TOTAL_DEBTS_AMOUNT_ASCENDING, false);
                }
            } else if (listType == ListType.LIST_DEBTS) {
                // Debts

                // Check current sort type
                if (sortType == SortType.DEBT_AMOUNT_ASCENDING) {

                    // Select sort by DebtAmount in ascending order
                    selectSortByDebtAmount(SortType.DEBT_AMOUNT_ASCENDING, true);

                    // Un-select sort by DebtAmount in descending order
                    selectSortByDebtAmount(SortType.DEBT_AMOUNT_DESCENDING, false);

                } else if (sortType == SortType.DEBT_AMOUNT_DESCENDING) {

                    // Select sort by DebtAmount in descending order
                    selectSortByDebtAmount(SortType.DEBT_AMOUNT_DESCENDING, true);

                    // Un-select sort by DebtAmount in ascending order
                    selectSortByDebtAmount(SortType.DEBT_AMOUNT_ASCENDING, false);
                }
            }
        }

        // Un-select other sort options

        // Check list type
        if (listType == ListType.LIST_CONTACTS) {

            selectSortByContactName(SortType.CONTACT_NAME_ASCENDING, false);
            selectSortByContactName(SortType.CONTACT_NAME_DESCENDING, false);

            selectSortByNoOfDebts(SortType.NO_OF_DEBTS_ASCENDING, false);
            selectSortByNoOfDebts(SortType.NO_OF_DEBTS_DESCENDING, false);

        } else if (listType == ListType.LIST_DEBTS) {

            selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_ASCENDING, false);
            selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_DESCENDING, false);

            selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_ASCENDING, false);
            selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_DESCENDING, false);

            selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_ASCENDING, false);
            selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_DESCENDING, false);
        }

        // Send broadcast for the selected sort operation
        sendSortTypeBroadCast();
    }

    /**
     * Function to select and un-select sort by number of debts ascending and descending buttons
     *
     * @param select   - Select / un-select sort buttons
     * @param sortType - Sort type
     */
    private void selectSortByNoOfDebts(SortType sortType, boolean select) {

        // Check if selecting
        if (select) {
            // Selecting sort options

            // Check sort type
            if (sortType == SortType.NO_OF_DEBTS_ASCENDING) {
                // Sorting by NoOfDebts ascending

                // Set current sort type
                currentlySelectedSortType = SortType.NO_OF_DEBTS_ASCENDING;

                // Change background resource to primary
                imageSortByNoOfDebtsAscending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_primary);

                // Set image resource
                imageSortByNoOfDebtsAscending
                        .setImageResource(R.drawable.ic_ascending_white_100px);

            } else if (sortType == SortType.NO_OF_DEBTS_DESCENDING) {
                // Sorting by NoOfDebts descending

                // Set current sort type
                currentlySelectedSortType = SortType.NO_OF_DEBTS_DESCENDING;

                // Change background resource to primary
                imageSortByNoOfDebtsDescending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_primary);

                // Set image resource
                imageSortByNoOfDebtsDescending
                        .setImageResource(R.drawable.ic_descending_white_100px);
            }
        } else {
            // Un-selecting sort options

            // Check sort type
            if (sortType == SortType.NO_OF_DEBTS_ASCENDING) {
                // Sorting by NoOfDebts ascending

                // Change background resource to primary
                imageSortByNoOfDebtsAscending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_grey);

                // Set image resource
                imageSortByNoOfDebtsAscending
                        .setImageResource(R.drawable.ic_ascending_black_100px);

            } else if (sortType == SortType.NO_OF_DEBTS_DESCENDING) {
                // Sorting by NoOfDebts descending

                // Change background resource to primary
                imageSortByNoOfDebtsDescending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_grey);

                // Set image resource
                imageSortByNoOfDebtsDescending
                        .setImageResource(R.drawable.ic_descending_black_100px);
            }
        }
    }

    /**
     * Function to sort list by number of debts
     *
     * @param sortType - Sort type
     */
    private void sortByNoOfDebts(SortType sortType) {

        // Check if sort type is null
        if (sortType == null) {

            // Check current sort type
            if (currentlySelectedSortType == SortType.NO_OF_DEBTS_ASCENDING) {

                // Select sort by NoOfDebts in ascending order
                selectSortByNoOfDebts(SortType.NO_OF_DEBTS_DESCENDING, true);

                // Un-select sort by NoOfDebts in ascending order
                selectSortByNoOfDebts(SortType.NO_OF_DEBTS_ASCENDING, false);

            } else if (currentlySelectedSortType == SortType.NO_OF_DEBTS_DESCENDING) {

                // Select sort by NoOfDebts in descending order
                selectSortByNoOfDebts(SortType.NO_OF_DEBTS_ASCENDING, true);

                // Un-select sort by NoOfDebts in descending order
                selectSortByNoOfDebts(SortType.NO_OF_DEBTS_DESCENDING, false);

            } else {

                // Select sort by NoOfDebts in ascending order
                selectSortByNoOfDebts(SortType.NO_OF_DEBTS_ASCENDING, true);

                // Un-select sort by NoOfDebts in ascending order
                selectSortByNoOfDebts(SortType.NO_OF_DEBTS_DESCENDING, false);
            }
        } else {

            // Check current sort type
            if (sortType == SortType.NO_OF_DEBTS_ASCENDING) {

                // Select sort by NoOfDebts in ascending order
                selectSortByNoOfDebts(SortType.NO_OF_DEBTS_ASCENDING, true);

                // Un-select sort by NoOfDebts in descending order
                selectSortByNoOfDebts(SortType.NO_OF_DEBTS_DESCENDING, false);

            } else if (sortType == SortType.NO_OF_DEBTS_DESCENDING) {

                // Select sort by NoOfDebts in descending order
                selectSortByNoOfDebts(SortType.NO_OF_DEBTS_DESCENDING, true);

                // Un-select sort by NoOfDebts in ascending order
                selectSortByNoOfDebts(SortType.NO_OF_DEBTS_ASCENDING, false);
            }
        }

        // Un-select other sort options

        // Check list type
        if (listType == ListType.LIST_CONTACTS) {

            selectSortByContactName(SortType.CONTACT_NAME_ASCENDING, false);
            selectSortByContactName(SortType.CONTACT_NAME_DESCENDING, false);

        } else if (listType == ListType.LIST_DEBTS) {

            selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_ASCENDING, false);
            selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_DESCENDING, false);

            selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_ASCENDING, false);
            selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_DESCENDING, false);

            selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_ASCENDING, false);
            selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_DESCENDING, false);
        }

        selectSortByDebtAmount(SortType.DEBT_AMOUNT_ASCENDING, false);
        selectSortByDebtAmount(SortType.DEBT_AMOUNT_DESCENDING, false);

        // Send broadcast for the selected sort operation
        sendSortTypeBroadCast();
    }

    /**
     * Function to select and un-select sort by DebtDateIssued ascending and descending buttons
     *
     * @param select   - Select / un-select sort buttons
     * @param sortType - Sort type
     */
    private void selectSortByDebtDateIssued(SortType sortType, boolean select) {

        // Check if selecting
        if (select) {
            // Selecting sort options

            // Check sort type
            if (sortType == SortType.DEBT_DATE_ISSUED_ASCENDING) {
                // Sorting by DebtDateIssued ascending

                // Set current sort type
                currentlySelectedSortType = SortType.DEBT_DATE_ISSUED_ASCENDING;

                // Change background resource to primary
                imageSortByDebtDateIssuedAscending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_primary);

                // Set image resource
                imageSortByDebtDateIssuedAscending
                        .setImageResource(R.drawable.ic_ascending_white_100px);

            } else if (sortType == SortType.DEBT_DATE_ISSUED_DESCENDING) {
                // Sorting by DebtDateIssued descending

                // Set current sort type
                currentlySelectedSortType = SortType.DEBT_DATE_ISSUED_DESCENDING;

                // Change background resource to primary
                imageSortByDebtDateIssuedDescending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_primary);

                // Set image resource
                imageSortByDebtDateIssuedDescending
                        .setImageResource(R.drawable.ic_descending_white_100px);
            }
        } else {
            // Un-selecting sort options

            // Check sort type
            if (sortType == SortType.DEBT_DATE_ISSUED_ASCENDING) {
                // Sorting by DebtDateIssued ascending

                // Change background resource to primary
                imageSortByDebtDateIssuedAscending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_grey);

                // Set image resource
                imageSortByDebtDateIssuedAscending
                        .setImageResource(R.drawable.ic_ascending_black_100px);

            } else if (sortType == SortType.DEBT_DATE_ISSUED_DESCENDING) {
                // Sorting by DebtDateIssued descending

                // Change background resource to primary
                imageSortByDebtDateIssuedDescending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_grey);

                // Set image resource
                imageSortByDebtDateIssuedDescending
                        .setImageResource(R.drawable.ic_descending_black_100px);
            }
        }
    }

    /**
     * Function to sort list by DebtDateIssued
     *
     * @param sortType - Sort type
     */
    private void sortByDebtDateIssued(SortType sortType) {

        // Check if sort type is null
        if (sortType == null) {

            // Check current sort type
            if (currentlySelectedSortType == SortType.DEBT_DATE_ISSUED_ASCENDING) {

                // Select sort by DebtDateIssued in ascending order
                selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_DESCENDING, true);

                // Un-select sort by DebtDateIssued in ascending order
                selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_ASCENDING, false);

            } else if (currentlySelectedSortType == SortType.DEBT_DATE_ISSUED_DESCENDING) {

                // Select sort by DebtDateIssued in descending order
                selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_ASCENDING, true);

                // Un-select sort by DebtDateIssued in descending order
                selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_DESCENDING, false);

            } else {

                // Select sort by DebtDateIssued in ascending order
                selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_ASCENDING, true);

                // Un-select sort by DebtDateIssued in ascending order
                selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_DESCENDING, false);
            }
        } else {

            // Check current sort type
            if (sortType == SortType.DEBT_DATE_ISSUED_ASCENDING) {

                // Select sort by DebtDateIssued in ascending order
                selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_ASCENDING, true);

                // Un-select sort by DebtDateIssued in descending order
                selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_DESCENDING, false);

            } else if (sortType == SortType.DEBT_DATE_ISSUED_DESCENDING) {

                // Select sort by DebtDateIssued in descending order
                selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_DESCENDING, true);

                // Un-select sort by DebtDateIssued in ascending order
                selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_ASCENDING, false);
            }
        }

        // Un-select other sort options

        // Check list type
        if (listType == ListType.LIST_CONTACTS) {

            selectSortByContactName(SortType.CONTACT_NAME_ASCENDING, false);
            selectSortByContactName(SortType.CONTACT_NAME_DESCENDING, false);

        } else if (listType == ListType.LIST_DEBTS) {

            selectSortByNoOfDebts(SortType.NO_OF_DEBTS_ASCENDING, false);
            selectSortByNoOfDebts(SortType.NO_OF_DEBTS_DESCENDING, false);

            selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_ASCENDING, false);
            selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_DESCENDING, false);

            selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_ASCENDING, false);
            selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_DESCENDING, false);
        }

        selectSortByDebtAmount(SortType.DEBT_AMOUNT_ASCENDING, false);
        selectSortByDebtAmount(SortType.DEBT_AMOUNT_DESCENDING, false);

        // Send broadcast for the selected sort operation
        sendSortTypeBroadCast();
    }

    /**
     * Function to select and un-select sort by DebtDateDue ascending and descending buttons
     *
     * @param select   - Select / un-select sort buttons
     * @param sortType - Sort type
     */
    private void selectSortByDebtDateDue(SortType sortType, boolean select) {

        // Check if selecting
        if (select) {
            // Selecting sort options

            // Check sort type
            if (sortType == SortType.DEBT_DATE_DUE_ASCENDING) {
                // Sorting by DebtDateDue ascending

                // Set current sort type
                currentlySelectedSortType = SortType.DEBT_DATE_DUE_ASCENDING;

                // Change background resource to primary
                imageSortByDebtDateDueAscending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_primary);

                // Set image resource
                imageSortByDebtDateDueAscending
                        .setImageResource(R.drawable.ic_ascending_white_100px);

            } else if (sortType == SortType.DEBT_DATE_DUE_DESCENDING) {
                // Sorting by DebtDateDue descending

                // Set current sort type
                currentlySelectedSortType = SortType.DEBT_DATE_DUE_DESCENDING;

                // Change background resource to primary
                imageSortByDebtDateDueDescending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_primary);

                // Set image resource
                imageSortByDebtDateDueDescending
                        .setImageResource(R.drawable.ic_descending_white_100px);
            }
        } else {
            // Un-selecting sort options

            // Check sort type
            if (sortType == SortType.DEBT_DATE_DUE_ASCENDING) {
                // Sorting by DebtDateDue ascending

                // Change background resource to primary
                imageSortByDebtDateDueAscending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_grey);

                // Set image resource
                imageSortByDebtDateDueAscending
                        .setImageResource(R.drawable.ic_ascending_black_100px);

            } else if (sortType == SortType.DEBT_DATE_DUE_DESCENDING) {
                // Sorting by DebtDateDue descending

                // Change background resource to primary
                imageSortByDebtDateDueDescending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_grey);

                // Set image resource
                imageSortByDebtDateDueDescending
                        .setImageResource(R.drawable.ic_descending_black_100px);
            }
        }
    }

    /**
     * Function to sort list by DebtDateDue
     *
     * @param sortType - Sort type
     */
    private void sortByDebtDateDue(SortType sortType) {

        // Check if sort type is null
        if (sortType == null) {

            // Check current sort type
            if (currentlySelectedSortType == SortType.DEBT_DATE_DUE_ASCENDING) {

                // Select sort by DebtDateDue in ascending order
                selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_DESCENDING, true);

                // Un-select sort by DebtDateDue in ascending order
                selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_ASCENDING, false);

            } else if (currentlySelectedSortType == SortType.DEBT_DATE_DUE_DESCENDING) {

                // Select sort by DebtDateDue in descending order
                selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_ASCENDING, true);

                // Un-select sort by DebtDateDue in descending order
                selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_DESCENDING, false);

            } else {

                // Select sort by DebtDateDue in ascending order
                selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_ASCENDING, true);

                // Un-select sort by DebtDateDue in ascending order
                selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_DESCENDING, false);
            }
        } else {

            // Check current sort type
            if (sortType == SortType.DEBT_DATE_DUE_ASCENDING) {

                // Select sort by DebtDateDue in ascending order
                selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_ASCENDING, true);

                // Un-select sort by DebtDateDue in descending order
                selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_DESCENDING, false);

            } else if (sortType == SortType.DEBT_DATE_DUE_DESCENDING) {

                // Select sort by DebtDateDue in descending order
                selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_DESCENDING, true);

                // Un-select sort by DebtDateDue in ascending order
                selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_ASCENDING, false);
            }
        }

        // Un-select other sort options

        // Check list type
        if (listType == ListType.LIST_CONTACTS) {

            selectSortByContactName(SortType.CONTACT_NAME_ASCENDING, false);
            selectSortByContactName(SortType.CONTACT_NAME_DESCENDING, false);

            selectSortByNoOfDebts(SortType.NO_OF_DEBTS_ASCENDING, false);
            selectSortByNoOfDebts(SortType.NO_OF_DEBTS_DESCENDING, false);

        } else if (listType == ListType.LIST_DEBTS) {

            selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_ASCENDING, false);
            selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_DESCENDING, false);

            selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_ASCENDING, false);
            selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_DESCENDING, false);
        }

        selectSortByDebtAmount(SortType.DEBT_AMOUNT_ASCENDING, false);
        selectSortByDebtAmount(SortType.DEBT_AMOUNT_DESCENDING, false);

        // Send broadcast for the selected sort operation
        sendSortTypeBroadCast();
    }

    /**
     * Function to select and un-select sort by DebtDateAdded ascending and descending
     * buttons
     *
     * @param select   - Select / un-select sort buttons
     * @param sortType - Sort type
     */
    private void selectSortByDebtDateAdded(SortType sortType, boolean select) {

        // Check if selecting
        if (select) {
            // Selecting sort options

            // Check sort type
            if (sortType == SortType.DEBT_DATE_ADDED_ASCENDING) {
                // Sorting by DebtDateAdded ascending

                // Set current sort type
                currentlySelectedSortType = SortType.DEBT_DATE_ADDED_ASCENDING;

                // Change background resource to primary
                imageSortByDebtDateAddedAscending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_primary);

                // Set image resource
                imageSortByDebtDateAddedAscending
                        .setImageResource(R.drawable.ic_ascending_white_100px);

            } else if (sortType == SortType.DEBT_DATE_ADDED_DESCENDING) {
                // Sorting by DebtDateAdded descending

                // Set current sort type
                currentlySelectedSortType = SortType.DEBT_DATE_ADDED_DESCENDING;

                // Change background resource to primary
                imageSortByDebtDateAddedDescending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_primary);

                // Set image resource
                imageSortByDebtDateAddedDescending
                        .setImageResource(R.drawable.ic_descending_white_100px);
            }
        } else {
            // Un-selecting sort options

            // Check sort type
            if (sortType == SortType.DEBT_DATE_ADDED_ASCENDING) {
                // Sorting by DebtDateAdded ascending

                // Change background resource to primary
                imageSortByDebtDateAddedAscending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_grey);

                // Set image resource
                imageSortByDebtDateAddedAscending
                        .setImageResource(R.drawable.ic_ascending_black_100px);

            } else if (sortType == SortType.DEBT_DATE_ADDED_DESCENDING) {
                // Sorting by DebtDateAdded descending

                // Change background resource to primary
                imageSortByDebtDateAddedDescending.setBackgroundResource(
                        R.drawable.outline_options_list_icons_background_grey);

                // Set image resource
                imageSortByDebtDateAddedDescending
                        .setImageResource(R.drawable.ic_descending_black_100px);
            }
        }
    }

    /**
     * Function to sort list by DebtDateAdded
     *
     * @param sortType - Sort type
     */
    private void sortByDebtDateAdded(SortType sortType) {

        // Check if sort type is null
        if (sortType == null) {

            // Check current sort type
            if (currentlySelectedSortType == SortType.DEBT_DATE_ADDED_ASCENDING) {

                // Select sort by DebtDateAdded in ascending order
                selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_DESCENDING, true);

                // Un-select sort by DebtDateAdded in ascending order
                selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_ASCENDING, false);

            } else if (currentlySelectedSortType == SortType.DEBT_DATE_ADDED_DESCENDING) {

                // Select sort by DebtDateAdded in descending order
                selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_ASCENDING, true);

                // Un-select sort by DebtDateAdded in descending order
                selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_DESCENDING, false);

            } else {

                // Select sort by DebtDateAdded in ascending order
                selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_ASCENDING, true);

                // Un-select sort by DebtDateAdded in ascending order
                selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_DESCENDING, false);
            }
        } else {

            // Check current sort type
            if (sortType == SortType.DEBT_DATE_ADDED_ASCENDING) {

                // Select sort by DebtDateAdded in ascending order
                selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_ASCENDING, true);

                // Un-select sort by DebtDateAdded in descending order
                selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_DESCENDING, false);

            } else if (sortType == SortType.DEBT_DATE_ADDED_DESCENDING) {

                // Select sort by DebtDateAdded in descending order
                selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_DESCENDING, true);

                // Un-select sort by DebtDateAdded in ascending order
                selectSortByDebtDateAdded(SortType.DEBT_DATE_ADDED_ASCENDING, false);
            }
        }

        // Un-select other sort options

        // Check list type
        if (listType == ListType.LIST_CONTACTS) {

            selectSortByContactName(SortType.CONTACT_NAME_ASCENDING, false);
            selectSortByContactName(SortType.CONTACT_NAME_DESCENDING, false);

            selectSortByNoOfDebts(SortType.NO_OF_DEBTS_ASCENDING, false);
            selectSortByNoOfDebts(SortType.NO_OF_DEBTS_DESCENDING, false);

        } else if (listType == ListType.LIST_DEBTS) {

            selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_ASCENDING, false);
            selectSortByDebtDateIssued(SortType.DEBT_DATE_ISSUED_DESCENDING, false);

            selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_ASCENDING, false);
            selectSortByDebtDateDue(SortType.DEBT_DATE_DUE_DESCENDING, false);
        }

        selectSortByDebtAmount(SortType.DEBT_AMOUNT_ASCENDING, false);
        selectSortByDebtAmount(SortType.DEBT_AMOUNT_DESCENDING, false);

        // Send broadcast for the selected sort operation
        sendSortTypeBroadCast();
    }

    /**
     * Function send broadcast for the selected sort operation
     */
    private void sendSortTypeBroadCast() {

        // Broadcast intent to sort by SortBy_ContactName
        Intent intentBroadCastSort = new Intent(BroadCastUtils.bcrAction_SortLists);

        // Add sort type to Broadcast
        intentBroadCastSort.putExtra("SORT_TYPE", currentlySelectedSortType);

        // Send sort intent-broadcast
        requireActivity().sendBroadcast(intentBroadCastSort);
    }
}
