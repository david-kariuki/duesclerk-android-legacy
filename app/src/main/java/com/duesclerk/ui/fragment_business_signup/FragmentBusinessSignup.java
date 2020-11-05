package com.duesclerk.ui.fragment_business_signup;


import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duesclerk.R;
import com.duesclerk.interfaces.Interface_CountryPicker;
import com.duesclerk.interfaces.Interface_SignInSignup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import custom.custom_utilities.InputFiltersUtils;
import custom.custom_utilities.ViewsUtils;
import custom.custom_views.dialog_fragments.bottom_sheets.CountryPickerFragment;

public class FragmentBusinessSignup extends Fragment implements Interface_CountryPicker {

    public static FragmentBusinessSignup newInstance() {
        return new FragmentBusinessSignup();
    }

    private Context mContext;
    private EditText editBusinessName, editCountry, editCity, editPhoneNumber, editEmailAddress;
    private TextInputEditText editPassword;
    private String countryCode, countryAlpha2;
    private CountryPickerFragment countryPickerFragment;
    private ImageView imageCountryFlag, imagePasswordToggle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_business_signup, container, false);

        mContext = requireActivity(); // Context

        Interface_SignInSignup interfaceSignUpSignIn = (Interface_SignInSignup) getActivity();

        // BottomSheetDialogFragments
        countryPickerFragment = new CountryPickerFragment(this);
        countryPickerFragment.setRetainInstance(true);
        countryPickerFragment.setCancelable(true);

        editBusinessName = view.findViewById(R.id.editSignUpActivity_BusinessName);
        editCountry = view.findViewById(R.id.editSignUpActivity_Country);
        editCity = view.findViewById(R.id.editSignUpActivity_CityTown);
        editPhoneNumber = view.findViewById(R.id.editSignUpActivity_PhoneNumber);
        editEmailAddress = view.findViewById(R.id.editSignUpActivity_EmailAddress);
        editPassword = view.findViewById(R.id.editSignUpActivity_Password);

        imageCountryFlag = view.findViewById(R.id.imageSignupActivity_CountryFlag);
        imagePasswordToggle = view.findViewById(R.id.imageSignupActivity_PasswordToggle);

        TextView textCreatePersonalAccount = view.findViewById(R.id.textCreatePersonalAccount);

        LinearLayout llSignIn = view.findViewById(R.id.llSignUpActivity_SignIn);
        LinearLayout llSignUp = view.findViewById(R.id.llSignUpActivity_SignUp);

        // Set Input Filters

        editEmailAddress.setFilters(new InputFilter[]{InputFiltersUtils.filterEmailAddress,
                new InputFilter.LengthFilter(InputFiltersUtils.maxEmailLength)});

        // Underline text
        textCreatePersonalAccount.setPaintFlags(textCreatePersonalAccount.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);

        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Objects.requireNonNull(editPassword.getText()).toString().length() > 0) {
                    // Show toggle password icon
                    imagePasswordToggle.setVisibility(View.VISIBLE); // Show toggle icon
                } else {
                    // Hide toggle password icon
                    imagePasswordToggle.setVisibility(View.INVISIBLE); // Hide toggle icon
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        imagePasswordToggle.setOnClickListener(view13 -> {
            // Toggle password visibility
            ViewsUtils.togglePasswordField(editPassword, imagePasswordToggle);
        });

        editCountry.setOnClickListener(v ->
                ViewsUtils.showBottomSheetDialogFragment(getParentFragmentManager(),
                countryPickerFragment, true));

        // Select SignIn tab
        llSignIn.setOnClickListener(v ->
            Objects.requireNonNull(interfaceSignUpSignIn).setTabPosition(0));

        // Pass business signup details to parent activity for signup
        llSignUp.setOnClickListener(v -> {
            if (checkFieldLengths()) {
                Objects.requireNonNull(interfaceSignUpSignIn).passBusinessAccountSignupDetails(
                        editBusinessName.getText().toString(),
                        countryCode, countryAlpha2,
                        editCity.getText().toString(),
                        editPhoneNumber.getText().toString(),
                        editEmailAddress.getText().toString(),
                        Objects.requireNonNull(editPassword.getText()).toString());
            }
        });

        // Select business SignUp tab
        textCreatePersonalAccount.setOnClickListener(v ->
                Objects.requireNonNull(interfaceSignUpSignIn).setTabPosition(1));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * Method to check field inputs
     *
     * @return field input status
     */
    private boolean checkFieldLengths() {
        return (InputFiltersUtils.checkBusinessNameLengthNotify(mContext, editBusinessName)
                && InputFiltersUtils.checkCountryLengthNotify(mContext, editCountry)
                && InputFiltersUtils.checkCityLengthNotify(mContext, editCity)
                && InputFiltersUtils.checkPhoneNumberValidNotify(mContext, editPhoneNumber)
                && InputFiltersUtils.checkEmailAddressValidNotify(mContext, editEmailAddress)
                && InputFiltersUtils.checkPasswordLengthNotify(mContext, editPassword));
    }

    @Override
    public void passCountryName(String countryName) {
        editCountry.setText(countryName); // Set country name
        ViewsUtils.hideKeyboard(requireActivity()); // Hide keyboard
    }

    @Override
    public void passCountryCode(String countryCode) {
        this.countryCode = countryCode;
        ViewsUtils.hideKeyboard(requireActivity()); // Hide keyboard
    }

    @Override
    public void passCountryAlpha2(String countryAlpha2) {
        this.countryAlpha2 = countryAlpha2;
        ViewsUtils.hideKeyboard(requireActivity()); // Hide keyboard
    }

    @Override
    public void passCountryAlpha3(String countryAlpha3) {

    }

    @Override
    public void passCountryFlag(int countryFlagId) {
        // Load flag to ImageView
        ViewsUtils.loadImageView(mContext, countryFlagId, imageCountryFlag);
    }
}