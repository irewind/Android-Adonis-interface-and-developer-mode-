package com.irewind.fragments.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;

import com.irewind.activities.IRMovieActivity;
import com.irewind.activities.IRMovieSettingsActivity;
import com.irewind.activities.IRTabActivity;

import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.VideoInfoEvent;
import com.irewind.sdk.api.event.VideoInfoFailEvent;
import com.irewind.sdk.api.event.VoteEvent;
import com.irewind.sdk.api.response.TagListResponse;
import com.irewind.sdk.model.Tag;

import com.irewind.sdk.model.User;
import com.irewind.sdk.model.Video;
import com.irewind.ui.views.RoundedImageView;
import com.irewind.ui.views.TagView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class IRAboutFragment extends Fragment implements View.OnClickListener{

    @InjectView(R.id.tags_view)
    TagView tagView;
    @InjectView(R.id.voteUp)
    LinearLayout upVote;
    @InjectView(R.id.voteDown)
    LinearLayout downVote;
    @InjectView(R.id.upVotes)
    TextView txtUpVote; //total votes up
    @InjectView(R.id.downVotes)
    TextView txtDownVote; //total votes down
    @InjectView(R.id.title)
    TextView txtTitle; //title of video
    @InjectView(R.id.views)
    TextView txtViews; //total number of views
    @InjectView(R.id.settings)
    ImageButton settings;
    @InjectView(R.id.textDescription)
    TextView txtDescription; //Description of the text;
    @InjectView(R.id.profileImage)
    RoundedImageView profileImage;
    @InjectView(R.id.username)
    TextView txtAuthorName;
    @InjectView(R.id.progress)
    CircularProgressBar progressBar;

    public Video video;

    @Inject
    ApiClient apiClient;

    public static IRAboutFragment newInstance() {
        IRAboutFragment fragment = new IRAboutFragment();
        return fragment;
    }

    public IRAboutFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irabout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        upVote.setOnClickListener(this);
        downVote.setOnClickListener(this);
        settings.setOnClickListener(this);

        updateVideoInfo(video);
    }

    @Override
    public void onResume() {
        super.onResume();

        apiClient.getEventBus().register(this);

        apiClient.videoById(video.getId());
        apiClient.listVideoTags(video.getId());
    }

    @Override
    public void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.voteUp:
                like();
                break;
            case R.id.voteDown:
                dislike();
                break;
            case R.id.settings:
                Intent movieIntent = new Intent(getActivity(), IRMovieSettingsActivity.class);
                movieIntent.putExtra("video", video);
                startActivity(movieIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
        }
    }

    private void updateVideoInfo(Video video) {
        txtTitle.setText(video.getTitle());
        txtDescription.setText(video.getDescription());
        txtViews.setText(video.getViews() + " views");
        txtUpVote.setText("" + video.getLikes());
        txtDownVote.setText("" + video.getDislikes());

        User user = video.getUser();

        if (user != null) {
            if (user.getPicture() != null && user.getPicture().trim().length() > 0) {
                Picasso.with(getActivity()).load(user.getPicture()).placeholder(R.drawable.img_default_picture).into(profileImage);
            }
            txtAuthorName.setText(user.getDisplayName());

            if (apiClient.getActiveUser() != null && apiClient.getActiveUser().getId() == user.getId()) {
                settings.setVisibility(View.VISIBLE);
            }
            else {
                settings.setVisibility(View.GONE);
            }
        }
    }

    private void like(){
        progressBar.setVisibility(View.VISIBLE);
        apiClient.likeVideo(video.getId());
    }

    private void dislike() {
        progressBar.setVisibility(View.VISIBLE);
        apiClient.dislikeVideo(video.getId());
    }

    // --- Events --- //

    @Subscribe
    public void onEvent(VideoInfoEvent event) {
        if (event.video != null) {
            this.video = event.video;
            updateVideoInfo(event.video);
        }
        progressBar.setVisibility(View.GONE);
    }

    @Subscribe
    public void onEvent(VideoInfoFailEvent event) {
        progressBar.setVisibility(View.GONE);
    }

    @Subscribe
    public void onEvent(TagListResponse event) {
        TagListResponse.EmbeddedResponse embeddedResponse = event.getEmbeddedResponse();
        if (embeddedResponse != null) {
            List<Tag> tags = embeddedResponse.getTags();
            if (tags != null) {
                ArrayList<TagView.Tag> tagArrayList = new ArrayList<TagView.Tag>();
                int tagCount = tags.size();
                for (int i = 0; i < tagCount && i < 5; i++) {
                    Tag tag = tags.get(i);
                    tagArrayList.add(new TagView.Tag(tag.getTagName(), getResources().getColor(R.color.tag_color)));
                }

                if (tagCount > 5) {
                    tagArrayList.add(new TagView.Tag("...", getResources().getColor(R.color.tab_gray_dots)));
                }

                tagView.setTags(tagArrayList, " ");
            }
        }
    }

    @Subscribe
    public void onEvent(VoteEvent event) {
        apiClient.videoById(video.getId());
    }
}
