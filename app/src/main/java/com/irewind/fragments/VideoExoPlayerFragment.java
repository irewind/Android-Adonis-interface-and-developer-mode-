package com.irewind.fragments;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.CaptioningManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.VideoSurfaceView;
import com.google.android.exoplayer.metadata.TxxxMetadata;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.text.SubtitleView;
import com.google.android.exoplayer.util.Util;
import com.irewind.R;
import com.irewind.utils.exoplayer.DemoUtil;
import com.irewind.utils.exoplayer.EventLogger;
import com.irewind.utils.exoplayer.VideoControllerView;
import com.irewind.utils.exoplayer.player.DefaultRendererBuilder;
import com.irewind.utils.exoplayer.player.DemoPlayer;
import com.irewind.utils.exoplayer.player.UnsupportedDrmException;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by mpopa on 2/26/2015.
 */
public class VideoExoPlayerFragment extends Fragment implements SurfaceHolder.Callback, View.OnClickListener,
        DemoPlayer.Listener, DemoPlayer.TextListener, DemoPlayer.Id3MetadataListener {
    public static final String CONTENT_TYPE_EXTRA = "content_type";
    public static final String CONTENT_ID_EXTRA = "content_id";

    private static final String TAG = "ExoPlayerFragment";

    private static final float CAPTION_LINE_HEIGHT_RATIO = 0.0533f;
    private static final int MENU_GROUP_TRACKS = 1;
    private static final int ID_OFFSET = 2;

    private Context mContext;

    private EventLogger eventLogger;
    private VideoControllerView mediaController;
    private View shutterView;
    private VideoSurfaceView surfaceView;
    private TextView debugTextView;
    private TextView playerStateTextView;
    private SubtitleView subtitleView;
    private ImageButton btnPlayPause;

    private DemoPlayer player;
    private boolean playerNeedsPrepare;

    private long playerPosition;
    private boolean enableBackgroundAudio;

    public static Uri contentUri;
    private int contentType = DemoUtil.TYPE_OTHER;
    public static String contentId = "";


    @InjectView(R.id.videoProgress)
    CircularProgressBar progressBarVideo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }


    public static VideoExoPlayerFragment newInstance(String contentUri, int contentType, String contentId) {
        VideoExoPlayerFragment fragment = new VideoExoPlayerFragment();
        Bundle bundle = new Bundle();

        bundle.putString("contentUri", contentUri);
        bundle.putInt("contentType", contentType);
        bundle.putString("contentId", contentId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rooTview = inflater.inflate(R.layout.fragment_exoplayer, container, false);

        ButterKnife.inject(this, rooTview);
        return rooTview;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            contentUri = Uri.parse(getArguments().getString("contentUri"));
            contentType = getArguments().getInt("contentType");
            contentId = getArguments().getString("contentId");
        }
        View root = view.findViewById(R.id.root);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
                return true;
            }
        });

        shutterView = view.findViewById(R.id.shutter);

        surfaceView = (VideoSurfaceView) view.findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
        debugTextView = (TextView) view.findViewById(R.id.debug_text_view);
        debugTextView.setVisibility(View.GONE);
        btnPlayPause = (ImageButton) view.findViewById(R.id.btnPlayPause);
        btnPlayPause.setOnClickListener(this);

        playerStateTextView = (TextView) view.findViewById(R.id.player_state_view);
        playerStateTextView.setVisibility(View.GONE);
        subtitleView = (SubtitleView) view.findViewById(R.id.subtitles);

        mediaController = new VideoControllerView(mContext);
        mediaController.setAnchorView((ViewGroup) root);


        DemoUtil.setDefaultCookieManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        configureSubtitleView();
        if (player == null) {
            preparePlayer();
        } else if (player != null) {
            player.setBackgrounded(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!enableBackgroundAudio) {
            releasePlayer();
        } else {
            player.setBackgrounded(true);
        }
        shutterView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    // OnClickListener methods

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnPlayPause:
                if (mediaController != null) {
                    if (mediaController.getmPlayer().isPlaying() && mediaController.getmPlayer().canPause()) {
                        mediaController.getmPlayer().pause();
                    } else {
                        mediaController.getmPlayer().start();
                        if (mediaController.getmPlayer().getCurrentPosition() == mediaController.getmPlayer().getDuration()) {
                            Log.d("player position:", "" + mediaController.getmPlayer().getCurrentPosition());
                            Log.d("player length:", "" + mediaController.getmPlayer().getDuration());
                        }
                        hide();
                    }
                    updatePausePlay();
                }
                break;
        }
    }

    // Internal methods

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        return new DefaultRendererBuilder(mContext, contentUri, debugTextView);
    }

    private void preparePlayer() {
        if (player == null) {
            player = new DemoPlayer(getRendererBuilder());
            player.addListener(this);
            player.setTextListener(this);
            player.setMetadataListener(this);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;
            mediaController.setMediaPlayer(player.getPlayerControl());
            mediaController.setEnabled(true);

            eventLogger = new EventLogger();
            eventLogger.startSession();
            player.addListener(eventLogger);
            player.setInfoListener(eventLogger);
            player.setInternalErrorListener(eventLogger);
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;

        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(true);
    }

    private void releasePlayer() {
        if (player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
            eventLogger.endSession();
            eventLogger = null;
        }
    }

    // DemoPlayer.Listener implementation

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            showControls();
            Log.d("player_state: ", "ended");
        }
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                progressBarVideo.setVisibility(View.VISIBLE);

                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                Log.d("player_state: ", "ended");
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                progressBarVideo.setVisibility(View.VISIBLE);
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                progressBarVideo.setVisibility(View.GONE);

                //  hide();
                break;
            default:
                text += "unknown";
                break;
        }
        playerStateTextView.setText(text);

    }

    @Override
    public void onError(Exception e) {
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            int stringId = unsupportedDrmException.reason == UnsupportedDrmException.REASON_NO_DRM
                    ? R.string.drm_error_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.drm_error_unsupported_scheme
                    : R.string.drm_error_unknown;
            Toast.makeText(mContext, stringId, Toast.LENGTH_LONG).show();
        }
        playerNeedsPrepare = true;

        showControls();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, float pixelWidthAspectRatio) {
        shutterView.setVisibility(View.GONE);
        surfaceView.setVideoWidthHeightRatio(
                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }

    // User controls


    private boolean haveTracks(int type) {
        return player != null && player.getTracks(type) != null;
    }


    private void toggleControlsVisibility() {
        if (mediaController.isShowing()) {
            hide();

        } else {
            fadeTime = 7000;
            updatePausePlay();
            showControls();
            show();
            mCounter.cancel();
            mCounter.start();
        }
    }

    private int fadeTime;
    CountDownTimer mCounter = new CountDownTimer(20000000, 500) {

        @Override
        public void onTick(long millisUntilFinished) {
            if (fadeTime > 0) {
                fadeTime -= 500;
            }
            try {
                if (fadeTime <= 0 && btnPlayPause.getAlpha() == 1) hide();
            } catch (NoSuchMethodError e) {
                if (fadeTime <= 0) hide();
            }
        }

        @Override
        public void onFinish() {
        }

    };

    public void hide() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(btnPlayPause, "alpha", 0f);
        animation.setDuration(500);
        animation.start();

        animation = ObjectAnimator.ofFloat(mediaController, "alpha", 0f);
        animation.setDuration(500);
        animation.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaController.hide();
                btnPlayPause.setVisibility(View.GONE);
            }
        }, 500);
    }

    public void show() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(btnPlayPause, "alpha", 1f);
        animation.setDuration(500);
        animation.start();

        animation = ObjectAnimator.ofFloat(mediaController, "alpha", 1f);
        animation.setDuration(500);
        animation.start();
    }

    public void updatePausePlay() {
        if (mediaController == null || mediaController.getmPlayer() == null) {
            return;
        }

        btnPlayPause.setVisibility(View.VISIBLE);
        if (mediaController.getmPlayer().isPlaying()) {
            btnPlayPause.setImageResource(R.drawable.pause);
        } else {
            btnPlayPause.setImageResource(R.drawable.play);
        }
    }

    private void showControls() {
        mediaController.show(0);

    }

    // DemoPlayer.TextListener implementation

    @Override
    public void onText(String text) {
        if (TextUtils.isEmpty(text)) {
            subtitleView.setVisibility(View.INVISIBLE);
        } else {
            subtitleView.setVisibility(View.VISIBLE);
            subtitleView.setText(text);
        }
    }

    // DemoPlayer.MetadataListener implementation

    @Override
    public void onId3Metadata(Map<String, Object> metadata) {
        for (int i = 0; i < metadata.size(); i++) {
            if (metadata.containsKey(TxxxMetadata.TYPE)) {
                TxxxMetadata txxxMetadata = (TxxxMetadata) metadata.get(TxxxMetadata.TYPE);
                Log.i(TAG, String.format("ID3 TimedMetadata: description=%s, value=%s",
                        txxxMetadata.description, txxxMetadata.value));
            }
        }
    }

    // SurfaceHolder.Callback implementation

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }

    private void configureSubtitleView() {
        CaptionStyleCompat captionStyle;
        float captionTextSize = getCaptionFontSize();
        if (Util.SDK_INT >= 19) {
            captionStyle = getUserCaptionStyleV19();
            captionTextSize *= getUserCaptionFontScaleV19();
        } else {
            captionStyle = CaptionStyleCompat.DEFAULT;
        }
        subtitleView.setStyle(captionStyle);
        subtitleView.setTextSize(captionTextSize);
    }

    private float getCaptionFontSize() {
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        return Math.max(getResources().getDimension(R.dimen.subtitle_minimum_font_size),
                CAPTION_LINE_HEIGHT_RATIO * Math.min(displaySize.x, displaySize.y));
    }

    @TargetApi(19)
    private float getUserCaptionFontScaleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) mContext.getSystemService(Context.CAPTIONING_SERVICE);
        return captioningManager.getFontScale();
    }

    @TargetApi(19)
    private CaptionStyleCompat getUserCaptionStyleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) mContext.getSystemService(Context.CAPTIONING_SERVICE);
        return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
    }

}


