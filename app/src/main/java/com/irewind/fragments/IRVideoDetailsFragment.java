package com.irewind.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRFullScreenMovieActivity;
import com.irewind.activities.IRMovieActivity;
import com.irewind.activities.IRTabActivity;
import com.irewind.adapters.IRVideoPagerAdapter;
import com.irewind.listeners.OrientationManager;
import com.irewind.sdk.model.User;
import com.irewind.sdk.model.Video;
import com.irewind.ui.views.NonSwipeableViewPager;
import com.irewind.utils.Log;
import com.jazzyviewpager.JazzyViewPager;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRVideoDetailsFragment extends Fragment implements View.OnClickListener, OrientationManager.OrientationListener {

    private VideoPlayerFragment videoPlayerFragment;

    @InjectView(R.id.viewPager)
    NonSwipeableViewPager mViewPager;
    @InjectView(R.id.about)
    Button btnAbout;
    @InjectView(R.id.related)
    Button btnRelated;
    @InjectView(R.id.comments)
    Button btnComments;

    public Video video;
    public User person;

    private SensorManager sensorManager;
    private SensorEventListener sensorEvent;

    private Handler mHandler;
    private Runnable mRunnable;

    private OrientationManager orientationManager;

    public static IRVideoDetailsFragment newInstance() {
        IRVideoDetailsFragment fragment = new IRVideoDetailsFragment();
        return fragment;
    }

    public IRVideoDetailsFragment() {
        // Required empty public constructor
    }

    private int resumePosition;

    @Override
    public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
        switch (screenOrientation) {
            case PORTRAIT:
            case REVERSED_PORTRAIT:
                Log.d("Sensor", "portrait");
                mHandler.removeCallbacks(mRunnable);
                break;
            case REVERSED_LANDSCAPE:
            case LANDSCAPE:
                Log.d("Sensor", "landscape");
                mHandler.postDelayed(mRunnable, 800);
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorEvent = new SensorEventListener() {
            int orientation = -1;
            ;

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[1] < 6.5 && event.values[1] > -6.5) {
                    if (orientation != 1) {
                        Log.d("Sensor", "Landscape");
                    }
                    orientation = 1;
                } else {
                    if (orientation != 0) {
                        Log.d("Sensor", "Portrait");
                    }
                    orientation = 0;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        };

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity != null && !activity.isFinishing()) {
                    Intent intent = new Intent(getActivity(), IRFullScreenMovieActivity.class);
                    if (video != null) {
                        intent.putExtra(IRFullScreenMovieActivity.EXTRA_VIDEO_URI, video.getMp4HighResolutionURL());
                        intent.putExtra(IRFullScreenMovieActivity.EXTRA_VIDEO_THUMBNAIL_URI, video.getThumbnail());
                    }
                    if (videoPlayerFragment.isPlaying()) {
                        intent.putExtra("video_pos", videoPlayerFragment.getVideoPosition());
                    }
                    startActivityForResult(intent, 100);
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK){
            resumePosition = data.getIntExtra("video_pos", 0);
            if (videoPlayerFragment != null) {
                videoPlayerFragment.autoplay = true;
                videoPlayerFragment.startPosition = resumePosition;
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        orientationManager = new OrientationManager(getActivity(), SensorManager.SENSOR_DELAY_NORMAL, this);
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

        videoPlayerFragment = (VideoPlayerFragment) getChildFragmentManager().findFragmentById(R.id.player_fragment);
        if (video != null) {
            videoPlayerFragment.setVideoURI(video.getMp4HighResolutionURL());
            videoPlayerFragment.setVideoThumbnailURI(video.getThumbnail());
        }

        btnAbout.setOnClickListener(this);
        btnRelated.setOnClickListener(this);
        btnComments.setOnClickListener(this);

        setupViewPager(JazzyViewPager.TransitionEffect.Standard);
        setupButtons(0);

        getView().setFocusableInTouchMode(true);

        getView().setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    stopPlayback();
                    return true;
                }
                return false;
            }
        } );
    }

    @Override
    public void onResume() {
        super.onResume();
        IRMovieActivity.abBack.setVisibility(View.VISIBLE);
        IRMovieActivity.abBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                stopPlayback();
            }
        });
        IRMovieActivity.abTitle.setText(getString(R.string.movies));
        IRMovieActivity.abSearch.setVisibility(View.GONE);
        IRMovieActivity.abAction.setVisibility(View.GONE);

        setSensorManager();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeSensorManager();
    }

    private void setSensorManager() {
        orientationManager.enable();
    }

    private void removeSensorManager() {
        orientationManager.disable();
    }

    private void setupViewPager(JazzyViewPager.TransitionEffect effect) {
        mViewPager.setTransitionEffect(effect);
        mViewPager.setAdapter(new IRVideoPagerAdapter(getChildFragmentManager(), mViewPager, video));
        mViewPager.setPageMargin(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                setupButtons(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
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

    private void setupButtons(int position) {
        switch (position) {
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
