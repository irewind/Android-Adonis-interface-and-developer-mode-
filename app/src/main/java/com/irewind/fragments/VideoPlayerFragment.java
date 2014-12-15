package com.irewind.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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

import com.irewind.Injector;
import com.irewind.R;
import com.irewind.player.SeekBarV3Fragment;
import com.irewind.utils.AppStatus;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class VideoPlayerFragment extends Fragment implements View.OnClickListener {
    @InjectView(R.id.videoView)
    VideoView videoView;
    @InjectView(R.id.imageViewPlay)
    ImageView playPause;
    @InjectView(R.id.seekBarLayout)
    LinearLayout layoutBottom;
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

    public String videoURI;
    public String videoThumbnailURI;
    public boolean autoplay = false;
    public int startPosition;

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

    public VideoPlayerFragment() {
        // Required empty public constructor
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

        ct.start();

        if (videoThumbnailURI != null) {
            Picasso.with(getActivity()).load(videoThumbnailURI).into(videoPlaceholder);
        }

        if (autoplay) {
            play();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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

    @SuppressLint("NewApi")
    public void hide() {

        try {
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) layoutBottom.getLayoutParams();
            params2.width = layoutBottom.getWidth();
            layoutBottom.setLayoutParams(params2);

            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) playPause.getLayoutParams();
            params3.setMargins((int) playPause.getX(), 0, 0, 0);
            try {
                params3.removeRule(RelativeLayout.CENTER_HORIZONTAL);
            } catch (Exception e) {

            }
            playPause.setLayoutParams(params3);


//            int mUIFlag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//
//            getActivity().getWindow().getDecorView().setSystemUiVisibility(mUIFlag);   //undocumented
//            getActivity().getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//
//                @Override
//                public void onSystemUiVisibilityChange(int visibility) {
//                    // TODO Auto-generated method stub
//                    if (visibility == 0) hideShow();
//                }
//            });
            //getWindowManager().getDefaultDisplay().getre

        } catch (NoSuchMethodError e) {
            // TODO: handle exception
        }
        try {


            ObjectAnimator animation = ObjectAnimator.ofFloat(playPause, "alpha", 0f);
            animation.setDuration(500);

            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int pixels = (int) (-layoutBottom.getHeight() * (animation.getAnimatedFraction()));

                    LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) layoutBottom.getLayoutParams();
                    params2.setMargins(0, -pixels, 0, 0);
                    layoutBottom.setLayoutParams(params2);

                }
            });

            animation.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) layoutBottom.getLayoutParams();
                    params2.setMargins(0, layoutBottom.getHeight(), 0, 0);
                    layoutBottom.setLayoutParams(params2);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // TODO Auto-generated method stub

                }
            });

            animation.start();

        } catch (NoClassDefFoundError e) {
            // TODO: handle exception
        }
    }

    public void show() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(playPause, "alpha", 1f);
        animation.setDuration(500);

        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int pixels = (int) (-layoutBottom.getHeight() * (1 - animation.getAnimatedFraction()));

                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) layoutBottom.getLayoutParams();
                params2.setMargins(0, -pixels, 0, 0);
                layoutBottom.setLayoutParams(params2);

            }
        });

        animation.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) layoutBottom.getLayoutParams();
                params2.setMargins(0, 0, 0, 0);
                layoutBottom.setLayoutParams(params2);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

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
}
