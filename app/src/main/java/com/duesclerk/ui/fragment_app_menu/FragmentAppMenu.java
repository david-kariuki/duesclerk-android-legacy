package com.duesclerk.ui.fragment_app_menu;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.duesclerk.R;
import com.duesclerk.activities.ClientProfileActivity;

public class FragmentAppMenu extends Fragment {

    private ViewModel_FragmentAppMenu mViewModel;

    public static FragmentAppMenu newInstance() {
        return new FragmentAppMenu();
    }

    private Context mContext;
    private LinearLayout llViewProfile, llAccountSettings, llHelpCentre, llFeedback, llAbout,
            llSettings, llLogOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_app_menu, container, false);

        mContext = requireActivity();
        llViewProfile = view.findViewById(R.id.llAppMenu_ViewProfile);
        llAccountSettings = view.findViewById(R.id.llAppMenu_AccountSettings);
        llHelpCentre = view.findViewById(R.id.llAppMenu_HelpCentre);
        llFeedback = view.findViewById(R.id.llAppMenu_Feedback);
        llAbout = view.findViewById(R.id.llAppMenu_About);
        llSettings = view.findViewById(R.id.llAppMenu_Settings);
        llLogOut = view.findViewById(R.id.llAppMenu_Logout);

        // List options onClick
        llViewProfile.setOnClickListener(v -> startActivity(new Intent(requireActivity(),
                ClientProfileActivity.class)));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ViewModel_FragmentAppMenu.class);

    }

}