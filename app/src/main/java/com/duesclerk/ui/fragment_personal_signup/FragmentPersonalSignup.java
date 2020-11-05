package com.duesclerk.ui.fragment_personal_signup;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duesclerk.R;
import com.duesclerk.interfaces.Interface_CountryPicker;
import com.duesclerk.interfaces.Interface_SignInSignup;
import com.duesclerk.interfaces.Interface_UserAccountInformation;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import custom.custom_utilities.DataUtils;
import custom.custom_utilities.InputFiltersUtils;
import custom.custom_utilities.UserAccountUtils;
import custom.custom_utilities.ViewsUtils;
import custom.custom_views.dialog_fragments.bottom_sheets.CountryPickerFragment;

public class FragmentPersonalSignup extends Fragment implements Interface_CountryPicker {

    private Context mContext;
    private EditText editFirstName, editLastName, editPhoneNumber, editEmailAddress;
    private TextInputEditText editCountry, editPassword;
    private String countryCode, countryAlpha2, selectedGender;
    private ImageView imagePasswordToggle, imageCountryFlag;
    private CountryPickerFragment countryPickerFragment;

    public static FragmentPersonalSignup newInstance() {
        return new FragmentPersonalSignup();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_personal_signup, container, false);

        mContext = requireActivity(); // Context

        Interface_SignInSignup interfaceSignUpSignIn = (Interface_SignInSignup) getActivity();

        // BottomSheetDialogFragments
        countryPickerFragment = new CountryPickerFragment(this);
        countryPickerFragment.setRetainInstance(true);
        countryPickerFragment.setCancelable(true);

        editFirstName = view.findViewById(R.id.editSignUpActivity_FirstName);
        editLastName = view.findViewById(R.id.editSignUpActivity_LastName);
        editPhoneNumber = view.findViewById(R.id.editSignUpActivity_PhoneNumber);
        editEmailAddress = view.findViewById(R.id.editSignUpActivity_EmailAddress);
        editCountry = view.findViewById(R.id.editSignUpActivity_Country);
        editPassword = view.findViewById(R.id.editSignUpActivity_Password);

        imageCountryFlag = view.findViewById(R.id.imageSignupActivity_CountryFlag);

        imagePasswordToggle = view.findViewById(R.id.imageSignupActivity_PasswordToggle);

        TextView textCreateBusinessAccount = view.findViewById(R.id.textCreateBusinessAccount);

        RadioButton radioGenderMale = view.findViewById(R.id.radioSignupGenderMale);
        RadioButton radioGenderFemale = view.findViewById(R.id.radioSignupGenderFemale);
        RadioButton radioGenderOther = view.findViewById(R.id.radioSignupGenderOther);

        LinearLayout llSignIn = view.findViewById(R.id.llSignUpActivity_SignIn);
        LinearLayout llSignUp = view.findViewById(R.id.llSignUpActivity_SignUp);

        // Set Input Filters
        editFirstName.setFilters(new InputFilter[]{InputFiltersUtils.filterNames,
                new InputFilter.LengthFilter(InputFiltersUtils.maxSingleNameLength)});
        editLastName.setFilters(new InputFilter[]{InputFiltersUtils.filterNames,
                new InputFilter.LengthFilter(InputFiltersUtils.maxSingleNameLength)});
        editEmailAddress.setFilters(new InputFilter[]{InputFiltersUtils.filterEmailAddress,
                new InputFilter.LengthFilter(InputFiltersUtils.maxEmailLength)});

        // Underline text
        textCreateBusinessAccount.setPaintFlags(textCreateBusinessAccount.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);

        editCountry.setOnClickListener(v ->
                ViewsUtils.showBottomSheetDialogFragment(getParentFragmentManager(),
                countryPickerFragment, true));

        radioGenderMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedGender = UserAccountUtils.KEY_GENDER_MALE; // Set gender value
            }
        });

        radioGenderFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedGender = UserAccountUtils.KEY_GENDER_FEMALE; // Set gender value
            }
        });

        radioGenderOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedGender = UserAccountUtils.KEY_GENDER_OTHER; // Set gender value
            }
        });

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

        // Select SignIn tab
        llSignIn.setOnClickListener(v ->
                Objects.requireNonNull(interfaceSignUpSignIn).setTabPosition(0));

        // Pass personal signup details to parent activity for signup
        llSignUp.setOnClickListener(v -> {
            if (checkFieldLengths()) {
                Objects.requireNonNull(interfaceSignUpSignIn).passPersonalAccountSignupDetails(
                        editFirstName.getText().toString(),
                        editLastName.getText().toString(),
                        editPhoneNumber.getText().toString(),
                        editEmailAddress.getText().toString(),
                        countryCode, countryAlpha2,
                        Objects.requireNonNull(editPassword.getText()).toString(),
                        selectedGender);
            }
        });

        // Select business SignUp tab
        textCreateBusinessAccount.setOnClickListener(v ->
                Objects.requireNonNull(interfaceSignUpSignIn).setTabPosition(2));

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
        return (InputFiltersUtils.checkPersonNameLengthNotify(mContext, editFirstName,
                true)
                && InputFiltersUtils.checkPersonNameLengthNotify(mContext, editLastName,
                false)
                && InputFiltersUtils.checkPhoneNumberValidNotify(mContext, editPhoneNumber)
                && InputFiltersUtils.checkEmailAddressValidNotify(mContext, editEmailAddress)
                && InputFiltersUtils.checkCountryLengthNotify(mContext, editCountry)
                && InputFiltersUtils.checkPasswordLengthNotify(mContext, editPassword)
                && InputFiltersUtils.checkGenderLengthNotify(mContext, selectedGender));
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