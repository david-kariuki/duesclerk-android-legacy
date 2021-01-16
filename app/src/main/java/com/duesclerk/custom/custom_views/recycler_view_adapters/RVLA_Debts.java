package com.duesclerk.custom.custom_views.recycler_view_adapters;

import android.content.Context;
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
import com.duesclerk.custom.custom_utilities.ContactUtils;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.java_beans.JB_Debts;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RVLA_Debts extends RecyclerView.Adapter<RVLA_Debts.RecyclerViewHolder> implements Filterable {

    private final Context mContext;
    private final ArrayList<JB_Debts> filterList;
    private ArrayList<JB_Debts> debts;
    private int lastPosition = 0;
    private DebtsFilter debtsFilter;

    private View viewHolderView; // ViewHolder view

    /**
     * Class constructor
     *
     * @param context - Class context
     * @param debts   - ArrayList with debts
     */
    public RVLA_Debts(Context context,
                      ArrayList<JB_Debts> debts) {

        this.mContext = context;
        this.debts = debts;
        this.filterList = debts;
    }

    @Override
    public @NotNull RecyclerViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_adapter_debts, parent, false);

        return new RecyclerViewHolder(layoutView); // Return view holder
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        // Set debt data to TextViews
        holder.textDebtCount.setText(String.valueOf(position + 1));
        holder.textDebtAmount.setText(debts.get(position).getDebtAmount());

        // Get and check contact type
        String contactType = debts.get(position).getDebtAmount();
        if (contactType.equals(ContactUtils.KEY_CONTACT_TYPE_PEOPLE_OWING_ME)) {

            holder.textDebtType.setText(DataUtils.getStringResource(mContext,
                    R.string.hint_debt_type_you_are_owed));

        } else if (contactType.equals(ContactUtils.KEY_CONTACT_TYPE_PEOPLE_I_OWE)) {

            holder.textDebtType.setText(DataUtils.getStringResource(mContext,
                    R.string.hint_debt_type_you_owe));
        }

        // Get debt description
        String debtDescription = debts.get(position).getDebtDescription();

        // Check debt description value
        if (!DataUtils.isEmptyString(debtDescription)) {

            holder.textDebtDescription.setText(debtDescription); // Set debt description
        }

        // Set debt date issued and date due
        holder.textDateDebtIssued.setText(debts.get(position).getDebtDateIssued());
        holder.textDateDebtDue.setText(debts.get(position).getDebtDateDue());

        holder.imageDropDown.setOnClickListener(v -> {

            // Check if expandable layout has been expanded
            if (!holder.expandableLayout.isExpanded()) {

                // Expand ExpandableLayout
                ViewsUtils.expandExpandableLayout(true, holder.expandableLayout);

                holder.imageDropDown.setRotation(180); // Rotate dropdown icon to invert it

            } else {

                // Expand ExpandableLayout
                ViewsUtils.expandExpandableLayout(false, holder.expandableLayout);

                holder.imageDropDown.setRotation(0); // Revert image rotation
            }
        });

        // Check for ViewHolder
        if (viewHolderView != null) {

            // List item OnClick
            holder.consContactItem.setOnClickListener(v -> {

//                // Start Debts activity
//                Intent intent = new Intent(v.getContext(), ContactDetailsAndDebtsActivity.class);
//
//                // Pass contact id
//                intent.putExtra(ContactUtils.FIELD_CONTACT_ID, this.debts
//                        .get(position).getContactId());
//
//                // Pass contact full name
//                intent.putExtra(ContactUtils.FIELD_CONTACT_ID, this.debts
//                        .get(position).getContactId());
//
//                v.getContext().startActivity(intent);
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
        if (this.debts.size() != 0) {

            itemCount = this.debts.size(); // Get item count
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

        if (debtsFilter == null) {
            debtsFilter = new RVLA_Debts.DebtsFilter();
        }

        return debtsFilter; // Return filter
    }

    /**
     * ViewHolder class
     */
    public class RecyclerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ConstraintLayout consContactItem;
        TextView textDebtCount, textDebtAmount, textDebtType, textDebtDescription,
                textDateDebtIssued, textDateDebtDue;
        ImageView imageDropDown;
        ExpandableLayout expandableLayout;

        RecyclerViewHolder(View convertView) {

            super(convertView);
            viewHolderView = convertView;

            consContactItem = convertView.findViewById(R.id.constraintLayout);

            textDebtCount = convertView.findViewById(R.id.textDebt_Count);
            textDebtAmount = convertView.findViewById(R.id.textDebt_Amount);
            textDebtType = convertView.findViewById(R.id.textDebt_Type);
            textDebtDescription = convertView.findViewById(R.id.textDebt_Description);
            textDateDebtIssued = convertView.findViewById(R.id.textDebt_DateIssued);
            textDateDebtDue = convertView.findViewById(R.id.textDebt_DateDue);

            imageDropDown = convertView.findViewById(R.id.imageDebt_DropDown);

            expandableLayout = convertView.findViewById(R.id.expandableDebt);
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
     * Debts filter class
     */
    @SuppressWarnings("unchecked")
    class DebtsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {

                // Constraint to uppercase
                constraint = constraint.toString().toUpperCase();

                ArrayList<JB_Debts> filters = new ArrayList<>();

                // Get specific items
                for (int i = 0; i < filterList.size(); i++) {

                    if (filterList.get(i).getDebtAmount().contains(constraint)
                            || filterList.get(i).getDebtDescription().toUpperCase()
                            .contains(constraint)) {

                        JB_Debts jbDebts = new JB_Debts(
                                filterList.get(i).getDebtId(),
                                filterList.get(i).getDebtAmount(),
                                filterList.get(i).getDebtDateIssued(),
                                filterList.get(i).getDebtDateDue(),
                                filterList.get(i).getDebtDescription());

                        filters.add(jbDebts); // Add java bean to ArrayList
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

            debts = (ArrayList<JB_Debts>) results.values; // Set values to ArrayList
            notifyDataSetChanged(); // Notify data set changed
        }
    }
}
