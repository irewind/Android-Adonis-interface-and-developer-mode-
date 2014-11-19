package com.irewind.fragments.movies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.irewind.R;

public class IRRelatedFragment extends Fragment {

    public static IRRelatedFragment newInstance() {
        IRRelatedFragment fragment = new IRRelatedFragment();
        return fragment;
    }

    public IRRelatedFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irrelated, container, false);
    }
}
