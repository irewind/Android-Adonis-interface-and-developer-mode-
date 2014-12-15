package com.irewind.activities;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;

import com.irewind.R;
import com.irewind.fragments.VideoPlayerFragment;
import com.irewind.listeners.OrientationManager;
import com.irewind.utils.Log;

import butterknife.ButterKnife;

public class IRFullScreenMovieActivity extends IRBaseActivity implements OrientationManager.OrientationListener {

    public final static String EXTRA_VIDEO_URI = "videoURI";
    public final static String EXTRA_VIDEO_THUMBNAIL_URI = "videoThumbnailURI";

    private VideoPlayerFragment videoPlayerFragment;

    private String videoURI;
    private String videoThumbnailURI;

    private Handler mHandler;
    private Runnable mRunnable;
    private OrientationManager orientationListener;

    @Override
    public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
        switch (screenOrientation) {
            case PORTRAIT:
            case REVERSED_PORTRAIT:
                Log.d("Sensor", "landscape");
                try {
                    mHandler.postDelayed(mRunnable, 800);
                } catch (Exception e) {

                }
                break;
            case REVERSED_LANDSCAPE:
            case LANDSCAPE:
                Log.d("Sensor", "portrait");
                try {
                    mHandler.removeCallbacks(mRunnable);
                } catch (Exception e) {

                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_irfull_screen_movie);

        ButterKnife.inject(this);

        mHandler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent data = new Intent();
                data.putExtra("video_pos", videoPlayerFragment.getVideoPosition());
                if (videoPlayerFragment.isPlaying()) {
                    setResult(RESULT_OK, data);
                }
                finish();
            }
        };

        orientationListener = new OrientationManager(this, SensorManager.SENSOR_DELAY_NORMAL, this);

        videoURI = getIntent().getStringExtra(EXTRA_VIDEO_URI);
        videoThumbnailURI = getIntent().getStringExtra(EXTRA_VIDEO_THUMBNAIL_URI);

        videoPlayerFragment = (VideoPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.player_fragment);
        videoPlayerFragment.setVideoURI(videoURI);
        videoPlayerFragment.setVideoThumbnailURI(videoThumbnailURI);
        videoPlayerFragment.autoplay = true;
        videoPlayerFragment.startPosition = getIntent().getIntExtra("video_pos", 0);
        Log.d("VIDEO_POS", getIntent().getIntExtra("video_pos", 0) + "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        orientationListener.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationListener.disable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayerFragment.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent data = new Intent();
        data.putExtra("video_pos", videoPlayerFragment.getVideoPosition());
        setResult(RESULT_OK, data);
        finish();
    }
}
