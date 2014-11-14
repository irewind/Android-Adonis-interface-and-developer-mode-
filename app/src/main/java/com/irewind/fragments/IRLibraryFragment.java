package com.irewind.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.irewind.R;

import butterknife.ButterKnife;
import com.jazzyviewpager.JazzyViewPager;

public class IRLibraryFragment extends Fragment {

    private JazzyViewPager mJazzyViewPager;

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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irlibrary, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mJazzyViewPager = (JazzyViewPager) view.findViewById(R.id.jazzy);
//        setupJazziness(JazzyViewPager.TransitionEffect.ZoomIn);
    }

    private void setupJazziness(JazzyViewPager.TransitionEffect effect) {
        mJazzyViewPager.setTransitionEffect(effect);
        mJazzyViewPager.setAdapter(new SlidePagerAdapter(getChildFragmentManager()));
        mJazzyViewPager.setPageMargin(0);
        mJazzyViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0){

                } else {

                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public class SlidePagerAdapter extends FragmentPagerAdapter {
        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
			/*
			 * IMPORTANT: This is the point. We create a RootFragment acting as
			 * a container for other fragments
			 */
            Fragment fragment;
            if (position == 0) {
                fragment = new IRMoreFragment().newInstance();
            } else {
                fragment = IRPeopleFragment.newInstance();
            }

            mJazzyViewPager.setObjectForPosition(fragment, position);

            return fragment;
        }


        @Override
        public int getCount() {
            return 2;
        }
    }
}
