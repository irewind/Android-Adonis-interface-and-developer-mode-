package com.irewind.activities;

import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.irewind.R;
import com.irewind.listeners.OrientationManager;
import com.irewind.utils.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRFullScreenMovieActivity extends IRBaseActivity implements OrientationManager.OrientationListener {

    private String videoUrl;
    @InjectView(R.id.videoView)
    VideoView videoView;
    private Handler mHandler;
    private Runnable mRunnable;
    private OrientationManager orientationListener;

    @Override
    public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
        switch(screenOrientation){
            case PORTRAIT:
            case REVERSED_PORTRAIT:
                Log.d("Sensor", "landscape");
                try {
                    mHandler.postDelayed(mRunnable, 800);
                } catch (Exception e){

                }
                break;
            case REVERSED_LANDSCAPE:
            case LANDSCAPE:
                Log.d("Sensor", "portrait");
                try {
                    mHandler.removeCallbacks(mRunnable);
                } catch (Exception e){

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
                finish();
            }
        };

        orientationListener = new OrientationManager(this, SensorManager.SENSOR_DELAY_NORMAL, this);

        videoUrl = getIntent().getStringExtra("video");

        if (videoUrl != null && videoUrl.length() > 0) {
//            videoView.setVideoURI(Uri.parse(videoURI));
            // Start the MediaController
            try {
                MediaController mediacontroller = new MediaController(this);
                mediacontroller.setAnchorView(videoView);
                // Get the URL from String VideoURL
                videoView.setMediaController(mediacontroller);
                videoView.setVideoURI(Uri.parse(videoUrl));
                videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        videoView.start();
                    }
                });
                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Toast.makeText(IRFullScreenMovieActivity.this, "Player-ul nu se poate initializa", Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
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
        if (videoView != null){
            videoView.stopPlayback();
            videoView.clearAnimation();
            videoView = null;
        }
    }
}
