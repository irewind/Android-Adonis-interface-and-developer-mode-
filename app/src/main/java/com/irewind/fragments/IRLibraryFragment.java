package com.irewind.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.irewind.R;

public class IRLibraryFragment extends Fragment {

    public static IRLibraryFragment newInstance() {
        IRLibraryFragment fragment = new IRLibraryFragment();
        return fragment;
    }

    public IRLibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_irlibrary, container, false);
    }
}
