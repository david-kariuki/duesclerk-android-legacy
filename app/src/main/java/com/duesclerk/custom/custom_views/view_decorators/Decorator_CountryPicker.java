package com.duesclerk.custom.custom_views.view_decorators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.duesclerk.R;

import org.jetbrains.annotations.NotNull;

public class Decorator_CountryPicker extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;

    public Decorator_CountryPicker(Context context) {

        // Get Drawable
        mDivider = ContextCompat.getDrawable(context, R.drawable.gradient_country_picker_divider);
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
