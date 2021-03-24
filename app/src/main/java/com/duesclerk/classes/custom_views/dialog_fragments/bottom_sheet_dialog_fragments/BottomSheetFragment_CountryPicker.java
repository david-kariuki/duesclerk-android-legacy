package com.duesclerk.classes.custom_views.dialog_fragments.bottom_sheet_dialog_fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duesclerk.R;
import com.duesclerk.activities.UserProfileActivity;
import com.duesclerk.classes.custom_utilities.application.ViewsUtils;
import com.duesclerk.classes.custom_utilities.user_data.DataUtils;
import com.duesclerk.classes.custom_utilities.user_data.UserAccountUtils;
import com.duesclerk.classes.custom_views.recycler_view_adapters.RVLA_CountryPicker;
import com.duesclerk.classes.custom_views.view_decorators.Decorators;
import com.duesclerk.classes.java_beans.JB_CountryData;
import com.duesclerk.interfaces.Interface_CountryPicker;
import com.duesclerk.ui.fragment_signup.FragmentSignup;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings({"rawtypes"})
@SuppressLint("ValidFragment")
public class BottomSheetFragment_CountryPicker extends BottomSheetDialogFragment {

    private final Context mContext;
    private final Interface_CountryPicker interfaceCountryPicker;
    private final Activity activity;
    private BottomSheetBehavior bottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private ArrayList<JB_CountryData> countryListArray;

    /**
     * Constructor for UserProfileActivity
     *
     * @param userProfileActivity - UserProfileActivity
     */
    public BottomSheetFragment_CountryPicker(UserProfileActivity userProfileActivity) {

        this.mContext = userProfileActivity.getApplicationContext();
        this.interfaceCountryPicker = userProfileActivity;
        this.activity = userProfileActivity;
    }

    /**
     * Constructor for Personal signup fragment
     *
     * @param fragmentSignup - Personal signup fragment
     */
    public BottomSheetFragment_CountryPicker(FragmentSignup fragmentSignup) {

        this.mContext = fragmentSignup.requireActivity();
        this.interfaceCountryPicker = fragmentSignup;
        this.activity = fragmentSignup.requireActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final BottomSheetDialog bottomSheetDialog = (BottomSheetDialog)
                super.onCreateDialog(savedInstanceState);

        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_country_picker,
                null);
        TextView textClose = contentView.findViewById(R.id.textCountryPicker_Dismiss);
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerViewCountryPicker);
        SearchView searchView = contentView.findViewById(R.id.searchViewCountryPicker);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                RecyclerView.VERTICAL,
                false);

        // Initialize And Set Item Decorator
        Decorators decoratorCountryPicker = new Decorators(
                BottomSheetFragment_CountryPicker.this);
        recyclerView.addItemDecoration(decoratorCountryPicker);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());

        // Get Country Data Array From Xml Parser
        try {

            countryListArray = new ArrayList<>();
            JB_CountryData pojoCountryData = null;

            InputStream inputStream = requireActivity().getAssets()
                    .open("country_data.xml");
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();

            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            String key, tagValue = "";
            int event = xmlPullParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                // Get Parser Name
                key = xmlPullParser.getName();

                switch (event) {

                    case XmlPullParser.START_TAG:

                        if (key.equals(UserAccountUtils.KEY_COUNTRY_ITEM)) {
                            pojoCountryData = new JB_CountryData(); // Initialize POJO
                        }
                        break;

                    case XmlPullParser.TEXT:

                        // Get Parser Text
                        tagValue = xmlPullParser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                        // Set Country Name
                        if (key.equalsIgnoreCase(UserAccountUtils.FIELD_COUNTRY_NAME)) {

                            // Set Country Code
                            Objects.requireNonNull(pojoCountryData).setCountryName(tagValue);

                        } else if (key.equalsIgnoreCase(UserAccountUtils.FIELD_COUNTRY_CODE)) {

                            // Set Country Alpha2
                            Objects.requireNonNull(pojoCountryData).setCountryCode(tagValue);

                        } else if (key.equalsIgnoreCase(UserAccountUtils.FIELD_COUNTRY_ALPHA2)) {

                            // Set Country Alpha3
                            Objects.requireNonNull(pojoCountryData).setCountryAlpha2(tagValue);

                        } else if (key.equalsIgnoreCase(UserAccountUtils.FIELD_COUNTRY_ALPHA3)) {

                            // Set Country Flag without file extension
                            Objects.requireNonNull(pojoCountryData).setCountryAlpha3(tagValue);

                        } else if (key.equalsIgnoreCase(UserAccountUtils.FIELD_COUNTRY_FLAG)) {

                            Objects.requireNonNull(pojoCountryData).setCountryFlag(
                                    tagValue.replace(".png", ""));

                        } else if (key.equalsIgnoreCase(UserAccountUtils.KEY_COUNTRY_ITEM)) {

                            if (pojoCountryData != null) {

                                // Add JavaBean to ArrayList
                                countryListArray.add(pojoCountryData);
                            }
                        }
                        break;

                    default:
                        break;
                }

                event = xmlPullParser.next(); // Move to next
            }

            inputStream.close(); // Close input stream

        } catch (IOException | XmlPullParserException ignored) {
        }

        // RecyclerView adapter
        RVLA_CountryPicker rvlaCountryPicker = new RVLA_CountryPicker(this,
                getExtractedCountryData());

        // Check for adapter observers
        if (!rvlaCountryPicker.hasObservers()) {

            rvlaCountryPicker.setHasStableIds(true); // Set has stable ids
        }

        recyclerView.setAdapter(rvlaCountryPicker);

        // Get SearchView id
        int searchViewId = searchView.getContext().getResources().getIdentifier(
                "android:id/search_src_text", null, null);

        // Get SearchView text
        TextView textView = searchView.findViewById(searchViewId);

        // Set SearchView text color
        textView.setTextColor(DataUtils.getColorResource(mContext, R.color.colorBlack));

        // Disable iconified
        searchView.setIconifiedByDefault(false);

        // Focus SearchView and show keyboard
        ViewsUtils.showKeyboardWithFocus(requireActivity(), searchView);

        // Add query text listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String arg0) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // Filter Text Input
                rvlaCountryPicker.getFilter().filter(query);
                return false;
            }
        });

        // Dismiss Dialog
        textClose.setOnClickListener(v -> dismiss());

        // Set BottomSheet callback
        bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };

        // Set owner activity
        bottomSheetDialog.setOwnerActivity(this.activity);

        // Remove window title
        bottomSheetDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Set transparent background
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set Custom View To Dialog
        bottomSheetDialog.setContentView(contentView);

        // Set BottomSheet behaviour from view
        bottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        return bottomSheetDialog;
    }

    /**
     * Function to get all country data as ArrayList<JB_CountryData>
     */
    private ArrayList<JB_CountryData> getExtractedCountryData() {
        ArrayList<JB_CountryData> countryDataArray = new ArrayList<>();
        JB_CountryData pojoCountryData;

        for (int i = 0; i < countryListArray.size(); i++) {
            pojoCountryData = new JB_CountryData();
            pojoCountryData.setCountryName(countryListArray.get(i).getCountryName());
            pojoCountryData.setCountryCode(countryListArray.get(i).getCountryCode());
            pojoCountryData.setCountryAlpha2(countryListArray.get(i).getCountryAlpha2());
            pojoCountryData.setCountryAlpha3(countryListArray.get(i).getCountryAlpha3());
            pojoCountryData.setCountryFlag(countryListArray.get(i).getCountryFlag());

            // Add POJO to StringArray
            countryDataArray.add(pojoCountryData);
        }
        return countryDataArray;
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback); // Remove CallBack
    }

    public void passSelectedCountryDataToInterface(String countryName, String countryCode,
                                                   String countryCodeWithCountryName,
                                                   String countryAlpha2, String countryAlpha3,
                                                   int countryFlagId) {
        // Pass country data to interface
        if (!DataUtils.isEmptyString(countryAlpha2)) {
            dismiss(); // Dismiss dialog

            // Pass country data to interface
            interfaceCountryPicker.passCountryName(countryName);
            interfaceCountryPicker.passCountryCode(countryCode);
            interfaceCountryPicker.passCountryCodeWithCountryName(countryCodeWithCountryName);
            interfaceCountryPicker.passCountryAlpha2(countryAlpha2);
            interfaceCountryPicker.passCountryAlpha3(countryAlpha3);
            interfaceCountryPicker.passCountryFlag(countryFlagId);
        }
    }
}
