package com.irewind.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRMovieActivity;
import com.irewind.activities.IRPersonActivity;
import com.irewind.adapters.IRRelatedAdapter;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.VideoListEvent;
import com.irewind.sdk.api.event.VideoListFailEvent;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.User;
import com.irewind.sdk.model.Video;
import com.irewind.ui.views.RoundedImageView;
import com.irewind.utils.AppStatus;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class IRPersonFragment extends Fragment implements AdapterView.OnItemClickListener {

    public User person;
    @InjectView(R.id.profileImageView)
    RoundedImageView profileImageView;
    @InjectView(R.id.nameTextView)
    TextView nameTextView;
    @InjectView(R.id.date)
    TextView date;
    @InjectView(R.id.videoListView)
    PullToRefreshListView mPullToRefreshListView;
    @InjectView(R.id.emptyText)
    TextView emptyText;
    @InjectView(R.id.progress)
    CircularProgressBar progressBar;
    @Inject
    ApiClient apiClient;
    private ListView mListView;
    private IRRelatedAdapter mAdapter;
    private int lastPageListed = 0;
    private int numberOfPagesAvailable = 0;

    public IRPersonFragment() {
        // Required empty public constructor
    }

    public static IRPersonFragment newInstance() {
        IRPersonFragment fragment = new IRPersonFragment();
        return fragment;
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irperson, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mListView = mPullToRefreshListView.getRefreshableView();

        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                fetch(0);
            }
        });
        mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (lastPageListed + 1 < numberOfPagesAvailable) {
                    fetch(lastPageListed + 1);
                }
            }
        });
        mListView.setOnItemClickListener(this);

        mAdapter = new IRRelatedAdapter(getActivity(), R.layout.row_related_list);

        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUserInfo(person);

        apiClient.getEventBus().register(this);
        fetch(0);

        IRPersonActivity.abBack.setVisibility(View.VISIBLE);
        IRPersonActivity.abBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
        IRPersonActivity.abSearch.setVisibility(View.GONE);
        IRPersonActivity.abAction.setVisibility(View.GONE);
        IRPersonActivity.abTitle.setText(person.getDisplayName());
    }

    @Override
    public void onPause() {
        super.onPause();

        mListView.setEmptyView(null);

        apiClient.getEventBus().unregister(this);

        apiClient.cancelListUserVideosTask();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Video video = mAdapter.getItem(position - 1);

        Intent movieIntent = new Intent(getActivity(), IRMovieActivity.class);
        movieIntent.putExtra("video", video);
        startActivity(movieIntent);
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private void updateUserInfo(User user) {
        if (user != null) {
            if (user.getPicture() != null && user.getPicture().trim().length() > 0) {
                Picasso.with(getActivity()).load(user.getPicture()).placeholder(R.drawable.img_default_picture).into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.img_default_picture);
            }
            nameTextView.setText(user.getDisplayName());
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            try {
                Date parsedDate = fromUser.parse(user.getCreatedDate());
                date.setText("Joined: " + DateUtils.getRelativeTimeSpanString(parsedDate.getTime(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
            } catch (ParseException e) {
                e.printStackTrace();
            }
          //  date.setText("Joined: " + user.getCreatedDate());
        } else {
            profileImageView.setImageResource(R.drawable.img_default_picture);
            nameTextView.setText("");
            date.setText("");
        }
    }

    // --- Events --- //

    void fetch(int page) {
        apiClient.listVideosForUser(person.getId(), page, 200);
    }

    @Subscribe
    public void onEvent(VideoListEvent event) {
        progressBar.setVisibility(View.INVISIBLE);
        if (!AppStatus.getInstance(getActivity()).isOnline()) {
            emptyText.setText(getString(R.string.no_internet_connection));
        } else {
            emptyText.setText(getString(R.string.no_videos));
        }
        mListView.setEmptyView(emptyText);

        List<Video> videos = event.videos;
        PageInfo pageInfo = event.pageInfo;

        if (pageInfo.getNumber() == 0) {
            mAdapter.setVideos(videos);

            if (mPullToRefreshListView.isRefreshing()) {
                mPullToRefreshListView.onRefreshComplete();
            }
        } else {
            mAdapter.appendVideos(videos);
        }

        lastPageListed = pageInfo.getNumber();
        numberOfPagesAvailable = pageInfo.getTotalPages();
    }

    @Subscribe
    public void onEvent(VideoListFailEvent event) {
        progressBar.setVisibility(View.INVISIBLE);
        if (!AppStatus.getInstance(getActivity()).isOnline()) {
            emptyText.setText(getString(R.string.no_internet_connection));
        } else {
            emptyText.setText(getString(R.string.no_videos));
        }
        mListView.setEmptyView(emptyText);
    }
}
