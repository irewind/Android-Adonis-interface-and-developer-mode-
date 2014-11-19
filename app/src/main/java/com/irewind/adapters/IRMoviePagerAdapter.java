package com.irewind.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.irewind.fragments.movies.IRAboutFragment;
import com.irewind.fragments.movies.IRCommentsFragment;
import com.irewind.fragments.movies.IRRelatedFragment;
import com.irewind.ui.views.NonSwipeableViewPager;

public class IRMoviePagerAdapter extends FragmentPagerAdapter {

    private NonSwipeableViewPager mJazzyViewPager;

    public IRMoviePagerAdapter(FragmentManager fm, NonSwipeableViewPager jazzy) {
        super(fm);
        this.mJazzyViewPager = jazzy;
    }

    @Override
    public Fragment getItem(int pos) {
        Fragment fragment;
        switch (pos) {
            case 0:
                fragment = IRAboutFragment.newInstance();
                break;
            case 1:
                fragment = IRRelatedFragment.newInstance();
                break;
            case 2:
                fragment = IRCommentsFragment.newInstance();
                break;
            default:
                fragment = IRAboutFragment.newInstance();
                break;
        }
        mJazzyViewPager.setObjectForPosition(fragment, pos);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}