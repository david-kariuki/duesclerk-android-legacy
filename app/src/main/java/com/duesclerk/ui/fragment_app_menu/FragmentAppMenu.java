package com.duesclerk.ui.fragment_app_menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.duesclerk.R;
import com.duesclerk.activities.AccountSettings;
import com.duesclerk.activities.ClientProfileActivity;
import com.duesclerk.custom.custom_utilities.ViewsUtils;
import com.duesclerk.custom.custom_views.dialog_fragments.bottom_sheets.LogoutFragment;

public class FragmentAppMenu extends Fragment {

    private LogoutFragment logoutFragment;

    public static FragmentAppMenu newInstance() {
        return new FragmentAppMenu();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_app_menu, container, false);

        Context mContext = requireActivity();
        LinearLayout llViewProfile = view.findViewById(R.id.llAppMenu_ViewProfile);
        LinearLayout llAccountSettings = view.findViewById(R.id.llAppMenu_AccountSettings);
        LinearLayout llHelpCentre = view.findViewById(R.id.llAppMenu_HelpCentre);
        LinearLayout llFeedback = view.findViewById(R.id.llAppMenu_Feedback);
        LinearLayout llAbout = view.findViewById(R.id.llAppMenu_About);
        LinearLayout llSettings = view.findViewById(R.id.llAppMenu_Settings);
        LinearLayout llLogOut = view.findViewById(R.id.llAppMenu_Logout);

        // Initialize logout fragment
        logoutFragment = new LogoutFragment(mContext);
        logoutFragment.setRetainInstance(true);
        logoutFragment.setCancelable(true);

        // List options onClick
        llViewProfile.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), ClientProfileActivity.class))
        );

        // Account settings onClick
        llAccountSettings.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), AccountSettings.class))
        );

        // Feedback settings onClick
        llFeedback.setOnClickListener(v -> {
        });

        // Log out onClick
        llLogOut.setOnClickListener(v -> ViewsUtils.showBottomSheetDialogFragment(
                getParentFragmentManager(),
                logoutFragment,
                true));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModel_FragmentAppMenu mViewModel =
                new ViewModelProvider(this).get(ViewModel_FragmentAppMenu.class);

    }

}