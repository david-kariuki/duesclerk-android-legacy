package com.duesclerk.ui.fragment_people_i_owe;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duesclerk.R;

public class FragmentPeople_I_Owe extends Fragment {

    private ViewModel_People_I_Owe mViewModel;

    public static FragmentPeople_I_Owe newInstance() {
        return new FragmentPeople_I_Owe();
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
    }

}