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
import com.duesclerk.activities.DebtsActivity;
import com.duesclerk.custom.custom_utilities.ContactUtils;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.UserAccountUtils;
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
    private String userId;

    private View viewHolderView; // ViewHolder view

    /**
     * Class constructor
     *
     * @param context  - Class context
     * @param contacts - ArrayList with contacts
     * @param userId   - Users id
     */
    public RVLA_Contacts(Context context,
                         ArrayList<JB_Contacts> contacts, String userId) {

        this.mContext = context;
        this.contacts = contacts;
        this.filterList = contacts;
        this.userId = userId;
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

        // Set country data to TextView
        holder.textContactsFullName.setText(this.contacts.get(position).getContactsFullName());
        holder.textContactsPhoneNumber.setText(this.contacts.get(position)
                .getContactsPhoneNumber());

        // Get contacts email address
        String contactsEmailAddress = this.contacts.get(position).getContactsEmailAddress();

        // Check for email address
        if (!DataUtils.isEmptyString(contactsEmailAddress)) {

            // Show contacts email address
            holder.textContactsEmailAddress.setVisibility(View.VISIBLE);

            // Set contacts email address
            holder.textContactsEmailAddress.setText(contactsEmailAddress);
        }

        // Check for ViewHolder
        if (viewHolderView != null) {

            // List item OnClick
            holder.consContactItem.setOnClickListener(v -> {

                // Start Debts activity
                Intent intent = new Intent(v.getContext(), DebtsActivity.class);

                // Pass contacts id
                intent.putExtra(ContactUtils.FIELD_CONTACT_ID,
                        this.contacts.get(position).getContactsId());

                // Pass user id
                intent.putExtra(UserAccountUtils.FIELD_USER_ID,
                        this.contacts.get(position).getContactsId());

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
        holder.clearAnimation();
    }

    // Animating single element
    private void setAnimation(View viewToAnimate, int position) {

        if (position > this.lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(this.mContext,
                    android.R.anim.slide_in_left);

            viewToAnimate.startAnimation(animation);
            this.lastPosition = position;
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

        TextView textContactsFullName, textContactsPhoneNumber, textContactsEmailAddress,
                textContactsTotalAmount;
        ImageView imageContactsPicture;
        ConstraintLayout consContactItem;

        RecyclerViewHolder(View convertView) {

            super(convertView);
            viewHolderView = convertView;

            textContactsFullName = convertView.findViewById(R.id.textContact_ContactsFullName);
            textContactsPhoneNumber = convertView.findViewById(R.id.textContact_ContactsPhoneNumber);
            textContactsEmailAddress = convertView.findViewById(R.id.textContact_ContactsEmailAddress);
            textContactsTotalAmount = convertView.findViewById(R.id.textContact_ContactsTotalAmount);
            consContactItem = convertView.findViewById(R.id.consCountryPicker_ContactItem);

            // Hide email address until set if present
            textContactsEmailAddress.setVisibility(View.GONE);
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

                    if (filterList.get(i).getContactsFullName().toUpperCase()
                            .contains(constraint)) {

                        JB_Contacts jbContacts = new JB_Contacts(
                                filterList.get(i).getContactsFullName(),
                                filterList.get(i).getContactsPhoneNumber(),
                                filterList.get(i).getContactsEmailAddress(),
                                filterList.get(i).getContactsAddress(),
                                filterList.get(i).getContactsType());

                        filters.add(jbContacts); // Add java bean to ArrayList
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
