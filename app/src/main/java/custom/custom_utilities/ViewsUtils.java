package custom.custom_utilities;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Objects;

import custom.custom_views.custom_glide.GlideApp;
import custom.custom_views.dialog_fragments.dialogs.DatePickerFragment;
import custom.custom_views.swipe_refresh.MultiSwipeRefreshLayout;


/**
 * This class contains functions that will be shared by views ad input fields in the project.
 * This functions perform:
 * Password toggle
 * Hide and show keyboard
 * Dismiss ProgressBar
 * Load urls to ImageViews
 * Expand and collapse ExpandableLayout
 * Switch TabLayout positions
 * Start/Stop SwipeRefreshLayout
 */
public class ViewsUtils {

    /**
     * Function to toggle password visibility
     *
     * @param editText  - to toggle visibility
     * @param imageView - to change image resource
     *                  Shows password if hidden and vice versa
     *                  Changes the visibility icon colour
     */
    public static void togglePasswordField(@NonNull EditText editText,
                                           @NonNull ImageView imageView) {
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
    public static void hideKeyboard(Activity activity) {
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
    public static void showKeyboard(Activity activity) {
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
    public static void showKeyboardWithFocus(Activity activity, View focusView) {
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
     * Function to hide progress dialog
     *
     * @param progressDialog - associated ProgressDialog
     */
    public static void dismissProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

    /**
     * Function to load picture urls to ImageView
     *
     * @param context   - to initialize GlideApp
     * @param strUrl    - String url to be loaded
     * @param imageView - associated ImageView
     */
    public static void loadUrlToImageView(Context context, String strUrl, ImageView imageView) {
        if (!strUrl.equals("")) {
            // Load Profile Picture
            GlideApp.with(context)
                    .load(strUrl)
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
     * Function to load drawables to ImageView
     *
     * @param context    - to initialize GlideApp
     * @param drawableId - drawableId of drawable to be loaded
     * @param imageView  - associated ImageView
     */
    public static void loadImageView(Context context, int drawableId, ImageView imageView) {
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
     * Function to load URI to ImageView
     *
     * @param context   - to initialize GlideApp
     * @param uri       - URI to be loaded
     * @param imageView - associated ImageView
     */
    public static void loadImageView(Context context, String uri, ImageView imageView) {
        if (uri != null) {
            // Load Profile Picture
            GlideApp.with(context)
                    .load(uri)
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
    public static void scrollUpScrollView(ScrollView scrollView) {
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
     * Function to show BottomSheetDialogFragment
     *
     * @param fragmentManager           - to show BottomSheetDialogFragment
     * @param bottomSheetDialogFragment - associated BottomSheetDialogFragment
     * @param show                      - boolean(show-true/false) - to show or hide
     *                                  BottomSheetDialogFragment
     */
    public static void showBottomSheetDialogFragment(
            FragmentManager fragmentManager, BottomSheetDialogFragment bottomSheetDialogFragment,
            final boolean show) {
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
     * Function to show DatePickerFragment
     *
     * @param fragmentManager    - to show DatePickerFragment
     * @param datePickerFragment - associated DatePickerFragment
     * @param show               - boolean(show-true/false) - to show or hide DatePickerFragment
     */
    public static void showDatePickerFragment(FragmentManager fragmentManager,
                                              DatePickerFragment datePickerFragment, boolean show) {
        if (show) {
            try {
                if (!datePickerFragment.isAdded() && !datePickerFragment.isRemoving()) {
                    // Show BottomSheetDialogFragment
                    datePickerFragment.show(fragmentManager, datePickerFragment.getTag());
                }
            } catch (Exception ignored) {
            }
        } else {
            if (datePickerFragment.isAdded()) {
                datePickerFragment.dismiss(); // Dismiss BottomSheet
            }
        }
    }

    /**
     * Function to show and start ShimmerFrameLayout and hide activities layout
     *
     * @param show               - boolean(show-true/false) - to show or hide ShimmerFrameLayout
     * @param shimmerFrameLayout - associated ShimmerFrameLayout
     */
    public static void showShimmerFrameLayout(boolean show, ShimmerFrameLayout shimmerFrameLayout) {
        if (show) {
            // Check if Shimmer is started, show layout and start animation
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            if (!shimmerFrameLayout.isShimmerStarted()) {
                shimmerFrameLayout.startShimmer();
            }
        } else {
            // Check if Shimmer is started, stop animation and hide layout
            shimmerFrameLayout.setVisibility(View.GONE);
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
    public static void expandExpandableLayout(boolean expand, ExpandableLayout expandableLayout) {
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
    public static void selectTabPosition(int position, TabLayout tabLayout) {
        Objects.requireNonNull(tabLayout.getTabAt(position)).select();
    }

    /**
     * Function to start SwipeRefreshLayout
     *
     * @param refresh              - Refresh state
     * @param swipeRefreshListener - Associated SwipeRefreshLayout
     */
    public static void startSwipeRefreshLayout(boolean refresh,
                                               MultiSwipeRefreshLayout swipeRefreshLayout,
                                               SwipeRefreshLayout.OnRefreshListener swipeRefreshListener) {
        // Set color scheme
        swipeRefreshLayout.setColorSchemeColors(DataUtils.getSwipeRefreshColorSchemeResources());

        if (refresh) {
            // Check if layout is already refreshing
            if (!swipeRefreshLayout.isRefreshing()) {
                // Start SwipeRefreshLayout
                swipeRefreshListener.onRefresh();
                swipeRefreshLayout.setRefreshing(true);
            }
        } else {
            // Stop SwipeRefreshLayout
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}