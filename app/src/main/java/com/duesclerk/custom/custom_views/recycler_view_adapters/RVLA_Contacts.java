package com.duesclerk.custom.custom_views.recycler_view_adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.duesclerk.R;
import com.duesclerk.activities.ContactDetailsAndDebtsActivity;
import com.duesclerk.custom.custom_utilities.application.ViewsUtils;
import com.duesclerk.custom.custom_utilities.user_data.ContactUtils;
import com.duesclerk.custom.custom_utilities.user_data.DataUtils;
import com.duesclerk.custom.java_beans.JB_Contacts;
import com.duesclerk.interfaces.Interface_Contacts;
import com.duesclerk.interfaces.Interface_IDS;
import com.duesclerk.ui.fragment_contacts.fragment_people_i_owe.FragmentPeople_I_Owe;
import com.duesclerk.ui.fragment_contacts.fragment_people_owing_me.FragmentPeopleOwingMe;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RVLA_Contacts extends RecyclerView.Adapter<RVLA_Contacts.RecyclerViewHolder>
        implements Filterable {

    public final ArrayList<String> checkedContactsIds;
    private final Interface_IDS interfaceIds;
    private final Interface_Contacts interfaceContacts;
    private ArrayList<JB_Contacts> contacts, filterListArray;
    private ContactsFilter contactsFilter;
    private View viewHolderView; // ViewHolder view

    /**
     * Class constructor for FragmentPeopleOwingMe
     *
     * @param contacts              - ArrayList with contact
     * @param fragmentPeopleOwingMe - Calling fragment
     */
    public RVLA_Contacts(ArrayList<JB_Contacts> contacts,
                         FragmentPeopleOwingMe fragmentPeopleOwingMe) {

        loadContactsArray(contacts); // Load contacts array

        // Initialize interface
        this.interfaceIds = fragmentPeopleOwingMe;
        this.interfaceContacts = fragmentPeopleOwingMe;

        checkedContactsIds = new ArrayList<>(); // Initialize ArrayList
    }

    /**
     * Class constructor for FragmentPeopleIOwe
     *
     * @param contacts           - ArrayList with contact
     * @param fragmentPeopleIOwe - Calling fragment
     */
    public RVLA_Contacts(ArrayList<JB_Contacts> contacts, FragmentPeople_I_Owe fragmentPeopleIOwe) {

        loadContactsArray(contacts); // Load contacts array

        // Initialize interface
        this.interfaceIds = fragmentPeopleIOwe;
        this.interfaceContacts = fragmentPeopleIOwe;

        checkedContactsIds = new ArrayList<>(); // Initialize ArrayList
    }

    /**
     * Function to load contacts arrays
     *
     * @param contacts - ArrayList<JB_Contacts>
     */
    private void loadContactsArray(ArrayList<JB_Contacts> contacts) {

        this.contacts = contacts; // Load contacts
        this.filterListArray = contacts; // Load contacts
    }

    @Override
    public @NotNull RecyclerViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_adapter_contacts, parent, false);

        return new RecyclerViewHolder(layoutView); // Return view holder
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerViewHolder holder, final int position) {

            String contactFullName = this.contacts.get(position).getContactFullName();

            // Check ViewHolder position
            if (position % 2 == 0) {
                // Position is even number

                // Set background resource
                holder.llContactAvatar.setBackgroundResource(R.drawable.circle_border_primary_fill_primary);

            } else {
                // Position is odd number

                // Set background resource
                holder.llContactAvatar.setBackgroundResource(R.drawable.circle_border_accent_fill_accent);
            }

            if (contactFullName.length() == 1) {

                // Set text to first character of contact full name
                holder.textContactAvatarText.setText(DataUtils.stringToTitleCase(
                        contactFullName.substring(0, 1)));
            } else {

                // Set text to first and second character of contact full name
                holder.textContactAvatarText.setText(DataUtils.stringToTitleCase(
                        contactFullName.substring(0, 2)));
            }

            // Set contact data to TextViews
            holder.textContactFullName.setText(contactFullName);
            holder.textContactPhoneNumber.setText(this.contacts.get(position)
                    .getContactPhoneNumber());

            // Get contact email address
            String contactEmailAddress = this.contacts.get(position).getContactEmailAddress();

            // Check for email address
            if (!DataUtils.isEmptyString(contactEmailAddress)) {

                // Show contact email address
                holder.textContactEmailAddress.setVisibility(View.VISIBLE);

                // Set contact email address
                holder.textContactEmailAddress.setText(contactEmailAddress);
            }

            // Get contacts total contacts amount
            String singleContactsTotalDebtsAmount = this.contacts.get(position)
                    .getSingleContactsDebtsTotalAmount();

            // Set contacts total contact amount
            holder.textSingleContactsDebtsTotalAmount.setText(singleContactsTotalDebtsAmount);

            // Check for contacts total contacts amount to bold text
            if (!singleContactsTotalDebtsAmount.equals("0")) {

                // Bold contacts total contact amount text
                holder.textSingleContactsDebtsTotalAmount.setTypeface(
                        holder.textSingleContactsDebtsTotalAmount.getTypeface(), Typeface.BOLD);
            }

            // Check if buttons layout is shown so as to show options buttons
            if (contacts.get(position).isShownButtonsLayout()) {
                // Layout buttons shown

                holder.llButtonsLayout.setVisibility(View.VISIBLE); // Show buttons layout

                // Check if contact menu at current position is showing
                if (contacts.get(position).isExpandedContactOptionsMenu()) {

                    // Hide ConstraintLayout with contact options
                    holder.consContactOptions.setVisibility(View.GONE);

                    // Expand contact options menu
                    ViewsUtils.expandExpandableLayout(true, holder.expandableContactMenu);

                } else {

                    // Collapse contact options menu
                    ViewsUtils.expandExpandableLayout(false, holder.expandableContactMenu);

                    // Show ConstraintLayout with contact options
                    holder.consContactOptions.setVisibility(View.VISIBLE);
                }
            } else {

                holder.llButtonsLayout.setVisibility(View.GONE); // Hide buttons layout
            }

            // Check if CheckBox at current position is shown
            if (contacts.get(position).showingCheckbox()) {

                holder.checkBox.setVisibility(View.VISIBLE); // Show CheckBox
                holder.llContactAvatar.setVisibility(View.GONE); // Hide contact avatar

                // Check / UnCheck CheckBox
                holder.checkBox.setChecked(contacts.get(position).checkBoxChecked());

            } else {

                holder.checkBox.setVisibility(View.GONE); // Hide CheckBox
                holder.llContactAvatar.setVisibility(View.VISIBLE); // Show contact avatar
            }


            // List item OnClick
            holder.consContactItem.setOnClickListener(v -> {

                if (contacts.get(position).isExpandedContactOptionsMenu()) {

                    // Set contact options menu to expanded
                    setExpandedContactOptionsMenu(false, position);
                }

                // Check if CheckBoxes are showing
                if (!showingCheckBoxes()) {
                    // CheckBoxes not showing

                    // Start debts activity
                    Intent intent = new Intent(v.getContext(), ContactDetailsAndDebtsActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

                    // Pass contact id
                    intent.putExtra(ContactUtils.FIELD_CONTACT_ID,
                            this.contacts.get(position).getContactId());

                    // Pass contact full name
                    intent.putExtra(ContactUtils.FIELD_CONTACT_FULL_NAME,
                            this.contacts.get(position).getContactFullName());

                    // Pass contact type
                    intent.putExtra(ContactUtils.FIELD_CONTACT_TYPE,
                            this.contacts.get(position).getContactType());

                    v.getContext().startActivity(intent); // Start activity
                } else {

                    // Check / UnCheck CheckBoxes
                    holder.checkBox.setChecked(!this.contacts.get(position).checkBoxChecked());
                }
            });

            // List item onLongClick
            holder.consContactItem.setOnLongClickListener(v -> {

                if (!showingCheckBoxes()) {

                    // Show / Hide contact item menu based on current state
                    setExpandedContactOptionsMenu(!contacts.get(position).isExpandedContactOptionsMenu(), position);
                }
                return true; // Return true
            });

            // Image contact options button onClick
            holder.imageContactOptions.setOnClickListener(v -> {

                // Set contact options menu to expanded
                setExpandedContactOptionsMenu(true, position);
            });

            holder.imageCollapseContactOptionsMenu.setOnClickListener(v -> {

                setExpandedContactOptionsMenu(false, position); // Collapse contact item menu
            });

            // Menu items onClick
            holder.imageDeleteContact.setOnClickListener(v -> {

                // Add contact id to checked contact ids
                checkedContactsIds.add(contacts.get(position).getContactId());

                setExpandedContactOptionsMenu(false, position); // Collapse contact item menu

                // Pass contacts ids to interface
                interfaceIds.passContactsIds(getCheckedContactsIds());

            });

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                // Set CheckBox checked to true / false
                contacts.get(position).setCheckBoxChecked(isChecked);

                if (isChecked) {

                    // Add contact id to checked contacts ids
                    checkedContactsIds.add(contacts.get(position).getContactId());

                } else {

                    // Remove contact id to checked contacts ids
                    checkedContactsIds.remove(contacts.get(position).getContactId());
                }

                // Show / Hide delete contacts FAB if any CheckBox is checked
                interfaceContacts.showFabDeleteContacts(anyCheckBoxChecked());
            });
    }

    @Override
    public long getItemId(int position) {

        return position; // Return item id
    }

    @Override
    public int getItemCount() {

        int itemCount = 0; // Item count

        // Check if array is null
        if (this.contacts.size() != 0) {

            itemCount = this.contacts.size(); // Get item count
        }

        return itemCount; // Return item count
    }

    @Override
    public void onViewDetachedFromWindow(final @NotNull RecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        holder.clearAnimation(); // Clear animation
    }

    /**
     * Function to set contact options menu to expanded or collapsed
     *
     * @param expanded - Expanded / collapsed
     * @param position - Contacts list position
     */
    public void setExpandedContactOptionsMenu(boolean expanded, int position) {

        // Loop through contacts list
        for (int i = 0; i < contacts.size(); i++) {

            // Collapse contact options menu at position
            contacts.get(i).setExpandedContactOptionsMenu(false);
        }

        // Set contact options menu to expanded
        contacts.get(position).setExpandedContactOptionsMenu(expanded);

        notifyDataSetChanged(); // Notify data set changed
    }

    /**
     * Function to collapse contacts options menu and contact details
     *
     * @param calledWithinAdapter - To prevent multiple calling of notifyDataSetChanged()
     *                            within methods
     */
    public void setCollapsedExpandedLayouts(boolean calledWithinAdapter) {

        // Loop through contacts list
        for (int i = 0; i < contacts.size(); i++) {

            // Set contact options menu to expanded
            contacts.get(i).setExpandedContactOptionsMenu(false);
        }

        // Check if (method called within adapter) value is false
        if (!calledWithinAdapter) {

            notifyDataSetChanged(); // Notify data set changed
        }
    }

    /**
     * Function to set CheckBoxes to shown
     *
     * @param setShown - Set shown
     */
    public void setShownListCheckBoxes(boolean setShown) {

        // Loop through contacts list
        for (int i = 0; i < contacts.size(); i++) {

            if (setShown) {

                // Show CheckBox at position
                contacts.get(i).setShowCheckBox(true);

            } else {

                contacts.get(i).setCheckBoxChecked(false); // Set CheckBox to unChecked
                contacts.get(i).setShowCheckBox(false); // Hide CheckBox at position
            }
        }

        setCollapsedExpandedLayouts(true); // Expand other expanded layout
        setShownButtonsLayout(!setShown); // Hide buttons layout

        notifyDataSetChanged(); // Notify data set changed
    }

    /**
     * Function to set buttons layout to shown
     *
     * @param setShown - Hidden / shown
     */
    public void setShownButtonsLayout(boolean setShown) {

        // Loop through contacts list
        for (int i = 0; i < contacts.size(); i++) {

            // Show CheckBox at position
            contacts.get(i).setShownButtonsLayout(setShown);
        }
    }

    /**
     * Function to check if contacts options menu and contact details are expanded
     */
    public boolean isExpandedOptions() {

        // Loop through contacts list
        for (int i = 0; i < contacts.size(); i++) {

            // Check if contact option menu or contact details layout is expanded
            if (contacts.get(i).isExpandedContactOptionsMenu()) {

                return true; // Return true
            }
        }

        return false; //  Return false
    }

    /**
     * Function to check if CheckBoxes are shown
     */
    public boolean showingCheckBoxes() {

        // Loop through contacts list
        for (int i = 0; i < contacts.size(); i++) {

            // Check if CheckBox is shown
            if (contacts.get(i).showingCheckbox()) {

                return true; // Return true
            }
        }

        return false; //  Return false
    }

    /**
     * Function to check if any CheckBox has been checked
     */
    private boolean anyCheckBoxChecked() {

        // Loop through contacts list
        for (int i = 0; i < contacts.size(); i++) {

            // Check if CheckBox is shown
            if (contacts.get(i).checkBoxChecked()) {

                return true; // Return true
            }
        }

        return false; //  Return false
    }

    /**
     * Function to get checked contacts ids
     */
    public String[] getCheckedContactsIds() {

        // Convert ArrayList to String array and return
        return checkedContactsIds.toArray(new String[0]);
    }

    @Override
    public Filter getFilter() {

        // Check if filter is null
        if (contactsFilter == null) {

            contactsFilter = new ContactsFilter(); // Initialize filter
        }

        return contactsFilter; // Return filter
    }

    /**
     * ViewHolder class
     */
    public class RecyclerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView textContactFullName, textContactPhoneNumber, textContactEmailAddress,
                textSingleContactsDebtsTotalAmount;
        TextView textContactAvatarText;
        ConstraintLayout consContactItem, consContactOptions;
        LinearLayout llContactAvatar, llButtonsLayout;
        ImageView imageContactOptions, imageEditContact, imageDeleteContact, imageCollapseContactOptionsMenu;
        ExpandableLayout expandableContactMenu;
        CheckBox checkBox;

        RecyclerViewHolder(View convertView) {
            super(convertView);

            viewHolderView = convertView;

            // Fiend views by ids
            textContactFullName = convertView.findViewById(R.id.textContact_ContactFullName);
            textContactPhoneNumber = convertView.findViewById(R.id.textContact_ContactPhoneNumber);
            textContactEmailAddress = convertView.findViewById(R.id.textContact_ContactEmailAddress);
            textSingleContactsDebtsTotalAmount = convertView.findViewById(R.id.textContact_ContactTotalAmount);
            textContactAvatarText = convertView.findViewById(R.id.textContact_ContactAvatarText);

            // Contact item
            consContactItem = convertView.findViewById(R.id.constraintLayout);
            consContactOptions = convertView.findViewById(R.id.consContact_ContactOptions);

            llContactAvatar = convertView.findViewById(R.id.llContact_ContactAvatar);

            // Hide email address until set if present
            textContactEmailAddress.setVisibility(View.GONE);

            imageContactOptions = convertView.findViewById(R.id.imageContact_Menu);

            // Contact options buttons
            imageEditContact = convertView.findViewById(R.id.imageContact_EditContact);
            imageDeleteContact = convertView.findViewById(R.id.imageContact_DeleteContact);
            imageCollapseContactOptionsMenu =
                    convertView.findViewById(R.id.imageContact_CollapseContactOptionsMenu);

            // Contact details and menu expandable layout
            expandableContactMenu = convertView.findViewById(R.id.expandableContact_Menu);

            // Buttons layout
            llButtonsLayout = convertView.findViewById(R.id.llContact_ButtonsLayout);

            // CheckBox
            checkBox = convertView.findViewById(R.id.cbContact_CheckBox);
        }

        /**
         * Function to clear animation
         */
        public void clearAnimation() {

            viewHolderView.clearAnimation(); // Clear animation
        }

        @Override
        public void onClick(View v) {
        }
    }

    /**
     * Contacts filter class
     */
    @SuppressWarnings("unchecked")
    class ContactsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {

                // Constraint to uppercase
                constraint = constraint.toString().toUpperCase();

                ArrayList<JB_Contacts> filters = new ArrayList<>();

                // Get specific items
                for (int i = 0; i < filterListArray.size(); i++) {

                    JB_Contacts jbContacts;
                    if (DataUtils.isEmptyString(filterListArray.get(i).getSingleContactsDebtsTotalAmount())) {

                        if (filterListArray.get(i).getContactFullName().toUpperCase().contains(constraint)
                                || filterListArray.get(i).getContactPhoneNumber().contains(constraint)
                                || filterListArray.get(i).getContactEmailAddress().toUpperCase()
                                .contains(constraint)
                        ) {
                            // Excluding contact total amount in search

                            // Create new result java bean
                            jbContacts = new JB_Contacts(
                                    filterListArray.get(i).getContactId(),
                                    filterListArray.get(i).getContactFullName(),
                                    filterListArray.get(i).getContactPhoneNumber(),
                                    filterListArray.get(i).getContactEmailAddress(),
                                    filterListArray.get(i).getContactAddress(),
                                    filterListArray.get(i).getContactType(),
                                    filterListArray.get(i).getSingleContactsDebtsTotalAmount());

                            filters.add(jbContacts); // Add java bean to ArrayList
                        }
                    } else {
                        // Including contact total amount in search

                        if (filterListArray.get(i).getContactFullName().toUpperCase().contains(constraint)
                                || filterListArray.get(i).getContactPhoneNumber().contains(constraint)
                                || filterListArray.get(i).getContactEmailAddress().toUpperCase()
                                .contains(constraint)
                                || filterListArray.get(i).getSingleContactsDebtsTotalAmount().contains(constraint)
                        ) {

                            // Create new result java bean
                            jbContacts = new JB_Contacts(
                                    filterListArray.get(i).getContactId(),
                                    filterListArray.get(i).getContactFullName(),
                                    filterListArray.get(i).getContactPhoneNumber(),
                                    filterListArray.get(i).getContactEmailAddress(),
                                    filterListArray.get(i).getContactAddress(),
                                    filterListArray.get(i).getContactType(),
                                    filterListArray.get(i).getSingleContactsDebtsTotalAmount());

                            filters.add(jbContacts); // Add java bean to ArrayList
                        }
                    }
                }

                results.count = filters.size(); // Update FilterResults count
                results.values = filters; // Update FilterResults values

            } else {

                results.count = filterListArray.size(); // Update FilterResults count
                results.values = filterListArray; // Update FilterResults values
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            contacts = (ArrayList<JB_Contacts>) results.values; // Set values to ArrayList
            notifyDataSetChanged(); // Notify data set changed
        }
    }
}
