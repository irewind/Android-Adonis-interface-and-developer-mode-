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
import android.widget.FrameLayout;

import com.irewind.Injector;
import com.irewind.R;
import com.irewind.adapters.IRVideoPagerAdapter;
import com.irewind.sdk.model.Video;
import com.irewind.ui.views.NonSwipeableViewPager;
import com.irewind.utils.Constants;
import com.irewind.utils.exoplayer.DemoUtil;
import com.jazzyviewpager.JazzyViewPager;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRVideoDetailsFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    VideoPlayerFragment videoPlayerFragment;
    VideoExoPlayerFragment videoExoPlayerFragment;

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
    private int playerType;
    private int layoutId;
    private View rootView;
    private FrameLayout frameLayout;

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

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 17) {
            playerType = Constants.TYPE_EXO;
            layoutId = R.layout.fragment_irvideo_details_exoplayer;
        } else {
            playerType = Constants.TYPE_NORMAL;
            layoutId = R.layout.fragment_irvideo_details;
        }

        rootView = inflater.inflate(layoutId, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        btnAbout.setOnClickListener(this);
        btnRelated.setOnClickListener(this);
        btnComments.setOnClickListener(this);

        setupVideoPlayer(playerType);
        updatePaneButtons();
        setupViewPager(JazzyViewPager.TransitionEffect.Standard);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            if (videoPlayerFragment != null) {
                videoPlayerFragment.getView().getLayoutParams().height = (int) getResources().getDimension(R.dimen.video_dim);
            } else {

                if (frameLayout != null) {
                    frameLayout.getLayoutParams().height = (int) getResources().getDimension(R.dimen.video_dim);
                }

            }

            tabs.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (videoPlayerFragment != null) {
                videoPlayerFragment.getView().getLayoutParams().height = ActionBar.LayoutParams.MATCH_PARENT;
            } else {
                if (frameLayout != null) {
                    frameLayout.getLayoutParams().height = ActionBar.LayoutParams.MATCH_PARENT;
                }


            }

            tabs.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
        }
    }

    private void setupVideoPlayer(int type) {

        switch (type) {
            case 1:
                videoPlayerFragment = (VideoPlayerFragment) getChildFragmentManager().findFragmentById(R.id.player_fragment);
                if (video != null) {
                    videoPlayerFragment.setVideoId(video.getId());
                    // videoPlayerFragment.setVideoURI(video.getMp4HighResolutionURL());
                    videoPlayerFragment.setVideoURI("http://player.vimeo.com/external/111527832.sd.mp4?s=1645e3111ac746d9cbf3a4f7e6ef357a");
                    videoPlayerFragment.setVideoThumbnailURI(video.getThumbnail());
                    videoPlayerFragment.startPosition = startPosition;
                }
                break;
            case 2:
                Bundle bundle = new Bundle();
                bundle.putString("contentUri", video.getMp4HighResolutionURL());

                bundle.putInt("contentType", DemoUtil.TYPE_DASH);
                bundle.putString("contentId", "" + video.getId());
                bundle.putString("url_thumbnail", "" + video.getThumbnail());
                frameLayout = (FrameLayout) rootView.findViewById(R.id.player_fragment);

                videoExoPlayerFragment = VideoExoPlayerFragment.newInstance(video.getMp4HighResolutionURL(), DemoUtil.TYPE_OTHER, "" + video.getId(), video.getThumbnail());
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .disallowAddToBackStack()
                        .replace(R.id.player_fragment, videoExoPlayerFragment)
                        .commit();
                // videoExoPlayerFragment = (VideoExoPlayerFragment) getChildFragmentManager().findFragmentById(R.id.player_fragment);

                break;
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
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        selectedPaneIndex = i;
        updatePaneButtons();
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

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
        if (videoPlayerFragment != null) {
            videoPlayerFragment.stop();
        }
    }
}
