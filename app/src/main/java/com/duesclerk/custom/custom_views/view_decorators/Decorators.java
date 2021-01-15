package com.duesclerk.custom.custom_views.view_decorators;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.duesclerk.R;
import com.duesclerk.custom.custom_views.dialog_fragments.bottom_sheets.BottomSheetFragment_CountryPicker;

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
     * Class constructor for activities
     */
    public Decorators(Activity activity) {

        // Get drawable
        mDivider = ContextCompat.getDrawable(activity,
                R.drawable.gradient_divider_white_primary_grey_white);
    }

    /**
     * Class constructor for fragments
     */
    public Decorators(Fragment fragment) {

        // Get drawable
        mDivider = ContextCompat.getDrawable(fragment.requireContext(),
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
