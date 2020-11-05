package custom.custom_views.recycler_view_adapters;

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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import custom.custom_utilities.DataUtils;
import custom.custom_utilities.ViewsUtils;
import custom.custom_views.dialog_fragments.bottom_sheets.CountryPickerFragment;
import custom.java_beans.JB_CountryData;

public class RVLA_CountryPicker extends RecyclerView.Adapter<RVLA_CountryPicker.RecyclerViewHolder>
        implements Filterable {

    private final Context mContext;
    private final ArrayList<JB_CountryData> filterList;
    private final CountryPickerFragment countryPickerFragment;
    private ArrayList<JB_CountryData> countryData;
    private CustomFilter customFilter;
    private int lastPosition = 0;
    // ViewHolder view
    private View viewHolderView;

    public RVLA_CountryPicker(CountryPickerFragment countryPickerFragment,
                              ArrayList<JB_CountryData> countryData) {
        this.mContext = countryPickerFragment.getContext();
        this.countryData = countryData;
        this.filterList = countryData;
        this.countryPickerFragment = countryPickerFragment;
    }

    @Override
    public @NotNull RecyclerViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_recycler_view_adapter_country_picker, parent, false);

        ViewsUtils.hideKeyboard(countryPickerFragment.requireActivity());
        return new RecyclerViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        // Get country drawable from country name
        int countryFlagID = DataUtils.getDrawableFromName(this.mContext,
                this.countryData.get(position).getCountryFlag());

        // Set country data to TextView
        holder.textCountryName.setText(this.countryData.get(position).getCountryName());
        String plusCountryCode = "+" + this.countryData.get(position).getCountryCode();
        holder.textCountryCode.setText(plusCountryCode);
        holder.textCountryAlpha2.setText(this.countryData.get(position).getCountryAlpha2());
        holder.textCountryAlpha3.setText(this.countryData.get(position).getCountryAlpha3());

        // Load country flags to ImageView
        ViewsUtils.loadImageView(this.mContext, countryFlagID, holder.imageViewCountryFlags);

        if (viewHolderView != null) {
            // List item OnClick
            holder.consCountryItem.setOnClickListener(v -> {
                // Pass country data to interface
                this.countryPickerFragment.passSelectedCountryDataToInterface(
                        this.countryData.get(position).getCountryName(),
                        this.countryData.get(position).getCountryCode(),
                        this.countryData.get(position).getCountryAlpha2(),
                        this.countryData.get(position).getCountryAlpha3(), countryFlagID);
            });
        }
        setAnimation(holder.itemView, position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        // Check if array is null
        if (this.countryData.size() != 0) {
            itemCount = this.countryData.size(); // Get item count
        }
        return itemCount;
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
        if (customFilter == null) {
            customFilter = new CustomFilter();
        }
        return customFilter;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView textCountryName, textCountryCode, textCountryAlpha2, textCountryAlpha3;
        ImageView imageViewCountryFlags;
        ConstraintLayout consCountryItem;

        RecyclerViewHolder(View convertView) {
            super(convertView);
            viewHolderView = convertView;

            textCountryName = convertView.findViewById(R.id.textCountryPicker_CountryName);
            textCountryCode = convertView.findViewById(R.id.textCountryPicker_CountryCode);
            textCountryAlpha2 = convertView.findViewById(R.id.textCountryPicker_CountryAlpha2);
            textCountryAlpha3 = convertView.findViewById(R.id.textCountryPicker_CountryAlpha3);
            imageViewCountryFlags = convertView.findViewById(R.id.imageCountryPicker_CountryFlag);
            consCountryItem = convertView.findViewById(R.id.consCountryPicker_CountryItem);
        }

        public void clearAnimation() {
            viewHolderView.clearAnimation();
        }

        @Override
        public void onClick(View v) {
        }
    }

    // Custom filter class
    @SuppressWarnings("unchecked")
    class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                // Constraint to uppercase
                constraint = constraint.toString().toUpperCase();

                ArrayList<JB_CountryData> filters = new ArrayList<>();

                // Get specific items
                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getCountryName().toUpperCase().contains(constraint)) {
                        JB_CountryData pojoCountryData = new JB_CountryData(
                                filterList.get(i).getCountryName(),
                                filterList.get(i).getCountryCode(),
                                filterList.get(i).getCountryAlpha2(),
                                filterList.get(i).getCountryAlpha3(),
                                filterList.get(i).getCountryFlag());
                        filters.add(pojoCountryData);
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
            countryData = (ArrayList<JB_CountryData>) results.values;
            notifyDataSetChanged();
        }

    }

}
