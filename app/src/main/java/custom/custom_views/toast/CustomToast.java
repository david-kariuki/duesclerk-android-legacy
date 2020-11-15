package custom.custom_views.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duesclerk.R;

import custom.custom_utilities.DataUtils;

@SuppressLint("StaticFieldLeak")
public class CustomToast {

    private static Toast toast;
    private static ImageView imageToastIcon;
    private static TextView textToastMessage;
    private static Animation animCustomToast;
    private static LinearLayout llRootView;
    private static View customView;
    private static boolean timeLong = false;

    /**
     * Function to show ordinary or informational message
     *
     * @param context    - context used to initialize Toast
     * @param message    - message to be shown
     * @param isTimeLong - boolean to set time to long or short
     * @param icon       - icon to match type of message or associated field
     */
    public static void infoMessage(Context context, String message, boolean isTimeLong, int icon) {
        // Set Toast Duration
        if (isTimeLong) {
            timeLong = true;
        }

        initToast(context); // Initialize Toast
        showToast(message, icon); // Show toast
    }

    @SuppressLint("InflateParams")
    private static void initToast(Context context) {
        // Initialize Layout Inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        // Initialize Toast object
        toast = new Toast(context);

        // Initialize CustomView
        customView = inflater.inflate(R.layout.custom_layout_toast, null);

        // Initialize Container Under CustomView
        llRootView = customView.findViewById(R.id.llCustomToast_Root);

        // Initialize Status Image Under CustomView
        imageToastIcon = customView.findViewById(R.id.imageCustomToast_ToastIcon);

        // Initialize Message Under CustomView
        textToastMessage = customView.findViewById(R.id.tvCustomToast_Message);

        // Initialize Animation
        animCustomToast = DataUtils.getAnimation(context, R.anim.anim_slide_up);

        // Set Animation Duration
        animCustomToast.setDuration(900);

        // Set Toast Duration
        if (timeLong) toast.setDuration((Toast.LENGTH_LONG));
        else toast.setDuration((Toast.LENGTH_SHORT));

        // Set Toast gravity
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 50);
    }

    /**
     * Function to show Toast
     *
     * @param message - message to be shown
     * @param icon    - icon to match type of message or associated field
     */
    private static void showToast(String message, int icon) {
        // Check if message is null
        if (message != null) {
            textToastMessage.setText(message); // Set Toast Message
        }

        setToastIcon(icon); // Set image resource
        llRootView.startAnimation(animCustomToast); // Animate (Slide) message
        imageToastIcon.startAnimation(getRotateAnimation()); // Animate (Shake) drawable
        toast.setView(customView); // Set toast custom view
        toast.show(); // Show Toast
    }

    /**
     * Function to set Toast icon
     *
     * @param icon - icon to match type of message or associated field
     */
    private static void setToastIcon(int icon) {
        if (icon == 0) {
            imageToastIcon.setVisibility(View.GONE); // Show Icon
        } else {
            imageToastIcon.setVisibility(View.VISIBLE); // Show Icon
            imageToastIcon.setImageResource(icon); // Set image resource
        }
    }

    /**
     * Function to create rotation animation
     */
    private static Animation getRotateAnimation() {
        Animation rotateAnimation = new RotateAnimation(-15, 15,
                50, 50, 0, 50);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.REVERSE);
        rotateAnimation.setDuration(500);
        return rotateAnimation;
    }

    /**
     * Function to show error message
     *
     * @param context      - context used to initialize Toast
     * @param errorMessage - message to be shown
     * @param icon         - icon to match type of message or associated field
     */
    public static void errorMessage(Context context, String errorMessage, int icon) {
        timeLong = true; // Set time to long
        initToast(context); // Initialize Toast
        showToast(errorMessage, icon); // Show toast
    }
}