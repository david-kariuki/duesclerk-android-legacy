package com.duesclerk.custom.custom_views.recycler_view_adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.duesclerk.R;
import com.duesclerk.activities.ContactDetailsAndDebtsActivity;
import com.duesclerk.custom.custom_utilities.user_data.ContactUtils;
import com.duesclerk.custom.custom_utilities.user_data.DataUtils;
import com.duesclerk.custom.java_beans.JB_Contacts;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RVLA_Contacts extends RecyclerView.Adapter<RVLA_Contacts.RecyclerViewHolder>
        implements Filterable {

    private final ArrayList<JB_Contacts> filterList;
    private ArrayList<JB_Contacts> contacts;
    private ContactsFilter contactsFilter;
    private View viewHolderView; // ViewHolder view

    /**
     * Class constructor
     *
     * @param contacts - ArrayList with contact
     */
    public RVLA_Contacts(ArrayList<JB_Contacts> contacts) {

        this.contacts = contacts;
        this.filterList = contacts;
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
        if (position %2 == 0) {
            // Position is even number

            // Set background resource
            holder.llAvatar.setBackgroundResource(R.drawable.circle_border_primary_fill_primary);

        } else {
            // Position is odd number

            // Set background resource
            holder.llAvatar.setBackgroundResource(R.drawable.circle_border_accent_fill_accent);
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

        // Get contacts total debts amount
        String contactsTotalDebtsAmount = this.contacts.get(position).getDebtsTotalAmount();

        // Set contacts total debt amount
        holder.textContactDebtTotalAmount.setText(contactsTotalDebtsAmount);

        // Check for contacts total debts amount to bold text
        if (!contactsTotalDebtsAmount.equals("0")) {

            // Bold contacts total debt amount text
            holder.textContactDebtTotalAmount.setTypeface(
                    holder.textContactDebtTotalAmount.getTypeface(), Typeface.BOLD);
        }

        // Check for ViewHolder
        if (viewHolderView != null) {

            // List item OnClick
            holder.consContactItem.setOnClickListener(v -> {

                // Start Debts activity
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

                v.getContext().startActivity(intent);
            });
        }
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
    public void onViewDetachedFromWindow(final @NotNull
                                                 RecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        holder.clearAnimation(); // Clear animation
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
                textContactDebtTotalAmount;
        TextView textContactAvatarText;
        ConstraintLayout consContactItem;
        LinearLayout llAvatar;

        RecyclerViewHolder(View convertView) {
            super(convertView);

            viewHolderView = convertView;

            // Fiend views by ids
            textContactFullName = convertView.findViewById(R.id.textContact_ContactFullName);
            textContactPhoneNumber = convertView.findViewById(R.id.textContact_ContactPhoneNumber);
            textContactEmailAddress = convertView.findViewById(R.id.textContact_ContactEmailAddress);
            textContactDebtTotalAmount = convertView.findViewById(R.id.textContact_ContactTotalAmount);
            textContactAvatarText = convertView.findViewById(R.id.textContact_ContactAvatarText);
            consContactItem = convertView.findViewById(R.id.constraintLayout);

            llAvatar = convertView.findViewById(R.id.llContact_ContactAvatar);

            // Hide email address until set if present
            textContactEmailAddress.setVisibility(View.GONE);
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
                for (int i = 0; i < filterList.size(); i++) {

                    JB_Contacts jbContacts;
                    if (DataUtils.isEmptyString(filterList.get(i).getDebtsTotalAmount())) {

                        if (filterList.get(i).getContactFullName().toUpperCase().contains(constraint)
                                || filterList.get(i).getContactPhoneNumber().contains(constraint)
                                || filterList.get(i).getContactEmailAddress().toUpperCase()
                                .contains(constraint)
                        ) {
                            // Excluding debt total amount in search

                            // Create new result java bean
                            jbContacts = new JB_Contacts(
                                    filterList.get(i).getContactId(),
                                    filterList.get(i).getContactFullName(),
                                    filterList.get(i).getContactPhoneNumber(),
                                    filterList.get(i).getContactEmailAddress(),
                                    filterList.get(i).getContactAddress(),
                                    filterList.get(i).getContactType(),
                                    filterList.get(i).getDebtsTotalAmount());

                            filters.add(jbContacts); // Add java bean to ArrayList
                        }
                    } else {
                        // Including debt total amount in search

                        if (filterList.get(i).getContactFullName().toUpperCase().contains(constraint)
                                || filterList.get(i).getContactPhoneNumber().contains(constraint)
                                || filterList.get(i).getContactEmailAddress().toUpperCase()
                                .contains(constraint)
                                || filterList.get(i).getDebtsTotalAmount().contains(constraint)
                        ) {

                            // Create new result java bean
                            jbContacts = new JB_Contacts(
                                    filterList.get(i).getContactId(),
                                    filterList.get(i).getContactFullName(),
                                    filterList.get(i).getContactPhoneNumber(),
                                    filterList.get(i).getContactEmailAddress(),
                                    filterList.get(i).getContactAddress(),
                                    filterList.get(i).getContactType(),
                                    filterList.get(i).getDebtsTotalAmount());

                            filters.add(jbContacts); // Add java bean to ArrayList
                        }
                    }
                }

                results.count = filters.size(); // Update FilterResults count
                results.values = filters; // Update FilterResults values

            } else {

                results.count = filterList.size(); // Update FilterResults count
                results.values = filterList; // Update FilterResults values
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
