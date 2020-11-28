package com.duesclerk.custom.custom_utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.duesclerk.R;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Objects;

public class DataUtils {
    /*
     * This class contains functions/methods for data manipulation(For one single point of change)
     * It contains functions to:
     * Check if String is empty.
     * Check if EditText is empty.
     * Check if Uri is empty.
     * Check if ArrayList is empty.
     * Convert dp(int/float) to pixels
     * Get string resources with/without placeholders
     * Get Color resources
     * Get integer resources
     * Get animation
     * Get image drawable by file name
     * Get data from drawable in byte array
     * Get SwipeRefreshLayout scheme colors
     * Filter network url
     * Set count unit in thouands and millions
     * */


    /**
     * Function to check if any string is empty
     *
     * @param string - associated string
     */
    public static boolean isEmptyString(String string) {
        return string.equals("");
    }

    /**
     * Function to check if EditText is empty
     *
     * @param editText - associated EditText
     */
    public static boolean isEmptyEditText(EditText editText) {
        return (editText.getText().toString().length() == 0);
    }

    /**
     * Function to check if Uri is empty
     *
     * @param uri - associated Uri String
     */
    public static boolean isEmptyUri(Uri uri) {
        return (uri == null);
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
     * Function to convert integer dp to pixels
     *
     * @param context - for getting resources
     * @param dp      - dp to be converted to pixels
     */
    public static int convertDpToPixels(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Function to convert double dp to pixels
     *
     * @param context - for getting resources
     * @param dp      - dp to be converted to pixels
     */
    public static int convertDpToPixels(Context context, double dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Function to get string resource without placeholder
     *
     * @param context  - for getting resources
     * @param stringId - string resource id
     */
    public static String getStringResource(Context context, int stringId) {
        return context.getResources().getString(stringId);
    }

    /**
     * Function to get string resource with text placeholder
     *
     * @param context     - for getting resources
     * @param stringId    - string resource id
     * @param placeholder - placeholder for string resource
     */
    public static String getStringResource(Context context, int stringId, String placeholder) {
        return context.getResources().getString(stringId, placeholder);
    }

    /**
     * Function to get string resource with multiple placeholders
     *
     * @param context  - for getting resources
     * @param stringId - string resource id
     * @param args     - placeholders strings
     */
    public static String getStringResource(Context context, int stringId, Object... args) {
        /*Object[] newArgs = new Object[args.length];

        Object argValue;
        for (int i = 0; i < args.length; i++) {
            argValue = args[i];

            if (argValue.getClass() == Integer.class) {
                // Argument is of type integer
                newArgs[i] = getStringResource(context, (Integer) argValue);
                Log.e("Type", "Integer : " + newArgs[i]);

            }

            if (argValue.getClass() == String.class) {
                // Argument is of type string

                // Add value to new arguments string array
                newArgs[i] = (String) argValue;
                Log.e("Type", "String : " + newArgs[i]);
            }
        }*/

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
    public static String getStringResource(Context context, int stringId, Integer... args) {
        // Create new Object array with same size as args length
        Object[] newArgs = new Object[args.length];

        // Loop through passed object with string id
        for (int i = 0; i < args.length; i++) {
            // Get string from string id adding them to new Object array
            newArgs[i] = context.getResources().getString((Integer) args[i]);
        }

        // Return string with placeholders
        return context.getResources().getString(stringId, newArgs);
    }

    /**
     * Function to get string resource with multiple string placeholders
     *
     * @param context  - for getting resources
     * @param stringId - string resource id
     * @param args     - placeholders ids
     */
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public static String getStringResource(Context context, int stringId, String... args) {
        // Create new Object array with same size as args length
        Object[] newArgs = new Object[args.length];

        // Loop through passed object with string id
        for (int i = 0; i < args.length; i++) {
            // Get string from string id adding them to new Object array
            if (args[i].getClass().equals(Integer.class)) {
                newArgs[i] = context.getResources().getString(Integer.parseInt(args[i]));
            } else {
                newArgs[i] = args[i];

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
    public static int getColorResource(Context context, int colorId) {
        if (Build.VERSION.SDK_INT <= 23) {
            //noinspection deprecation
            return context.getResources().getColor(colorId);
        } else {
            return ContextCompat.getColor(context, colorId);
        }
    }

    /**
     * Function to get string resource
     *
     * @param context   - for getting resources
     * @param integerId - integer resource id
     */
    public static int getIntegerResource(Context context, int integerId) {
        return context.getResources().getInteger(integerId);
    }

    /**
     * Function to get anim
     *
     * @param context - for getting animations
     * @param animId  - animId
     */
    public static Animation getAnimation(Context context, int animId) {
        return AnimationUtils.loadAnimation(context, animId);
    }

    /**
     * Function to get drawable id from drawable name or concatenating drawable name to get
     * another drawable
     *
     * @param context       - for getting resources and package name
     * @param drawableName  - drawable id
     * @param extraNamePart - extra string to concatenate to drawable name
     */
    public static int getDrawableFromName(Context context, String drawableName,
                                          @Nullable String extraNamePart) {
        if (extraNamePart == null) extraNamePart = "";
        return context.getResources().getIdentifier(
                (drawableName + extraNamePart),
                "drawable",
                context.getPackageName());
    }

    /**
     * Function to get drawable id from drawable name or concatenating drawable name to get
     * another drawable
     *
     * @param context      - for getting resources and package name
     * @param drawableName - drawable id
     */
    public static int getDrawableFromName(Context context, String drawableName) {
        return context.getResources().getIdentifier(
                (drawableName),
                "drawable",
                context.getPackageName());
    }

    /**
     * Function to get data from drawable in byte array
     *
     * @param bitmap - Associated bitmap
     */
    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
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

        return colorSchemeResources.clone();

    }

    /**
     * Function to remove unwanted characters from url string
     *
     * @param string - url
     */
    public static String filterNetworkUrl(@NotNull String string) {
        StringBuilder stringNoSpaces = null;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != ' ') {
                //noinspection ConstantConditions
                stringNoSpaces.append(string.charAt(i));
            }
        }
        return Objects.requireNonNull(stringNoSpaces).toString();
    }

    /**
     * Function to set count unit in thouands and millions
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
            // 100,000 to less than 1,000,000
        } else if ((number >= 100000) && (number < 1000000)) {
            String string = roundDouble((double) number / 1000, 0) + "K";
            labelledString = string.replace(".0", "");
            return labelledString;
            // 1000,000 to less than 100,000,000
        } else if ((number >= 1000000) && (number < 100000000)) {
            String string = roundDouble((double) number / 1000000, 1) + "M";
            labelledString = string.replace(".0", "");
            return labelledString;
        } else if ((number >= 10000000) && (number < 1000000000)) {
            String string = roundDouble((double) number / 1000000, 0) + "M";
            labelledString = string.replace(".0", "");
            return labelledString;
        }
        return String.valueOf(number);
    }

    /**
     * Function to round double to 2 decimal places
     *
     * @param value - double value
     */
    public static double roundDouble(double value, int decimalPoints) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(decimalPoints, RoundingMode.DOWN);
        return bd.doubleValue();
    }
}
