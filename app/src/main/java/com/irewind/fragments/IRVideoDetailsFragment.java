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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRTabActivity;
import com.irewind.adapters.IRVideoPagerAdapter;
import com.irewind.player.SeekBarV3Fragment;
import com.irewind.sdk.model.Video;
import com.irewind.ui.views.NonSwipeableViewPager;
import com.jazzyviewpager.JazzyViewPager;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRVideoDetailsFragment extends Fragment implements View.OnClickListener {

    @InjectView(R.id.videoView1)
    VideoView videoView;
    @InjectView(R.id.imageViewPlay)
    ImageView playPause;
    @InjectView(R.id.seekBarLayout)
    LinearLayout layoutBottom;
    @InjectView(R.id.jazzyVideo)
    NonSwipeableViewPager mJazzyViewPager;
    @InjectView(R.id.about)
    Button btnAbout;
    @InjectView(R.id.related)
    Button btnRelated;
    @InjectView(R.id.comments)
    Button btnComments;

    private SeekBarV3Fragment seekBar;
    private int fadeTime = 3000;
    boolean isPlaying = true;
    boolean autoPause;

    public Video video;

    public static CountDownTimer sCt;
    public static VideoView sVideoView;

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

    public static IRVideoDetailsFragment newInstance() {
        IRVideoDetailsFragment fragment = new IRVideoDetailsFragment();
        return fragment;
    }

    public IRVideoDetailsFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_irvideo_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        btnAbout.setOnClickListener(this);
        btnRelated.setOnClickListener(this);
        btnComments.setOnClickListener(this);

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

        String videoURI = video.getMp4HighResolutionURL();
        if (videoURI == null || videoURI.length() == 0) {
            videoURI = video.getOggHighResolutionURL();
        }
        if (videoURI == null || videoURI.length() == 0) {
            videoURI = video.getOggLowResolutionURL();
        }

        if (videoURI != null && videoURI.length() > 0) {
//            videoView.setVideoURI(Uri.parse(videoURI));
            // Start the MediaController
            try {
                MediaController mediacontroller = new MediaController(
                        getActivity());
                mediacontroller.setAnchorView(videoView);
                // Get the URL from String VideoURL
                videoView.setMediaController(mediacontroller);
                videoView.setVideoURI(Uri.parse(videoURI));
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
                        Toast.makeText(getActivity(), "Player-ul nu se poate initializa", Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }

//        videoView.start();

        videoView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) hideShow();

                return true;
            }
        });

        setupJazziness(JazzyViewPager.TransitionEffect.Standard);
        setupButtons(0);

        playPause.setOnClickListener(this);
        ct.start();

        sCt = ct;
        sVideoView = videoView;
    }

    @Override
    public void onResume() {
        super.onResume();
        IRTabActivity.abBack.setVisibility(View.VISIBLE);
        IRTabActivity.abBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView != null) {
                    ct.cancel();
                    videoView.stopPlayback();
                    videoView.clearAnimation();
                    videoView = null;
                }
                IRTabActivity.mLibraryFragment = IRLibraryFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
                ft.replace(R.id.container, IRTabActivity.mLibraryFragment)
                        .disallowAddToBackStack()
                        .commit();
            }
        });
        IRTabActivity.abTitle.setText(getString(R.string.movies));
        IRTabActivity.abSearch.setVisibility(View.GONE);
        IRTabActivity.abAction.setVisibility(View.GONE);
        if (autoPause && videoView.getCurrentPosition() != videoView.getDuration()) {
            videoView.resume();
            autoPause = false;
        }
        if (IRTabActivity.searchItem != null)
            IRTabActivity.searchItem.collapseActionView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause();
            autoPause = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            ct.cancel();
            videoView.stopPlayback();
            videoView.clearAnimation();
        } catch (NullPointerException e) {
        }
        videoView = null;
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


            int mUIFlag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            getActivity().getWindow().getDecorView().setSystemUiVisibility(mUIFlag);   //undocumented
            getActivity().getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    // TODO Auto-generated method stub
                    if (visibility == 0) hideShow();
                }
            });
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
            fadeTime = 30000;
            show();
        }
    }

    private void setupJazziness(JazzyViewPager.TransitionEffect effect) {
        mJazzyViewPager.setTransitionEffect(effect);
        mJazzyViewPager.setAdapter(new IRVideoPagerAdapter(getChildFragmentManager(), mJazzyViewPager, video));
        mJazzyViewPager.setPageMargin(0);
        mJazzyViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                        fadeTime = 30000;
                    }
                } else {
                    hideShow();
                }
                break;
            case R.id.about:
                if (mJazzyViewPager.getCurrentItem() != 0) {
                    mJazzyViewPager.setCurrentItem(0, false);
                }
                break;
            case R.id.related:
                if (mJazzyViewPager.getCurrentItem() != 1) {
                    mJazzyViewPager.setCurrentItem(1, false);
                }
                break;
            case R.id.comments:
                if (mJazzyViewPager.getCurrentItem() != 2) {
                    mJazzyViewPager.setCurrentItem(2, false);
                }
                break;
        }
    }

    private void setupButtons(int position){
        switch (position){
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
}
