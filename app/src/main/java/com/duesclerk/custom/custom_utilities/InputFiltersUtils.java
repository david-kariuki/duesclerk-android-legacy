package com.duesclerk.custom.custom_utilities;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Patterns;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.duesclerk.R;
import com.duesclerk.custom.custom_views.toast.CustomToast;

import java.util.Objects;

/**
 * This class filters inputs on different fields. It contains input filters for
 * persons names (first and last),
 * business names,
 * phone numbers,
 * email addresses,
 * country,
 * city,
 * password,
 * gender and
 * verification codes
 */
public class InputFiltersUtils {

    // Input Field Lengths
    public static final int minSingleNameLength = 1;
    public static final int minUsernameLength = 2;
    public static final int minPasswordLength = 8;
    public static final int minVerificationCodeLength = 6;
    public static final int maxSingleNameLength = 50;
    public static final int maxPhoneNumberLength = 15;
    public static final int maxPhoneNumberLengthNoCode = 12;
    public static final int maxEmailLength = 320;
    private static final int minPhoneNumberLength = 7;


    // Filter first and last names
    public static InputFilter filterNames = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; i++) {
            if (!Character.isLetter(source.charAt(i))) {
                return "";
            } else {
                if (source.length() > 1) {
                    char last = source.charAt(source.length() - 1);
                    if (Character.isUpperCase(last)) {
                        char lastLower = Character.toLowerCase(last);
                        String currentValue, trimmed;
                        currentValue = source.toString();
                        // Remove last character
                        trimmed = currentValue.substring(0, source.length() - 1);
                        trimmed = trimmed + lastLower;
                        StringBuilder sourceBuilder = new StringBuilder();
                        for (int loop = 0; loop < trimmed.length(); loop++) {
                            char c = trimmed.charAt(loop);
                            sourceBuilder.append(c); // Set char to textView
                        }
                        source = sourceBuilder.toString();
                        return source;
                    }
                }
            }
        }
        return null;
    };

    // Filter email addresses
    public static InputFilter filterEmailAddress = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; i++) {
            if (Character.isWhitespace(source.charAt(i))) return "";
        }
        return null;
    };

    // Filter phone numbers
    public static InputFilter filterPhoneNumber = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; i++) {
            if (!Character.isDigit(source.charAt(i))) return "";
        }
        return null;
    };

    // Filter email addresses
    public static InputFilter filterGender = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; i++) {
            if (!Character.isLetter(source.charAt(i))) {
                if (!Character.isWhitespace(source.charAt(i))) {
                    if (!String.valueOf(source.charAt(i)).equals("-")) {
                        return "";
                    }
                }
            }
        }
        return null;
    };

    // Filter verification codes
    public static InputFilter filterVerificationCodes = (source, start, end, dest, dstart, dend) ->
    {
        for (int i = start; i < end; i++) {
            if (Character.isLetterOrDigit(source.charAt(i))) return "";
        }
        return null;
    };

    /**
     * This function prevents the username from starting with a period
     * If a period is detected as the first character, it will be deleted
     *
     * @param context  - for Toast
     * @param editable - Changing text
     * @param editText - associated input field
     */
    public static void blockLeadingPeriod(Context context, Editable editable, EditText editText) {
        if (editable.length() > 0) {
            if (String.valueOf(editable.charAt(0)).equals(".")) {
                String currentValue, trimmed;
                if (editable.length() > 0) {
                    currentValue = editable.toString();
                    trimmed = currentValue.substring(1); // Remove leading period
                    editText.setText(null);
                    for (int i = 0; i < trimmed.length(); i++) {
                        char c = trimmed.charAt(i);
                        editText.append(String.valueOf(c)); // Set char to textView
                    }
                } else {
                    editText.setText(null);
                }
                CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                        R.string.error_cannot_start_with_a_period,
                        R.string.hint_usernames), 0);
            }
        }
    }

    /**
     * This function prevents repetition of periods after the first position
     * It checks for the length of username if greater than 2
     * It gets the last and second last characters and checks if both are periods
     * It will delete the last period if the second last character was also a period
     *
     * @param context  - for Toast
     * @param editable - Changing text
     * @param editText - associated input field
     */
    public static void blockRecurringPeriods(Context context, Editable editable,
                                             EditText editText) {
        if (editable.length() > 0) {

            if (editable.length() > 2) {
                String currentValue, trimmed;
                char last, secondLast;
                secondLast = editable.charAt(editable.length() - 2);
                last = editable.charAt(editable.length() - 1);
                if (String.valueOf(secondLast).equals(".")) {
                    if (String.valueOf(last).equals(".")) {
                        currentValue = editable.toString();
                        trimmed = currentValue.substring(0, editable.length() - 1); // Remove
                        // last period
                        editText.setText(null);
                        for (int i = 0; i < trimmed.length(); i++) {
                            char c = trimmed.charAt(i);
                            editText.append(String.valueOf(c)); // Set char to textView
                        }
                        CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                                R.string.error_periods_cannot_be_repeated_in,
                                DataUtils.getStringResource(context, R.string.hint_usernames).toLowerCase()),
                                0);
                    }
                }
            }
        }
    }

    public static boolean checkPersonNameLengthNotify(Context context, @NonNull EditText editText,
                                                      boolean isFirstName) {
        boolean valueAcceptable = false;

        if (DataUtils.isEmptyEditText(editText)) {
            if (isFirstName) {
                // Toast Error Message
                CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                        R.string.error_first_name_null), R.drawable.ic_baseline_person_24_white);
                editText.setError(DataUtils.getStringResource(context,
                        R.string.error_first_name_null)); // Enable Error Icon
            } else {
                // Toast Error Message
                CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                        R.string.error_last_name_null), R.drawable.ic_baseline_person_24_white);
                editText.setError(DataUtils.getStringResource(context,
                        R.string.error_last_name_null)); // Enable Error Icon
            }
        } else if (Objects.requireNonNull(editText.getText()).length()
                < InputFiltersUtils.minSingleNameLength) {
            if (isFirstName) {
                // Toast Error Message
                CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                        R.string.error_first_name_length,
                        String.valueOf(InputFiltersUtils.minSingleNameLength)),
                        R.drawable.ic_baseline_person_24_white);
                editText.setError(DataUtils.getStringResource(context,
                        R.string.error_first_name_length_short)); // Enable Error Icon
            } else {
                // Toast Error Message
                CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                        R.string.error_last_name_length,
                        String.valueOf(InputFiltersUtils.minSingleNameLength)),
                        R.drawable.ic_baseline_person_24_white);
                // Enable Error Icon
                editText.setError(DataUtils.getStringResource(context,
                        R.string.error_last_name_length_short));
            }
        } else {
            valueAcceptable = true;
        }
        return valueAcceptable;
    }

    /**
     * Function to check business name length
     *
     * @param context  - context used to show toast
     * @param editText - Character Sequence
     * @return boolean
     */
    public static boolean checkBusinessNameLengthNotify(Context context,
                                                        @NonNull EditText editText) {
        boolean valueAcceptable = false;
        if (Objects.requireNonNull(editText.getText()).length() == 0) {
            // Toast Error Message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_business_name_null),
                    R.drawable.ic_baseline_business_24_white);
            // Enable Error Icon
            editText.setError(DataUtils.getStringResource(context,
                    R.string.error_business_name_null));
        } else {
            valueAcceptable = true;
        }
        return valueAcceptable;
    }

    /**
     * Function to check email address length and format validity
     *
     * @param editText - Character Sequence
     * @return boolean
     */
    public static boolean checkEmailAddressValidNotify(@NonNull EditText editText) {
        return ((Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches())
                && (editText.getText().toString().length() <= maxEmailLength));
    }

    /**
     * Function to check email address length and format validity
     *
     * @param context  - context used to show toast
     * @param editText - Character Sequence
     * @return boolean
     */
    public static boolean checkEmailAddressValidNotify(@NonNull Context context,
                                                       @NonNull EditText editText) {
        boolean valueAcceptable = false;
        if ((Objects.requireNonNull(editText.getText()).length() > InputFiltersUtils.maxEmailLength)
                || (!Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches())) {
            // Toast Error Message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_email_address_invalid), R.drawable.ic_baseline_email_24_white);
            // Enable Error Icon
            editText.setError(DataUtils.getStringResource(context,
                    R.string.error_email_address_invalid_short));
        } else {
            valueAcceptable = true;
        }

        return valueAcceptable;
    }

    /**
     * Function to check phone number length and format validity
     *
     * @param editText - Character Sequence
     * @return boolean
     */
    public static boolean checkPhoneNumberValid(@NonNull EditText editText) {
        return ((Patterns.PHONE.matcher(editText.getText().toString()).matches())
                && (editText.getText().toString().length() >= minPhoneNumberLength));
    }

    /**
     * Function to check phone number length and format validity
     *
     * @param context  - context used to show toast
     * @param editText - Character Sequence
     * @return boolean
     */
    public static boolean checkPhoneNumberValidNotify(@NonNull Context context,
                                                      @NonNull EditText editText) {
        boolean valueAcceptable = false;
        if ((Objects.requireNonNull(editText.getText()).length()
                < InputFiltersUtils.minPhoneNumberLength)
                || (!Patterns.PHONE.matcher(editText.getText().toString()).matches())) {
            // Toast Error Message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_phone_number_invalid),
                    R.drawable.ic_baseline_phone_24_white);
            // Enable Error Icon
            editText.setError(DataUtils.getStringResource(context,
                    R.string.error_phone_number_invalid_short));
        } else {
            valueAcceptable = true;
        }
        return valueAcceptable;
    }

    /**
     * Function to check phone number length and format validity
     *
     * @param context  - context used to show toast
     * @param editText - Character Sequence
     * @return boolean
     */
    public static boolean checkPasswordLengthNotify(@NonNull Context context,
                                                    @NonNull EditText editText) {
        boolean valueAcceptable = false;
        if (Objects.requireNonNull(editText.getText()).length() == 0) {
            // Toast Error Message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_password_length,
                    String.valueOf(minPasswordLength)),
                    R.drawable.ic_baseline_alternate_email_24_white);
            // Enable Error Icon
            editText.setError(DataUtils.getStringResource(context,
                    R.string.error_password_length_short));
        } else {
            valueAcceptable = true;
        }
        return valueAcceptable;
    }

    /**
     * Function to check phone number length and format validity
     *
     * @param context             - context used to show toast
     * @param editCurrentPassword - Character Sequence
     * @return boolean
     */
    public static boolean comparePasswordChangeNotify(@NonNull Context context,
                                                      @NonNull EditText editCurrentPassword,
                                                      @NonNull EditText editNewPassword) {
        boolean valueAcceptable = false;
        if (editCurrentPassword.getText().toString().equals(
                editNewPassword.getText().toString())) {
            // Toast Error Message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_password_select_new),
                    R.drawable.ic_baseline_lock_24_white);
            // Enable Error Icon
            editNewPassword.setError(DataUtils.getStringResource(context,
                    R.string.error_password_select_new));
        } else {
            valueAcceptable = true;
        }
        return valueAcceptable;
    }

    public static boolean compareNewPasswords(@NonNull Context context,
                                              @NonNull EditText editNewPassword,
                                              @NonNull EditText editConfirmPassword) {
        boolean valueAcceptable = false;

        if (!editNewPassword.getText().toString().equals(
                editConfirmPassword.getText().toString())) {

            // Toast Error Message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_new_password_mismatch),
                    R.drawable.ic_baseline_lock_24_white);
            // Enable Error Icon
            editConfirmPassword.setError(DataUtils.getStringResource(context,
                    R.string.error_new_password_mismatch));
        } else {
            valueAcceptable = true;
        }
        return valueAcceptable;
    }

    /**
     * Function to check country length
     *
     * @param context  - context used to show toast
     * @param editText - Character Sequence
     * @return boolean
     */
    public static boolean checkCountryLengthNotify(@NonNull Context context,
                                                   @NonNull EditText editText) {
        boolean valueAcceptable = false;
        if (!(Objects.requireNonNull(editText.getText()).length() > 0)) {
            // Toast Error Message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_country_null), R.drawable.ic_baseline_location_on_24_white);
            editText.setError(null); // Enable Error Icon
        } else {
            valueAcceptable = true;
        }
        return valueAcceptable;
    }

    /**
     * Function to check city length
     *
     * @param context  - context used to show toast
     * @param editText - Character Sequence
     * @return boolean
     */
    public static boolean checkCityLengthNotify(@NonNull Context context,
                                                @NonNull EditText editText) {
        boolean valueAcceptable = false;
        if (!(Objects.requireNonNull(editText.getText()).length() > 0)) {
            // Toast Error Message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_city_null), R.drawable.ic_baseline_location_city_24_white);
            editText.requestFocus();
            editText.setError(null); // Enable Error Icon
        } else {
            valueAcceptable = true;
        }
        return valueAcceptable;
    }

    /**
     * Function to check gender length
     *
     * @param context  - context used to show toast
     * @param editText - Character Sequence
     * @return boolean
     */
    public static boolean checkGenderLengthNotify(@NonNull Context context,
                                                  @NonNull EditText editText) {
        boolean valueAcceptable = false;
        if (!(Objects.requireNonNull(editText.getText()).length() > 0)) {
            // Toast Error Message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_gender_null),
                    R.drawable.ic_gender_neutral_100px_white);

            editText.setError(null); // Enable Error Icon
        } else {
            valueAcceptable = true;
        }
        return valueAcceptable;
    }

    /**
     * Function to check gender length
     *
     * @param context - context used to show toast
     * @param gender  - Character Sequence
     * @return boolean
     */
    public static boolean checkGenderLengthNotify(@NonNull Context context,
                                                  @NonNull String gender) {
        boolean valueAcceptable = false;
        if (!(gender.length() > 0)) {
            // Toast Error Message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_gender_null),
                    R.drawable.ic_gender_neutral_100px_white);
        } else {
            valueAcceptable = true;
        }
        return valueAcceptable;
    }

    /**
     * Function to check date of birth length
     *
     * @param context  - context used to show toast
     * @param editText - Character Sequence
     * @return boolean
     */
    public static boolean checkDateOfBirthLengthNotify(@NonNull Context context,
                                                       @NonNull EditText editText) {
        boolean valueAcceptable = false;
        if (!(Objects.requireNonNull(editText.getText()).length() > 0)) {
            // Toast Error Message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_date_of_birth_null),
                    R.drawable.ic_baseline_calendar_today_24_white);
            editText.setError(null); // Enable Error Icon
        } else {
            valueAcceptable = true;
        }
        return valueAcceptable;
    }
}
