package com.irewind.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.irewind.Injector;
import com.irewind.R;

public class IRRewindFunctionalityFragment extends Fragment {

    public static IRRewindFunctionalityFragment newInstance() {
        IRRewindFunctionalityFragment fragment = new IRRewindFunctionalityFragment();
        return fragment;
    }

    public IRRewindFunctionalityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irrewind_functionality, container, false);
    }

}
