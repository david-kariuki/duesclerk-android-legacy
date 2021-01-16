package com.duesclerk.custom.custom_views.recycler_view_adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.duesclerk.R;
import com.duesclerk.activities.ContactDetailsDebtsActivity;
import com.duesclerk.custom.custom_utilities.ContactUtils;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.java_beans.JB_Contacts;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RVLA_Contacts extends RecyclerView.Adapter<RVLA_Contacts.RecyclerViewHolder>
        implements Filterable {

    private final Context mContext;
    private final ArrayList<JB_Contacts> filterList;
    private ArrayList<JB_Contacts> contacts;
    private int lastPosition = 0;
    private ContactsFilter contactsFilter;

    private View viewHolderView; // ViewHolder view

    /**
     * Class constructor
     *
     * @param context  - Class context
     * @param contacts - ArrayList with contact
     */
    public RVLA_Contacts(Context context,
                         ArrayList<JB_Contacts> contacts) {

        this.mContext = context;
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
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        // Set contact data to TextViews
        holder.textContactFullName.setText(this.contacts.get(position).getContactFullName());
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

        // Check for ViewHolder
        if (viewHolderView != null) {

            // List item OnClick
            holder.consContactItem.setOnClickListener(v -> {

                // Start Debts activity
                Intent intent = new Intent(v.getContext(), ContactDetailsDebtsActivity.class);

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

        setAnimation(holder.itemView, position); // Set animation
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

    // Animating single element
    private void setAnimation(View viewToAnimate, int position) {

        if (position > this.lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(this.mContext,
                    android.R.anim.slide_in_left);

            viewToAnimate.startAnimation(animation); // Start animation

            this.lastPosition = position; // Set position to last position
        }
    }

    @Override
    public Filter getFilter() {

        if (contactsFilter == null) {
            contactsFilter = new ContactsFilter();
        }

        return contactsFilter; // Return filter
    }

    /**
     * ViewHolder class
     */
    public class RecyclerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView textContactFullName, textContactPhoneNumber, textContactEmailAddress,
                textContactTotalAmount;
        ImageView imageContactPicture;
        ConstraintLayout consContactItem;

        RecyclerViewHolder(View convertView) {

            super(convertView);
            viewHolderView = convertView;

            textContactFullName = convertView.findViewById(R.id.textContact_ContactFullName);
            textContactPhoneNumber = convertView.findViewById(R.id.textContact_ContactPhoneNumber);
            textContactEmailAddress = convertView.findViewById(R.id.textContact_ContactEmailAddress);
            textContactTotalAmount = convertView.findViewById(R.id.textContact_ContactTotalAmount);
            consContactItem = convertView.findViewById(R.id.constraintLayout);

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

                            jbContacts = new JB_Contacts(
                                    filterList.get(i).getContactFullName(),
                                    filterList.get(i).getContactPhoneNumber(),
                                    filterList.get(i).getContactEmailAddress(),
                                    filterList.get(i).getContactAddress(),
                                    filterList.get(i).getContactType());

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

                            jbContacts = new JB_Contacts(
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
