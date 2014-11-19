package com.irewind.fragments.movies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.irewind.R;


public class IRAboutFragment extends Fragment {

    public static IRAboutFragment newInstance() {
        IRAboutFragment fragment = new IRAboutFragment();
        return fragment;
    }

    public IRAboutFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irabout, container, false);
    }
}
