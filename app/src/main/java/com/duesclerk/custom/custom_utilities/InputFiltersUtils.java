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
    public static final int LENGTH_MIN_SINGLE_NAME = 1;
    public static final int LENGTH_MIN_PASSWORD = 8;
    public static final int LENGTH_MAX_SINGLE_NAME = 50;
    public static final int LENGTH_MAX_PHONE_NUMBER = 15;
    public static final int LENGTH_MAX_EMAIL_ADDRESS = 320;
    public static final int LENGTH_VERIFICATION_CODE = 6;
    private static final int LENGTH_MIN_PHONE_NUMBER = 7;


    // This filter ensures only digits input to the names fields
    // It also capitalizes the first character of a name and sets the rest to lower case
    public static InputFilter filterNames = (source, start, end, dest, dstart,
                                             dend) -> {
        for (int i = start; i < end; i++) {
            if (!Character.isLetter(source.charAt(i))) {

                return "";

            } else {
                // Character is letter

                // Check for single character input
                if (source.length() > 1) {

                    // Get last character
                    char last = source.charAt(source.length() - 1);

                    // Check if last character is uppercase
                    if (Character.isUpperCase(last)) {

                        // Convert character to lower case
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

                        //editNamesField.setText(null);
                        //editNamesField.setText(sourceBuilder.toString());
                    }
                }
            }
        }
        return null;
    };

    // This filter prevents white spaces input to the email address field
    public static InputFilter filterEmailAddress = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; i++) {
            if (Character.isWhitespace(source.charAt(i))) {
                return "";
            }
        }
        return null;
    };

    //  This filter ensures only letters and digits input to the verification code field
    public static InputFilter filterVerificationCodes = (source, start, end, dest, dstart, dend) ->
    {
        for (int i = start; i < end; i++) {
            if (!Character.isLetterOrDigit(source.charAt(i))) {
                return "";
            }
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

                    editText.setText(null); // Clear text
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
                secondLast = editable.charAt(editable.length() - 2); // Get second last character
                last = editable.charAt(editable.length() - 1); // Get last character

                if (String.valueOf(secondLast).equals(".")) {
                    if (String.valueOf(last).equals(".")) {

                        currentValue = editable.toString(); // Get current value

                        // Remove last period
                        trimmed = currentValue.substring(0, editable.length() - 1);

                        editText.setText(null); // Clear text

                        for (int i = 0; i < trimmed.length(); i++) {

                            char c = trimmed.charAt(i); // Get character at position
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

    /**
     * Function to check persons name length and notify on error
     *
     * @param context     - Context to get string resources
     * @param editText    - Associated EditText
     * @param isFirstName - Boolean to get first name or last name resources
     * @return boolean
     */
    public static boolean checkPersonNameLengthNotify(Context context, @NonNull EditText editText,
                                                      boolean isFirstName) {

        if (DataUtils.isEmptyEditText(editText)) {
            if (isFirstName) {

                // Toast error message
                CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                        R.string.error_first_name_null), R.drawable.ic_baseline_person_24_white);

                editText.setError(DataUtils.getStringResource(context,
                        R.string.error_first_name_null)); // Enable error icon

            } else {

                // Toast error message
                CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                        R.string.error_last_name_null), R.drawable.ic_baseline_person_24_white);

                // Enable error icon
                editText.setError(DataUtils.getStringResource(context,
                        R.string.error_last_name_null));
            }

        } else if (Objects.requireNonNull(editText.getText()).length()
                < InputFiltersUtils.LENGTH_MIN_SINGLE_NAME) {

            if (isFirstName) {

                // Toast error message
                CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                        R.string.error_first_name_length,
                        String.valueOf(InputFiltersUtils.LENGTH_MIN_SINGLE_NAME)),
                        R.drawable.ic_baseline_person_24_white);

                // Enable error icon
                editText.setError(DataUtils.getStringResource(context,
                        R.string.error_first_name_length_short));

            } else {

                // Toast error message
                CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                        R.string.error_last_name_length,
                        String.valueOf(InputFiltersUtils.LENGTH_MIN_SINGLE_NAME)),
                        R.drawable.ic_baseline_person_24_white);

                // Enable error icon
                editText.setError(DataUtils.getStringResource(context,
                        R.string.error_last_name_length_short));
            }
        } else {

            return true; // Return true on value acceptable
        }

        return false; // Return false on value not acceptable
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

        if (Objects.requireNonNull(editText.getText()).length() == 0) {

            // Toast error message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_business_name_null),
                    R.drawable.ic_baseline_business_24_white);

            // Enable error icon
            editText.setError(DataUtils.getStringResource(context,
                    R.string.error_business_name_null));

        } else {

            return true; // Return true on value acceptable
        }

        return false; // Return false on value not acceptable
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

        if ((Objects.requireNonNull(editText.getText()).length()
                > InputFiltersUtils.LENGTH_MAX_EMAIL_ADDRESS)
                || (!Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches())
        ) {

            // Toast error message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_email_address_invalid), R.drawable.ic_baseline_email_24_white);

            // Enable error icon
            editText.setError(DataUtils.getStringResource(context,
                    R.string.error_email_address_invalid_short));

        } else {

            return true; // Return true on value acceptable
        }

        return false; // Return false on value not acceptable
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

        if ((Objects.requireNonNull(editText.getText()).length()
                < InputFiltersUtils.LENGTH_MIN_PHONE_NUMBER)
                || (!Patterns.PHONE.matcher(editText.getText().toString()).matches())
        ) {

            // Toast error message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_phone_number_invalid),
                    R.drawable.ic_baseline_phone_24_white);

            // Enable error icon
            editText.setError(DataUtils.getStringResource(context,
                    R.string.error_phone_number_invalid_short));

        } else {

            return true; // Return true on value acceptable
        }

        return false; // Return false on value not acceptable
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

        if (Objects.requireNonNull(editText.getText()).length() == 0) {

            // Toast error message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_password_length,
                    String.valueOf(LENGTH_MIN_PASSWORD)),
                    R.drawable.ic_baseline_alternate_email_24_white);

            // Enable error icon
            editText.setError(DataUtils.getStringResource(context,
                    R.string.error_password_length_short));

        } else {

            return true; // Return true on value acceptable
        }

        return false; // Return false on value not acceptable
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

        if (editCurrentPassword.getText().toString().equals(
                editNewPassword.getText().toString())) {

            // Toast error message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_password_select_new),
                    R.drawable.ic_baseline_lock_24_white);

            // Enable error icon
            editNewPassword.setError(DataUtils.getStringResource(context,
                    R.string.error_password_select_new));

        } else {

            return true; // Return true on value acceptable
        }

        return false; // Return false on value not acceptable
    }

    public static boolean compareNewPasswords(@NonNull Context context,
                                              @NonNull EditText editNewPassword,
                                              @NonNull EditText editConfirmPassword) {

        if (!editNewPassword.getText().toString().equals(
                editConfirmPassword.getText().toString())) {

            // Toast error message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_new_password_mismatch),
                    R.drawable.ic_baseline_lock_24_white);

            // Enable error icon
            editConfirmPassword.setError(DataUtils.getStringResource(context,
                    R.string.error_new_password_mismatch));

        } else {

            return true; // Return true on value acceptable
        }

        return false; // Return false on value not acceptable
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

        if (!(Objects.requireNonNull(editText.getText()).length() > 0)) {

            // Toast error message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_country_null), R.drawable.ic_baseline_location_on_24_white);

            editText.setError(null); // Enable error icon

        } else {

            return true; // Return true on value acceptable
        }

        return false; // Return false on value not acceptable
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

        if (!(Objects.requireNonNull(editText.getText()).length() > 0)) {

            // Toast error message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_city_null), R.drawable.ic_baseline_location_city_24_white);

            editText.requestFocus(); // Request focus
            editText.setError(null); // Enable error icon

        } else {

            return true; // Return true on value acceptable
        }

        return false; // Return false on value not acceptable
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

        if (!(gender.length() > 0)) {

            // Toast error message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_gender_null),
                    R.drawable.ic_gender_neutral_100px_white);

        } else {

            return true;
        }

        return false;
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

        if (!(Objects.requireNonNull(editText.getText()).length() > 0)) {

            // Toast error message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_date_of_birth_null),
                    R.drawable.ic_baseline_calendar_today_24_white);

            editText.setError(null); // Enable error icon

        } else {

            return true; // Return true on value acceptable
        }

        return false; // Return false on value not acceptable
    }

    /**
     * Function to check business name length
     *
     * @param context  - context used to show toast
     * @param editText - Character Sequence
     * @return boolean
     */
    public static boolean checkVerificationLengthNotify(Context context,
                                                        @NonNull EditText editText) {

        if (Objects.requireNonNull(editText.getText()).length() < LENGTH_VERIFICATION_CODE) {

            // Toast error message
            CustomToast.errorMessage(context, DataUtils.getStringResource(context,
                    R.string.error_verification_code_length,
                    String.valueOf(LENGTH_VERIFICATION_CODE)),
                    R.drawable.ic_baseline_business_24_white);

            // Enable error icon
            editText.setError(DataUtils.getStringResource(context,
                    R.string.error_verification_code_length,
                    String.valueOf(LENGTH_VERIFICATION_CODE)));

        } else {

            return true; // Return true on value acceptable
        }

        return false; // Return false on value not acceptable
    }

}
