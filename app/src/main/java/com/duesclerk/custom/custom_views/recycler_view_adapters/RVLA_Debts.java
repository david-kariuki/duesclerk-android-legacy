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
import com.duesclerk.custom.custom_utilities.application.ViewsUtils;
import com.duesclerk.custom.custom_utilities.user_data.ContactUtils;
import com.duesclerk.custom.custom_utilities.user_data.DataUtils;
import com.duesclerk.custom.java_beans.JB_Debts;
import com.duesclerk.interfaces.Interface_IDS;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RVLA_Debts extends RecyclerView.Adapter<RVLA_Debts.RecyclerViewHolder>
        implements Filterable {

    private final Context mContext;
    private final ArrayList<JB_Debts> filterList;
    private ArrayList<JB_Debts> debts;
    private int lastPosition = 0;
    private DebtsFilter debtsFilter;
    private View viewHolderView; // ViewHolder view
    private Interface_IDS interfaceIds;

    /**
     * Class constructor
     *
     * @param context - Class context
     * @param debts   - ArrayList with debts
     */
    public RVLA_Debts(Context context,
                      ArrayList<JB_Debts> debts, Interface_IDS interfaceIds) {

        this.mContext = context;
        this.debts = debts;
        this.filterList = debts;
        this.interfaceIds = interfaceIds;
    }

    @Override
    public @NotNull RecyclerViewHolder onCreateViewHolder(@NotNull ViewGroup parent,
                                                          int viewType) {

        // Create view
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_adapter_debts, parent, false);

        return new RecyclerViewHolder(view); // Return view holder
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerViewHolder holder, final int position) {

        // Set debt data to TextViews
        holder.textDebtCount.setText(String.valueOf(position + 1));
        holder.textDebtAmount.setText(debts.get(position).getDebtAmount());

        // Check and set contact type
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

        // Check if debt description is empty
        if (!DataUtils.isEmptyString(debtDescription)) {

            holder.textDebtDescription.setText(debtDescription); // Set debt description
        }

        // Set debt date issued and date due
        holder.textDateDebtIssued.setText(debts.get(position).getDebtDateIssued());
        holder.textDateDebtDue.setText(debts.get(position).getDebtDateDue());

        // Check if menu at current position is showing
        if (debts.get(position).isExpandedDebtOptionsMenu()) {

            // Hide ConstraintLayout with debt options
            holder.consDebtOptions.setVisibility(View.GONE);

            // Expand debt options menu
            ViewsUtils.expandExpandableLayout(true, holder.expandableDebtMenu);

        } else {

            // Collapse debt options menu
            ViewsUtils.expandExpandableLayout(false, holder.expandableDebtMenu);

            // Show ConstraintLayout with debt options
            holder.consDebtOptions.setVisibility(View.VISIBLE);
        }

        // Check if menu at current position is showing
        if (debts.get(position).isExpandedDebtDetailsLayout()) {

            // Show ConstraintLayout with debt options
            holder.consDebtOptions.setVisibility(View.VISIBLE);

            // Expand ExpandableLayout
            ViewsUtils.expandExpandableLayout(true, holder.expandableDebtDetails);

            // Rotate dropdown icon to invert it
            holder.imageDebtDetailsDropDown.setRotation(180);

        } else {

            // Expand ExpandableLayout
            ViewsUtils.expandExpandableLayout(false, holder.expandableDebtDetails);

            holder.imageDebtDetailsDropDown.setRotation(0); // Revert image rotation
        }

        // Debts list item OnClick
        holder.consContactItem.setOnClickListener(v -> {

            if (debts.get(position).isExpandedDebtOptionsMenu()) {

                // Set debt options menu to expanded
                setExpandedDebtOptionsMenu(false, position);

            } else {
                // Start Debts activity
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
            }
        });

        // List item onLongClick
        holder.consContactItem.setOnLongClickListener(v -> {

            // Show / Hide debt item menu based on current state
            setExpandedDebtOptionsMenu(!debts.get(position).isExpandedDebtOptionsMenu(), position);

            return true; // Return true
        });

        // Image debt options button onClick
        holder.imageDebtOptions.setOnClickListener(v -> {

            // Set debt options menu to expanded
            setExpandedDebtOptionsMenu(true, position);
        });

        // Image drop down onClick
        holder.imageDebtDetailsDropDown.setOnClickListener(v -> {

            // Set debt details layout to expanded
            setExpandedDebtDetailsLayout(position);
        });

        holder.imageCollapseDebtOptionsMenu.setOnClickListener(v -> {

            setExpandedDebtOptionsMenu(false, position); // Collapse debt item menu
        });

        // Menu items onClick
        holder.imageDeleteDebt.setOnClickListener(v -> {

            // Pass debts ids to interface
            interfaceIds.passDebtsIds(new String[]{debts.get(position).getDebtId()});

            setExpandedDebtOptionsMenu(false, position); // Collapse debt item menu
        });

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
    public void onViewDetachedFromWindow(@NotNull RecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        holder.clearAnimation(); // Clear animation
    }

    /**
     * Function to animate list items
     */
    private void setAnimation(View viewToAnimate, int position) {

        // Check if current position is greater than last position
        if (position > this.lastPosition) {

            // Create animation object
            Animation animation = AnimationUtils.loadAnimation(this.mContext,
                    android.R.anim.slide_in_left);

            viewToAnimate.startAnimation(animation); // Start animation

            this.lastPosition = position; // Set last position to current position
        }
    }

    /**
     * Function to set debt options menu to expanded or collapsed
     *
     * @param expanded - Expanded / collapsed
     * @param position - Debts list position
     */
    public void setExpandedDebtOptionsMenu(boolean expanded, int position) {

        // Loop through debts list
        for (int i = 0; i < debts.size(); i++) {

            // Collapse debt options menu at position
            debts.get(i).setExpandedDebtOptionsMenu(false);

            // Check if current position in loop equals to passed position to
            // collapse debt details layout
            if (i == position) {

                // Collapse debt details layout
                debts.get(i).setExpandedDebtDetailsLayout(false);
            }
        }

        // Set debt options menu to expanded
        debts.get(position).setExpandedDebtOptionsMenu(expanded);

        notifyDataSetChanged(); // Notify data set changed
    }

    /**
     * Function to set debt details layout to expanded or collapsed
     *
     * @param position - Debts list position
     */
    public void setExpandedDebtDetailsLayout(int position) {

        // Set debt details layout to expanded / collapsed based on current state
        debts.get(position).setExpandedDebtDetailsLayout(
                !debts.get(position).isExpandedDebtDetailsLayout());

        notifyDataSetChanged(); // Notify data set changed
    }

    /**
     * Function to collapse debts options menu and debt details
     */
    public void setCollapsedExpandedLayouts() {

        // Loop through debts list
        for (int i = 0; i < debts.size(); i++) {

            debts.get(i).setExpandedDebtOptionsMenu(false);
            debts.get(i).setExpandedDebtDetailsLayout(false);
        }

        notifyDataSetChanged(); // Notify data set changed
    }

    /**
     * Function to check if debts options menu and debt details are expanded
     */
    public boolean isExpandedOptionsOrDetails() {

        // Loop through debts list
        for (int i = 0; i < debts.size(); i++) {

            if (debts.get(i).isExpandedDebtOptionsMenu()
                    || debts.get(i).isExpandedDebtDetailsLayout()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Filter getFilter() {

        // Check if filter is null
        if (debtsFilter == null) {

            debtsFilter = new RVLA_Debts.DebtsFilter(); // Initialize filter
        }

        return debtsFilter; // Return filter
    }

    /**
     * ViewHolder class
     */
    public class RecyclerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ConstraintLayout consContactItem, consDebtOptions;
        TextView textDebtCount, textDebtAmount, textDebtType, textDebtDescription,
                textDateDebtIssued, textDateDebtDue;
        ImageView imageDebtDetailsDropDown, imageDebtOptions;
        ImageView imageEditDebt, imageDeleteDebt, imageCollapseDebtOptionsMenu;
        ExpandableLayout expandableDebtDetails, expandableDebtMenu;

        RecyclerViewHolder(View convertView) {

            super(convertView);
            viewHolderView = convertView;

            // Debt item
            consContactItem = convertView.findViewById(R.id.constraintLayout);
            consDebtOptions = convertView.findViewById(R.id.consDebt_DebtOptions);

            // Debt details
            textDebtCount = convertView.findViewById(R.id.textDebt_Count);
            textDebtAmount = convertView.findViewById(R.id.textDebt_Amount);
            textDebtType = convertView.findViewById(R.id.textDebt_Type);
            textDebtDescription = convertView.findViewById(R.id.textDebt_Description);
            textDateDebtIssued = convertView.findViewById(R.id.textDebt_DateIssued);
            textDateDebtDue = convertView.findViewById(R.id.textDebt_DateDue);

            imageDebtDetailsDropDown = convertView.findViewById(R.id.imageDebt_DropDown);
            imageDebtOptions = convertView.findViewById(R.id.imageDebt_Menu);

            // Debt options buttons
            imageEditDebt = convertView.findViewById(R.id.imageDebt_EditDebt);
            imageDeleteDebt = convertView.findViewById(R.id.imageDebt_DeleteDebt);
            imageCollapseDebtOptionsMenu = convertView.findViewById(R.id.imageDebt_CollapseDebtOptionsMenu);

            // Debt details and menu expandable layout
            expandableDebtDetails = convertView.findViewById(R.id.expandableDebt_Details);
            expandableDebtMenu = convertView.findViewById(R.id.expandableDebt_Menu);
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

                        // Create new result java bean
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
