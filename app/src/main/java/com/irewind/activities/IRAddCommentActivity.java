package com.irewind.activities;

import android.content.Context;
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
import com.irewind.sdk.api.event.CommentAddEvent;
import com.irewind.sdk.api.event.CommentAddFailEvent;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRAddCommentActivity extends IRBaseActivity {

    public static final String EXTRA_VIDEO_ID_KEY = "videoId";
    public static final String EXTRA_PARENT_COMMENT_ID_KEY = "parentCommentId";
    public static final String EXTRA_PROFILE_IMAGE_KEY = "profileImage";

    private long videoId;
    private long parentCommentId;
    private String profileImage;

    @Inject
    ApiClient apiClient;

    @InjectView(R.id.close)
    ImageButton btnClose;

    @InjectView(R.id.profileImageComment)
    ImageView profileImageView;

    @InjectView(R.id.commentEdit)
    EditText commentEdit;

    @InjectView(R.id.post)
    Button btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.activity_ircomment);

        Injector.inject(this);
        ButterKnife.inject(this);

        Bundle extras = getIntent().getExtras();

        videoId = extras.getLong(EXTRA_VIDEO_ID_KEY, -1);
        parentCommentId = extras.getLong(EXTRA_PARENT_COMMENT_ID_KEY, -1);
        profileImage = extras.getString(EXTRA_PROFILE_IMAGE_KEY, null);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentEdit.getWindowToken(), 0);
                finish();
            }
        });

        commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        apiClient.getEventBus().register(this);

        if (profileImage != null && profileImage.length() > 0) {
            Picasso.with(this).load(profileImage).into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.img_default_picture);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);
    }

    void addComment() {
        if (parentCommentId == -1) {
            apiClient.addComment(videoId, commentEdit.getText().toString());
        }
        else {
            apiClient.replyComment(videoId, commentEdit.getText().toString(), parentCommentId);
        }
    }

    @Subscribe
    public void onEvent(CommentAddEvent event) {

        apiClient.listVideoComments(videoId, 0, 20);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentEdit.getWindowToken(), 0);
        finish();
    }

    @Subscribe
    public void onEvent(CommentAddFailEvent event) {

    }
}
