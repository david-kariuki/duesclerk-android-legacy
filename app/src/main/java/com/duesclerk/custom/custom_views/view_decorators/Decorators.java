package com.duesclerk.custom.custom_views.view_decorators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.duesclerk.R;
import com.duesclerk.custom.custom_views.dialog_fragments.bottom_sheets.BottomSheetFragment_CountryPicker;
import com.duesclerk.ui.fragment_contacts.fragment_people_i_owe.FragmentPeople_I_Owe;
import com.duesclerk.ui.fragment_contacts.fragment_peopleowingme.FragmentPeopleOwingMe;

import org.jetbrains.annotations.NotNull;

public class Decorators extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;

    /**
     * Class constructor for country picker divider
     */
    public Decorators(BottomSheetFragment_CountryPicker countryPicker) {

        Context context = countryPicker.requireActivity();

        // Get Drawable
        mDivider = ContextCompat.getDrawable(context,
                R.drawable.gradient_divider_white_accent_white);
    }

    /**
     * Class constructor for fragment people owing me
     */
    public Decorators(FragmentPeopleOwingMe fragmentPeopleOwingMe) {

        // Get drawable
        mDivider = ContextCompat.getDrawable(fragmentPeopleOwingMe.requireContext(),
                R.drawable.gradient_divider_white_primary_grey_white);
    }

    /**
     * Class constructor for fragment people I Owe
     */
    public Decorators(FragmentPeople_I_Owe fragmentPeopleIOwe) {

        // Get drawable
        mDivider = ContextCompat.getDrawable(fragmentPeopleIOwe.requireContext(),
                R.drawable.gradient_divider_white_primary_grey_white);
    }

    @Override
    public void onDrawOver(@NotNull Canvas c, RecyclerView parent,
                           RecyclerView.@NotNull State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
