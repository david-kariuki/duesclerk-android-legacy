package com.duesclerk.custom.custom_views.recycler_view_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.duesclerk.R;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.java_beans.JB_Contacts;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RVLA_Contacts extends RecyclerView.Adapter<RVLA_Contacts.RecyclerViewHolder> {

    private final Context mContext;
    private final ArrayList<JB_Contacts> contacts;
    private int lastPosition = 0;

    private View viewHolderView; // ViewHolder view

    /**
     * Class constructor
     *
     * @param context - Class context
     */
    public RVLA_Contacts(Context context,
                         ArrayList<JB_Contacts> contacts) {
        this.mContext = context;
        this.contacts = contacts;
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
}
