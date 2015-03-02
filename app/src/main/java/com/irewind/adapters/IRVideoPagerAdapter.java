package com.irewind.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.irewind.fragments.movies.IRAboutFragment;
import com.irewind.fragments.movies.IRCommentsFragment;
import com.irewind.fragments.movies.IRRelatedFragment;
import com.irewind.sdk.model.User;
import com.irewind.sdk.model.Video;
import com.irewind.ui.views.NonSwipeableViewPager;

public class IRVideoPagerAdapter extends FragmentPagerAdapter {

    private NonSwipeableViewPager mJazzyViewPager;
    private Video video;

    public IRVideoPagerAdapter(FragmentManager fm, NonSwipeableViewPager jazzy, Video video) {
        super(fm);
        this.mJazzyViewPager = jazzy;
        this.video = video;
    }

    @Override
    public Fragment getItem(int pos) {
        Fragment fragment;
        switch (pos) {
            case 0:
                fragment = IRAboutFragment.newInstance();
                ((IRAboutFragment)fragment).video = video;
                break;
            case 1:
                fragment = IRRelatedFragment.newInstance();
                ((IRRelatedFragment)fragment).video = video;
                break;
            case 2:
                fragment = IRCommentsFragment.newInstance();
                ((IRCommentsFragment)fragment).video = video;
                break;
            default:
                fragment = IRAboutFragment.newInstance();
                ((IRAboutFragment)fragment).video = video;
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
