package com.irewind.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.irewind.R;
import com.irewind.activities.IRTabActivity;

public class IRMoreTermsFragment extends Fragment {

    public static IRMoreTermsFragment newInstance() {
        IRMoreTermsFragment fragment = new IRMoreTermsFragment();
        return fragment;
    }

    public IRMoreTermsFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irmore_terms, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        IRTabActivity.abBack.setVisibility(View.VISIBLE);
        IRTabActivity.abBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IRTabActivity.mMoreFragment = IRMoreFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
                ft.replace(R.id.container, IRTabActivity.mMoreFragment)
                        .disallowAddToBackStack()
                        .commit();
            }
        });
        IRTabActivity.abTitle.setText(getString(R.string.term_cond));
        IRTabActivity.abSearch.setVisibility(View.GONE);
    }
}
