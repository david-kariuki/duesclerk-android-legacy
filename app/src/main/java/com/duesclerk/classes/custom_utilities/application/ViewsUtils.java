package com.duesclerk.classes.custom_utilities.application;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.duesclerk.R;
import com.duesclerk.classes.custom_utilities.user_data.DataUtils;
import com.duesclerk.classes.custom_views.custom_glide.GlideApp;
import com.duesclerk.classes.custom_views.dialog_fragments.dialogs.DialogFragment_DatePicker;
import com.duesclerk.classes.custom_views.swipe_refresh.MultiSwipeRefreshLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Objects;


/**
 * This class contains functions that will be shared by views ad input fields in the project.
 * This functions perform:
 * Password toggle
 * Hide and show keyboard
 * Load urls to ImageViews
 * Expand and collapse ExpandableLayout
 * Switch TabLayout positions
 * Start/Stop SwipeRefreshLayout
 * Initialize ProgressDialog
 * Show ProgressDialog
 * Dismiss ProgressDialog
 * Initialize SearchView
 */
public class ViewsUtils {

    /**
     * Function to toggle password visibility
     *
     * @param editText  - to toggle visibility
     * @param imageView - to change image resource
     *                  Toggle password visibility
     *                  Changes the visibility icon colour
     */
    public static void togglePasswordField(@NonNull final EditText editText,
                                           @NonNull final ImageView imageView) {

        if (editText.getTransformationMethod() == null) {

            editText.setTransformationMethod(new PasswordTransformationMethod());
            editText.setSelection(Objects.requireNonNull(editText.getText()).length());

            // Set image resource
            imageView.setImageResource(R.drawable.ic_baseline_visibility_24_primary_grey);

        } else {

            editText.setTransformationMethod(null);
            editText.setSelection(Objects.requireNonNull(editText.getText()).length());

            // Set image resource
            imageView.setImageResource(R.drawable.ic_baseline_visibility_off_24_primary_dark);
        }
    }

    /**
     * Function to hide keyboard
     *
     * @param activity - to get system service
     */
    public static void hideKeyboard(final Activity activity) {

        View view = activity.findViewById(android.R.id.content);

        if (view != null) {

            InputMethodManager imm = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Function to show keyboard
     *
     * @param activity - to get context and content view
     */
    @SuppressWarnings("unused")
    public static void showKeyboard(final Activity activity) {

        View contentView = activity.findViewById(android.R.id.content);

        if (contentView != null) {

            InputMethodManager inputMethodManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    /**
     * Function to show keyboard
     *
     * @param activity  - to get context and content view
     * @param focusView . to enable and request focus
     */
    public static void showKeyboardWithFocus(final Activity activity, final View focusView) {

        View contentView = activity.findViewById(android.R.id.content);

        if (contentView != null && focusView != null) {

            InputMethodManager inputMethodManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            focusView.setFocusable(true);
            focusView.requestFocus();
        }
    }

    /**
     * Function to load drawables to ImageView
     *
     * @param context    - to initialize GlideApp
     * @param drawableId - drawableId of drawable to be loaded
     * @param imageView  - associated ImageView
     */
    public static void loadImageView(final Context context, final int drawableId,
                                     final ImageView imageView) {

        if (drawableId != 0) {

            // Load Profile Picture
            GlideApp.with(context)
                    .load(drawableId)
                    .centerCrop()
                    // .override(450, Target.SIZE_ORIGINAL)
                    .error(R.drawable.img_placeholder_user_grey)
                    .placeholder(R.drawable.img_placeholder_user_grey)
                    .encodeQuality(100)
                    .skipMemoryCache(false)
                    .onlyRetrieveFromCache(false)
                    .priority(Priority.HIGH)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target,
                                                    boolean isFirstResource) {

                            return false; // return false for the error placeholder to be set
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {

                            return false;
                        }
                    }).into(imageView);
        } else {

            imageView.setImageResource(R.drawable.img_placeholder_user_grey);
        }
    }

    /**
     * Function to scroll up ScrollView
     *
     * @param scrollView - associated ScrollView
     */
    public static void scrollUpScrollView(final ScrollView scrollView) {

        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        // Scroll up ScrollView
                        scrollView.fullScroll(View.FOCUS_UP);
                        scrollView.smoothScrollTo(0, 0);
                    }
                });
    }

    /**
     * Function to how DialogFragment
     *
     * @param fragmentManager - to manage fragment
     * @param dialogFragment  - associated DialogFragment
     * @param show            - boolean(show-true/false) - to show or hide
     *                        DialogFragment
     */
    public static void showDialogFragment(final FragmentManager fragmentManager,
                                          final DialogFragment dialogFragment,
                                          final boolean show) {
        // Check if set to show
        if (show) {

            try {

                if (!dialogFragment.isAdded() && !dialogFragment.isRemoving()) {

                    // Show BottomSheetDialogFragment
                    dialogFragment.show(fragmentManager, dialogFragment.getTag());
                }
            } catch (Exception ignored) {
            }
        } else {

            if (dialogFragment.isAdded()) {

                dialogFragment.dismiss(); // Dismiss BottomSheet
            }
        }
    }

    /**
     * Function to show BottomSheetDialogFragment
     *
     * @param fragmentManager           - to manage fragment
     * @param bottomSheetDialogFragment - associated BottomSheetDialogFragment
     * @param show                      - boolean(show-true/false) - to show or hide
     *                                  BottomSheetDialogFragment
     */
    public static void showBottomSheetDialogFragment(final FragmentManager fragmentManager,
                                                     final BottomSheetDialogFragment
                                                             bottomSheetDialogFragment,
                                                     final boolean show) {

        // Check if set to show
        if (show) {

            try {

                if (!bottomSheetDialogFragment.isAdded()
                        && !bottomSheetDialogFragment.isRemoving()) {

                    bottomSheetDialogFragment.show(fragmentManager,
                            bottomSheetDialogFragment.getTag()); // Show BottomSheetDialogFragment
                }
            } catch (Exception ignored) {
            }
        } else {

            if (bottomSheetDialogFragment.isAdded()) {

                bottomSheetDialogFragment.dismiss(); // Dismiss BottomSheet
            }
        }
    }

    /**
     * Function to show DialogFragment_DatePicker
     *
     * @param fragmentManager          - to show DialogFragment_DatePicker
     * @param dialogFragmentDatePicker - associated DialogFragment_DatePicker
     * @param show                     - boolean(show-true/false) - to show or hide DatePicker
     */
    public static void showDatePickerFragment(final FragmentManager fragmentManager,
                                              final DialogFragment_DatePicker
                                                      dialogFragmentDatePicker,
                                              final boolean show) {
        if (show) {

            try {

                if (!dialogFragmentDatePicker.isAdded()
                        && !dialogFragmentDatePicker.isRemoving()) {

                    // Show BottomSheetDialogFragment
                    dialogFragmentDatePicker.show(fragmentManager, dialogFragmentDatePicker.getTag());
                }
            } catch (Exception ignored) {
            }
        } else {

            if (dialogFragmentDatePicker.isAdded()) {

                dialogFragmentDatePicker.dismiss(); // Dismiss BottomSheet
            }
        }
    }

    /**
     * Function to show and start ShimmerFrameLayout and hide activities layout
     *
     * @param show               - boolean(show-true/false) - to show or hide ShimmerFrameLayout
     * @param shimmerFrameLayout - associated ShimmerFrameLayout
     */
    public static void showShimmerFrameLayout(final boolean show, final ShimmerFrameLayout
            shimmerFrameLayout) {

        if (show) {

            // Check if Shimmer is started, show layout and start animation
            shimmerFrameLayout.setVisibility(View.VISIBLE);

            // Check if shimmer frame layout is started
            if (shimmerFrameLayout.isShimmerStarted()) {

                shimmerFrameLayout.stopShimmer(); // Stop shimmer
            }

            shimmerFrameLayout.startShimmer(); // Start shimmer

        } else {

            // Check if Shimmer is started, stop animation and hide layout
            shimmerFrameLayout.setVisibility(View.GONE); // Hide shimmer frame layout

            // Check if shimmer frame layout is started
            if (shimmerFrameLayout.isShimmerStarted()) {

                shimmerFrameLayout.stopShimmer();
            }
        }
    }

    /**
     * Function to expand and collapse ExpandableLayout
     *
     * @param expand           - boolean expand (TRUE/FALSE)
     * @param expandableLayout - associated view
     */
    public static void expandExpandableLayout(final boolean expand,
                                              final ExpandableLayout expandableLayout) {

        if (expand) {

            // Check if layout is collapsed
            if (!expandableLayout.isExpanded()) {

                expandableLayout.expand(); // Expand ExpandableLayout
            }
        } else {

            // Check if layout is expanded
            if (expandableLayout.isExpanded()) {

                expandableLayout.collapse(); // Collapse ExpandableLayout
            }
        }
    }

    /**
     * Function to select TabLayout position
     *
     * @param position  - tab position
     * @param tabLayout - associated view
     */
    public static void selectTabPosition(final int position, final TabLayout tabLayout) {

        Objects.requireNonNull(tabLayout.getTabAt(position)).select();
    }

    /**
     * Function to start SwipeRefreshLayout
     *
     * @param refresh              - Refresh state
     * @param swipeRefreshListener - Associated SwipeRefreshLayout
     */
    public static void showSwipeRefreshLayout(final boolean refresh, final boolean showAnimation,
                                              final MultiSwipeRefreshLayout swipeRefreshLayout,
                                              final SwipeRefreshLayout.OnRefreshListener
                                                      swipeRefreshListener) {

        // Set view offset
//        swipeRefreshLayout.setProgressViewOffset(false,
//                DataUtils.getIntegerResource(mContext, R.integer.int_swipe_refresh_offset_start),
//                DataUtils.getIntegerResource(mContext, R.integer.int_swipe_refresh_offset_end));
        try {

            // Set color scheme
            swipeRefreshLayout.setColorSchemeColors(
                    DataUtils.getSwipeRefreshColorSchemeResources());

            if (refresh) {

                // Check if layout is already refreshing
                if (swipeRefreshLayout.isRefreshing()) {

                    swipeRefreshLayout.setRefreshing(false); // Stop refreshing
                }

                swipeRefreshListener.onRefresh(); // Call onRefresh listener

                // Check if to show animation
                if (showAnimation) {

                    swipeRefreshLayout.setRefreshing(true); // Start refreshing
                }
            } else {

                // Stop SwipeRefreshLayout
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Function to initialize ProgressDialog
     *
     * @param activity   - Owner activity
     * @param cancelable - ProgressDialog cancelable value
     *
     * @return ProgressDialog
     */
    public static ProgressDialog initProgressDialog(final Activity activity,
                                                    final boolean cancelable) {

        // Set ProgressDialog style
        ProgressDialog progressDialog = new ProgressDialog(activity, R.style.Style_ProgressDialog);
        progressDialog.setCancelable(cancelable); // Set cancelable

        return progressDialog; // Return ProgressDialog
    }


    /**
     * Function to show progress dialog
     *
     * @param progressDialog - ProgressDialog to be displayed
     * @param title          - ProgressBar title
     * @param message        - ProgressBar message
     */
    public static void showProgressDialog(final ProgressDialog progressDialog, final String title,
                                          final String message) {

        // Check if progress dialog is showing
        if (!progressDialog.isShowing()) {

            progressDialog.setTitle(title); // Set ProgressDialog title
            progressDialog.setMessage(message); // Set ProgressDialog message
            progressDialog.show(); // Show progress dialog
        }
    }

    /**
     * Function to hide progress dialog
     *
     * @param progressDialog - associated ProgressDialog
     */
    public static void dismissProgressDialog(final ProgressDialog progressDialog) {

        // Check if ProgressDialog is showing
        if (progressDialog.isShowing()) {

            progressDialog.dismiss(); // Dismiss ProgressDialog
        }
    }

    /**
     * Function to initialize SearchView for Activities
     *
     * @param activity     - Activity to get SearchView id and context
     * @param searchViewId - Associated SearchView id
     */
    public static SearchView initSearchView(final Activity activity, final int searchViewId) {

        // Create SearchView
        SearchView searchView = activity.findViewById(searchViewId);

        // Return setup SearchView
        return setupSearchView(searchView, activity.getApplicationContext());
    }

    /**
     * Function to initialize SearchView for fragments
     *
     * @param context      - Context to get resources
     * @param view         - View to get SearchView id
     * @param searchViewId - Associated SearchView id
     */
    public static SearchView initSearchView(final Context context, final View view,
                                            final int searchViewId) {

        // Create SearchView
        SearchView searchView = view.findViewById(searchViewId);

        // Return setup SearchView
        return setupSearchView(searchView, context);
    }

    /**
     * Function to set up SearchView
     *
     * @param searchView - Associated SearchView
     * @param context    - Context to get resources
     */
    private static SearchView setupSearchView(final SearchView searchView, final Context context) {

        // Get SearchView text
        TextView textView = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        // Set SearchView text and hint color
        textView.setTextColor(DataUtils.getColorResource(context,
                R.color.colorBlack));
        textView.setHintTextColor(DataUtils.getColorResource(context,
                R.color.colorBlack));

        // Search plate
        View searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchPlate.setBackgroundColor(DataUtils.getColorResource(context,
                R.color.colorWhite));

        // Search edit frame
        View searchEditFrame = searchView.findViewById(androidx.appcompat.R.id.search_edit_frame);
        searchEditFrame.setBackgroundColor(DataUtils.getColorResource(context,
                R.color.colorWhite));

        searchView.setIconifiedByDefault(false); // Disable iconified
        searchView.clearFocus(); // Clear SearchView focus

        return searchView;
    }
}
