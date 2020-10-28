package com.duesclerk.ui.peopleowingme;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duesclerk.R;

public class PeopleOwingMeFragment extends Fragment {

    private ViewModel_PeopleOwingMe mViewModel;

    public static PeopleOwingMeFragment newInstance() {
        return new PeopleOwingMeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_people_owing_me, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ViewModel_PeopleOwingMe.class);
        // TODO: Use the ViewModel
    }

}