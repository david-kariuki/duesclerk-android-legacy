package com.duesclerk.custom.custom_views.dialog_fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.duesclerk.R;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.interfaces.Interface_UserAccount_Information;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class DialogFragment_DatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    // Interface to pass date
    private final Interface_UserAccount_Information interfaceUserAccountInformation;
    private final LayoutInflater inflater;
    private final Context mContext;
    private final boolean restrictToMinAge;
    private final String dialogTitle;

    public DialogFragment_DatePicker(Context context, boolean restrictToMinAge, String dialogTitle) {
        this.mContext = context;
        this.restrictToMinAge = restrictToMinAge;
        this.dialogTitle = dialogTitle;
        this.interfaceUserAccountInformation = (Interface_UserAccount_Information) context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View titleView = inflater.inflate(
                R.layout.title_date_picker, null, false);

        TextView textTitle = titleView.findViewById(R.id.textViewDatePickerTitle);

        // Set title and age restriction message
        textTitle.setText(dialogTitle);

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        /*if (restrictToMinAge) {
            // Subtract min age in years (adding 1 to make the resulting current year scroll to
            // last position of DatePicker)
            year = calendar.get(Calendar.YEAR) - (DataUtils.getIntegerResource(mContext,
                    R.integer.int_min_user_age) + 1);
            textTitle_Message.setText(DataUtils.getStringResource(mContext,
                    R.string.msg_minimum_age_is, R.integer.int_min_user_age));
        } else {
            year = calendar.get(Calendar.YEAR);
        }*/


        // Initialize DatePicker with theme and date
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                R.style.DatePickerDialogTheme, this, year, month, day);
        datePickerDialog.setCustomTitle(titleView);
        datePickerDialog.setCancelable(true);
        datePickerDialog.setCanceledOnTouchOutside(true);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        // Set Dialog ShowListener
        datePickerDialog.setOnShowListener(dialogInterface -> {
            Button negButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE);
            Button posButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE);
            negButton.setTextColor(DataUtils.getColorResource(mContext, R.color.colorPrimary));
            negButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            posButton.setTextColor(DataUtils.getColorResource(mContext, R.color.colorPrimary));
            posButton.setTypeface(posButton.getTypeface(), Typeface.BOLD);
            posButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        });

        // Remove window title
        datePickerDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return datePickerDialog;
    }

    /**
     * Function to send date on date set
     *
     * @param view  - DatePicker
     * @param year  - Year
     * @param month - Month of the year
     * @param day   - Day of the year
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Create a Date object with user chosen date
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0); // Set time in Millis
        cal.set(year, month, day, 0, 0, 0);
        Date chosenDate = cal.getTime();
        // DateFormat dfMediumUS, dfMediumUK, dfShort, dfLong;
        DateFormat dfFull;
        // String strDFMediumUS, strDFMediumUK, strDFShort, strDFLong;
        String strDFFull;

        // Format the date using style medium and US locale
        // dfMediumUS = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        // strDFMediumUS = dfMediumUS.format(chosenDate);

        // Format the date using style medium and UK locale
        // dfMediumUK = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
        // strDFMediumUK = dfMediumUK.format(chosenDate);

        // Format the date using style short
        // dfShort = DateFormat.getDateInstance(DateFormat.SHORT);
        // strDFShort = dfShort.format(chosenDate);

        // Format the date using style long
        // dfLong = DateFormat.getDateInstance(DateFormat.LONG);
        // strDFLong = dfLong.format(chosenDate);

        // Format the date using style full
        dfFull = DateFormat.getDateInstance(DateFormat.FULL);
        strDFFull = dfFull.format(chosenDate);

        // Pass formatted date to interface
        interfaceUserAccountInformation.passDatePickerSelectedDate(strDFFull);
    }
}
