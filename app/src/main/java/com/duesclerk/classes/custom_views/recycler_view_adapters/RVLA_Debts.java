package com.duesclerk.classes.custom_views.recycler_view_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.duesclerk.R;
import com.duesclerk.activities.ContactDetailsAndDebtsActivity;
import com.duesclerk.classes.custom_utilities.application.ViewsUtils;
import com.duesclerk.classes.custom_utilities.user_data.ContactUtils;
import com.duesclerk.classes.custom_utilities.user_data.DataUtils;
import com.duesclerk.classes.java_beans.JB_Debts;
import com.duesclerk.interfaces.Interface_Debts;
import com.duesclerk.interfaces.Interface_IDS;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RVLA_Debts extends RecyclerView.Adapter<RVLA_Debts.RecyclerViewHolder>
        implements Filterable {

    public final ArrayList<String> checkedDebtsIds;
    private final Context mContext;
    private final ArrayList<JB_Debts> filterList;
    private final Interface_IDS interfaceIds;
    private final Interface_Debts interfaceDebts;
    private ArrayList<JB_Debts> debts;
    private DebtsFilter debtsFilter;
    private int lastPosition = 0;
    private View viewHolderView; // ViewHolder view

    /**
     * Class constructor
     *
     * @param context                        - Activity context
     * @param debts                          - ArrayList with debts
     * @param contactDetailsAndDebtsActivity - ContactDetailsAndDebtsActivity
     */
    public RVLA_Debts(final @NonNull Context context, @NonNull ArrayList<JB_Debts> debts,
                      final @NonNull ContactDetailsAndDebtsActivity contactDetailsAndDebtsActivity) {

        this.mContext = context; // Initialize context

        // Initialize ArrayLists
        this.debts = debts;
        this.filterList = debts;
        this.checkedDebtsIds = new ArrayList<>();

        // Initialize interfaces
        this.interfaceIds = contactDetailsAndDebtsActivity;
        this.interfaceDebts = contactDetailsAndDebtsActivity;
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

        // Check ViewHolder position
        if (position % 2 == 0) {
            // Position is even number

            // Set background resource
            holder.llDebtNumber.setBackgroundResource(
                    R.drawable.circle_border_primary_fill_primary);

        } else {
            // Position is odd number

            // Set background resource
            holder.llDebtNumber.setBackgroundResource(R.drawable.circle_border_accent_fill_accent);
        }

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

        // Check if buttons layout is shown so as to show options buttons
        if (debts.get(position).shownButtonsLayout()) {
            // Layout buttons shown

            holder.llButtons.setVisibility(View.VISIBLE); // Show buttons layout

            // Check if debt menu at current position is showing
            if (debts.get(position).expandedDebtOptionsMenu()) {

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
            if (debts.get(position).expandedDebtDetailsLayout()) {

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
        } else {

            holder.llButtons.setVisibility(View.GONE); // Hide buttons layout
        }

        setAnimation(holder.itemView, position); // Set animation

        // Check if CheckBox at current position is shown
        if (debts.get(position).showingCheckbox()) {

            holder.checkBox.setVisibility(View.VISIBLE); // Show CheckBox
            holder.llDebtNumber.setVisibility(View.GONE); // Hide debt number

            // Check / UnCheck CheckBox
            holder.checkBox.setChecked(debts.get(position).checkBoxChecked());

        } else {

            holder.checkBox.setVisibility(View.GONE); // Hide CheckBox
            holder.llDebtNumber.setVisibility(View.VISIBLE); // Show debt number
        }

        // Debts list item OnClick
        holder.consDebtItem.setOnClickListener(v -> {

            if (debts.get(position).expandedDebtOptionsMenu()) {

                // Set debt options menu to expanded
                setExpandedDebtOptionsMenu(false, position);
            }

            // Check if CheckBoxes are showing
            if (showingCheckBoxes()) {
                // CheckBoxes showing

                // Check / Uncheck CheckBox
                holder.checkBox.setChecked(!debts.get(position).checkBoxChecked());

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

        // CheckBox check change listener
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            // Set CheckBox checked to true / false
            debts.get(position).setCheckBoxChecked(isChecked);

            if (isChecked) {

                // Add debt id to checked debts ids
                checkedDebtsIds.add(debts.get(position).getDebtId());

            } else {

                // Remove debt id to checked debts ids
                checkedDebtsIds.remove(debts.get(position).getDebtId());
            }

            // Show / Hide delete debts FAB if any CheckBox is checked
            interfaceDebts.showDeleteDebtsFab(anyCheckBoxChecked());
        });

        // List item onLongClick
        holder.consDebtItem.setOnLongClickListener(v -> {

            if (!showingCheckBoxes()) {

                // Show / Hide debt item menu based on current state
                setExpandedDebtOptionsMenu(!debts.get(position).expandedDebtOptionsMenu(), position);
            }

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

            // Add debt id to checked debt ids
            checkedDebtsIds.add(debts.get(position).getDebtId());

            setExpandedDebtOptionsMenu(false, position); // Collapse debt item menu

            // Pass debts ids to interface
            interfaceIds.passDebtsIds(getCheckedDebtsIds());

        });

        // Edit debt onClick
        holder.imageEditDebt.setOnClickListener(v -> {

            setExpandedDebtOptionsMenu(false, position); // Collapse debt item menu

            // Create JavaBean to store debt details
            JB_Debts jbDebts = new JB_Debts();

            // Add debt details to JavaBean
            jbDebts.setDebtId(debts.get(position).getDebtId());
            jbDebts.setDebtAmount(debts.get(position).getDebtAmount());
            jbDebts.setDebtDateIssued(debts.get(position).getDebtDateIssued());
            jbDebts.setDebtDateDue(debts.get(position).getDebtDateDue());
            jbDebts.setDebtDescription(debts.get(position).getDebtDescription());

            // Pass debt details JavaBean to interface
            interfaceDebts.passDebtDetails(jbDebts);
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
     *
     * @param viewToAnimate - View to animate
     * @param position      - View position in the list
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
                !debts.get(position).expandedDebtDetailsLayout());

        notifyDataSetChanged(); // Notify data set changed
    }

    /**
     * Function to collapse debts options menu and debt details
     *
     * @param calledWithinAdapter - To prevent multiple calling of notifyDataSetChanged()
     *                            within methods
     */
    public void setCollapsedExpandedLayouts(boolean calledWithinAdapter) {

        // Loop through debts list
        for (int i = 0; i < debts.size(); i++) {

            // Set debt options menu to expanded
            debts.get(i).setExpandedDebtOptionsMenu(false);

            // Set debt details layout to expanded
            debts.get(i).setExpandedDebtDetailsLayout(false);
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

        // Loop through debts list
        for (int i = 0; i < debts.size(); i++) {

            if (setShown) {

                // Show CheckBox at position
                debts.get(i).setShowCheckBox(true);

            } else {

                debts.get(i).setCheckBoxChecked(false); // Set CheckBox to unChecked
                debts.get(i).setShowCheckBox(false); // Hide CheckBox at position
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

        // Loop through debts list
        for (int i = 0; i < debts.size(); i++) {

            // Show CheckBox at position
            debts.get(i).setShownButtonsLayout(setShown);
        }
    }

    /**
     * Function to check if debts options menu and debt details are expanded
     */
    public boolean isExpandedOptionsOrDetails() {

        // Loop through debts list
        for (int i = 0; i < debts.size(); i++) {

            // Check if debt option menu or debt details layout is expanded
            if (debts.get(i).expandedDebtOptionsMenu() || debts.get(i).expandedDebtDetailsLayout()) {

                return true; // Return true
            }
        }

        return false; //  Return false
    }

    /**
     * Function to check if CheckBoxes are shown
     */
    public boolean showingCheckBoxes() {

        // Loop through debts list
        for (int i = 0; i < debts.size(); i++) {

            // Check if CheckBox is shown
            if (debts.get(i).showingCheckbox()) {

                return true; // Return true
            }
        }

        return false; //  Return false
    }

    /**
     * Function to check if any CheckBox has been checked
     */
    private boolean anyCheckBoxChecked() {

        // Loop through debts list
        for (int i = 0; i < debts.size(); i++) {

            // Check if CheckBox is shown
            if (debts.get(i).checkBoxChecked()) {

                return true; // Return true
            }
        }

        return false; //  Return false
    }

    /**
     * Function to get checked debts ids
     */
    public String[] getCheckedDebtsIds() {

        // Convert ArrayList to String array and return
        return checkedDebtsIds.toArray(new String[0]);
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

        ConstraintLayout consDebtItem, consDebtOptions;
        TextView textDebtCount, textDebtAmount, textDebtType, textDebtDescription,
                textDateDebtIssued, textDateDebtDue;
        ImageView imageDebtDetailsDropDown, imageDebtOptions;
        ImageView imageEditDebt, imageDeleteDebt, imageCollapseDebtOptionsMenu;
        ExpandableLayout expandableDebtDetails, expandableDebtMenu;
        LinearLayout llDebtNumber, llButtons;
        CheckBox checkBox;

        RecyclerViewHolder(View convertView) {

            super(convertView);
            viewHolderView = convertView;

            // Debt item
            consDebtItem = convertView.findViewById(R.id.constraintLayout);
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

            // Debt number
            llDebtNumber = convertView.findViewById(R.id.llDebt_DebtNumber);
            llButtons = convertView.findViewById(R.id.llDebt_ButtonsLayout);

            // CheckBox
            checkBox = convertView.findViewById(R.id.cbDebt_CheckBox);
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

            // Check if constraint is null
            if (constraint != null && constraint.length() > 0) {

                // Convert constraint to uppercase
                constraint = constraint.toString().toUpperCase();

                // Create an ArrayList for filtered data
                ArrayList<JB_Debts> filters = new ArrayList<>();

                // Loop through list
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
                                filterList.get(i).getDebtDescription(),
                                filterList.get(i).getContactId(),
                                filterList.get(i).getContactType(),
                                filterList.get(i).getUserId(),
                                filterList.get(i).expandedDebtOptionsMenu(),
                                filterList.get(i).expandedDebtDetailsLayout(),
                                filterList.get(i).showingCheckbox(),
                                filterList.get(i).checkBoxChecked(),
                                filterList.get(i).shownButtonsLayout()
                        );

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
