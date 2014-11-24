package com.irewind.activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.irewind.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRFullScreenMovieActivity extends IRBaseActivity {

    private String videoUrl;
    @InjectView(R.id.videoView)
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_irfull_screen_movie);

        ButterKnife.inject(this);

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
}
