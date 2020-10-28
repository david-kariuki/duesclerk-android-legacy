package com.duesclerk.ui.people_i_owe;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duesclerk.R;

public class People_I_OweFragment extends Fragment {

    private ViewModel_People_I_Owe mViewModel;

    public static People_I_OweFragment newInstance() {
        return new People_I_OweFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_people_i_owe, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ViewModel_People_I_Owe.class);
        // TODO: Use the ViewModel
    }

}