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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duesclerk.R;
import com.duesclerk.classes.custom_utilities.application.ViewsUtils;
import com.duesclerk.classes.custom_utilities.user_data.InputFiltersUtils;
import com.duesclerk.classes.custom_views.dialog_fragments.bottom_sheets.BottomSheetFragment_CountryPicker;
import com.duesclerk.interfaces.Interface_CountryPicker;
import com.duesclerk.interfaces.Interface_SignInSignup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class FragmentPersonalSignup extends Fragment implements Interface_CountryPicker {

    private Context mContext;
    private EditText editFirstName, editLastName, editEmailAddress, editCountry;
    private TextInputEditText editPassword;
    private String countryCode, countryAlpha2;
    private ImageView imagePasswordToggle, imageCountryFlag;
    private BottomSheetFragment_CountryPicker bottomSheetFragmentCountryPicker;

    public static FragmentPersonalSignup newInstance() {
        return new FragmentPersonalSignup();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_personal_signup, container, false);

        mContext = getContext(); // Context

        Interface_SignInSignup interfaceSignUpSignIn = (Interface_SignInSignup) getActivity();

        // BottomSheetDialogFragments
        bottomSheetFragmentCountryPicker = new BottomSheetFragment_CountryPicker(this);
        bottomSheetFragmentCountryPicker.setRetainInstance(true);
        bottomSheetFragmentCountryPicker.setCancelable(true);

        editFirstName = view.findViewById(R.id.editSignUpActivity_FirstName);
        editLastName = view.findViewById(R.id.editSignUpActivity_LastName);
        editEmailAddress = view.findViewById(R.id.editSignUpActivity_EmailAddress);
        editCountry = view.findViewById(R.id.editSignUpActivity_Country);
        editPassword = view.findViewById(R.id.editSignUpActivity_Password);

        imageCountryFlag = view.findViewById(R.id.imageSignupActivity_CountryFlag);

        imagePasswordToggle = view.findViewById(R.id.imageSignupActivity_PasswordToggle);

        TextView textCreateBusinessAccount = view.findViewById(R.id.textCreateBusinessAccount);

        LinearLayout llSignIn = view.findViewById(R.id.llSignUpActivity_SignIn);
        LinearLayout llSignUp = view.findViewById(R.id.llSignUpActivity_SignUp);

        // Set Input Filters
        editFirstName.setFilters(new InputFilter[]{
                InputFiltersUtils.filterNames,
                new InputFilter.LengthFilter(InputFiltersUtils.LENGTH_MAX_SINGLE_NAME)
        });
        editLastName.setFilters(new InputFilter[]{
                InputFiltersUtils.filterNames,
                new InputFilter.LengthFilter(InputFiltersUtils.LENGTH_MAX_SINGLE_NAME)
        });
        editEmailAddress.setFilters(new InputFilter[]{InputFiltersUtils.filterEmailAddress,
                new InputFilter.LengthFilter(InputFiltersUtils.LENGTH_MAX_EMAIL_ADDRESS)});

        // Underline text
        textCreateBusinessAccount.setPaintFlags(textCreateBusinessAccount.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);

        editCountry.setOnClickListener(v ->
                ViewsUtils.showBottomSheetDialogFragment(getParentFragmentManager(),
                        bottomSheetFragmentCountryPicker, true));

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
                        editEmailAddress.getText().toString(),
                        countryCode, countryAlpha2,
                        Objects.requireNonNull(editPassword.getText()).toString());
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
                && InputFiltersUtils.checkEmailAddressValidNotify(mContext, editEmailAddress)
                && InputFiltersUtils.checkCountryLengthNotify(mContext, editCountry)
                && InputFiltersUtils.checkPasswordLengthNotify(mContext, editPassword));
    }

    @Override
    public void passCountryName(String countryName) {
    }

    @Override
    public void passCountryCode(String countryCode) {
        this.countryCode = countryCode;
        ViewsUtils.hideKeyboard(requireActivity()); // Hide keyboard
    }

    @Override
    public void passCountryCodeWithCountryName(String countryCodeAndName) {
        editCountry.setText(countryCodeAndName); // Set country name
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
