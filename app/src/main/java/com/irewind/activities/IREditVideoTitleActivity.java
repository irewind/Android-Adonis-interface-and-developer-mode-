package com.irewind.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.VideoUpdateEvent;
import com.irewind.sdk.api.event.VideoUpdateFailEvent;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class IREditVideoTitleActivity extends IRBaseActivity {

    public static final String EXTRA_VIDEO_ID_KEY = "videoId";
    public static final String EXTRA_VIDEO_TITLE_KEY = "videoTitle";
    public static final String EXTRA_PROFILE_IMAGE_KEY = "profileImage";

    private long videoId;
    private String videoTitle;
    private String profileImage;

    @Inject
    ApiClient apiClient;

    @InjectView(R.id.close)
    ImageButton btnClose;

    @InjectView(R.id.profileImage)
    ImageView profileImageView;

    @InjectView(R.id.titleEdit)
    EditText titleEdit;

    @InjectView(R.id.post)
    Button btnPost;

    @InjectView(R.id.progress)
    CircularProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.activity_edit_video_title);

        Injector.inject(this);
        ButterKnife.inject(this);

        Bundle extras = getIntent().getExtras();

        videoId = extras.getLong(EXTRA_VIDEO_ID_KEY, -1);
        videoTitle = extras.getString(EXTRA_VIDEO_TITLE_KEY);
        profileImage = extras.getString(EXTRA_PROFILE_IMAGE_KEY, null);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);
                finish();
            }
        });

        if (videoTitle != null) {
            titleEdit.setText(videoTitle);
        }
        else {
            titleEdit.setText("");
        }

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTitle();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        apiClient.getEventBus().register(this);

        if (profileImage != null && profileImage.length() > 0) {
            Picasso.with(this).load(profileImage).placeholder(R.drawable.ic_launcher).into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.img_default_picture);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);
    }

    void updateTitle() {
        this.videoTitle = titleEdit.getText().toString();
        if (this.videoTitle == null || this.videoTitle.trim().length() == 0) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        apiClient.updateVideoTitle(videoId, this.videoTitle);
    }

    @Subscribe
    public void onEvent(VideoUpdateEvent event) {
        progressBar.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_VIDEO_TITLE_KEY, this.videoTitle);
        Intent intent = getIntent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Subscribe
    public void onEvent(VideoUpdateFailEvent event) {
        progressBar.setVisibility(View.GONE);
    }
}
