package com.irewind.fragments;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.player.SeekBarV3Fragment;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.VideoViewCountUpdateEvent;
import com.irewind.utils.AppStatus;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class VideoPlayerFragment extends Fragment implements View.OnClickListener {
    @InjectView(R.id.videoView)
    VideoView videoView;
    @InjectView(R.id.imageViewPlay)
    ImageView playPause;
    @InjectView(R.id.seekBarLayout)
    LinearLayout seekBarLayout;
    @InjectView(R.id.videoProgress)
    CircularProgressBar progressBarVideo;
    @InjectView(R.id.playerLayout)
    RelativeLayout playerLayout;
    @InjectView(R.id.placeholderLayout)
    RelativeLayout placeholderLayout;
    @InjectView(R.id.videoPlaceholder)
    ImageView videoPlaceholder;

    private SeekBarV3Fragment seekBar;
    private int fadeTime = 3000;
    boolean isPlaying = false;
    boolean autoPause;

    public long videoId;
    public String videoURI;
    public String videoThumbnailURI;
    public boolean autoplay = false;
    public int startPosition;

    @Inject
    ApiClient apiClient;

    CountDownTimer ct = new CountDownTimer(20000000, 500) {

        @Override
        public void onTick(long millisUntilFinished) {
            if (videoView != null) {
                seekBar.setTime(videoView.getCurrentPosition(), videoView.getDuration());
                if (fadeTime > 0) {
                    fadeTime -= 500;
                }
                try {
                    if (fadeTime <= 0 && playPause.getAlpha() == 1) hide();
                } catch (NoSuchMethodError e) {
                    if (fadeTime <= 0) hide();
                }
            }
        }

        @Override
        public void onFinish() {
        }

    };

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public String getVideoURI() {
        return videoURI;
    }

    public void setVideoURI(String videoURI) {
        this.videoURI = videoURI;
    }

    public String getVideoThumbnailURI() {
        return videoThumbnailURI;
    }

    public void setVideoThumbnailURI(String videoThumbnailURI) {
        this.videoThumbnailURI = videoThumbnailURI;
    }

    public int getVideoPosition(){
        return videoView != null ? videoView.getCurrentPosition():0;
    }

    public boolean isPlaying(){
        return videoView != null ? videoView.isPlaying() : false;
    }

    private boolean markedAsViewed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_player, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        seekBar = (SeekBarV3Fragment) getChildFragmentManager().findFragmentById(R.id.seek_fragment);
        seekBar.setOnSeekBarEventListener(new SeekBarV3Fragment.onSeekBarEvent() {

            @Override
            public void seek(double x) {

                try {
                    videoView.seekTo((int) (x * videoView.getDuration()));
                } catch (IllegalStateException e) {
                } catch (NullPointerException e) {
                }

            }
        });

        playPause.setVisibility(View.INVISIBLE);

        videoView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) hideShow();

                return true;
            }
        });

        playPause.setOnClickListener(this);

        placeholderLayout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        apiClient.getEventBus().register(this);

        ct.start();

        if (videoThumbnailURI != null && videoThumbnailURI.trim().length() > 0) {
            Picasso.with(getActivity()).load(videoThumbnailURI).into(videoPlaceholder);
        }

        if (autoplay) {
            play();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);

        if (videoView != null) {
            videoView.pause();
            playPause.setImageResource(R.drawable.play);
            autoPause = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stop();
    }

    public void play() {
        placeholderLayout.setVisibility(View.INVISIBLE);
        playerLayout.setVisibility(View.VISIBLE);

        if (videoURI != null && videoURI.length() > 0) {
//            videoView.setVideoURI(Uri.parse(videoURI));
            // Start the MediaController
            try {
                MediaController mediacontroller = new MediaController(
                        getActivity());
                mediacontroller.setAnchorView(videoView);
                mediacontroller.setVisibility(View.INVISIBLE);
                // Get the URL from String VideoURL
                videoView.setMediaController(mediacontroller);
                videoView.setVideoURI(Uri.parse(videoURI));
                videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        videoView.start();
                        playPause.setImageResource(R.drawable.pause);
                        isPlaying = true;
                        progressBarVideo.setVisibility(View.INVISIBLE);
                        playPause.setVisibility(View.VISIBLE);
                        videoView.seekTo(startPosition);

                        if (!markedAsViewed && startPosition == 0 && videoId > 0) {
                            apiClient.increaseViewCount(videoId);
                        }
                    }
                });
                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Toast.makeText(getActivity(), "Player-ul nu se poate initializa", Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    public void stop() {
        try {
            if (videoView != null) {
                ct.cancel();
                videoView.stopPlayback();
                videoView.clearAnimation();
                videoView = null;
            }
        } catch (NullPointerException e) {
        }
    }

    public void hide() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(playPause, "alpha", 0f);
        animation.setDuration(500);
        animation.start();

        animation = ObjectAnimator.ofFloat(seekBarLayout, "alpha", 0f);
        animation.setDuration(500);
        animation.start();
    }

    public void show() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(playPause, "alpha", 1f);
        animation.setDuration(500);
        animation.start();

        animation = ObjectAnimator.ofFloat(seekBarLayout, "alpha", 1f);
        animation.setDuration(500);
        animation.start();
    }

    public void hideShow() {
        if (fadeTime > 0) {
            fadeTime = 0;
        } else {
            fadeTime = 5000;
            show();
            ct.cancel();
            ct.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewPlay:
                if (fadeTime > 0) {
                    isPlaying = !isPlaying;
                    if (isPlaying) {
                        playPause.setImageResource(R.drawable.pause);
                        try {
                            videoView.start();
                        } catch (IllegalStateException e) {
                        }
                        fadeTime = 1000;
                    } else {
                        playPause.setImageResource(R.drawable.play);
                        try {
                            videoView.pause();
                        } catch (IllegalStateException e) {
                        }
                        fadeTime = 5000;
                        ct.cancel();
                        ct.start();
                    }
                } else {
                    hideShow();
                }
                break;
            case R.id.placeholderLayout:
                if (!AppStatus.getInstance(getActivity()).isOnline()) {
                    Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    break;
                }
                play();
                break;
        }
    }

    // --- Events --- //

    @Subscribe
    public void onEvent(VideoViewCountUpdateEvent event) {
        markedAsViewed = true;
    }
}
