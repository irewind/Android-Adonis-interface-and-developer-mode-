package com.irewind.fragments.movies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.sdk.model.Video;
import com.irewind.ui.views.TagView;

import butterknife.ButterKnife;
import butterknife.InjectView;


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
    @InjectView(R.id.username)
    TextView txtAuthorName;

    public Video video;

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

        txtTitle.setText(video.getTitle());
        txtDescription.setText(video.getDescription());
        txtViews.setText("" + video.getViews());
        txtUpVote.setText("" + video.getLikes());
        txtDownVote.setText("" + video.getDislikes());
        txtAuthorName.setText(video.getAuthorName());
    }

    private void populateTag(){
        //TODO Remove
        TagView.Tag[] tags = {
                new TagView.Tag("Sample tag", getResources().getColor(R.color.tag_color)),
                new TagView.Tag("Another", getResources().getColor(R.color.tag_color)),
                new TagView.Tag("one", getResources().getColor(R.color.tag_color)),
                new TagView.Tag("nisip", getResources().getColor(R.color.tag_color)),
                new TagView.Tag("plaja", getResources().getColor(R.color.tag_color)),
                new TagView.Tag("hai una", getResources().getColor(R.color.tag_color)),
                new TagView.Tag("...", getResources().getColor(R.color.tab_gray_dots)),
        };
        tagView.setTags(tags, " ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.voteUp:
                vote(true);
                break;
            case R.id.voteDown:
                vote(false);
                break;
            case R.id.settings:
                break;
        }
    }

    private void vote(boolean type){
        if (type){
            //TODO vote up
        } else {
            //TODO vote down
        }
    }
}
