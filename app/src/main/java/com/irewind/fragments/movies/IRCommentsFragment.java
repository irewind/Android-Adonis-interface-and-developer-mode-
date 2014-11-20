package com.irewind.fragments.movies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.irewind.R;
import com.irewind.sdk.model.Video;


public class IRCommentsFragment extends Fragment {

    public Video video;

    public static IRCommentsFragment newInstance() {
        IRCommentsFragment fragment = new IRCommentsFragment();
        return fragment;
    }

    public IRCommentsFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_ircomments, container, false);
    }
}
