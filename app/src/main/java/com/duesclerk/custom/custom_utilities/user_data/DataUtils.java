package com.duesclerk.custom.custom_utilities.user_data;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.duesclerk.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class DataUtils {
    /*
     * This class contains functions/methods for data manipulation(For one single point of change)
     *
     * It contains functions to:
     * Check if String is empty.
     * Check if EditText is empty.
     * Check if JSONArray is empty.
     * Check if JSONObject is empty.
     * Check if ArrayList is empty.
     * Convert dp(int/float) to pixels.
     * Get string resources with/without placeholders.
     * Get Color resources.
     * Get integer resources.
     * Get animation.
     * Get image drawable by file name.
     * Get data from drawable in byte array.
     * Get SwipeRefreshLayout scheme colors.
     * Filter network url.
     * */


    /**
     * Function to check if any string is empty
     *
     * @param string - associated string
     */
    public static boolean isEmptyString(@NonNull String string) {

        return (string.equals(""));
    }

    /**
     * Function to check if EditText is empty
     *
     * @param editText - associated EditText
     */
    public static boolean isEmptyEditText(@NonNull EditText editText) {

        return (editText.getText().toString().length() == 0);
    }

    /**
     * Function to check if JSONArray is empty
     *
     * @param jsonArray - associated JSONArray
     */
    public static boolean isEmptyJSONArray(JSONArray jsonArray) {

        return ((jsonArray == null));
    }

    /**
     * Function to check if JSONObject is empty
     *
     * @param jsonObject - associated JSONObject
     */
    public static boolean isEmptyJSONObject(JSONObject jsonObject) {

        return (jsonObject == null);
    }

    /**
     * Function to check if any string array is empty
     *
     * @param stringArray - associated String array
     */
    public static boolean isEmptyStringArray(@NonNull String[] stringArray) {

        return (stringArray.length == 0);
    }

    /**
     * Function to check if any ArrayList is empty
     *
     * @param arrayList - associated ArrayList
     */
    public static boolean isEmptyArrayList(@SuppressWarnings("rawtypes") ArrayList arrayList) {

        return (arrayList == null || arrayList.isEmpty() || arrayList.get(0) == null);
    }

    /**
     * Function to clear string array
     *
     * @param array - String array
     */
    public static void clearStringArray(String[] array) {

        // Catch NullPointerException
        try {

            // Check if String array is empty/null
            if (!isEmptyStringArray(array)) {

                Arrays.fill(array, null); // Fill all elements with null
            }
        } catch (NullPointerException ignored) {
        }

    }

    /**
     * Function to clear ArrayList<String>
     *
     * @param array - ArrayList<String>
     */
    public static void clearArrayList(ArrayList<String> array) {

        // Catch NullPointerException
        try {

            // Check if ArrayList is empty/null
            if (!isEmptyArrayList(array)) {

                array.clear(); // Clear ArrayList
            }
        } catch (NullPointerException ignored) {
        }

    }

    /**
     * Function to convert integer dp to pixels
     *
     * @param context - for getting resources
     * @param dp      - dp to be converted to pixels
     */
    public static int convertDpToPixels(@NonNull Context context, int dp) {

        // Return pixels
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Function to convert double dp to pixels
     *
     * @param context - for getting resources
     * @param dp      - dp to be converted to pixels
     */
    public static int convertDpToPixels(@NonNull Context context, double dp) {

        // Return pixels
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Function to get string resource without placeholder
     *
     * @param context  - for getting resources
     * @param stringId - string resource id
     */
    public static String getStringResource(@NonNull Context context, int stringId) {

        // Return string resource
        return context.getResources().getString(stringId);
    }

    /**
     * Function to get string resource with text placeholder
     *
     * @param context     - for getting resources
     * @param stringId    - string resource id
     * @param placeholder - placeholder for string resource
     */
    public static String getStringResource(@NonNull Context context, int stringId,
                                           @NonNull String placeholder) {

        // Return string resource
        return context.getResources().getString(stringId, placeholder);
    }

    /**
     * Function to get string resource with multiple placeholders
     *
     * @param context  - for getting resources
     * @param stringId - string resource id
     * @param args     - placeholders strings
     */
    public static String getStringResource(@NonNull Context context, int stringId,
                                           @NonNull Object... args) {

        // Return string with placeholders
        return context.getResources().getString(stringId, args);
    }

    /**
     * Function to get string resource with multiple integer placeholders
     *
     * @param context  - for getting resources
     * @param stringId - string resource id
     * @param args     - placeholders ids
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getStringResource(@NonNull Context context, int stringId,
                                           @NonNull Integer... args) {

        // Create new Object array with same size as args length
        Object[] newArgs = new Object[args.length];

        // Loop through passed object with string id
        for (int i = 0; i < args.length; i++) {

            // Get string from string id adding them to new Object array
            newArgs[i] = context.getResources().getString(args[i]);
        }

        // Return string with placeholders
        return context.getResources().getString(stringId, Arrays.stream((newArgs)).toArray());
    }

    /**
     * Function to get string resource with multiple string placeholders
     *
     * @param context  - for getting resources
     * @param stringId - string resource id
     * @param args     - placeholders ids
     */
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public static String getStringResource(@NonNull Context context, int stringId,
                                           @NonNull String... args) {

        // Create new Object array with same size as args length
        Object[] newArgs = new Object[args.length];

        // Loop through passed object with string id
        for (int i = 0; i < args.length; i++) {

            // Get string from string id adding them to new Object array
            if (args[i].getClass().equals(Integer.class)) {

                // Set arguments
                newArgs[i] = context.getResources().getString(Integer.parseInt(args[i]));

            } else {

                newArgs[i] = args[i]; // Set arguments
            }
        }

        // Return string with placeholders
        return context.getResources().getString(stringId, newArgs);
    }

    /**
     * Function to get color resource
     *
     * @param context - for getting resources
     * @param colorId - color resource id
     */
    public static int getColorResource(@NonNull Context context, int colorId) {

        // Check for SDK version
        if (Build.VERSION.SDK_INT <= 23) {

            //noinspection deprecation
            return context.getResources().getColor(colorId); // Return color

        } else {

            return ContextCompat.getColor(context, colorId); // Return color
        }
    }

    /**
     * Function to get integer resources
     *
     * @param context   - for getting resources
     * @param integerId - integer resource id
     */
    public static int getIntegerResource(@NonNull Context context, int integerId) {

        return context.getResources().getInteger(integerId);
    }

    /**
     * Function to get dimension resources
     *
     * @param context     - for getting resources
     * @param dimensionId - dimension resource id
     */
    public static float getDimenResource(@NonNull Context context, int dimensionId) {

        return context.getResources().getDimension(dimensionId);
    }

    /**
     * Function to get anim
     *
     * @param context - for getting animations
     * @param animId  - animId
     */
    public static Animation getAnimation(@NonNull Context context, int animId) {

        return AnimationUtils.loadAnimation(context, animId);
    }

    /**
     * Function to get drawable id from drawable name or concatenating drawable name to get
     * another drawable
     *
     * @param context      - for getting resources and package name
     * @param drawableName - drawable id
     */
    public static int getDrawableFromName(@NonNull Context context, @NonNull String drawableName) {

        // Return drawable id
        return context.getResources().getIdentifier(
                (drawableName),
                "drawable",
                context.getPackageName());
    }

    /**
     * Function to get SwipeRefreshLayout colors
     */
    public static int[] getSwipeRefreshColorSchemeResources() {

        int[] colorSchemeResources = {
                R.color.colorBlue900,
                R.color.colorBlue700,
                R.color.colorBlue500,
                R.color.colorBlue200,

                // Reverse colors
                R.color.colorBlue500,
                R.color.colorBlue700
        };

        return colorSchemeResources.clone(); // Clone and return ColorScheme array
    }

    /**
     * Function to set count unit in thousands and millions
     *
     * @param number - number to set unit
     */
    public static String getNumberUnit(int number) {

        String labelledString;

        // 1000 to less than 100,000
        if ((number >= 10000) && (number < 100000)) {

            String string = roundDouble((double) number / 1000, 1) + "K";
            labelledString = string.replace(".0", "");
            return labelledString;

        } else if ((number >= 100000) && (number < 1000000)) {
            // 100,000 to less than 1,000,000

            String string = roundDouble((double) number / 1000, 0) + "K";
            labelledString = string.replace(".0", "");
            return labelledString;

        } else if ((number >= 1000000) && (number < 100000000)) {
            // 1000,000 to less than 100,000,000

            String string = roundDouble((double) number / 1000000, 1) + "M";
            labelledString = string.replace(".0", "");
            return labelledString;

        } else if ((number >= 10000000) && (number < 1000000000)) {
            // 10,000,000 to less than 100,000,000

            String string = roundDouble((double) number / 1000000, 0) + "M";
            labelledString = string.replace(".0", "");
            return labelledString;
        }

        return String.valueOf(number); // Return number unit
    }

    /**
     * Function to round double to 2 decimal places
     *
     * @param value - double value
     */
    public static double roundDouble(double value, int decimalPoints) {

        // Get BigDecimal
        BigDecimal bigDecimal = new BigDecimal(Double.toString(value));
        bigDecimal = bigDecimal.setScale(decimalPoints, RoundingMode.DOWN);

        return bigDecimal.doubleValue(); // Return rounded double value
    }
}
