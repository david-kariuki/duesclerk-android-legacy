package com.duesclerk.classes.custom_views.dialog_fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.duesclerk.R;
import com.duesclerk.classes.custom_utilities.application.ApplicationClass;
import com.duesclerk.classes.custom_utilities.application.BroadCastUtils;
import com.duesclerk.classes.custom_utilities.application.PermissionUtils;
import com.duesclerk.classes.custom_utilities.application.RequestCodeUtils;
import com.duesclerk.classes.custom_utilities.application.ViewsUtils;
import com.duesclerk.classes.custom_utilities.application.VolleyUtils;
import com.duesclerk.classes.custom_utilities.user_data.ContactUtils;
import com.duesclerk.classes.custom_utilities.user_data.DataUtils;
import com.duesclerk.classes.custom_utilities.user_data.InputFiltersUtils;
import com.duesclerk.classes.custom_utilities.user_data.UserAccountUtils;
import com.duesclerk.classes.custom_views.toast.CustomToast;
import com.duesclerk.classes.network.InternetConnectivity;
import com.duesclerk.classes.network.NetworkTags;
import com.duesclerk.classes.network.NetworkUrls;
import com.duesclerk.classes.storage_adapters.UserDatabase;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.picture.ContactPictureType;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogFragment_AddContact extends DialogFragment {

    // Get class simple name
    // private final String TAG = DialogFragment_AddContact.class.getSimpleName();

    private final LayoutInflater inflater;
    private final Context mContext;
    private final int mainActivityTabLayoutPosition;
    private EditText editContactFullName, editContactPhoneNumber, editEmailAddress,
            editContactAddress;
    private String contactsEmailAddress, contactsAddress, contactType;
    private ProgressDialog progressDialog;
    private ExpandableLayout expandableLayoutDropDown;
    private ImageView imageDropDown;

    /**
     * Class constructor
     *
     * @param context - Context
     */
    public DialogFragment_AddContact(Context context, int mainActivityTabLayoutPosition) {

        this.mContext = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Set MainActivity TabLayout position
        this.mainActivityTabLayoutPosition = mainActivityTabLayoutPosition;
    }

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialogAddContact = super.onCreateDialog(savedInstanceState);

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_add_contact,
                null, false);

        // ImageViews
        ImageView imageAddContact = dialogView.findViewById(R.id.imageAddContact_AddContact);
        this.imageDropDown = dialogView.findViewById(R.id.imageAddContact_DropDown);

        // EditTexts
        this.editContactFullName = dialogView.findViewById(R.id.editAddContact_FullName);
        this.editContactPhoneNumber = dialogView.findViewById(R.id.editAddContact_PhoneNumber);
        this.editEmailAddress = dialogView.findViewById(R.id.editAddContact_EmailAddress);
        this.editContactAddress = dialogView.findViewById(R.id.editAddContact_Address);

        // TextViews
        TextView textPeopleOwingMe = dialogView.findViewById(R.id.textAddContact_PeopleOwingMe);
        TextView textPeopleIOwe = dialogView.findViewById(R.id.textAddContact_PeopleIOwe);

        // LinearLayouts
        LinearLayout llCancel = dialogView.findViewById(R.id.llAddContact_Cancel);
        LinearLayout llAddContact = dialogView.findViewById(R.id.llAddContact_Add);

        // Radio buttons
        RadioButton radioPeopleOwingMe = dialogView.findViewById(R.id.radioAddContact_PeopleOwingMe);
        RadioButton radioPeopleIOwe = dialogView.findViewById(R.id.radioAddContact_PeopleIOwe);

        // ExpandableLayout
        this.expandableLayoutDropDown = dialogView.findViewById(
                R.id.expandableAddContact_OptionalContactInformation);

        // Initialize ProgressDialog
        this.progressDialog = ViewsUtils.initProgressDialog(requireActivity(), false);

        UserDatabase database = new UserDatabase(mContext); // Initialize user database object

        // Switch MainActivity current TabLayout position and check the radio button with
        // the contact type matching MainActivities current fragment
        switch (this.mainActivityTabLayoutPosition) {
            case 0:

                radioPeopleOwingMe.setChecked(true); // Check radio button

                // Set contact type
                this.contactType = ContactUtils.KEY_CONTACT_TYPE_PEOPLE_OWING_ME;
                break;

            case 1:

                radioPeopleIOwe.setChecked(true); // Check radio button

                // Set contact type
                this.contactType = ContactUtils.KEY_CONTACT_TYPE_PEOPLE_I_OWE;
                break;

            default:

                radioPeopleIOwe.setChecked(false); // Uncheck radio button
                radioPeopleOwingMe.setChecked(false); // Uncheck radio button
                break;
        }

        // RadioButton ColorStateList
        ColorStateList colorStateList = new ColorStateList(

                new int[][]{

                        new int[]{-android.R.attr.state_checked}, // Unchecked
                        new int[]{android.R.attr.state_checked} // Checked
                },

                new int[]{

                        DataUtils.getColorResource(mContext, R.color.colorBlack), // Unchecked
                        DataUtils.getColorResource(mContext, R.color.colorPrimary) // Checked
                }
        );

        // Set button tint list
        radioPeopleOwingMe.setButtonTintList(colorStateList);
        radioPeopleIOwe.setButtonTintList(colorStateList);

        // RadioButton onClick
        radioPeopleOwingMe.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                // Get contact type
                this.contactType = ContactUtils.KEY_CONTACT_TYPE_PEOPLE_OWING_ME;

                // Change label text color
                textPeopleOwingMe.setTextColor(DataUtils.getColorResource(mContext,
                        R.color.colorPrimary));

            } else {

                // Change label text color
                textPeopleOwingMe.setTextColor(DataUtils.getColorResource(mContext,
                        R.color.colorBlack));
            }
        });

        // RadioButton onClick
        radioPeopleIOwe.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                // Get contact type
                this.contactType = ContactUtils.KEY_CONTACT_TYPE_PEOPLE_I_OWE;

                // Change label text color
                textPeopleIOwe.setTextColor(DataUtils.getColorResource(mContext,
                        R.color.colorPrimary));

            } else {

                // Change label text color
                textPeopleIOwe.setTextColor(DataUtils.getColorResource(mContext,
                        R.color.colorBlack));
            }
        });

        // Add contact onClick
        imageAddContact.setOnClickListener(v -> {

            if (PermissionUtils.requestContactsPermission(DialogFragment_AddContact.this)) {

                // Call ContactPickerActivity
                Intent intent = new Intent(requireActivity(), ContactPickerActivity.class)
                        .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE,
                                ContactPictureType.ROUND.name())
                        .putExtra(ContactPickerActivity.EXTRA_SHOW_CHECK_ALL, false)
                        .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION,
                                ContactDescription.ADDRESS.name())
                        .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE,
                                ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER,
                                ContactSortOrder.AUTOMATIC.name())
                        .putExtra(ContactPickerActivity.EXTRA_SELECT_CONTACTS_LIMIT, 1)
                        .putExtra(ContactPickerActivity.EXTRA_ONLY_CONTACTS_WITH_PHONE, true);

                // Start activity
                startActivityForResult(intent, RequestCodeUtils.REQUEST_CODE_CONTACTS);
            }
        });

        // Cancel onClick
        llCancel.setOnClickListener(v -> dismiss()); // Dismiss dialog

        // Add contact onClick
        llAddContact.setOnClickListener(v -> {

            // Check fields
            if (checkFieldInputs()) {
                // Fields ok

                // Check for email address
                if (!DataUtils.isEmptyEditText(editEmailAddress)) {

                    // Get email address
                    this.contactsEmailAddress = editEmailAddress.getText().toString();
                }

                // Check for contact address
                if (!DataUtils.isEmptyEditText(editContactAddress)) {

                    // Get contact address
                    this.contactsAddress = editContactAddress.getText().toString();
                }

                String userId = database.getUserAccountInfo(null).get(0).getUserId();
                String contactsFullName = editContactFullName.getText().toString();
                String contactsPhoneNumber = editContactPhoneNumber.getText().toString();

                // Add/Upload  contact
                this.addContact(userId, contactsFullName, contactsPhoneNumber);

            }
        });

        // Dropdown onClick
        imageDropDown.setOnClickListener(
                v -> {

                    // Check if ExpandableLayout is expanded
                    if (!expandableLayoutDropDown.isExpanded()) {

                        // Expand ExpandableLayout
                        ViewsUtils.expandExpandableLayout(true, expandableLayoutDropDown);

                        imageDropDown.setRotation(180); // Rotate drop down image

                    } else {

                        // Collapse ExpandableLayout
                        ViewsUtils.expandExpandableLayout(false, expandableLayoutDropDown);

                        imageDropDown.setRotation(0); // Rotate drop down image
                    }
                }
        );

        // Remove window title
        dialogAddContact.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Set width to match parent and height to wrap content
        Window window = dialogAddContact.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        // Set dialog transparent background
        dialogAddContact.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddContact.setContentView(dialogView);

        return dialogAddContact;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            // Get dialog inputs from savedInstanceState
            String savedContactsFullName =
                    savedInstanceState.getString(ContactUtils.FIELD_CONTACT_FULL_NAME);
            String savedContactsPhoneNumber =
                    savedInstanceState.getString(ContactUtils.FIELD_CONTACT_PHONE_NUMBER);
            String savedContactsEmailAddress =
                    savedInstanceState.getString(ContactUtils.FIELD_CONTACT_EMAIL_ADDRESS);
            String savedContactsAddress =
                    savedInstanceState.getString(ContactUtils.FIELD_CONTACT_ADDRESS);

            // Check for values and set to EditTexts
            if (!DataUtils.isEmptyString(savedContactsFullName)) {

                // Set contacts full name
                editContactFullName.setText(savedContactsFullName);
            }
            if (!DataUtils.isEmptyString(savedContactsPhoneNumber)) {

                // Set contacts phone number
                editContactFullName.setText(savedContactsPhoneNumber);
            }
            if (!DataUtils.isEmptyString(savedContactsEmailAddress)) {

                // Set contacts email address
                editContactFullName.setText(savedContactsEmailAddress);
            }
            if (!DataUtils.isEmptyString(savedContactsAddress)) {

                // Set contacts address
                editContactFullName.setText(savedContactsAddress);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save dialog inputs to outState

        // Check for field values and set to outState
        if (!DataUtils.isEmptyEditText(editContactFullName)) {

            // Get and put contacts full name
            outState.putString(ContactUtils.FIELD_CONTACT_FULL_NAME,
                    editContactFullName.getText().toString());
        }

        if (!DataUtils.isEmptyEditText(editContactPhoneNumber)) {

            // Get and put contacts phone number
            outState.putString(ContactUtils.FIELD_CONTACT_PHONE_NUMBER,
                    editContactPhoneNumber.getText().toString());
        }

        if (!DataUtils.isEmptyEditText(editEmailAddress)) {

            // Get and put contacts email address
            outState.putString(ContactUtils.FIELD_CONTACT_EMAIL_ADDRESS,
                    editEmailAddress.getText().toString());
        }

        if (!DataUtils.isEmptyEditText(editContactAddress)) {

            // Get and put contacts address
            outState.putString(ContactUtils.FIELD_CONTACT_ADDRESS,
                    editContactAddress.getText().toString());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodeUtils.REQUEST_CODE_CONTACTS && resultCode == Activity.RESULT_OK &&
                data != null && data.hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {

            // Get contacts
            List<Contact> contacts = (List<Contact>) data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
            for (Contact contact : contacts) {
                setSelectedContactInfo(contact); // Get selected contact information
            }

            // Get groups
            /*List<Group> groups = (List<Group>) data.getSerializableExtra(ContactPickerActivity.RESULT_GROUP_DATA);
            for (Group group : groups) {
                //Contact contact = (Contact) group.getContacts();
                // process the groups...
            }*/
        }
    }

    /**
     * Function to clear field inputs
     */
    private void clearFieldInputsFocusAndError() {

        // Clear EditText inputs
        editContactFullName.setText(null);
        editContactPhoneNumber.setText(null);
        editEmailAddress.setText(null);
        editContactAddress.setText(null);

        // Clear EditText focus
        editContactFullName.clearFocus();
        editContactPhoneNumber.clearFocus();
        editEmailAddress.clearFocus();
        editContactAddress.clearFocus();

        // Clear EditText error
        editContactFullName.setError(null);
        editContactPhoneNumber.setError(null);
        editEmailAddress.setError(null);
        editContactAddress.setError(null);
    }

    /**
     * Function to check field lengths and values and notify by toast on error
     */
    private boolean checkFieldInputs() {
        boolean fieldOk;


        fieldOk = (checkContactType()
                && InputFiltersUtils.checkFullNameLengthNotify(mContext, editContactFullName)
                && InputFiltersUtils.checkPhoneNumberValidNotify(mContext, editContactPhoneNumber)
        );

        // Validate email address
        if (!DataUtils.isEmptyEditText(editEmailAddress)) {
            fieldOk = InputFiltersUtils.checkEmailAddressValidNotify(mContext, editEmailAddress);
        }

        return fieldOk;
    }

    /**
     * Function to check if contact type is checked
     */
    private boolean checkContactType() {
        boolean checked = true;

        if (DataUtils.isEmptyString(contactType)) {
            checked = false; // Set checked t false

            CustomToast.errorMessage(mContext,
                    DataUtils.getStringResource(mContext, R.string.error_contact_type_null),
                    R.drawable.ic_baseline_person_add_alt_1_24_white);
        }

        return checked;
    }

    /**
     * Function to set contact information to respective EditTexts
     *
     * @param contact - Selected contact
     */
    private void setSelectedContactInfo(Contact contact) {

        // Get required data
        // Uri photoUri = contact.getPhotoUri();
        String fullName = (contact.getFirstName() + " " + contact.getLastName())
                .replace("---", "").trim();
        String phoneNumber = getAllPhoneNumberTypesValues(contact); // Get all phone number types
        String emailAddress = getAllEmailAddressTypesValues(contact);
        String address = getAllContactAddressTypesValues(contact);

        clearFieldInputsFocusAndError(); // Clear input fields

        try {

            // Check for full name
            if (!DataUtils.isEmptyString(fullName)) {

                editContactFullName.setText(fullName); // Set full name
            }

            // Check for phone number
            if (!DataUtils.isEmptyString(phoneNumber)) {

                editContactPhoneNumber.setText(phoneNumber); // Set phone number
            }

            // Check for email address
            if (!DataUtils.isEmptyString(emailAddress)) {

                editEmailAddress.setText(emailAddress); // Set phone number
            }

            // Check for address
            if (!DataUtils.isEmptyString(address)) {

                editContactAddress.setText(address); // Set address
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Function to get all phone number types values
     * This prevents getting null phone number by getting the next phone number type
     * if the previous is null
     *
     * @param contact - Selected contact
     */
    private String getAllPhoneNumberTypesValues(Contact contact) {

        String phoneNumber = null;

        // Catch in case of exceptions
        try {

            // Get phone number types
            String phoneMain = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_MAIN));
            String phoneMobile = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_MOBILE));
            String phoneHome = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_HOME));
            String phoneWork = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_WORK));
            String phoneWorkMobile = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_WORK_MOBILE));
            String phoneOther = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_OTHER));
            String phoneCustom = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_CUSTOM));
            String phoneCompanyMain = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_COMPANY_MAIN));
            String phoneFaxHome = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_FAX_HOME));
            String phoneFaxWork = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_FAX_WORK));
            String phoneOtherFax = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_OTHER_FAX));
            String phoneMMS = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_MMS));
            String phoneISDN = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_ISDN));
            String phoneAssistant = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_ASSISTANT));
            String phoneCar = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_CAR));
            String phoneCallback = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_CALLBACK));
            String phonePager = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_PAGER));
            String phoneWorkPager = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_WORK_PAGER));
            String phoneRadio = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_RADIO));
            String phoneTelex = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_TELEX));
            String phoneTTYTDD = (contact.getPhone(ContactsContract.CommonDataKinds
                    .Phone.TYPE_TTY_TDD));

            // Get phone number if not null by order
            phoneNumber = getPhoneNumberByOrder(
                    phoneMain, phoneMobile, phoneHome, phoneWork, phoneWorkMobile, phoneOther,
                    phoneCustom, phoneCompanyMain, phoneFaxHome, phoneFaxWork, phoneOtherFax, phoneMMS,
                    phoneISDN, phoneAssistant, phoneCar, phoneCallback, phonePager, phoneWorkPager,
                    phoneRadio, phoneTelex, phoneTTYTDD);

        } catch (Exception ignored) {
        }

        return phoneNumber; // Return phone number
    }

    /**
     * Function to get all email address types values
     * This prevents getting null email address by getting the next email address
     * type if the previous is null
     *
     * @param contact - Selected contact
     */
    private String getAllEmailAddressTypesValues(Contact contact) {

        String emailAddress = null;

        // Catch in case of exceptions
        try {

            // Get phone number types
            String emailMobile = (contact.getEmail(ContactsContract.CommonDataKinds
                    .Email.TYPE_MOBILE));
            String emailHome = (contact.getEmail(ContactsContract.CommonDataKinds
                    .Email.TYPE_HOME));
            String emailWork = (contact.getEmail(ContactsContract.CommonDataKinds
                    .Email.TYPE_WORK));
            String emailOther = (contact.getEmail(ContactsContract.CommonDataKinds
                    .Email.TYPE_OTHER));
            String emailCustom = (contact.getEmail(ContactsContract.CommonDataKinds
                    .Email.TYPE_CUSTOM));

            // Get phone number if not null by order
            emailAddress = getEmailAddressByOrder(emailMobile, emailHome, emailWork,
                    emailOther, emailCustom);

        } catch (Exception ignored) {
        }

        return emailAddress; // Return email address
    }

    /**
     * Function to get all email address types values
     * This prevents getting null email address by getting the next email address
     * type if the previous is null
     *
     * @param contact - Selected contact
     */
    private String getAllContactAddressTypesValues(Contact contact) {

        String contactAddress = null;

        // Catch in case of exceptions
        try {

            // Get phone number types
            String addressHome = (contact.getAddress(ContactsContract.CommonDataKinds
                    .SipAddress.TYPE_HOME));
            String addressWork = (contact.getAddress(ContactsContract.CommonDataKinds
                    .SipAddress.TYPE_WORK));
            String addressOther = (contact.getAddress(ContactsContract.CommonDataKinds
                    .SipAddress.TYPE_OTHER));
            String addressCustom = (contact.getAddress(ContactsContract.CommonDataKinds
                    .SipAddress.TYPE_CUSTOM));

            // Get phone number if not null by order
            contactAddress = getContactAddressByOrder(addressHome, addressWork,
                    addressOther, addressCustom);

        } catch (Exception ignored) {
        }

        return contactAddress; // Return contact address
    }

    /**
     * Function to get all phone number types and check all for value.
     * It returns a phone number based on the parameter order
     *
     * @param phoneMain        - Phone number type main
     * @param phoneMobile      - Phone number type mobile
     * @param phoneHome        - Phone number type home
     * @param phoneWork        - Phone number type work
     * @param phoneWorkMobile  - Phone number type work_mobile
     * @param phoneOther       - Phone number type other
     * @param phoneCustom      - Phone number type custom
     * @param phoneCompanyMain - Phone number type company_main
     * @param phoneFaxHome     - Phone number type fax_home
     * @param phoneFaxWork     - Phone number type fax_work
     * @param phoneOtherFax    - Phone number type other_fax
     * @param phoneMMS         - Phone number type MMS
     * @param phoneISDN        - Phone number type ISDN
     * @param phoneAssistant   - Phone number type assistant
     * @param phoneCar         - Phone number type car
     * @param phoneCallback    - Phone number type callback
     * @param phonePager       - Phone number type pager
     * @param phoneWorkPager   - Phone number type work_pager
     * @param phoneRadio       - Phone number type radio
     * @param phoneTelex       - Phone number type telex
     * @param phoneTTYTDD      - Phone number type ttytdd
     */
    private String getPhoneNumberByOrder(
            final String phoneMain, final String phoneMobile, final String phoneHome,
            final String phoneWork, final String phoneWorkMobile, final String phoneOther,
            final String phoneCustom, final String phoneCompanyMain, final String phoneFaxHome,
            final String phoneFaxWork, final String phoneOtherFax, final String phoneMMS,
            final String phoneISDN, final String phoneAssistant, final String phoneCar,
            final String phoneCallback, final String phonePager, final String phoneWorkPager,
            final String phoneRadio, final String phoneTelex, final String phoneTTYTDD
    ) {
        String phoneNumber = null;

        try {

            // Check for phone number types
            if (!DataUtils.isEmptyString(phoneMain)) {
                phoneNumber = phoneMain; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneMobile)) {
                phoneNumber = phoneMobile; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneHome)) {
                phoneNumber = phoneHome; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneWork)) {
                phoneNumber = phoneWork; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneWorkMobile)) {
                phoneNumber = phoneWorkMobile; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneOther)) {
                phoneNumber = phoneOther; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneCustom)) {
                phoneNumber = phoneCustom; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneCompanyMain)) {
                phoneNumber = phoneCompanyMain; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneFaxHome)) {
                phoneNumber = phoneFaxHome; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneFaxWork)) {
                phoneNumber = phoneFaxWork; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneOtherFax)) {
                phoneNumber = phoneOtherFax; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneMMS)) {
                phoneNumber = phoneMMS; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneISDN)) {
                phoneNumber = phoneISDN; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneAssistant)) {
                phoneNumber = phoneAssistant; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneCar)) {
                phoneNumber = phoneCar; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneCallback)) {
                phoneNumber = phoneCallback; // Set phone number

            } else if (!DataUtils.isEmptyString(phonePager)) {
                phoneNumber = phonePager; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneWorkPager)) {
                phoneNumber = phoneWorkPager; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneRadio)) {
                phoneNumber = phoneRadio; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneTelex)) {
                phoneNumber = phoneTelex; // Set phone number

            } else if (!DataUtils.isEmptyString(phoneTTYTDD)) {
                phoneNumber = phoneTTYTDD; // Set phone number
            }

        } catch (Exception ignored) {
        }

        return phoneNumber; // Return phone number
    }

    /**
     * Function to get all email address types and check all for value.
     * It returns a email address based on the parameter order
     *
     * @param emailMobile - Email address type mobile
     * @param emailHome   - Email address type home
     * @param emailWork   - Email address type work
     * @param emailOther  - Email address type other
     * @param emailCustom - Email address type custom
     */
    private String getEmailAddressByOrder(
            final String emailMobile, final String emailHome, final String emailWork,
            final String emailOther, final String emailCustom) {

        String emailAddress = null;

        try {

            // Check for email address types
            if (!DataUtils.isEmptyString(emailMobile)) {
                emailAddress = emailMobile; // Set email address

            } else if (!DataUtils.isEmptyString(emailHome)) {
                emailAddress = emailHome; // Set email address

            } else if (!DataUtils.isEmptyString(emailWork)) {
                emailAddress = emailWork; // Set email address

            } else if (!DataUtils.isEmptyString(emailOther)) {
                emailAddress = emailOther; // Set email address

            } else if (!DataUtils.isEmptyString(emailCustom)) {
                emailAddress = emailCustom; // Set email address
            }
        } catch (Exception ignored) {
        }

        return emailAddress; // Return email address
    }

    /**
     * Function to get all contact address types and check all for value.
     * It returns a email contact based on the parameter order
     *
     * @param addressHome   - Contact address type home
     * @param addressWork   - Contact address type work
     * @param addressOther  - Contact address type other
     * @param addressCustom - Contact address type custom
     */
    private String getContactAddressByOrder(
            final String addressHome, final String addressWork,
            final String addressOther, final String addressCustom) {

        String contactAddress = null;

        try {

            // Check for contact address types
            if (!DataUtils.isEmptyString(addressHome)) {
                contactAddress = addressHome; // Set email address

            } else if (!DataUtils.isEmptyString(addressWork)) {
                contactAddress = addressWork; // Set email address

            } else if (!DataUtils.isEmptyString(addressOther)) {
                contactAddress = addressOther; // Set email address

            } else if (!DataUtils.isEmptyString(addressCustom)) {
                contactAddress = addressCustom; // Set email address
            }
        } catch (Exception ignored) {
        }

        return contactAddress; // Return contact address
    }

    /**
     * Function to add contact to remote database
     *
     * @param userId              - Users id
     * @param contactsFullName    - Contacts full name
     * @param contactsPhoneNumber - Contacts phone number
     */
    private void addContact(final String userId, final String contactsFullName,
                            final String contactsPhoneNumber) {

        // Check Internet Connection states
        if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
            // Connected

            ViewsUtils.hideKeyboard(requireActivity()); // Hide keyboard if showing

            // Show dialog
            ViewsUtils.showProgressDialog(progressDialog,
                    DataUtils.getStringResource(mContext,
                            R.string.title_adding_contact),
                    DataUtils.getStringResource(mContext,
                            R.string.msg_adding_contact, contactsFullName)
            );

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    NetworkUrls.ContactURLS.URL_ADD_CONTACT, response -> {

                // Log Response
                // Log.d(TAG, "Add contact response:" + response);

                ViewsUtils.dismissProgressDialog(progressDialog); // Hide Dialog

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);

                    // Check for error
                    if (!error) {
                        // New contact added successfully

                        // Show success message
                        CustomToast.infoMessage(mContext,
                                DataUtils.getStringResource(mContext,
                                        R.string.msg_contact_adding_successful, contactsFullName),
                                false, R.drawable.ic_baseline_person_add_alt_1_24_white);

                        try {

                            // Send broadcast to set switch account type action text
                            Intent intentBroadcast = null;
                            if (contactType.equals(ContactUtils.KEY_CONTACT_TYPE_PEOPLE_OWING_ME)) {

                                intentBroadcast = new Intent(
                                        BroadCastUtils.bcrActionReloadPeopleOwingMe);

                            } else if (contactType.equals(ContactUtils.KEY_CONTACT_TYPE_PEOPLE_I_OWE)) {

                                intentBroadcast = new Intent(
                                        BroadCastUtils.bcrActionReloadPeopleIOwe);
                            }

                            requireActivity().sendBroadcast(intentBroadcast); // Send broadcast

                        } finally {

                            dismiss(); // Dismiss dialog
                        }
                    } else {
                        // Error adding contact

                        String errorMessage = jsonObject.getString(
                                VolleyUtils.KEY_ERROR_MESSAGE);

                        // Toast error message
                        CustomToast.errorMessage(
                                mContext,
                                errorMessage,
                                R.drawable.ic_baseline_person_add_alt_1_24_white);

                        // Cancel Pending Request
                        ApplicationClass.getClassInstance().cancelPendingRequests(
                                NetworkTags.ContactsNetworkTags.TAG_ADD_CONTACT_STRING_REQUEST);
                    }
                } catch (Exception ignored) {
                }
            }, volleyError -> {

                // Log Response
                // Log.e(TAG, "Add contact response error : "
                //      + volleyError.getMessage());

                ViewsUtils.dismissProgressDialog(progressDialog); // Hide Dialog

                // Check request response
                if (volleyError.getMessage() == null || volleyError instanceof NetworkError
                        || volleyError instanceof ServerError || volleyError instanceof
                        AuthFailureError || volleyError instanceof TimeoutError) {

                    CustomToast.errorMessage(mContext, DataUtils.getStringResource(mContext,
                            R.string.error_network_connection_error_message_short),
                            R.drawable.ic_sad_cloud_100px_white);

                } else {

                    // Toast Connection Error Message
                    CustomToast.errorMessage(mContext, volleyError.getMessage(),
                            R.drawable.ic_sad_cloud_100px_white);
                }

                // Clear url cache
                ApplicationClass.getClassInstance().deleteUrlVolleyCache(
                        NetworkUrls.ContactURLS.URL_ADD_CONTACT);
            }) {
                @Override
                protected void deliverResponse(String response) {
                    super.deliverResponse(response);
                }

//                @Override
//                public Map<String, String> getHeaders() {
//                    HashMap<String, String> headers = new HashMap<>();
//                    headers.put("Content-Type", "application/json");
//                    // headers.put(VolleyUtils.KEY_API_KEY, VolleyUtils.getApiKey(mContext));
//                    return headers;
//                }

                @Override
                protected Map<String, String> getParams() {
                    @SuppressWarnings({"unchecked", "rawtypes"}) Map<String, String> params =
                            new HashMap();

                    // Put userId to Map params
                    params.put(UserAccountUtils.FIELD_USER_ID, userId);

                    // Put contact info to Map params
                    params.put(ContactUtils.FIELD_CONTACT_FULL_NAME, contactsFullName);
                    params.put(ContactUtils.FIELD_CONTACT_PHONE_NUMBER, contactsPhoneNumber);

                    // Put contact type
                    params.put(ContactUtils.FIELD_CONTACT_TYPE, contactType);

                    // Check for set email address
                    if (!DataUtils.isEmptyEditText(editEmailAddress)) {

                        params.put(ContactUtils.FIELD_CONTACT_EMAIL_ADDRESS, contactsEmailAddress);
                    }

                    // Check for set contact address
                    if (!DataUtils.isEmptyEditText(editContactAddress)) {

                        params.put(ContactUtils.FIELD_CONTACT_ADDRESS, contactsAddress);
                    }

                    // Log.e(TAG, params.toString());
                    return params; // Return params
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    return super.parseNetworkError(volleyError);
                }

                @Override
                public void deliverError(VolleyError error) {
                    super.deliverError(error);
                }
            };

            // Set Request Priority
            ApplicationClass.getClassInstance().setPriority(Request.Priority.HIGH);

            // Set retry policy
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(

                    DataUtils.getIntegerResource(mContext,
                            R.integer.int_volley_account_request_initial_timeout_ms),
                    DataUtils.getIntegerResource(mContext,
                            R.integer.int_volley_account_request_max_timeout_retry),
                    1.0f));

            // Set request caching to false
            stringRequest.setShouldCache(false);

            // Adding request to request queue
            ApplicationClass.getClassInstance().addToRequestQueue(stringRequest,
                    NetworkTags.ContactsNetworkTags.TAG_ADD_CONTACT_STRING_REQUEST);

        } else {

            // Toast network connection message
            CustomToast.errorMessage(
                    mContext,
                    DataUtils.getStringResource(mContext,
                            R.string.error_network_connection_error_message_long),
                    R.drawable.ic_sad_cloud_100px_white);
        }
    }
}
