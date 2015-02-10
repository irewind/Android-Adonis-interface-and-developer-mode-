package com.irewind.fragments;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.irewind.Injector;
import com.irewind.R;
import com.irewind.adapters.IRVideoPagerAdapter;
import com.irewind.sdk.model.Video;
import com.irewind.ui.views.NonSwipeableViewPager;
import com.jazzyviewpager.JazzyViewPager;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRVideoDetailsFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    VideoPlayerFragment videoPlayerFragment;

    @InjectView(R.id.tabs)
    View tabs;

    @InjectView(R.id.about)
    Button btnAbout;
    @InjectView(R.id.related)
    Button btnRelated;
    @InjectView(R.id.comments)
    Button btnComments;

    @InjectView(R.id.viewPager)
    NonSwipeableViewPager mViewPager;

    private int startPosition;
    private Video video;

    private int selectedPaneIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_irvideo_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        btnAbout.setOnClickListener(this);
        btnRelated.setOnClickListener(this);
        btnComments.setOnClickListener(this);

        setupVideoPlayer();;
        updatePaneButtons();
        setupViewPager(JazzyViewPager.TransitionEffect.Standard);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            videoPlayerFragment.getView().getLayoutParams().height = (int)getResources().getDimension(R.dimen.video_dim);

            tabs.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            videoPlayerFragment.getView().getLayoutParams().height = ActionBar.LayoutParams.MATCH_PARENT;

            tabs.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
        }
    }

    private void  setupVideoPlayer() {
        videoPlayerFragment = (VideoPlayerFragment) getChildFragmentManager().findFragmentById(R.id.player_fragment);
        if (video != null) {
            videoPlayerFragment.setVideoId(video.getId());
            videoPlayerFragment.setVideoURI(video.getMp4HighResolutionURL());
            videoPlayerFragment.setVideoThumbnailURI(video.getThumbnail());
            videoPlayerFragment.startPosition = startPosition;
        }
    }

    private void setupViewPager(JazzyViewPager.TransitionEffect effect) {
        mViewPager.setTransitionEffect(effect);
        mViewPager.setAdapter(new IRVideoPagerAdapter(getChildFragmentManager(), mViewPager, video));
        mViewPager.setPageMargin(0);
        mViewPager.setOnPageChangeListener(this);
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about:
                if (mViewPager.getCurrentItem() != 0) {
                    mViewPager.setCurrentItem(0, false);
                }
                break;
            case R.id.related:
                if (mViewPager.getCurrentItem() != 1) {
                    mViewPager.setCurrentItem(1, false);
                }
                break;
            case R.id.comments:
                if (mViewPager.getCurrentItem() != 2) {
                    mViewPager.setCurrentItem(2, false);
                }
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {}

    @Override
    public void onPageSelected(int i) {
        selectedPaneIndex = i;
        updatePaneButtons();
    }

    @Override
    public void onPageScrollStateChanged(int i) {}

    private void updatePaneButtons() {
        switch (selectedPaneIndex) {
            case 0:
                btnAbout.setSelected(true);
                btnRelated.setSelected(false);
                btnComments.setSelected(false);
                break;
            case 1:
                btnAbout.setSelected(false);
                btnRelated.setSelected(true);
                btnComments.setSelected(false);
                break;
            case 2:
                btnAbout.setSelected(false);
                btnRelated.setSelected(false);
                btnComments.setSelected(true);
                break;
        }
    }

    public void stopPlayback() {
        videoPlayerFragment.stop();
    }
}
